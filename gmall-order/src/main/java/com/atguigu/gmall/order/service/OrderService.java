package com.atguigu.gmall.order.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.core.exception.OrderException;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderItemVO;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptors.LoginInterceptor;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallOmsClient omsClient;

    @Autowired
    private GmallCartClient cartClient;

    @Autowired
    private GmallUmsClient umsClient;

    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private GmallWmsClient wmsClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String TOKEN_PREFIX = "order:token:";

    // TODO: 2020/12/4 此教程和雷丰阳的不同地方在于,在购物车和订单确认乃至支付模块没有进行用户登录的拦截,所以从登录拦截器获取用户信息之后
    //     * 该教程就将用户信息不停的在异步方法之间传递,没有发生丢失请求头和异步编排时获取请求头数据为空的情况
    //     * 但是实际生产中肯定是要进行登录校验的,肯定是雷丰阳老师的教学更符合现实
    //     * 雷丰阳做了用户登录的拦截,即是如果在cookie或者session中获取不到用户信息,就会让其重新登录,所以需要写请求拦截器以便
    //     * 在远程调用的时候把请求头给添加进去,在异步编排的时候从RequestContextHolder里获取request请求信息,将request请求信息
    //     * 传递到其他异步线程的RequestContextHolder里
    /**
    * 订单确认
    */
    public OrderConfirmVO confirm() {

        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();

        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Long userId = userInfo.getId();
        if (userId == null) {
            return null;
        }

//        List<CompletableFuture> futures = new ArrayList<>();

        CompletableFuture<Void> addressCompletableFuture = CompletableFuture.runAsync(() -> {
            // 获取用户的收货地址列表， 根据用户id查询收货地址列表
            Resp<List<MemberReceiveAddressEntity>> addressResp = this.umsClient.queryAddressesByUserId(userId);
            List<MemberReceiveAddressEntity> memberReceiveAddressEntities = addressResp.getData();
            orderConfirmVO.setAddresses(memberReceiveAddressEntities);
        }, threadPoolExecutor);
//        futures.add(addressCompletableFuture);

        // 获取购物车中选中的商品信息  skuId count
        // TODO: 2020/12/4   注意这里涉及到一个购物车的数据是否是最新的问题，这个地方如此处理是想着购物车的数据会发生变化，所以订单确认时获取的信息全是最新的
        //  而不是用购物车里的老数据直接返回。不过这种遍历过程中远程调用的方式确实存在性能较低的情况。到时候可以看看产品经理是处于什么方面的考虑，
        //  如果时性能就直接从redis中取，如果要求准确性，就远程重新获取商品信息
        CompletableFuture<Void> bigSkuCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Resp<List<Cart>> cartsResp = this.cartClient.queryCheckedCartsByUserId(userId);
            List<Cart> cartList = cartsResp.getData();
            if (CollectionUtils.isEmpty(cartList)) {
                throw new OrderException("请勾选购物车商品！");
            }
            return cartList;
        }, threadPoolExecutor).thenAcceptAsync(cartList -> {
            List<OrderItemVO> itemVOS = cartList.stream().map(cart -> {
                OrderItemVO orderItemVO = new OrderItemVO();
                Long skuId = cart.getSkuId();
                CompletableFuture<Void> skuCompletableFuture = CompletableFuture.runAsync(() -> {
                    Resp<SkuInfoEntity> skuInfoEntityResp = this.pmsClient.querySkuById(skuId);
                    SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
                    if (skuInfoEntity != null) {
                        orderItemVO.setWeight(skuInfoEntity.getWeight());
                        orderItemVO.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
                        orderItemVO.setPrice(skuInfoEntity.getPrice());
                        orderItemVO.setTitle(skuInfoEntity.getSkuTitle());
                        orderItemVO.setSkuId(skuId);
                        orderItemVO.setCount(cart.getCount());
                    }
                });

                CompletableFuture<Void> saleAttrCompletableFuture = CompletableFuture.runAsync(() -> {
                    Resp<List<SkuSaleAttrValueEntity>> saleAttrValueResp = this.pmsClient.querySkuSaleAttrValuesBySkuId(skuId);
                    List<SkuSaleAttrValueEntity> attrValueEntities = saleAttrValueResp.getData();
                    orderItemVO.setSaleAttrValues(attrValueEntities);
                }, threadPoolExecutor);

                CompletableFuture<Void> wareSkuCompletableFuture = CompletableFuture.runAsync(() -> {
                    Resp<List<WareSkuEntity>> wareSkuResp = this.wmsClient.queryWareSkusBySkuId(skuId);
                    List<WareSkuEntity> wareSkuEntities = wareSkuResp.getData();
                    if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                        orderItemVO.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
                    }
                }, threadPoolExecutor);

                CompletableFuture.allOf(skuCompletableFuture, saleAttrCompletableFuture, wareSkuCompletableFuture).join();
                return orderItemVO;
            }).collect(Collectors.toList());
            orderConfirmVO.setOrderItems(itemVOS);
        }, threadPoolExecutor);

        // 查询用户信息，获取积分
        CompletableFuture<Void> memberCompletableFuture = CompletableFuture.runAsync(() -> {
            Resp<MemberEntity> memberEntityResp = this.umsClient.queryMemberById(userId);
            MemberEntity memberEntity = memberEntityResp.getData();
            orderConfirmVO.setBounds(memberEntity.getIntegration());
        }, threadPoolExecutor);

        // 生成一个唯一标志，防止重复提交（响应到页面有一份，有一份保存到redis中）
        CompletableFuture<Void> tokenCompletableFuture = CompletableFuture.runAsync(() -> {
            String orderToken = IdWorker.getIdStr();
            orderConfirmVO.setOrderToken(orderToken);
            this.redisTemplate.opsForValue().set(TOKEN_PREFIX + orderToken, orderToken);
        }, threadPoolExecutor);

        CompletableFuture.allOf(addressCompletableFuture, bigSkuCompletableFuture, memberCompletableFuture, tokenCompletableFuture).join();

        return orderConfirmVO;
    }

    public OrderEntity submit(OrderSubmitVO submitVO) {

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        // 获取orderToken
        String orderToken = submitVO.getOrderToken();

        // 1. 防重复提交【令牌的对比和删除必须保证原子性，需要用lua脚本实现】，查询redis中有没有orderToken信息，有，则是第一次提交，放行并删除redis中的orderToken
        // lua脚本的意思是，redis执行get操作，获取到key对应的redis的值，如果传的参数值和其存获取的值相等，则执行删除操作，将存的值删除，删除成功返回1，否则返回0。0令牌校验失败，1删除成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        // this.redisTemplate.execute(）的第一个参数是指定lua脚本执行之后的返回值类型DefaultRedisScript(String script, @Nullable Class<T>
        // resultType)第二个参数是要获取的值对应的key的集合，第三个参数是传入的参数值，是一个可变数组
        Long flag = this.redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(TOKEN_PREFIX + orderToken), orderToken);
        if (flag == 0) {
            throw new OrderException("订单不可重复提交！");
        }

        // 2. 校验价格，总价一致放行
        List<OrderItemVO> items = submitVO.getOrderItems(); // 送货清单
        BigDecimal totalPrice = submitVO.getTotalPrice(); // 总价
        if (CollectionUtils.isEmpty(items)) {
            throw new OrderException("没有购买的商品，请到购物车中勾选商品！");
        }
        // todo 获取实时总价信息，案例里计算的比较简单，没有减去积分，折扣等等
        BigDecimal currentTotalPrice = items.stream().map(item -> {
            Resp<SkuInfoEntity> skuInfoEntityResp = this.pmsClient.querySkuById(item.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            if (skuInfoEntity != null) {
                return skuInfoEntity.getPrice().multiply(new BigDecimal(item.getCount()));
            }
            return new BigDecimal(0);
        }).reduce((a, b) -> a.add(b)).get();
        // todo 判断实时总价和页面的总价格是否一致，这个地方严格要求不能损失精度，其实在0.01的范围内还是可以接受的
        if (currentTotalPrice.compareTo(totalPrice) != 0) {
            throw new OrderException("页面已过期，请刷新页面后重新下单！");
        }


        // 3. 校验库存是否充足并锁定库存，一次性提示所有库存不够的商品信息 （远程接口待开发）
        List<SkuLockVO> lockVOS = items.stream().map(orderItemVO -> {
            SkuLockVO skuLockVO = new SkuLockVO();
            skuLockVO.setSkuId(orderItemVO.getSkuId());
            skuLockVO.setCount(orderItemVO.getCount());
            skuLockVO.setOrderToken(orderToken);
            return skuLockVO;
        }).collect(Collectors.toList());
        Resp<Object> wareResp = this.wmsClient.checkAndLockStore(lockVOS);
        if (wareResp.getCode() != 0) {
            throw new OrderException(wareResp.getMsg());
        }

//        int i = 1 / 0;

        // 4. 下单（创建订单及订单详情， 远程接口待开发）
        Resp<OrderEntity> orderEntityResp = null;
        try {
            submitVO.setUserId(userInfo.getId());
            orderEntityResp = this.omsClient.saveOrder(submitVO);
        } catch (Exception e) {
            e.printStackTrace();
            // 发送消息给wms，解锁对应的库存
            this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE", "stock.unlock", orderToken);
            throw new OrderException("服务器错误，创建订单失败！");
        }

        // 5. 删除购物车 （发送消息删除购物车）
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userInfo.getId());
        List<Long> skuIds = items.stream().map(OrderItemVO::getSkuId).collect(Collectors.toList());
        map.put("skuIds", skuIds);
        this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE", "cart.delete", map);

        if (orderEntityResp != null) {
            return orderEntityResp.getData();
        }

        return null;
    }

    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println("这是一个定时任务");
        }, 10l, 20l, TimeUnit.SECONDS);
    }

}

package com.atguigu.gmall.order.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.order.pay.AlipayTemplate;
import com.atguigu.gmall.order.pay.PayAsyncVo;
import com.atguigu.gmall.order.pay.PayVo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("confirm")
    public Resp<OrderConfirmVO> confirm() {

        OrderConfirmVO confirmVO = this.orderService.confirm();
        return Resp.ok(confirmVO);
    }

    /**
     * 将支付页让浏览器展示
     * 这个接口主要就是调阿里的支付接口,展示支付页而已,所以只传订单编号过来也是可以的,根据订单编号来查询出总额
     * @param submitVO
     * @return
     */
//    @PostMapping(value = "submit", produces = "text/html")// 明确的告诉浏览器我返回的类型是text/html
    @PostMapping(value = "submit")// 明确的告诉浏览器我返回的类型是text/html
    public String submit(@RequestBody OrderSubmitVO submitVO) throws AlipayApiException {

        OrderEntity orderEntity = this.orderService.submit(submitVO);
        PayVo payVo = new PayVo();
        // 传过去交易编号,支付宝会根据交易编号和我们的appkeyid为我们做支付重复的校验
        payVo.setOut_trade_no(orderEntity.getOrderSn());
        payVo.setTotal_amount(orderEntity.getPayAmount() != null ? orderEntity.getPayAmount().toString() : "100");
        payVo.setSubject("谷粒商城");
        payVo.setBody("支付平台");
        String form = this.alipayTemplate.pay(payVo);
        return form;
    }

    /**
     * 支付成功之后回调要调用的方法.此回调要调用的方法路径在配置文件里指定.
     * 支付宝会给我们返回响应数据,其响应数据我们用payAsyncVo封装
     * @param payAsyncVo
     * @return
     */
    @PostMapping("pay/success")
    public Resp<Object> paySuccess(PayAsyncVo payAsyncVo) {
        // 支付成功之后,需要做更新订单状态,减库存,加积分
        this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE", "order.pay", payAsyncVo.getOut_trade_no());
        return Resp.ok(null);
    }

    @PostMapping("seckill/{skuId}")
    public Resp<Object> seckill(@PathVariable("skuId") Long skuId) {


        RSemaphore semaphore = this.redissonClient.getSemaphore("semphore:lock:" + skuId);
        semaphore.trySetPermits(500);

        if (semaphore.tryAcquire()) {
            // 获取redis中的库存信息
            String countString = this.redisTemplate.opsForValue().get("order:seckill:" + skuId);

            // 没有，秒杀结束
            if (StringUtils.isEmpty(countString) || Integer.parseInt(countString) == 0) {
                return Resp.ok("秒杀结束");
            }
            Integer count = Integer.parseInt(countString);

            // 减库存
            this.redisTemplate.opsForValue().set("order:seckill:" + skuId, String.valueOf(--count));

            // 发送消息给消息队列，将来真正的减库存
            SkuLockVO skuLockVO = new SkuLockVO();
            skuLockVO.setCount(1);
            skuLockVO.setSkuId(skuId);
            String orderToken = IdWorker.getIdStr();
            skuLockVO.setOrderToken(orderToken);
            this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE", "order.seckill", skuLockVO);

            RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("count:down:" + orderToken);
            countDownLatch.trySetCount(1);

            semaphore.release();
            // 响应成功
            return Resp.ok("恭喜你，秒杀成功！赶紧付款吧");
        }
        return Resp.ok("再接再励！！");
    }

    @GetMapping("seckill/{orderToken}")
    public Resp<Object> querySeckill(@PathVariable("orderToken") String orderToken) throws InterruptedException {
        RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("count:down:" + orderToken);
        countDownLatch.await();


        // 查询订单，并响应
        // 发送feign请求 查询订单

        return Resp.ok(null);
    }
}

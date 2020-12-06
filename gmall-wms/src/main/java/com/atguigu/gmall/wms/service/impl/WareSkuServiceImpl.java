package com.atguigu.gmall.wms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String KEY_PREFIX = "stock:lock";

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageVo(page);
    }

    @Override
    @Transactional
    public String checkAndLockStore(List<SkuLockVO> skuLockVOS) {

        if (CollectionUtils.isEmpty(skuLockVOS)) {
            return "没有选中的商品";
        }

        // 检验并锁定库存
        skuLockVOS.forEach(skuLockVO -> {
            lockStore(skuLockVO);
        });

        // 如果有其他商品锁定库存失败,则解锁已锁定的库存
        List<SkuLockVO> unLockSku = skuLockVOS.stream().filter(skuLockVO -> skuLockVO.getLock() == false).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(unLockSku)) {
            // 解锁已锁定商品的库存
            List<SkuLockVO> lockSku = skuLockVOS.stream().filter(SkuLockVO::getLock).collect(Collectors.toList());
            lockSku.forEach(skuLockVO -> {
                this.wareSkuDao.unLockStore(skuLockVO.getWareSkuId(), skuLockVO.getCount());
            });
            // 提示锁定失败的商品
            List<Long> skuIds = unLockSku.stream().map(SkuLockVO::getSkuId).collect(Collectors.toList());
            return "下单失败，商品库存不足：" + skuIds.toString();
        }

        // 如果锁定成功,此时再把token放到redis里,等待用户支付时使用
        String orderToken = skuLockVOS.get(0).getOrderToken();
        this.redisTemplate.opsForValue().set(KEY_PREFIX + orderToken, JSON.toJSONString(skuLockVOS));

        // 锁定成功，发送延时消息，用于客户取消订单或者超时未支付时的 定时解锁
        this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE", "stock.ttl", orderToken);

        return null;
    }

    private void lockStore(SkuLockVO skuLockVO){

        // TODO: 2020/12/5 此处使用锁的方式是恰当的,即便不指定在finally里释放锁,由于reddissonLock有看门狗机制,过期也会把锁释放掉,不会像可重入锁那样造成死锁
        RLock lock = this.redissonClient.getLock("stock:" + skuLockVO.getSkuId());
        lock.lock();
        // 查询剩余库存够不够
        List<WareSkuEntity> wareSkuEntities = this.wareSkuDao.checkStore(skuLockVO.getSkuId(), skuLockVO.getCount());
        if (!CollectionUtils.isEmpty(wareSkuEntities)) {
            // 锁定库存信息
            Long id = wareSkuEntities.get(0).getId();
            this.wareSkuDao.lockStore(id, skuLockVO.getCount());
            skuLockVO.setWareSkuId(id);
            skuLockVO.setLock(true);
        } else {
            skuLockVO.setLock(false);
        }

        lock.unlock();
    }

}

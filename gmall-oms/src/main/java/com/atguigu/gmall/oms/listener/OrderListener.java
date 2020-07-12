package com.atguigu.gmall.oms.listener;

import com.atguigu.gmall.oms.dao.OrderDao;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.ums.vo.UserBoundsVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrderListener {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RabbitListener(queues = {"ORDER-DEAD-QUEUE"})
    public void closeOrder(String orderToken){
        // 如果执行了关单操作
        if(this.orderDao.closeOrder(orderToken) == 1){
            // 解锁库存
            /**
             * @param exchange the name of the exchange
             * @param routingKey the routing key
             * @param message a message to send
             * @param messagePostProcessor a processor to apply to the message before it is sent   可以通过这个来指定消息的唯一id
             */
            this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE", "stock.unlock", orderToken);
        }
    }

    /**
     *  我们可以用rabbitListener注解中的bindings来指定绑定关系[即消息通过那个交换机路由到那个队列],如果不在此指定的话,也可以在配置文件中通过配置的方式来指定,
     *  rabbitListener注解指定监听的队列也是可以的
     *
     *
     * 监听支付成功之后的回调,更新订单状态,减库存,加积分
     * 传过来的订单号这个参数我们用orderToken字符串接收
     * 减库存和加积分这两个业务实现分别都是在库存服务和用户服务,所以我们依然用消息队列来发送消息
     * @param orderToken
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ORDER-PAY-QUEUE", durable = "true"),
            exchange = @Exchange(value = "GMALL-ORDER-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"order.pay"}
    ))
    public void payOrder(String orderToken, Message message, Channel channel) {

//        获取发送标识 是自增的
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            /**
             * @param prefetchSize 没限制就是0
             * @param prefetchCount 会告诉RabbitMQ不要同时给一个消费者推送多于N个消息，即一旦有N个消息还没有ack，则该consumer将block掉，直到有消息ack
             * @param global true\false 是否将上面设置应用于channel，简单点说，就是上面限制是channel级别的还是consumer级别
             */
//            channel.basicQos(1,10,true);

            //        手动签收确认
//        * @param deliveryTag the tag from the received {@link com.rabbitmq.client.AMQP.Basic.GetOk} or {@link com.rabbitmq.client.AMQP.Basic.Deliver}
//     * @param multiple 批量签收
            channel.basicAck(deliveryTag,false);

            // 拒签
//            long deliveryTag, boolean multiple 是否批量拒收, boolean requeue 消息是否重新入队
//            channel.basicNack(deliveryTag,false,true);
        } catch (IOException e) {

        }
        // 更新订单状态
        if (this.orderDao.payOrder(orderToken) == 1) {
            // 减库存
            this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE", "stock.minus", orderToken);

            // 加积分[通过订单编号获取用户id,用户的成长积分和赠送积分]
            OrderEntity orderEntity = this.orderDao.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderToken));
            UserBoundsVO boundsVO = new UserBoundsVO();
            boundsVO.setMemberId(orderEntity.getMemberId());
            boundsVO.setGrowth(orderEntity.getGrowth());
            boundsVO.setIntegration(orderEntity.getIntegration());
            // 目前加积分的监听未实现
            this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE", "user.bounds", boundsVO);
        }
    }
}

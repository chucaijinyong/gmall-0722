package com.atguigu.gmall.oms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建交换机或者队列和路由也可以用AmqpAdmin这个类,但是用spring给我们往容器中注入bean这种方式更简单
 * 如果rabbitMQ里面没有它就会自动创建这些交换机或者队列和路由
 */

@Configuration
public class RabbitMqConfig {

    /**
     * 如果你更改了队列的属性之后,要把之前的队列给删掉,要不然它还用的是原来的队列
     * @return
     */
    @Bean("ORDER-TTL-QUEUE")
    public Queue ttlQueue(){
//        死信队列是有属性,这些属性分别是队列或消息过期之后将这些队列里的消息发送到那个交换机,通过什么路由键,这些消息的过期时间是多少
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", "GMALL-ORDER-EXCHANGE");
        map.put("x-dead-letter-routing-key", "order.dead");
//        以毫秒为单位
        map.put("x-message-ttl", 1200000);
//        String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        return new Queue("ORDER-TTL-QUEUE", true, false, false, map);
    }

    @Bean("ORDER-TTL-BINDING")
    public Binding ttlBinding(){
//        String destination目的地,即是队列,
//        Binding.DestinationType destinationType目的地的类型,即是queue还是topic,
//        String exchange, String routingKey, Map<String, Object> arguments
        return new Binding("ORDER-TTL-QUEUE", Binding.DestinationType.QUEUE, "GMALL-ORDER-EXCHANGE", "order.ttl", null);
    }

    @Bean("ORDER-DEAD-QUEUE")
    public Queue dlQueue(){
        return new Queue("ORDER-DEAD-QUEUE", true, false, false, null);
    }

    @Bean("ORDER-DEAD-BINDING")
    public Binding deadBinding(){

        return new Binding("ORDER-DEAD-QUEUE", Binding.DestinationType.QUEUE, "GMALL-ORDER-EXCHANGE", "order.dead", null);
    }

}

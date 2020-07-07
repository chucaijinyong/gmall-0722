package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author chubaodong
 * @version v1.0.0
 * @Package : com.atguigu.gmall.order.config
 * @Description : 此接口来指定生产端发送消息到达broker 和exchange交换机发送消息持久化到队列
 *                  以及消费端没有成功消费消息的处理方案
 * @Create on : 2020/7/7 19:36
 **/
@Configuration
public class RabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 使用json序列化机制将消息转换为json
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    /**
     * 定制消息失败时回调的处理方式
     */

    @PostConstruct // 让该配置对象创建完成之后执行
    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * Confirmation callback.
             * @param correlationData correlation data for the callback. 消息关联标识
             * @param ack true for ack, false for nack 消息是否成功收到
             * @param cause An optional cause, for nack, when available, otherwise null. 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {

            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * Returned message callback.
             * @param message the returned message. 投递失败的消息
             * @param replyCode the reply code. 返回状态码
             * @param replyText the reply text. 回复的文本内容
             * @param exchange the exchange. 交换机
             * @param routingKey the routing key. 路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {

            }
        });
    }
}

package com.atguigu.gmall.pms.controller;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * @author chubaodong
 * @version v1.0.0
 * @Package : com.atguigu.gmall.pms.controller
 * @Description : TODO
 * @Create on : 2020/7/6 21:33
 **/
public class DemoListener {

    @RabbitListener(queues = {})
    public String getListener(Message message) {
        System.out.println(message);
        return null;
    }
}

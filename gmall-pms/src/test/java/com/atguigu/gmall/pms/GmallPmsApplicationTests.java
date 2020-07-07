package com.atguigu.gmall.pms;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallPmsApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test1() {
        rabbitTemplate.convertAndSend("chucai_exchange","chucai_rk","songhaixiawoaini");

    }
}

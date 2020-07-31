package com.atguigu.gmall.pms;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class GmallPmsApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test1() {
//        System.out.println(stringRedisTemplate.opsForValue().get("name"));
//        for (String man : stringRedisTemplate.boundListOps("welcome").range(0, -1)) {
//            System.out.println(man);
//        }
//        System.out.println("==========================================");
//        for (Object man : stringRedisTemplate.boundHashOps("man").keys()) {
//            System.out.println(man);
//        }
        System.out.println("==========================================");
        for (String s : stringRedisTemplate.opsForSet().difference("baodong", "haixia")) {
            System.out.println(s);
        }


//        rabbitTemplate.convertAndSend("chucai_exchange","chucai_rk","songhaixiawoaini");

    }
}

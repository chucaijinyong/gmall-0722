package com.atguigu.gmall.pms;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    @Test
    public void test2(){

        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(1);
        LocalDate localDate1 = now.plusDays(2);

        System.out.println("localDate = " + localDate);
        System.out.println("localDate1 = " + localDate1);

        LocalTime min = LocalTime.MIN;
        System.out.println("min = " + min);
        LocalTime max = LocalTime.MAX;
        System.out.println("max = " + max);

        LocalDateTime of = LocalDateTime.of(now, min);
        String format = of.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("format = " + format);
    }
}

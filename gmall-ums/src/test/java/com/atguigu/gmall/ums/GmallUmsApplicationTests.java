package com.atguigu.gmall.ums;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallUmsApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test1(){
        System.out.println(DigestUtils.md5Hex("123456"));
        System.out.println(DigestUtils.md5Hex("123456"+"YzcmCZNvbXocrsz9dm8e"));
    }

}

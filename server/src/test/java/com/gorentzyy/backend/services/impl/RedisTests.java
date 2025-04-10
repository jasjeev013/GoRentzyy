package com.gorentzyy.backend.services.impl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Disabled
    @Test
    void testMail(){
//        System.out.println(redisTemplate==null);

//        redisTemplate.opsForValue().set("email","jasjeev99@gmail.com");

        Object email =  redisTemplate.opsForValue().get("email");
        System.out.println("Email " + email);
        int a = 1;
    }
}

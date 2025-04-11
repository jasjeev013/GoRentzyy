package com.gorentzyy.backend.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gorentzyy.backend.services.RedisService;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate redisTemplate;
    private final ModelMapper modelMapper;

    public RedisServiceImpl(RedisTemplate redisTemplate, ModelMapper modelMapper) {
        this.redisTemplate = redisTemplate;
        this.modelMapper = modelMapper;
    }

    @Override
    public <T> T get(String key,Class<T> responseClass) {
        try {
            Object o = redisTemplate.opsForValue().get(key);
            return modelMapper.map(o,responseClass);
        }catch (Exception e){
            throw e;
        }

    }

    @Override
    public void set(String key, Object o, Long ttl) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key,jsonValue,ttl, TimeUnit.SECONDS);

        }catch (Exception e){
            throw new RuntimeException("Redis Fault");
        }
    }
}

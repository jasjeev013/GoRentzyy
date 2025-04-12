package com.gorentzyy.backend.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gorentzyy.backend.services.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }


    public <T> Optional<T> get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(objectMapper.convertValue(value, clazz));
        } catch (Exception e) {
            log.error("Redis get operation failed for key: {}", key, e);
            return Optional.empty();
        }
    }

    public void set(String key, Object value, Duration ttl) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            if (ttl != null) {
                ops.set(key, value, ttl);
            } else {
                ops.set(key, value);
            }
        } catch (Exception e) {
            log.error("Redis set operation failed for key: {}", key, e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }
}

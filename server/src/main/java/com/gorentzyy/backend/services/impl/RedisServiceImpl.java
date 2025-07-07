package com.gorentzyy.backend.services.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gorentzyy.backend.services.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
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
    /**
     * Retrieves a list of objects from Redis for the given key
     * @param key The Redis key to retrieve
     * @param elementClass The class type of elements in the list
     * @return Optional containing the list if found, empty otherwise
     */
    public <T> Optional<List<T>> getList(String key, Class<T> elementClass) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null || !(value instanceof String json)) {
                return Optional.empty();
            }

            JavaType type = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, elementClass);

            List<T> list = objectMapper.readValue(json, type);
            return Optional.of(list);
        } catch (Exception e) {
            log.error("Redis getList operation failed for key: {}", key, e);
            return Optional.empty();
        }
    }


    /**
     * Stores a list of objects in Redis with optional TTL
     * @param key The Redis key to store under
     * @param value The list of values to store
     * @param ttl Optional time-to-live duration
     */
    public <T> void setList(String key, List<T> value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            if (ttl != null) {
                ops.set(key, json, ttl);
            } else {
                ops.set(key, json);
            }
        } catch (Exception e) {
            log.error("Redis setList operation failed for key: {}", key, e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }

}

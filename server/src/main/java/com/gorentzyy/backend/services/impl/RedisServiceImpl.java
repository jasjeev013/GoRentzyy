package com.gorentzyy.backend.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gorentzyy.backend.exceptions.RedisOperationException;
import com.gorentzyy.backend.services.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    private static final String REDIS_OPERATION_FAILED = "Redis operation failed";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> clazz) {
        if (!StringUtils.hasText(key)) {
            log.warn("Attempt to get value with empty key");
            return Optional.empty();
        }

        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.debug("Cache miss for key: {}", key);
                return Optional.empty();
            }
            log.debug("Cache hit for key: {}", key);
            return Optional.ofNullable(objectMapper.convertValue(value, clazz));
        } catch (Exception e) {
            log.error("Failed to get value from Redis for key: {}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(String key) {
        if (!StringUtils.hasText(key)) {
            log.warn("Attempt to delete with empty key");
            return;
        }

        try {
            Boolean deleted = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(deleted)) {
                log.debug("Successfully deleted key: {}", key);
            } else {
                log.debug("Key not found for deletion: {}", key);
            }
        } catch (Exception e) {
            log.error("Failed to delete key: {} from Redis", key, e);
            throw new RedisOperationException(REDIS_OPERATION_FAILED+e);
        }
    }

    @Override
    public void set(String key, Object value, Duration ttl) {
        validateKeyValue(key, value);

        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            if (ttl != null) {
                ops.set(key, value, ttl);
                log.debug("Set key: {} with TTL: {} seconds", key, ttl.getSeconds());
            } else {
                ops.set(key, value);
                log.debug("Set key: {} with no TTL", key);
            }
        } catch (Exception e) {
            log.error("Failed to set value in Redis for key: {}", key, e);
            throw new RedisOperationException(REDIS_OPERATION_FAILED+e);
        }
    }

    @Override
    public <T> Optional<List<T>> getList(String key, Class<T> elementClass) {
        if (!StringUtils.hasText(key)) {
            log.warn("Attempt to get list with empty key");
            return Optional.empty();
        }

        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null || !(value instanceof String json)) {
                log.debug("Cache miss for list key: {}", key);
                return Optional.empty();
            }

            JavaType type = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, elementClass);

            List<T> list = objectMapper.readValue(json, type);
            log.debug("Cache hit for list key: {}, size: {}", key, list.size());
            return Optional.of(list);
        } catch (Exception e) {
            log.error("Failed to get list from Redis for key: {}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public <T> void setList(String key, List<T> value, Duration ttl) {
        validateKeyValue(key, value);

        try {
            String json = objectMapper.writeValueAsString(value);
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();

            if (ttl != null) {
                ops.set(key, json, ttl);
                log.debug("Set list for key: {} with TTL: {} seconds, size: {}",
                        key, ttl.getSeconds(), value.size());
            } else {
                ops.set(key, json);
                log.debug("Set list for key: {} with no TTL, size: {}", key, value.size());
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize list for key: {}", key, e);
            throw new RedisOperationException("Failed to serialize data for Redis: "+e);
        } catch (Exception e) {
            log.error("Failed to set list in Redis for key: {}", key, e);
            throw new RedisOperationException(REDIS_OPERATION_FAILED+e);
        }
    }

    @Override
    public Boolean hasKey(String key) {
        if (!StringUtils.hasText(key)) {
            log.warn("Attempt to check existence with empty key");
            return false;
        }

        try {
            Boolean exists = redisTemplate.hasKey(key);
            log.debug("Checked existence for key: {}, result: {}", key, exists);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Failed to check key existence: {}", key, e);
            return false;
        }
    }

    @Override
    public Boolean expire(String key, Duration ttl) {
        if (!StringUtils.hasText(key)) {
            log.warn("Attempt to expire with empty key");
            return false;
        }
        if (ttl == null || ttl.isNegative() || ttl.isZero()) {
            log.warn("Invalid TTL provided for key: {}", key);
            return false;
        }

        try {
            Boolean result = redisTemplate.expire(key, ttl.toMillis(), TimeUnit.MILLISECONDS);
            if (Boolean.TRUE.equals(result)) {
                log.debug("Set expiration for key: {} to {} seconds", key, ttl.getSeconds());
            } else {
                log.debug("Key not found for expiration: {}", key);
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to set expiration for key: {}", key, e);
            return false;
        }
    }

    private void validateKeyValue(String key, Object value) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Redis key cannot be null or empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("Redis value cannot be null");
        }
    }
}
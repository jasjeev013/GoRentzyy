package com.gorentzyy.backend.services;

import java.time.Duration;
import java.util.Optional;

public interface RedisService {

    <T> Optional<T> get(String key, Class<T> responseClass);
    void set(String key, Object o, Duration ttl);
}

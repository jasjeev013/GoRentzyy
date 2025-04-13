package com.gorentzyy.backend.services;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface RedisService {

    <T> Optional<T> get(String key, Class<T> responseClass);
    void set(String key, Object o, Duration ttl);

    <T> Optional<List<T>> getList(String key, Class<T> elementClass);
    
    <T> void setList(String key, List<T> value, Duration ttl);
}

package com.gorentzyy.backend.services;

public interface RedisService {

    <T> T get(String key,Class<T> responseClass);
    void set(String key,Object o,Long ttl);
}

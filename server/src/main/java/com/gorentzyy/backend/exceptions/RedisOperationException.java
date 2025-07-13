package com.gorentzyy.backend.exceptions;

public class RedisOperationException extends RuntimeException{
    public RedisOperationException(String message) {
        super(message);
    }
}

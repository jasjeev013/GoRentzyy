package com.gorentzyy.backend.exceptions;

public class PromotionCodeAlreadyExistsException extends RuntimeException {
    public PromotionCodeAlreadyExistsException(String message) {
        super(message);
    }
}

package com.gorentzyy.backend.exceptions;

public class PaymentProcessingException extends RuntimeException {
    public PaymentProcessingException(String message) {
        super(message);
    }
}

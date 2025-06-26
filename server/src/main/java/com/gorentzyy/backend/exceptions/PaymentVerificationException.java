package com.gorentzyy.backend.exceptions;

public class PaymentVerificationException extends RuntimeException{
    public PaymentVerificationException(String message) {
        super(message);
    }
}

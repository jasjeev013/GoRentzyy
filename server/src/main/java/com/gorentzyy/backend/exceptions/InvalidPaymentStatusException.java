package com.gorentzyy.backend.exceptions;

public class InvalidPaymentStatusException extends RuntimeException{
    public InvalidPaymentStatusException(String message) {
        super(message);
    }
}

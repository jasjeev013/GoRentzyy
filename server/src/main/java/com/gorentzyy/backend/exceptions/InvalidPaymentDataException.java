package com.gorentzyy.backend.exceptions;

public class InvalidPaymentDataException extends RuntimeException{
    public InvalidPaymentDataException(String message) {
        super(message);
    }
}

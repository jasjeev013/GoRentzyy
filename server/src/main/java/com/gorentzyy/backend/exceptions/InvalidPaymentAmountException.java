package com.gorentzyy.backend.exceptions;

public class InvalidPaymentAmountException extends RuntimeException{
    public InvalidPaymentAmountException(String message) {
        super(message);
    }
}

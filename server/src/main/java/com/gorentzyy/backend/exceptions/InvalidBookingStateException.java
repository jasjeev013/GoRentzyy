package com.gorentzyy.backend.exceptions;

public class InvalidBookingStateException extends RuntimeException{

    public InvalidBookingStateException(String message) {
        super(message);
    }
}

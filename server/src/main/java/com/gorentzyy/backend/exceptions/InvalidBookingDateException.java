package com.gorentzyy.backend.exceptions;

public class InvalidBookingDateException extends RuntimeException{

    public InvalidBookingDateException(String message) {
        super(message);
    }
}

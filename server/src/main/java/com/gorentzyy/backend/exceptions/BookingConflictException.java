package com.gorentzyy.backend.exceptions;

public class BookingConflictException extends RuntimeException{
    public BookingConflictException(String message) {
        super(message);
    }
}

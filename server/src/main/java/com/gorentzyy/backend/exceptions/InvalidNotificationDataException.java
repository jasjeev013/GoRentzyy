package com.gorentzyy.backend.exceptions;

public class InvalidNotificationDataException extends RuntimeException {
    public InvalidNotificationDataException(String message) {
        super(message);
    }
}

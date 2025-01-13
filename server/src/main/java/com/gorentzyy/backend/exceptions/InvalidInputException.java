package com.gorentzyy.backend.exceptions;

// Custom Exception for invalid user input
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}

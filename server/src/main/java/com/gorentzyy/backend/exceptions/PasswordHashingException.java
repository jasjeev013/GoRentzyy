package com.gorentzyy.backend.exceptions;

// Custom Exception for password hashing failure
public class PasswordHashingException extends RuntimeException {
    public PasswordHashingException(String message) {
        super(message);
    }
}

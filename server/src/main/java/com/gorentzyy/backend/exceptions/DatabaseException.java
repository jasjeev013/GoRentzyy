package com.gorentzyy.backend.exceptions;

// Custom Exception for database-related errors
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
}

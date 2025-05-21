package com.gorentzyy.backend.exceptions;

public class InvalidFileTypeException extends RuntimeException{
    public InvalidFileTypeException(String message) {
        super(message);
    }
}

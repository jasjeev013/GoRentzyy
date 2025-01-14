package com.gorentzyy.backend.exceptions;

public class InvalidCarDataException extends RuntimeException{

    public InvalidCarDataException(String message) {
        super(message);
    }
}

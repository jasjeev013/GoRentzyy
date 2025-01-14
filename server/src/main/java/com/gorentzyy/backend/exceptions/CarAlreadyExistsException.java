package com.gorentzyy.backend.exceptions;

public class CarAlreadyExistsException extends RuntimeException{
    public CarAlreadyExistsException(String message) {
        super(message);
    }
}

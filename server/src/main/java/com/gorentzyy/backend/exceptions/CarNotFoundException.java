package com.gorentzyy.backend.exceptions;

public class CarNotFoundException extends RuntimeException{
    public CarNotFoundException(String message) {
        super(message);
    }
}

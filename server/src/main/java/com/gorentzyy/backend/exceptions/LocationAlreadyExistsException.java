package com.gorentzyy.backend.exceptions;

public class LocationAlreadyExistsException extends RuntimeException{

    public LocationAlreadyExistsException(String message) {
        super(message);
    }
}

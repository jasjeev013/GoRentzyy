package com.gorentzyy.backend.exceptions;

public class InvalidReviewException extends RuntimeException{
    public InvalidReviewException(String message) {
        super(message);
    }
}

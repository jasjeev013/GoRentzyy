package com.gorentzyy.backend.exceptions;

public class DuplicateReviewException extends RuntimeException{
    public DuplicateReviewException(String message) {
        super(message);
    }
}

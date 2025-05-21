package com.gorentzyy.backend.exceptions;

public class FileSizeExceededException extends RuntimeException{
    public FileSizeExceededException(String message) {
        super(message);
    }
}

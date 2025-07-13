package com.gorentzyy.backend.exceptions;

public class EmailSendingException extends RuntimeException{

    public EmailSendingException(String message) {
        super(message);
    }
}

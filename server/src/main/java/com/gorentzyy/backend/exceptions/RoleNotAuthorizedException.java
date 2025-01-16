package com.gorentzyy.backend.exceptions;

public class RoleNotAuthorizedException extends RuntimeException {
    public RoleNotAuthorizedException(String message) {
        super(message);
    }
}
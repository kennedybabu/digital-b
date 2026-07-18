package com.example.customerservice.commons.exception;

public class PreconditionRequiredException extends RuntimeException {
    public PreconditionRequiredException(String message) {
        super(message);
    }
}

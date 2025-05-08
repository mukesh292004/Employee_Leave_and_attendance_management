package com.example.exception;

public class UnauthorizedAccessException extends AuthException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

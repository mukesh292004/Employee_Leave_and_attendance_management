package com.example.exception;

public class TokenValidationException extends AuthException {
    public TokenValidationException(String message) {
        super(message);
    }
}

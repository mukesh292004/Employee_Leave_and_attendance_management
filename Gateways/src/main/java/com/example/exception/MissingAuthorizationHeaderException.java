package com.example.exception;

public class MissingAuthorizationHeaderException extends AuthException {
    public MissingAuthorizationHeaderException(String message) {
        super(message);
    }
}

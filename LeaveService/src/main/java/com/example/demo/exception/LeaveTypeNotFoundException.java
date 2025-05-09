package com.example.demo.exception;

public class LeaveTypeNotFoundException extends RuntimeException {
    public LeaveTypeNotFoundException(String message) {
        super(message);
    }
}

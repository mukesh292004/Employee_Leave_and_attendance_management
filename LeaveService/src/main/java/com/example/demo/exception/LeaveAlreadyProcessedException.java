package com.example.demo.exception;

public class LeaveAlreadyProcessedException extends RuntimeException {
    public LeaveAlreadyProcessedException(String message) {
        super(message);
    }
}

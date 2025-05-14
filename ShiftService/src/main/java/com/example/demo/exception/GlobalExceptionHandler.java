package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> createResponse(HttpStatus status, String error, String message, String path) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        response.put("path", path);
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = createResponse(HttpStatus.BAD_REQUEST, "Validation Error", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), "/shifts");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SwapRequestException.class)
    public ResponseEntity<Map<String, Object>> handleSwapRequestException(SwapRequestException ex) {
        Map<String, Object> response = createResponse(HttpStatus.CONFLICT, "Swap Request Error", ex.getMessage(), "/shifts/requestSwap");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidShiftDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidShiftDataException(InvalidShiftDataException ex) {
        Map<String, Object> response = createResponse(HttpStatus.BAD_REQUEST, "Invalid Shift Data", ex.getMessage(), "/shifts/save");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShiftNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleShiftNotFoundException(ShiftNotFoundException ex) {
        Map<String, Object> response = createResponse(HttpStatus.NOT_FOUND, "Shift Not Found", ex.getMessage(), "/shifts/findById");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> response = createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), "/shifts");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

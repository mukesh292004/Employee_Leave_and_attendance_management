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

    private static final String ATTENDANCE_PATH = "/attendance";
    

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
        Map<String, Object> response = createResponse(HttpStatus.BAD_REQUEST, "Validation Error", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), ATTENDANCE_PATH);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
        Map<String, Object> response = createResponse(HttpStatus.BAD_REQUEST, "Validation Error", ex.getMessage(), ATTENDANCE_PATH);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClockInException.class)
    public ResponseEntity<Map<String, Object>> handleClockInException(ClockInException ex) {
        Map<String, Object> response = createResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), ATTENDANCE_PATH + "/clockin");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClockOutException.class)
    public ResponseEntity<Map<String, Object>> handleClockOutException(ClockOutException ex) {
        Map<String, Object> response = createResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), ATTENDANCE_PATH + "/clockout");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<Map<String, Object>> handleReportGenerationException(ReportGenerationException ex) {
        Map<String, Object> response = createResponse(HttpStatus.NOT_FOUND, "Attendance Record Not Found", ex.getMessage(), ATTENDANCE_PATH + "/getmonthlyreport");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AttendanceException.class)
    public ResponseEntity<Map<String, Object>> handleAttendanceException(AttendanceException ex) {
        Map<String, Object> response = createResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), ATTENDANCE_PATH);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> response = createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), ATTENDANCE_PATH);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

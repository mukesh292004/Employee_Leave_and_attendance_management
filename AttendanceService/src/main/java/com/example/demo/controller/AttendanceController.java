package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Attendance;
import com.example.demo.service.AttendanceService;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService service;

    // URL: http://localhost:8081/attendance/clockin/{id}
    @PostMapping("/clockin/{id}")
    public Attendance clockIn(@PathVariable("id") int employeeId) {
        return service.clockIn(employeeId);
    }

    // URL: http://localhost:8081/attendance/clockout/{id}
    @PostMapping("/clockout/{id}")
    public Attendance clockOut(@PathVariable("id") int employeeId) {
        return service.clockOut(employeeId);
    }

    // URL: http://localhost:8081/attendance/history/{id}
    @GetMapping("/history/{id}")
    public List<Attendance> getAttendanceHistory(@PathVariable("id") int employeeId) {
        return service.getAttendanceHistory(employeeId);
    }
}

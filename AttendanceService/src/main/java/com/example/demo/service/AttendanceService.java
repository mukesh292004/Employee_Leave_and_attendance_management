package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Attendance;
import com.example.demo.repository.AttendanceRepository;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository repo;

    public Attendance clockIn(int employeeId) {
        LocalDate today = LocalDate.now();
        repo.findByEmployeeIdAndDate(employeeId, today).ifPresent(a -> {
            throw new RuntimeException("Already clocked in");
        });

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setDate(today);
        attendance.setClockIn(LocalDateTime.now());

        return repo.save(attendance);
    }

    public Attendance clockOut(int employeeId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = repo.findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new RuntimeException("No clock-in record found for today"));

        attendance.setClockOut(LocalDateTime.now());
        attendance.setWorkHours(calculateWorkHours(attendance.getClockIn(), attendance.getClockOut()));

        return repo.save(attendance);
    }

    public List<Attendance> getAttendanceHistory(int employeeId) {
        return repo.findAllByEmployeeId(employeeId);
    }

    private Long calculateWorkHours(LocalDateTime clockIn, LocalDateTime clockOut) {
        Duration duration = Duration.between(clockIn, clockOut);
        return duration.toHours();
    }
}

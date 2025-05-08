package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Attendance;
import java.time.LocalDate;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    Optional<Attendance> findByEmployeeIdAndDate(int employeeId, LocalDate date);
    List<Attendance> findAllByEmployeeId(int employeeId);
}

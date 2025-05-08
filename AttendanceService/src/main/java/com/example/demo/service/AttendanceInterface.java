package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Attendance;
import com.example.demo.model.MonthlyReport;

public interface AttendanceInterface {

	Attendance clockIn(int employeeId);

	Attendance clockOut(int employeeId);

	List<Attendance> getAttendanceHistory(int employeeId);

	Optional<MonthlyReport> getMonthlyReport(int employeeId, int month);
}

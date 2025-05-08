package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Attendance;
import com.example.demo.model.MonthlyReport;
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

	// URL: http://localhost:8081/attendance/getmonthlyreport/{id}/{month}
	@GetMapping("/getmonthlyreport/{id}/{month}")
	public ResponseEntity<Optional<MonthlyReport>> getMonthlyReport(@PathVariable("id") int employeeId,
			@PathVariable("month") int month) {
		Optional<MonthlyReport> report = service.getMonthlyReport(employeeId, month);
		return ResponseEntity.ok(report);
	}
}

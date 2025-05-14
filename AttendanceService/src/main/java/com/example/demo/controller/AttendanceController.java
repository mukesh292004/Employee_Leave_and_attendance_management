package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Attendance;
import com.example.demo.model.MonthlyReport;
import com.example.demo.service.AttendanceService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/attendance")
@Validated
@AllArgsConstructor
public class AttendanceController {

	private AttendanceService service;
    
	@PostMapping("/clockin/{id}")
	public Attendance clockIn(@PathVariable("id") int employeeId) {
		return service.clockIn(employeeId);
	}

	@PostMapping("/clockout/{id}")
	public Attendance clockOut(@PathVariable("id") int employeeId) {
		return service.clockOut(employeeId);
	}

	@GetMapping("/history/{id}")
	public List<Attendance> getAttendanceHistory(@PathVariable("id") int employeeId) {
		return service.getAttendanceHistory(employeeId);
	}

	@GetMapping("/getmonthlyreport/{id}/{month}")
	public ResponseEntity<Optional<MonthlyReport>> getMonthlyReport(@PathVariable("id") int employeeId,
			@PathVariable("month") int month) {
		Optional<MonthlyReport> report = service.getMonthlyReport(employeeId, month);
		return ResponseEntity.ok(report);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteAttendanceByEmployee(@PathVariable("id") int employeeId) {
		service.deleteAttendancesByEmployee(employeeId);
		return ResponseEntity.noContent().build();
	}

}

package com.example.demo.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.exception.ClockInException;
import com.example.demo.exception.ClockOutException;
import com.example.demo.exception.ReportGenerationException;
import com.example.demo.feignclient.LeaveClient;
import com.example.demo.model.Attendance;
import com.example.demo.model.MonthlyReport;
import com.example.demo.repository.AttendanceRepository;

@Service
public class AttendanceService implements AttendanceInterface {

	private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);

	@Autowired
	AttendanceRepository repo;

	@Autowired
	private LeaveClient leaveClient;

	public Attendance clockIn(int employeeId) {
		LocalDate today = LocalDate.now();
		repo.findByEmployeeIdAndDate(employeeId, today).ifPresent(a -> {
			logger.error("Clock-in attempt failed: Employee has already clocked in for today.");
			throw new ClockInException("Employee has already clocked in for today.");
		});

		Attendance attendance = new Attendance();
		attendance.setEmployeeId(employeeId);
		attendance.setDate(today);
		attendance.setClockIn(LocalDateTime.now());

		logger.info("Employee {} clocked in at {}", employeeId, attendance.getClockIn());
		return repo.save(attendance);
	}

	public Attendance clockOut(int employeeId) {
		LocalDate today = LocalDate.now();
		Attendance attendance = repo.findByEmployeeIdAndDate(employeeId, today).orElseThrow(() -> {
			logger.error("Clock-out attempt failed: No clock-in record found for today.");
			return new ClockOutException("No clock-in record found for today.");
		});

		if (attendance.getClockOut() != null) {
			throw new ClockOutException("Employee has already clocked out for today.");
		}

		attendance.setClockOut(LocalDateTime.now());
		attendance.setWorkHours(calculateWorkHours(attendance.getClockIn(), attendance.getClockOut()));

		logger.info("Employee {} clocked out at {}. Work hours: {}", employeeId, attendance.getClockOut(),
				attendance.getWorkHours());
		return repo.save(attendance);
	}

	public List<Attendance> getAttendanceHistory(int employeeId) {
		logger.info("Fetching attendance history for employee {}", employeeId);
		return repo.findAllByEmployeeId(employeeId);
	}

	private Long calculateWorkHours(LocalDateTime clockIn, LocalDateTime clockOut) {
		Duration duration = Duration.between(clockIn, clockOut);
		return duration.toHours();
	}

	public Optional<MonthlyReport> getMonthlyReport(int employeeId, int month) {
		try {
			int approvedLeaveCount = leaveClient.getApprovedLeaveCountForMonth(employeeId, month);
			List<Attendance> attendanceRecords = repo.findByEmployeeIdAndMonth(employeeId, month);

			if (attendanceRecords.isEmpty()) {
				logger.warn("No attendance records found for employee {} in month {}", employeeId, month);
				return Optional.empty();
			}

			int totalDaysInMonth = attendanceRecords.size();
			int presentDays = totalDaysInMonth - approvedLeaveCount;
			double averageWorkingHours = attendanceRecords.stream()
					.filter(attendance -> attendance.getWorkHours() != null).mapToDouble(Attendance::getWorkHours)
					.average().orElse(0);
			double minWorkingHours = attendanceRecords.stream().filter(attendance -> attendance.getWorkHours() != null)
					.mapToDouble(Attendance::getWorkHours).min().orElse(0);
			double maxWorkingHours = attendanceRecords.stream().filter(attendance -> attendance.getWorkHours() != null)
					.mapToDouble(Attendance::getWorkHours).max().orElse(0);

			MonthlyReport report = new MonthlyReport();
			report.setEmployeeId(employeeId);
			report.setPresentDays(presentDays);
			report.setAbsentDays(approvedLeaveCount);
			report.setAverageWorkingHours(averageWorkingHours);
			report.setMinWorkingHours(minWorkingHours);
			report.setMaxWorkingHours(maxWorkingHours);

			logger.info("Generated monthly report for employee {}: {}", employeeId, report);
			return Optional.of(report);
		} catch (Exception e) {
			logger.error("Error generating monthly report for employee {}: {}", employeeId, e.getMessage());
			throw new ReportGenerationException("An error occurred while generating the monthly report.");
		}
	}

	public void deleteAttendancesByEmployee(int employeeId) {
		repo.deleteById(employeeId);
	}

}

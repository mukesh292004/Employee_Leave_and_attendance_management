package com.example.demo.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AttendanceService implements AttendanceInterface {

	private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);

	private AttendanceRepository repo;

	private LeaveClient leaveClient;

	// Clocks in an employee for the current day using the current date and time.
	// Validates whether the employee has already clocked in for today
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

	// Clocks out an employee for the current day using the current date and time.
	// Validates whether the employee has clocked in for today and whether they have
	// already clocked out.
	public Attendance clockOut(int employeeId) {
		LocalDate today = LocalDate.now();
		Attendance attendance = repo.findByEmployeeIdAndDate(employeeId, today).orElseThrow(() -> {
			logger.error("Clock-out attempt failed: No clock-in record found for today.");
			return new ClockOutException("No clock-in record found for today.");
		});
		// Check already clocked..
		if (attendance.getClockOut() != null) {
			throw new ClockOutException("Employee has already clocked out for today.");
		}
		attendance.setClockOut(LocalDateTime.now());
		attendance.setWorkHours(calculateWorkHours(attendance.getClockIn(), attendance.getClockOut()));

		logger.info("Employee {} clocked out at {}. Work hours: {}", employeeId, attendance.getClockOut(),
				attendance.getWorkHours());
		return repo.save(attendance);
	}

	// Fetches the attendance history for a specific employee
	public List<Attendance> getAttendanceHistory(int employeeId) {
	    logger.info("Fetching attendance history for employee {}", employeeId);
	    List<Attendance> attendanceRecords = repo.findAllByEmployeeId(employeeId);
	    
	    if (attendanceRecords.isEmpty()) {
	        logger.warn("No attendance records found for employee {}.", employeeId);
	        throw new ReportGenerationException("No attendance records found for employee " + employeeId);
	    }
	    
	    return attendanceRecords;
	}


	// Calculates the number of work hours between clock-in and clock-out times.
	private Long calculateWorkHours(LocalDateTime clockIn, LocalDateTime clockOut) {
		Duration duration = Duration.between(clockIn, clockOut);
		return duration.toHours();
	}

	// Generates a monthly attendance report for a specific employee.
	public Optional<MonthlyReport> getMonthlyReport(int employeeId, int month) {
		try {
			// Using leave client to fetch the number of absent days.
			int approvedLeaveCount = leaveClient.getApprovedLeaveCountForMonth(employeeId, month);
			List<Attendance> attendanceRecords = repo.findByEmployeeIdAndMonth(employeeId, month);

			// Check if attendance records are empty and throw an exception if true.
			if (attendanceRecords.isEmpty()) {
				logger.warn("No attendance records found for employee {} in month {}", employeeId, month);
				throw new ReportGenerationException(
						"No attendance records found for employee " + employeeId + " in month " + month);
			}

			int totalDaysInMonth = attendanceRecords.size();
			// calculate the absent days...
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
		} catch (ReportGenerationException e) {
			logger.error("Error generating monthly report for employee {}: {}", employeeId, e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Unexpected error generating monthly report for employee {}: {}", employeeId, e.getMessage());
			throw new ReportGenerationException("An unexpected error occurred while generating the monthly report.");
		}
	}

	
	// Delete attendance records when Employee is Deleted...
	public void deleteAttendancesByEmployee(int employeeId) {
		repo.deleteByEmployeeId(employeeId);
	}

}

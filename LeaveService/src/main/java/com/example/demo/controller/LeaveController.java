package com.example.demo.controller;

import com.example.demo.model.LeaveRequest;
import com.example.demo.model.LeaveBalance;
import com.example.demo.service.LeaveService;
import lombok.RequiredArgsConstructor;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

	private final LeaveService leaveService;

	@PostMapping("/apply")
	public ResponseEntity<String> applyLeave(@RequestBody LeaveRequest request) {
		String response = leaveService.applyLeave(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/approve/{requestId}")
	public ResponseEntity<String> approveLeave(@PathVariable int requestId) {
		String response = leaveService.approveLeave(requestId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/balance/{employeeId}")
	public ResponseEntity<List<LeaveBalance>> getLeaveBalance(@PathVariable int employeeId) {
		List<LeaveBalance> leaveBalances = leaveService.getLeaveBalance(employeeId);
		return ResponseEntity.ok(leaveBalances);
	}

	@PostMapping("/reject/{requestId}")
	public ResponseEntity<String> rejectLeave(@PathVariable int requestId) {
		String response = leaveService.rejectLeave(requestId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/history")
	public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
		List<LeaveRequest> requests = leaveService.getAllLeaveRequests();
		return ResponseEntity.ok(requests);
	}

	@GetMapping("/history/{status}")
	public ResponseEntity<List<LeaveRequest>> getLeaveHistoryByStatus(@PathVariable String status) {
		List<LeaveRequest> leaveBalances = leaveService.getLeaveHistoryByStatus(status);
		return ResponseEntity.ok(leaveBalances);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteLeaveRequest(@PathVariable int id) {
		String result = leaveService.deleteLeaveRequest(id);
		return ResponseEntity.ok(result);
	}
	@PostMapping("/initialize/{employeeId}")
	public ResponseEntity<String> initializeLeaveBalance(@PathVariable int employeeId) {
	    try {
	        leaveService.initializeLeaveBalanceForEmployee(employeeId);
	        return ResponseEntity.ok("Leave balance initialized successfully.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error initializing leave balance.");
	    }
	}

	



}

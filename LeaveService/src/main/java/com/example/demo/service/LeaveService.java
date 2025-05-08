package com.example.demo.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.LeaveBalance;
import com.example.demo.model.LeaveRequest;
import com.example.demo.repository.LeaveBalanceRepository;
import com.example.demo.repository.LeaveRequestRepository;
import com.example.demo.util.LeaveTypes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveService {

	private final LeaveBalanceRepository leaveBalanceRepo;
	private final LeaveRequestRepository leaveRequestRepo;

	// Called when employee is created
	public void initializeLeaveBalanceForEmployee(int employeeId) {
		LeaveTypes.leaves().forEach((type, count) -> {
			LeaveBalance balance = new LeaveBalance();
			balance.setEmployeeId(employeeId);
			balance.setLeaveType(type);
			balance.setBalance(count);
			leaveBalanceRepo.save(balance);
		});
	}

	public String applyLeave(LeaveRequest request) {
		Optional<LeaveBalance> optionalBalance = leaveBalanceRepo
				.findByEmployeeIdAndLeaveType((long) request.getEmployeeId(), request.getLeaveType());

		if (optionalBalance.isEmpty()) {
			return "Leave type not found.";
		}

		LeaveBalance balance = optionalBalance.get();
		int daysRequested = (int) (request.getEndDate().toInstant().toEpochMilli()
				- request.getStartDate().toInstant().toEpochMilli()) / (1000 * 60 * 60 * 24) + 1;

		if (balance.getBalance() < daysRequested) {
			return "Insufficient leave balance!";
		}

		request.setStatus("Pending");
		leaveRequestRepo.save(request);

		return "Leave request submitted.";
	}

	public List<LeaveBalance> getLeaveBalance(int employeeId) {
		return leaveBalanceRepo.findByEmployeeId((long) employeeId);
	}

	// Simulate approval
	public String approveLeave(int requestId) {
		Optional<LeaveRequest> requestOpt = leaveRequestRepo.findById(requestId);
		if (requestOpt.isEmpty()) {
			return "Leave request not found.";
		}

		LeaveRequest request = requestOpt.get();
		if (!request.getStatus().equals("Pending")) {
			return "Request already processed.";
		}

		Optional<LeaveBalance> balanceOpt = leaveBalanceRepo
				.findByEmployeeIdAndLeaveType((long) request.getEmployeeId(), request.getLeaveType());
		if (balanceOpt.isEmpty()) {
			return "Balance not found.";
		}

		LeaveBalance balance = balanceOpt.get();
		int days = (int) (request.getEndDate().toInstant().toEpochMilli()
				- request.getStartDate().toInstant().toEpochMilli()) / (1000 * 60 * 60 * 24) + 1;

		if (balance.getBalance() < days) {
			return "Insufficient balance.";
		}

		balance.setBalance(balance.getBalance() - days);
		request.setStatus("Approved");

		leaveBalanceRepo.save(balance);
		leaveRequestRepo.save(request);

		return "Leave approved.";
	}

	// rejection
	public String rejectLeave(int requestId) {
		Optional<LeaveRequest> requestOpt = leaveRequestRepo.findById(requestId);
		if (requestOpt.isEmpty()) {
			return "Leave request not found.";
		}

		LeaveRequest request = requestOpt.get();
		if (!request.getStatus().equals("Pending")) {
			return "Request already processed.";
		}

		request.setStatus("Rejected");
		leaveRequestRepo.save(request);

		return "Leave rejected.";
	}

	// get history
	public List<LeaveRequest> getAllLeaveRequests() {
		return leaveRequestRepo.findAll();
	}

	public List<LeaveRequest> getLeaveHistoryByStatus(String status) {
		return leaveRequestRepo.findByStatus(status);
	}

	public String deleteLeaveRequest(int id) {
		Optional<LeaveRequest> leaveBalanceOpt = leaveRequestRepo.findById(id);
		if (leaveBalanceOpt.isPresent()) {
			LeaveRequest leaveBalance = leaveBalanceOpt.get();
			if ("Pending".equalsIgnoreCase(leaveBalance.getStatus())) {
				leaveBalanceRepo.deleteById(id);
				return "Leave request deleted successfully";
			} else {
				return "Leave request has already been processed";
			}
		} else {
			return "Leave request not found";
		}
	}
	public int getApprovedLeaveCountForMonth(int employeeId, int month) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.MONTH, month - 1); 
	    calendar.set(Calendar.DAY_OF_MONTH, 1);
	    Date startDate = calendar.getTime();

	    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
	    Date endDate = calendar.getTime();

	    List<LeaveRequest> approvedLeaves = leaveRequestRepo.findByEmployeeIdAndStatusAndStartDateBetween(employeeId, "Approved", startDate, endDate);
	    return approvedLeaves.size();
	}

	

	


}

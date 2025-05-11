package com.example.demo.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.exception.*;
import com.example.demo.model.LeaveBalance;
import com.example.demo.model.LeaveRequest;
import com.example.demo.repository.LeaveBalanceRepository;
import com.example.demo.repository.LeaveRequestRepository;
import com.example.demo.util.LeaveTypes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveService implements LeaveBalanceInterface, LeaveRequestInterface {

    private final LeaveBalanceRepository leaveBalanceRepo;
    private final LeaveRequestRepository leaveRequestRepo;

    @Override
    public void initializeLeaveBalanceForEmployee(int employeeId) {
        try {
            LeaveTypes.leaves().forEach((type, count) -> {
                LeaveBalance balance = new LeaveBalance();
                balance.setEmployeeId(employeeId);
                balance.setLeaveType(type);
                balance.setBalance(count);
                leaveBalanceRepo.save(balance);
            });
        } catch (Exception e) {
            throw new LeaveInitializationException("Failed to initialize leave balance for employee.");
        }
    }

    @Override
    public String applyLeave(LeaveRequest request) {
        LeaveBalance balance = leaveBalanceRepo
                .findByEmployeeIdAndLeaveType((long) request.getEmployeeId(), request.getLeaveType())
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave type not found for employee."));

        int daysRequested = (int) (request.getEndDate().toInstant().toEpochMilli()
                - request.getStartDate().toInstant().toEpochMilli()) / (1000 * 60 * 60 * 24) + 1;

        if (balance.getBalance() < daysRequested) {
            throw new InsufficientLeaveBalanceException("Insufficient leave balance!");
        }

        request.setStatus("Pending");
        leaveRequestRepo.save(request);

        return "Leave request submitted.";
    }

    @Override
    public List<LeaveBalance> getLeaveBalance(int employeeId) {
        return leaveBalanceRepo.findByEmployeeId((long) employeeId);
    }

    @Override
    public String approveLeave(int requestId) {
        LeaveRequest request = leaveRequestRepo.findById(requestId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave request not found."));

        if (!request.getStatus().equals("Pending")) {
            throw new LeaveAlreadyProcessedException("Request already processed.");
        }

        LeaveBalance balance = leaveBalanceRepo
                .findByEmployeeIdAndLeaveType((long) request.getEmployeeId(), request.getLeaveType())
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave balance not found."));

        int days = (int) (request.getEndDate().toInstant().toEpochMilli()
                - request.getStartDate().toInstant().toEpochMilli()) / (1000 * 60 * 60 * 24) + 1;

        if (balance.getBalance() < days) {
            throw new InsufficientLeaveBalanceException("Insufficient balance.");
        }

        balance.setBalance(balance.getBalance() - days);
        request.setStatus("Approved");

        leaveBalanceRepo.save(balance);
        leaveRequestRepo.save(request);

        return "Leave approved.";
    }

    @Override
    public String rejectLeave(int requestId) {
        LeaveRequest request = leaveRequestRepo.findById(requestId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave request not found."));

        if (!request.getStatus().equals("Pending")) {
            throw new LeaveAlreadyProcessedException("Request already processed.");
        }

        request.setStatus("Rejected");
        leaveRequestRepo.save(request);

        return "Leave rejected.";
    }

    @Override
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepo.findAll();
    }

    @Override
    public List<LeaveRequest> getLeaveHistoryByStatus(String status) {
        return leaveRequestRepo.findByStatus(status);
    }

    @Override
    public String deleteLeaveRequest(int id) {
        LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException("Leave request not found."));
        if (!"Pending".equalsIgnoreCase(leaveRequest.getStatus())) {
            throw new LeaveAlreadyProcessedException("Leave request has already been processed.");
        }
        leaveRequestRepo.deleteById(id);
        return "Leave request deleted successfully";
    }

    @Override
    public int getApprovedLeaveCountForMonth(int employeeId, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = calendar.getTime();
        List<LeaveRequest> approvedLeaves = leaveRequestRepo.findByEmployeeIdAndStatusAndStartDateBetween(
        employeeId, "Approved", startDate, endDate);
        return approvedLeaves.size();
    }
    @Override
    public void deleteByEmployeeId(int employeeId) {
        leaveRequestRepo.deleteById(employeeId);
        leaveBalanceRepo.deleteById(employeeId);
    }

	
}

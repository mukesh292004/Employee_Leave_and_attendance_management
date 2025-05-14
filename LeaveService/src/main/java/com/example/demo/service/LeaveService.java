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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveService implements LeaveBalanceInterface, LeaveRequestInterface {

    private final LeaveBalanceRepository leaveBalanceRepo;
    private final LeaveRequestRepository leaveRequestRepo;
    
    // By Default it initializes when the employee is created.
    @Override
    public void initializeLeaveBalanceForEmployee(int employeeId) {
        try {
            LeaveTypes.leaves().forEach((type, count) -> {
                LeaveBalance balance = new LeaveBalance();
                balance.setEmployeeId(employeeId);
                balance.setLeaveType(type);
                balance.setBalance(count);
                leaveBalanceRepo.save(balance);
                log.info("Initialized leave balance for employee {} with leave type {} and balance {}", employeeId, type, count);
            });
        } catch (Exception e) {
            log.error("Failed to initialize leave balance for employee {}", employeeId, e);
            throw new LeaveInitializationException("Failed to initialize leave balance for employee.");
        }
    }

    // Method to apply for leave
    @Override
    public String applyLeave(LeaveRequest request) {
        LeaveBalance balance = leaveBalanceRepo
                .findByEmployeeIdAndLeaveType(request.getEmployeeId(), request.getLeaveType())
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave type not found for employee."));

        int daysRequested = (int) (request.getEndDate().toInstant().toEpochMilli()
                - request.getStartDate().toInstant().toEpochMilli()) / (1000 * 60 * 60 * 24) + 1;

        if (balance.getBalance() < daysRequested) {
            log.warn("Insufficient leave balance for employee {} for leave type {}", request.getEmployeeId(), request.getLeaveType());
            throw new InsufficientLeaveBalanceException("Insufficient leave balance!");
        }

        request.setStatus("Pending");
        leaveRequestRepo.save(request);
        log.info("Leave request submitted for employee {} for leave type {}", request.getEmployeeId(), request.getLeaveType());

        return "Leave request submitted.";
    }

    // Method to get leave balance for an employee
    @Override
    public List<LeaveBalance> getLeaveBalance(int employeeId) {
        log.info("Fetching leave balance for employee {}", employeeId);
        return leaveBalanceRepo.findByEmployeeId(employeeId);
    }

    // Method to approve leave request
    @Override
    public String approveLeave(int requestId) {
        LeaveRequest request = leaveRequestRepo.findById(requestId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave request not found."));

        if (!request.getStatus().equals("Pending")) {
            log.warn("Leave request {} already processed", requestId);
            throw new LeaveAlreadyProcessedException("Request already processed.");
        }

        LeaveBalance balance = leaveBalanceRepo
                .findByEmployeeIdAndLeaveType(request.getEmployeeId(), request.getLeaveType())
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave balance not found."));

        int days = (int) (request.getEndDate().toInstant().toEpochMilli()
                - request.getStartDate().toInstant().toEpochMilli()) / (1000 * 60 * 60 * 24) + 1;

        if (balance.getBalance() < days) {
            log.warn("Insufficient balance for employee {} for leave type {}", request.getEmployeeId(), request.getLeaveType());
            throw new InsufficientLeaveBalanceException("Insufficient balance.");
        }

        balance.setBalance(balance.getBalance() - days);
        request.setStatus("Approved");

        leaveBalanceRepo.save(balance);
        leaveRequestRepo.save(request);
        log.info("Leave approved for request {} for employee {}", requestId, request.getEmployeeId());

        return "Leave approved.";
    }

    // Method to reject leave request
    @Override
    public String rejectLeave(int requestId) {
        LeaveRequest request = leaveRequestRepo.findById(requestId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave request not found."));

        if (!request.getStatus().equals("Pending")) {
            log.warn("Leave request {} already processed", requestId);
            throw new LeaveAlreadyProcessedException("Request already processed.");
        }

        request.setStatus("Rejected");
        leaveRequestRepo.save(request);
        log.info("Leave rejected for request {} for employee {}", requestId, request.getEmployeeId());

        return "Leave rejected.";
    }

    // Method to get all leave requests
    @Override
    public List<LeaveRequest> getAllLeaveRequests() {
        log.info("Fetching all leave requests");
        return leaveRequestRepo.findAll();
    }

    // Method to get leave history by status
    @Override
    public List<LeaveRequest> getLeaveHistoryByStatus(String status) {
        log.info("Fetching leave requests with status {}", status);
        return leaveRequestRepo.findByStatus(status);
    }

    // Method to delete leave request
    @Override
    public String deleteLeaveRequest(int id) {
        LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException("Leave request not found."));
        if (!"Pending".equalsIgnoreCase(leaveRequest.getStatus())) {
            log.warn("Leave request {} has already been processed", id);
            throw new LeaveAlreadyProcessedException("Leave request has already been processed.");
        }
        leaveRequestRepo.deleteById(id);
        log.info("Leave request {} deleted successfully", id);
        return "Leave request deleted successfully";
    }

    // Method to get approved leave count for a month
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
        log.info("Fetching approved leave count for employee {} for month {}", employeeId, month);
        return approvedLeaves.size();
    }

    // Method to delete leave records by employee ID
    @Override
    public void deleteByEmployeeId(int employeeId) {
        leaveRequestRepo.deleteByEmployeeId(employeeId);
        leaveBalanceRepo.deleteByEmployeeId(employeeId);
        log.info("Deleted leave records for employee {}", employeeId);
    }

    // Method to reset all leave balances end of year
    @Override
    public void resetAllLeaveBalances() {
        List<LeaveBalance> allBalances = leaveBalanceRepo.findAll();
        allBalances.forEach(balance -> {
            Integer originalBalance = LeaveTypes.leaves().get(balance.getLeaveType());
            if (originalBalance != null) {
                balance.setBalance(originalBalance);
                leaveBalanceRepo.save(balance);
                log.info("Reset leave balance for employee {} for leave type {}", balance.getEmployeeId(), balance.getLeaveType());
            }
        });
    }
}

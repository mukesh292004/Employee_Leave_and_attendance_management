package com.example.demo.service;

import com.example.demo.model.LeaveRequest;

import java.util.Date;
import java.util.List;

public interface LeaveRequestInterface {
    String applyLeave(LeaveRequest request);
    String approveLeave(int requestId);
    String rejectLeave(int requestId);
    List<LeaveRequest> getAllLeaveRequests();
    List<LeaveRequest> getLeaveHistoryByStatus(String status);
    int getApprovedLeaveCountForMonth(int employeeId, int month);
    void deleteByEmployeeId(int id);
}

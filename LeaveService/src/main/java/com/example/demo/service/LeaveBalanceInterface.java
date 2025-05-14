package com.example.demo.service;

import com.example.demo.model.LeaveBalance;

import java.util.List;

public interface LeaveBalanceInterface {
    List<LeaveBalance> getLeaveBalance(int employeeId);
    void initializeLeaveBalanceForEmployee(int employeeId);
    String deleteLeaveRequest(int id);
    void deleteByEmployeeId(int id);
	void resetAllLeaveBalances();
}

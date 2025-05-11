package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.LeaveBalance;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Integer> {
	List<LeaveBalance> findByEmployeeId(long employeeId);
	Optional<LeaveBalance> findByEmployeeIdAndLeaveType(long employeeId, String leaveType);
	

}

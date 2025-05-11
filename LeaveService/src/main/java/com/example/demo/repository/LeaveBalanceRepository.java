package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.LeaveBalance;

import jakarta.transaction.Transactional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Integer> {
	List<LeaveBalance> findByEmployeeId(long employeeId);

	Optional<LeaveBalance> findByEmployeeIdAndLeaveType(long employeeId, String leaveType);

	@Transactional
	@Modifying
	@Query("DELETE FROM LeaveBalance lb WHERE lb.employeeId = :employeeId")
	void deleteByEmployeeId(int employeeId);

}

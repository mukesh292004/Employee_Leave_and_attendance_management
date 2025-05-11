package com.example.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.LeaveRequest;

import jakarta.transaction.Transactional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
	List<LeaveRequest> findByEmployeeId(int employeeId);

	List<LeaveRequest> findByStatus(String status);

	List<LeaveRequest> findByEmployeeIdAndStatusAndStartDateBetween(int employeeId, String status, Date startDate,
			Date endDate);

	@Transactional
	@Modifying
	@Query("DELETE FROM LeaveRequest lr WHERE lr.employeeId = :employeeId")
	void deleteByEmployeeId(int employeeId);

}

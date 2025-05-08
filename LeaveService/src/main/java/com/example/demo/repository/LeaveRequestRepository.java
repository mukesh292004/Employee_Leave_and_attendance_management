package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.LeaveBalance;
import com.example.demo.model.LeaveRequest;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
	List<LeaveRequest> findByEmployeeId(Long employeeId);
	List<LeaveRequest> findByStatus(String status);
}

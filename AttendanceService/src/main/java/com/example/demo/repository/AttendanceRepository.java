package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Attendance;

import jakarta.transaction.Transactional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
	Optional<Attendance> findByEmployeeIdAndDate(int employeeId, LocalDate date);

	List<Attendance> findAllByEmployeeId(int employeeId);

	@Query("SELECT a FROM Attendance a WHERE a.employeeId = :employeeId AND MONTH(a.date) = :month")
	List<Attendance> findByEmployeeIdAndMonth(@Param("employeeId") int employeeId, @Param("month") int month);

	@Transactional
	@Modifying
	@Query("DELETE FROM Attendance a WHERE a.employeeId = :employeeId")
	void deleteByEmployeeId(int employeeId);

}

package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Shift;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {

	List<Shift> findBySwapRequestedTrue();


}

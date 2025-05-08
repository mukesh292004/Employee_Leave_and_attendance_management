package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Shift;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
}

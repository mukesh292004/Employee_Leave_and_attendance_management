package com.example.demo.service;

import java.util.List;
import com.example.demo.model.Shift;

public interface ShiftInterface {
	List<Shift> findAll();

	Shift findById(int id);

	void save(Shift shift);

	String deleteById(int id);

	String requestSwap(int id);

	String approveSwap(int id1, int id2);
	
	String rejectSwap(int id);
}

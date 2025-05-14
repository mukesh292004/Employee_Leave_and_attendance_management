package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Shift;
import com.example.demo.service.ShiftInterface;

@RestController
@RequestMapping("/shifts")
public class ShiftController {

	@Autowired
	private ShiftInterface shiftService;

	@GetMapping("/findall")
	public List<Shift> getAllShifts() {
		return shiftService.findAll();
	}

	@GetMapping("/findById/{id}")
	public Shift getShiftById(@PathVariable int id) {
		return shiftService.findById(id);
	}

	@PostMapping("/save")
	public String createShift(@RequestBody Shift shift) {
		shiftService.save(shift);
		return "Shift saved Successfully";
	}

	@DeleteMapping("/delete/{id}")
	public String deleteShift(@PathVariable int id) {
		return shiftService.deleteById(id);
	}

	@PostMapping("/requestSwap/{id}")
	public String requestSwap(@PathVariable int id) {
		return shiftService.requestSwap(id);
	}

	@PostMapping("/approveSwap/{id1}/{id2}")
	public String approveSwap(@PathVariable int id1, @PathVariable int id2) {
		return shiftService.approveSwap(id1, id2);
	}

	@PostMapping("/rejectSwap/{id}")
	public String rejectSwap(@PathVariable int id) {
		return shiftService.rejectSwap(id);
	}
}
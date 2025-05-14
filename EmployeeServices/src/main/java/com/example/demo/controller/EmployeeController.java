package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/employees")
@AllArgsConstructor
public class EmployeeController {

	private EmployeeService employeeService;

	//Employee Creation
	@PostMapping
	public Employee createEmployee(@RequestBody Employee employee) {
		return employeeService.saveEmployee(employee);
	}

	// getAllEmployees
	@GetMapping
	public List<Employee> getAllEmployees() {
		return employeeService.getAllEmployees();
	}

	// get employee by ID
	@GetMapping("/{id}")
	public Optional<Employee> getEmployeeById(@PathVariable Integer id) {
		return employeeService.getEmployeeById(id);
	}

	//getEmployee by Email
	@GetMapping("/email/{email}")
	public Optional<Employee> getEmployeeByEmail(@PathVariable String email) {
		return employeeService.getEmployeeByEmail(email);
	}

	// update the Employee details
	@PutMapping("/{id}")
	public Employee updateEmployee(@PathVariable Integer id, @RequestBody Employee employeeDetails) {
		return employeeService.updateEmployee(id, employeeDetails);
	}

	// Delete employee By ID
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteEmployee(@PathVariable Integer id) {
	    employeeService.deleteEmployee(id);
	    return ResponseEntity.ok("Employee ID " + id + " successfully deleted.");
	}


   // check wheather the Employee exist or not ...
	@GetMapping("/check/{id}/{name}/{email}/{role}")
	public boolean doesEmployeeExist(@PathVariable Integer id, @PathVariable String name, @PathVariable String email, @PathVariable String role) {
	    Optional<Employee> existingEmployee = employeeService.getEmployeeById(id);
	    if (existingEmployee.isPresent()) {
	        Employee fetchedEmployee = existingEmployee.get();
	        return fetchedEmployee.getName().equals(name) &&
	               fetchedEmployee.getEmail().equals(email) &&
	               fetchedEmployee.getRole().equals(role);
	    }
	    return false;
	}


}

package com.example.demo.service;

import com.example.demo.model.Employee;

import java.util.List;
import java.util.Optional;
public interface EmployeeInterface {

	    Employee saveEmployee(Employee employee);

	    List<Employee> getAllEmployees();

	    Optional<Employee> getEmployeeById(Integer id);

	    Optional<Employee> getEmployeeByEmail(String email);

	    void deleteEmployee(Integer id);

	    Employee updateEmployee(Integer id, Employee employeeDetails);

	    boolean doesEmployeeExist(Integer id);
	}


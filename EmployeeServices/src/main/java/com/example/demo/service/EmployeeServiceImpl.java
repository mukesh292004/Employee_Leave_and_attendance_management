package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.feignclient.LeaveClient;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private LeaveClient leaveServiceClient;

	public Employee saveEmployee(Employee employee) {
	    Employee savedEmployee = employeeRepository.save(employee);
	    leaveServiceClient.initializeLeaveBalance(savedEmployee.getId());
	    return savedEmployee;
	}


	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}

	public Optional<Employee> getEmployeeById(Integer id) {
		return employeeRepository.findById(id);
	}

	public Optional<Employee> getEmployeeByEmail(String email) {
		return employeeRepository.findByEmail(email);
	}

	public void deleteEmployee(Integer id) {
		employeeRepository.deleteById(id);
	}

	public Employee updateEmployee(Integer id, Employee employeeDetails) {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Employee not found with id " + id));

		employee.setName(employeeDetails.getName());
		employee.setEmail(employeeDetails.getEmail());
		employee.setPassword(employeeDetails.getPassword());
		employee.setRole(employeeDetails.getRole());
		employee.setDepartment(employeeDetails.getDepartment());
		employee.setContact(employeeDetails.getContact());

		return employeeRepository.save(employee);
	}
	

      public boolean doesEmployeeExist(Integer id) {
    	  return employeeRepository.findById(id).isPresent();
    	  }

}

package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.feignclient.AttendanceClient;
import com.example.demo.feignclient.LeaveClient;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmployeeService implements EmployeeInterface {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

	private EmployeeRepository employeeRepository;

	private LeaveClient leaveServiceClient;

	private AttendanceClient attendanceClient;

	// save employee and initialize the leaveBalance using leaveServiceClient
	@Override
	public Employee saveEmployee(Employee employee) {
		logger.info("Saving employee: {}", employee);

		// Check for duplicate email
		if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
			logger.warn("Conflict: An employee with email {} already exists.", employee.getEmail());
			throw new ConflictException("An employee with this email already exists.");
		}

		// Additional custom validation (optional)
		if (employee.getContact() != null && !employee.getContact().matches("\\d{10}")) {
			logger.warn("Validation failed: Contact number {} is not a 10-digit number.", employee.getContact());
			throw new ValidationException("Contact number must be a 10-digit number.");
		}

		Employee savedEmployee = employeeRepository.save(employee);
		// Initializing leave balance using leave client
		leaveServiceClient.initializeLeaveBalance(savedEmployee.getId());
		logger.info("Employee saved successfully: {}", savedEmployee);
		return savedEmployee;
	}

	// Fetching all employees
	@Override
	public List<Employee> getAllEmployees() {
		logger.info("Fetching all employees");
		return employeeRepository.findAll();
	}

	// Fetch employee by ID
	@Override
	public Optional<Employee> getEmployeeById(Integer id) {
	    logger.info("Fetching employee by id: {}", id);
	    Optional<Employee> employee = employeeRepository.findById(id);
	    if (!employee.isPresent()) {
	        throw new ResourceNotFoundException("Employee not found with id " + id);
	    }
	    return employee;
	}


	// Fetch employee by Email
	@Override
	public Optional<Employee> getEmployeeByEmail(String email) {
		logger.info("Fetching employee by email: {}", email);
		Optional<Employee> employee = employeeRepository.findByEmail(email);
		if (!employee.isPresent()) {
	        throw new ResourceNotFoundException("Employee not found with Email " + email);
	    }
	    return employee;
	}

	// Update Employee By ID
	@Override
	public Employee updateEmployee(Integer id, Employee employeeDetails) {
		logger.info("Updating employee with id: {}", id);
		// Check employee Exist or not
		Employee employee = employeeRepository.findById(id).orElseThrow(() -> {
			logger.error("Resource not found: Employee with id {} not found.", id);
			return new ResourceNotFoundException("Employee not found with id " + id);
		});
		// Check for conflict and validate.
		if (!employee.getEmail().equals(employeeDetails.getEmail())
				&& employeeRepository.findByEmail(employeeDetails.getEmail()).isPresent()) {
			logger.warn("Conflict: Another employee with email {} already exists.", employeeDetails.getEmail());
			throw new ConflictException("Another employee with this email already exists.");
		}
		employee.setName(employeeDetails.getName());
		employee.setEmail(employeeDetails.getEmail());
		employee.setRole(employeeDetails.getRole());
		employee.setDepartment(employeeDetails.getDepartment());
		employee.setContact(employeeDetails.getContact());

		Employee updatedEmployee = employeeRepository.save(employee);
		logger.info("Employee updated successfully: {}", updatedEmployee);
		return updatedEmployee;
	}

	// check wheather employee exixt or not
	// used in AUTH Registration..
	@Override
	public boolean doesEmployeeExist(Integer id) {
		logger.info("Checking if employee exists with id: {}", id);
		return employeeRepository.findById(id).isPresent();
	}

	// delete employee by id
	@Override
	public void deleteEmployee(Integer id) {
		logger.info("Deleting employee with id: {}", id);
		// check wheather employee exist or not
		if (!employeeRepository.existsById(id)) {
			logger.error("Resource not found: Employee with id {} not found.", id);
			throw new ResourceNotFoundException("Employee not found with id " + id);
		}

		// Deleting an employee and corresponding data in leave and attendance services
		leaveServiceClient.deleteLeavesByEmployee(id);
		attendanceClient.deleteAttendancesByEmployee(id);
		employeeRepository.deleteById(id);
		logger.info("Employee deleted successfully with id: {}", id);
	}
}

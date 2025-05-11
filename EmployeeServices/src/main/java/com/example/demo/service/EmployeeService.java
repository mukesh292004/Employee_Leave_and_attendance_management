package com.example.demo.service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.feignclient.AttendanceClient;
import com.example.demo.feignclient.LeaveClient;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService implements EmployeeInterface {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveClient leaveServiceClient;
    @Autowired
    private AttendanceClient attendanceClient;

    @Override
    public Employee saveEmployee(Employee employee) {
        // Check for duplicate email
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            throw new ConflictException("An employee with this email already exists.");
        }

        // Additional custom validation (optional)
        if (employee.getContact() != null && !employee.getContact().matches("\\d{10}")) {
            throw new ValidationException("Contact number must be a 10-digit number.");
        }

        Employee savedEmployee = employeeRepository.save(employee);
        leaveServiceClient.initializeLeaveBalance(savedEmployee.getId());
        return savedEmployee;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Integer id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

   

    @Override
    public Employee updateEmployee(Integer id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        if (!employee.getEmail().equals(employeeDetails.getEmail()) &&
                employeeRepository.findByEmail(employeeDetails.getEmail()).isPresent()) {
            throw new ConflictException("Another employee with this email already exists.");
        }

        employee.setName(employeeDetails.getName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setRole(employeeDetails.getRole());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setContact(employeeDetails.getContact());

        return employeeRepository.save(employee);
    }

    @Override
    public boolean doesEmployeeExist(Integer id) {
        return employeeRepository.findById(id).isPresent();
    }
    @Override
    public void deleteEmployee(Integer id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id " + id);
        }
        leaveServiceClient.deleteLeavesByEmployee(id);          
        attendanceClient.deleteAttendancesByEmployee(id); 
        employeeRepository.deleteById(id);
    }
    
}

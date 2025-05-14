package com.example.demo;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.feignclient.AttendanceClient;
import com.example.demo.feignclient.LeaveClient;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveClient leaveClient;

    @Mock
    private AttendanceClient attendanceClient;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee(1, "John Doe", "john.doe@example.com", "Developer", "IT", "1234567890");
    }

    @Test
    void testSaveEmployee() {
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee savedEmployee = employeeService.saveEmployee(employee);

        assertNotNull(savedEmployee);
        assertEquals(employee.getName(), savedEmployee.getName());
        assertEquals(employee.getEmail(), savedEmployee.getEmail());
        verify(leaveClient, times(1)).initializeLeaveBalance(employee.getId());
    }

    @Test
    void testSaveEmployee_Conflict() {
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));

        ConflictException exception = assertThrows(ConflictException.class, () -> employeeService.saveEmployee(employee));

        assertEquals("An employee with this email already exists.", exception.getMessage());
    }

    @Test
    void testUpdateEmployee() {
        Employee updatedEmployee = new Employee(1, "John Doe", "john.doe@example.com", "Senior Developer", "IT", "0987654321");
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        Employee result = employeeService.updateEmployee(1, updatedEmployee);

        assertNotNull(result);
        assertEquals("Senior Developer", result.getRole());
        assertEquals("0987654321", result.getContact());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee_EmployeeNotFound() {
        Employee updatedEmployee = new Employee(1, "John Doe", "john.doe@example.com", "Senior Developer", "IT", "0987654321");
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(1, updatedEmployee));

        assertEquals("Employee not found with id 1", exception.getMessage());
    }

    @Test
    void testDeleteEmployee() {
        when(employeeRepository.existsById(1)).thenReturn(true);
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1);

        verify(leaveClient, times(1)).deleteLeavesByEmployee(1);
        verify(attendanceClient, times(1)).deleteAttendancesByEmployee(1);
        verify(employeeRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteEmployee_EmployeeNotFound() {
        when(employeeRepository.existsById(1)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(1));

        assertEquals("Employee not found with id 1", exception.getMessage());
    }

    @Test
    void testDoesEmployeeExist() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

        boolean exists = employeeService.doesEmployeeExist(1);

        assertTrue(exists);
    }

    @Test
    void testDoesEmployeeExist_EmployeeNotFound() {
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        boolean exists = employeeService.doesEmployeeExist(1);

        assertFalse(exists);
    }
}

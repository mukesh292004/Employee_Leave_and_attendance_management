package com.example.demo;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.feignclient.AttendanceClient;
import com.example.demo.feignclient.LeaveClient;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

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

    private static Employee testEmployee;

    AutoCloseable closeable;

    @BeforeAll
    static void beforeAllTests() {
        System.out.println("This runs once before all tests");
        testEmployee = new Employee(1, "Mukesh", "mukesh@example.com", "Employee", "IT", "1234567890");
    }

    @BeforeEach
    void beforeEachTest() {
        closeable = MockitoAnnotations.openMocks(this);
        System.out.println("This runs before each test");
    }

    @AfterEach
    void afterEachTest() throws Exception {
        closeable.close();
        System.out.println("This runs after each test");
    }

    @AfterAll
    static void afterAllTests() {
        System.out.println("This runs once after all tests");
    }

    @Test
    void testGetEmployeeByIdReturnsEmployee() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));

        Optional<Employee> result = employeeService.getEmployeeById(1);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getName());
        verify(employeeRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteEmployeeThrowsExceptionIfNotFound() {
        when(employeeRepository.existsById(99)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.deleteEmployee(99));

        assertEquals("Employee not found with id 99", exception.getMessage());
        verify(employeeRepository, times(1)).existsById(99);
    }
}
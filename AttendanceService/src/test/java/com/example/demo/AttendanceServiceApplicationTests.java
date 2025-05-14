package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.exception.ClockInException;
import com.example.demo.feignclient.LeaveClient;
import com.example.demo.model.Attendance;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.service.AttendanceService;

class AttendanceServiceApplicationTests {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private LeaveClient leaveClient;

    @InjectMocks
    private AttendanceService attendanceService;

    AutoCloseable closeable;

    @BeforeAll
    static void setupAll() {
        System.out.println("Starting AttendanceService tests");
    }

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }

    @Test
    void testClockInSuccess() {
        int employeeId = 3;
        LocalDate today = LocalDate.now();

        when(attendanceRepository.findByEmployeeIdAndDate(employeeId, today))
                .thenReturn(Optional.empty());

        Attendance result = attendanceService.clockIn(employeeId);

        assertEquals(employeeId, result.getEmployeeId());
        assertNotNull(result.getClockIn());

        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    void testClockInAlreadyDone() {
        int employeeId = 2;
        LocalDate today = LocalDate.now();
        Attendance alreadyExists = new Attendance();
        alreadyExists.setEmployeeId(employeeId);
        alreadyExists.setDate(today);

        when(attendanceRepository.findByEmployeeIdAndDate(employeeId, today))
                .thenReturn(Optional.of(alreadyExists));

        assertThrows(ClockInException.class, () -> attendanceService.clockIn(employeeId));
    }

    @Test
    void testClockOutSuccess() {
        int employeeId = 2;
        LocalDate today = LocalDate.now();
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setDate(today);
        attendance.setClockIn(LocalDateTime.now().minusHours(8));

        when(attendanceRepository.findByEmployeeIdAndDate(employeeId, today))
                .thenReturn(Optional.of(attendance));

        Attendance result = attendanceService.clockOut(employeeId);

        assertNotNull(result.getClockOut());
        assertNotNull(result.getWorkHours());

        verify(attendanceRepository, times(1)).save(attendance);
    }

   

}
package com.example.demo;

import com.example.demo.exception.ClockInException;
import com.example.demo.exception.ClockOutException;
import com.example.demo.feignclient.LeaveClient;
import com.example.demo.model.Attendance;
import com.example.demo.model.MonthlyReport;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.service.AttendanceService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AttendanceServiceApplicationTests {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private LeaveClient leaveClient;

    @InjectMocks
    private AttendanceService attendanceService;

    private Attendance attendance;

    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Starting test cases...");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        attendance = new Attendance(1, 1, LocalDate.now(), LocalDateTime.now(), null, null);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test completed.");
    }

    @Test
    void testClockIn() {
        when(attendanceRepository.findByEmployeeIdAndDate(anyInt(), any())).thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        Attendance result = attendanceService.clockIn(1);
        assertNotNull(result);
        assertEquals(1, result.getEmployeeId());
        assertNotNull(result.getClockIn());
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    void testClockInAlreadyClockedIn() {
        when(attendanceRepository.findByEmployeeIdAndDate(anyInt(), any())).thenReturn(Optional.of(attendance));

        assertThrows(ClockInException.class, () -> attendanceService.clockIn(1));
        verify(attendanceRepository, times(0)).save(any(Attendance.class));
    }

    @Test
    void testClockOut() {
        attendance.setClockIn(LocalDateTime.now().minusHours(8));
        when(attendanceRepository.findByEmployeeIdAndDate(anyInt(), any())).thenReturn(Optional.of(attendance));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        Attendance result = attendanceService.clockOut(1);
        assertNotNull(result);
        assertNotNull(result.getClockOut());
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    void testClockOutNoClockIn() {
        when(attendanceRepository.findByEmployeeIdAndDate(anyInt(), any())).thenReturn(Optional.empty());

        assertThrows(ClockOutException.class, () -> attendanceService.clockOut(1));
        verify(attendanceRepository, times(0)).save(any(Attendance.class));
    }

    @Test
    void testClockOutAlreadyClockedOut() {
        attendance.setClockOut(LocalDateTime.now());
        when(attendanceRepository.findByEmployeeIdAndDate(anyInt(), any())).thenReturn(Optional.of(attendance));

        assertThrows(ClockOutException.class, () -> attendanceService.clockOut(1));
        verify(attendanceRepository, times(0)).save(any(Attendance.class));
    }

    @Test
    void testGetAttendanceHistory() {
        List<Attendance> attendanceList = Collections.singletonList(attendance);
        when(attendanceRepository.findAllByEmployeeId(anyInt())).thenReturn(attendanceList);

        List<Attendance> result = attendanceService.getAttendanceHistory(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(attendanceRepository, times(1)).findAllByEmployeeId(anyInt());
    }

    @Test
    void testGetAttendanceHistoryNoRecords() {
        when(attendanceRepository.findAllByEmployeeId(anyInt())).thenReturn(Collections.emptyList());

        assertThrows(ClockInException.class, () -> attendanceService.getAttendanceHistory(1));
        verify(attendanceRepository, times(1)).findAllByEmployeeId(anyInt());
    }

    @Test
    void testGetMonthlyReport() {
        MonthlyReport report = new MonthlyReport();
        report.setEmployeeId(1);
        when(leaveClient.getApprovedLeaveCountForMonth(anyInt(), anyInt())).thenReturn(5);
        when(attendanceRepository.findByEmployeeIdAndMonth(anyInt(), anyInt())).thenReturn(Collections.singletonList(attendance));

        Optional<MonthlyReport> result = attendanceService.getMonthlyReport(1, 1);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getEmployeeId());
    }

    @Test
    void testDeleteAttendanceByEmployee() {
        doNothing().when(attendanceRepository).deleteByEmployeeId(anyInt());
        
        attendanceService.deleteAttendancesByEmployee(1);
        verify(attendanceRepository, times(1)).deleteByEmployeeId(anyInt());
    }
}

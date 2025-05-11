package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.exception.InsufficientLeaveBalanceException;
import com.example.demo.model.LeaveBalance;
import com.example.demo.model.LeaveRequest;
import com.example.demo.repository.LeaveBalanceRepository;
import com.example.demo.repository.LeaveRequestRepository;
import com.example.demo.service.LeaveService;

class LeaveServiceATests {

    @InjectMocks
    private LeaveService leaveService;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepo;

    @Mock
    private LeaveRequestRepository leaveRequestRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApplyLeave_Success() {
        LeaveRequest request = new LeaveRequest(0, 1, "casual",
                new Date(), new Date(), null);

        LeaveBalance balance = new LeaveBalance(1, 1, "casual", 10);
        when(leaveBalanceRepo.findByEmployeeIdAndLeaveType(1L, "casual"))
                .thenReturn(Optional.of(balance));

        String result = leaveService.applyLeave(request);

        assertEquals("Leave request submitted.", result);
        verify(leaveRequestRepo, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    void testApplyLeave_InsufficientBalance() {
        LeaveRequest request = new LeaveRequest(0, 1, "casual",
                new Date(), new Date(), null);

        LeaveBalance balance = new LeaveBalance(1, 1, "casual", 0);
        when(leaveBalanceRepo.findByEmployeeIdAndLeaveType(1L, "casual"))
                .thenReturn(Optional.of(balance));

        assertThrows(InsufficientLeaveBalanceException.class,
                () -> leaveService.applyLeave(request));
    }

    @Test
    void testApproveLeave_Success() {
        LeaveRequest request = new LeaveRequest(1, 1, "sick",
                new Date(), new Date(), "Pending");

        LeaveBalance balance = new LeaveBalance(1, 1, "sick", 10);

        when(leaveRequestRepo.findById(1)).thenReturn(Optional.of(request));
        when(leaveBalanceRepo.findByEmployeeIdAndLeaveType(1L, "sick"))
                .thenReturn(Optional.of(balance));

        String result = leaveService.approveLeave(1);

        assertEquals("Leave approved.", result);
        verify(leaveRequestRepo).save(any(LeaveRequest.class));
        verify(leaveBalanceRepo).save(any(LeaveBalance.class));
    }

    @Test
    void testRejectLeave_Success() {
        LeaveRequest request = new LeaveRequest(1, 1, "casual",
                new Date(), new Date(), "Pending");

        when(leaveRequestRepo.findById(1)).thenReturn(Optional.of(request));

        String result = leaveService.rejectLeave(1);
        assertEquals("Leave rejected.", result);
        assertEquals("Rejected", request.getStatus());
    }

    @Test
    void testGetAllLeaveRequests() {
        when(leaveRequestRepo.findAll()).thenReturn(List.of(new LeaveRequest(), new LeaveRequest()));
        List<LeaveRequest> result = leaveService.getAllLeaveRequests();
        assertEquals(2, result.size());
    }
}
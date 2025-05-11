package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.exception.ShiftNotFoundException;
import com.example.demo.model.Shift;
import com.example.demo.repository.ShiftRepository;
import com.example.demo.service.ShiftService;

public class ShiftServiceApplicationTests {

    @InjectMocks
    private ShiftService shiftService;

    @Mock
    private ShiftRepository shiftRepository;

    private Shift sampleShift;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleShift = new Shift();
    }

    @Test
    void testFindById_ShiftExists() {
        when(shiftRepository.findById(1)).thenReturn(Optional.of(sampleShift));

        Shift result = shiftService.findById(1);
        assertNotNull(result);
        assertEquals("John", result.getName());
    }

    @Test
    void testFindById_ShiftNotFound() {
        when(shiftRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ShiftNotFoundException.class, () -> shiftService.findById(1));
    }

    @Test
    void testSave_ValidShift() {
        shiftService.save(sampleShift);
        verify(shiftRepository, times(1)).save(sampleShift);
    }

    @Test
    void testDeleteById_Success() {
        doNothing().when(shiftRepository).deleteById(1);

        String response = shiftService.deleteById(1);
        assertEquals("Shift Deleted", response);
    }

    @Test
    void testRequestSwap_Success() {
        sampleShift.setSwapRequested(false);
        when(shiftRepository.findById(1)).thenReturn(Optional.of(sampleShift));

        String result = shiftService.requestSwap(1);
        assertTrue(sampleShift.isSwapRequested());
        assertEquals("Swap request submitted", result);
    }

    @Test
    void testApproveSwap_Success() {
        Shift shift2 = new Shift();
        sampleShift.setSwapRequested(true);

        when(shiftRepository.findById(1)).thenReturn(Optional.of(sampleShift));
        when(shiftRepository.findById(2)).thenReturn(Optional.of(shift2));

        String result = shiftService.approveSwap(1, 2);
        assertEquals("Swap approved and shifts updated", result);
    }

    @Test
    void testRejectSwap_Success() {
        sampleShift.setSwapRequested(true);
        when(shiftRepository.findById(1)).thenReturn(Optional.of(sampleShift));

        String result = shiftService.rejectSwap(1);
        assertEquals("Swap request rejected for shift ID 1", result);
        assertFalse(sampleShift.isSwapRequested());
    }
}
package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.InvalidShiftDataException;
import com.example.demo.exception.ShiftNotFoundException;
import com.example.demo.exception.SwapRequestException;
import com.example.demo.model.Shift;
import com.example.demo.repository.ShiftRepository;

@Service
public class ShiftService implements ShiftInterface {

    @Autowired
    ShiftRepository repository;

    @Override
    public List<Shift> findAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving shifts", e);
        }
    }

    @Override
    public Shift findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ShiftNotFoundException("Shift not found with ID: " + id));
    }

    @Override
    public void save(Shift shift) {
        try {
            // Validate shift data
            if (shift.getEmployeeId() <= 0 || shift.getName() == null || shift.getShiftDate() == null || shift.getShiftTime() == null) {
                throw new InvalidShiftDataException("Invalid shift data provided");
            }

            // Ensure that the shift date is not in the past
            if (shift.getShiftDate().isBefore(LocalDate.now())) {
                throw new InvalidShiftDataException("Shift date cannot be in the past");
            }

            // Validate shift time format (HH:mm:ss)
            if (!isValidTimeFormat(shift.getShiftTime())) {
                throw new InvalidShiftDataException("Invalid shift time format. Please use HH:mm:ss");
            }

            repository.save(shift);
        } catch (Exception e) {
            throw new RuntimeException("Error saving shift", e);
        }
    }

    @Override
    public String deleteById(int id) {
        try {
            Optional<Shift> shift = repository.findById(id);
            if (shift.isPresent()) {
                repository.deleteById(id);
                return "Shift Deleted";
            } else {
                throw new ShiftNotFoundException("Shift not found with ID: " + id);
            }
        } catch (Exception e) {
            throw new ShiftNotFoundException("Shift not found with ID: " + id);
        }
    }

    @Override
    public String requestSwap(int id) {
        Optional<Shift> optionalShift = repository.findById(id);
        if (optionalShift.isPresent()) {
            Shift shift = optionalShift.get();
            if (shift.isSwapRequested()) {
                throw new SwapRequestException("Swap request has already been submitted for this shift");
            }
            shift.setSwapRequested(true);
            repository.save(shift);
            return "Swap request submitted";
        } else {
            throw new ShiftNotFoundException("Shift not found with ID: " + id);
        }
    }

    @Override
    public String approveSwap(int id1, int id2) {
        Optional<Shift> optionalShift1 = repository.findById(id1);
        Optional<Shift> optionalShift2 = repository.findById(id2);

        if (optionalShift1.isPresent() && optionalShift2.isPresent()) {
            Shift shift1 = optionalShift1.get();
            Shift shift2 = optionalShift2.get();

            // Ensure both shifts have requested a swap
            if (!shift1.isSwapRequested() || !shift2.isSwapRequested()) {
                throw new SwapRequestException("Both shifts must request a swap");
            }

            // Ensure the shifts are not the same (avoid swapping the same shift)
            if (shift1.getId() == shift2.getId()) {
                throw new SwapRequestException("Cannot swap the same shift");
            }

            // Swap employee IDs
            int tempEmployeeId = shift1.getEmployeeId();
            shift1.setEmployeeId(shift2.getEmployeeId());
            shift2.setEmployeeId(tempEmployeeId);

            // Reset swapRequested flag
            shift1.setSwapRequested(false);
            shift2.setSwapRequested(false);

            // Save updated shifts
            repository.save(shift1);
            repository.save(shift2);

            return "Swap approved and shifts updated";
        } else {
            throw new ShiftNotFoundException("One or both shifts not found");
        }
    }

    @Override
    public String rejectSwap(int id) {
        Optional<Shift> optionalShift = repository.findById(id);
        if (optionalShift.isPresent()) {
            Shift shift = optionalShift.get();
            if (shift.isSwapRequested()) {
                shift.setSwapRequested(false);
                repository.save(shift);
                return "Swap request rejected for shift ID " + id;
            } else {
                throw new SwapRequestException("No swap request found for shift ID " + id);
            }
        } else {
            throw new ShiftNotFoundException("Shift not found with ID: " + id);
        }
    }

    // Helper method to validate shift time format (HH:mm:ss)
    private boolean isValidTimeFormat(String time) {
        try {
            String[] parts = time.split(":");
            if (parts.length == 3) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                int second = Integer.parseInt(parts[2]);

                // Validate each part of the time
                if (hour >= 0 && hour < 24 && minute >= 0 && minute < 60 && second >= 0 && second < 60) {
                    return true;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

package com.example.demo.service;

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
        return repository.findById(id).orElseThrow(() -> new ShiftNotFoundException("Shift not found with ID: " + id));
    }

    @Override
    public void save(Shift shift) {
        try {
            if (shift.getId() <= 0 || shift.getEmployeeId() <= 0 || shift.getName() == null || shift.getShiftDate() == null || shift.getShiftTime() == null) {
                throw new InvalidShiftDataException("Invalid shift data provided");
            }
            repository.save(shift);
        } catch (Exception e) {
            throw new RuntimeException("Error saving shift", e);
        }
    }

    @Override
    public String deleteById(int id) {
        try {
            repository.deleteById(id);
            return "Shift Deleted";
        } catch (Exception e) {
            throw new ShiftNotFoundException("Shift not found with ID: " + id);
        }
    }

    @Override
    public String requestSwap(int id) {
        Optional<Shift> optionalShift = repository.findById(id);
        if (optionalShift.isPresent()) {
            Shift shift = optionalShift.get();
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

            if (shift1.isSwapRequested() && shift2.isSwapRequested() && !shift1.getShiftTime().equals(shift2.getShiftTime())) {
                int tempEmployeeId = shift1.getEmployeeId();
                shift1.setEmployeeId(shift2.getEmployeeId());
                shift2.setEmployeeId(tempEmployeeId);

                shift1.setSwapRequested(false);
                shift2.setSwapRequested(false);

                repository.save(shift1);
                repository.save(shift2);

                return "Swap approved and shifts updated";
            } else {
                throw new SwapRequestException("Swap conditions not met or not requested");
            }
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
}

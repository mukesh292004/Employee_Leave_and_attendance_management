package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Shift;
import com.example.demo.repository.ShiftRepository;

@Service
public class ShiftServiceImp implements ShiftService {

	@Autowired
	ShiftRepository repository;

	@Override
	public List<Shift> findAll() {
		return repository.findAll();
	}

	@Override
	public Shift findById(int id) {
		return repository.findById(id).orElseThrow();
	}

	@Override
	public void save(Shift shift) {
		repository.save(shift);
	}

	@Override
	public String deleteById(int id) {
		repository.deleteById(id);
		return "Shift Deleted";
	}

	@Override
	public String requestSwap(int id) {
		Optional<Shift> optionalShift = repository.findById(id);
 
		if (optionalShift.isPresent()) {
			Shift shift = optionalShift.get();
 
			shift.setSwapRequested(true);
 
			repository.save(shift);
 
			return "Swap request submitted";
		}
		return "Shift not found";
	}

	@Override
	public String approveSwap(int id1, int id2) {
		Optional<Shift> optionalShift1 = repository.findById(id1);
		Optional<Shift> optionalShift2 = repository.findById(id2);

		if (optionalShift1.isPresent() && optionalShift2.isPresent()) {
			Shift shift1 = optionalShift1.get();
			Shift shift2 = optionalShift2.get();

			if (shift1.isSwapRequested() && shift2.isSwapRequested()
					&& !shift1.getShiftTime().equals(shift2.getShiftTime())) {

				int tempEmployeeId = shift1.getEmployeeId();
				shift1.setEmployeeId(shift2.getEmployeeId());
				shift2.setEmployeeId(tempEmployeeId);

				shift1.setSwapRequested(false);
				shift2.setSwapRequested(false);

				repository.save(shift1);
				repository.save(shift2);

				return "Swap approved and shifts updated";
			} else {
				return "Swap conditions not met or not requested";
			}
		}
		return "One or both shifts not found";
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
				return "No swap request found for shift ID " + id;
			}
		}
		return "Shift not Found";
	}
}

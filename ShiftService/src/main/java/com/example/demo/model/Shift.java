package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Shift {
	@Id
	private int id;
	private int employeeId;
	private String name;
	private String shiftDate;
	private String shiftTime;
	private boolean swapRequested;
}

package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Shift {

    @Id
    @Positive(message = "ID must be a positive number")
    private int id;

    @Positive(message = "Employee ID must be a positive number")
    private int employeeId;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Shift date is mandatory")
//    @FutureOrPresent(message = "Shift date must be in the present or future")
    private String shiftDate;

    @NotNull(message = "Shift time is mandatory")
    private String shiftTime;

    @Column(nullable = false)
    private boolean swapRequested = false;
}
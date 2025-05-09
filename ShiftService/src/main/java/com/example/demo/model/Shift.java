package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
    @FutureOrPresent(message = "Shift date must be in the present or future")
    private String shiftDate;

    @NotBlank(message = "Shift time is mandatory")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Shift time must be in HH:mm format")
    private String shiftTime;

    private boolean swapRequested;
}

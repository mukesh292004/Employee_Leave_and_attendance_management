package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-generate the ID
    @Positive(message = "ID must be a positive number")
    private int id;

    @Positive(message = "Employee ID must be a positive number")
    private int employeeId;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Shift date is mandatory")
    @FutureOrPresent(message = "Shift date must be in the present or future")
    private LocalDate shiftDate;

    @NotNull(message = "Shift time is mandatory")
    private LocalTime shiftTime;

    @Column(nullable = false)
    private boolean swapRequested = false;
}

package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Shift {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;

    @Positive(message = "Employee ID must be a positive number")
    private int employeeId;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Shift date is mandatory")
    @FutureOrPresent(message = "Shift date must be in the present or future")
    private LocalDate shiftDate;  // Using LocalDate for the date (yyyy-MM-dd)

    @NotNull(message = "Shift time is mandatory")
    private String shiftTime;  // Using String for time (HH:mm:ss)

    @Column(nullable = false)
    private boolean swapRequested = false;
}

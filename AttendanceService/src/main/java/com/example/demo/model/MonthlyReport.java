package com.example.demo.model;

import lombok.Data;

@Data
public class MonthlyReport {
    private int employeeId;
    private int presentDays;
    private int absentDays;
    private double averageWorkingHours;
    private double minWorkingHours;
    private double maxWorkingHours;
}

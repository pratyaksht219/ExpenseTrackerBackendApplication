package com.example.expensetracker.Analytics.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyAverageDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalExpenseAverage;
}

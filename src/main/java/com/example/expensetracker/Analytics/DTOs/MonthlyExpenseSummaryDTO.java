package com.example.expensetracker.Analytics.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyExpenseSummaryDTO {
    private int month;
    private int year;
    private double total;
}

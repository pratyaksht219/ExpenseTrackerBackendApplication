package com.example.expensetracker.Analytics.Service;

import com.example.expensetracker.Analytics.DTOs.CategoryExpenseDTO;
import com.example.expensetracker.Analytics.DTOs.DailyAverageDTO;
import com.example.expensetracker.Analytics.DTOs.MonthlyExpenseDTO;
import com.example.expensetracker.Analytics.DTOs.TopSpendingDayDTO;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {
    List<MonthlyExpenseDTO> getMonthlyExpenses (Long userId, Integer month, Integer year);
    List<DailyAverageDTO> getAverageSpendingForACustomRange (Long userId, LocalDate startDate, LocalDate endDate);
    List<CategoryExpenseDTO> getCategoryWiseExpenses (Long userId);
    List<TopSpendingDayDTO> getTopSpendingDays (Long userId);
}

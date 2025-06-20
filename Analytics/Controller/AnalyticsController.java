package com.example.expensetracker.Analytics.Controller;

import com.example.expensetracker.Analytics.DTOs.*;
import com.example.expensetracker.Analytics.Service.AnalyticsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    AnalyticsService analyticsService;

    @GetMapping("/monthly/user/{userId}/month/{month}/year/{year}")
    public ResponseEntity<List<MonthlyExpenseDTO>> getMonthlyExpense(@PathVariable Long userId,
                                                                     @Valid @Size(min=1, max=12, message = "Month should be between 1 and 12") @PathVariable Integer month,
                                                                     @Valid @PathVariable Integer year) {
        List<MonthlyExpenseDTO> monthlyExpenseDTOS = analyticsService.getMonthlyExpenses(userId, month, year);
        return new ResponseEntity<>(monthlyExpenseDTOS, HttpStatus.OK);
    }

    @GetMapping("/category-expenses/user/{userId}")
    public ResponseEntity<List<CategoryExpenseDTO>> getCategoryWiseExpenses(@PathVariable Long userId) {
        List<CategoryExpenseDTO> categoryExpenseDTOS = analyticsService.getCategoryWiseExpenses(userId);
        return new ResponseEntity<List<CategoryExpenseDTO>>(categoryExpenseDTOS, HttpStatus.OK);
    }
    @GetMapping("/top-spending/user/{userId}")
    public ResponseEntity<List<DailyAverageDTO>> getAverageSpendingForACustomRange(@PathVariable Long userId,
                                                                 @Valid @PathVariable LocalDate startDate,
                                                                 @Valid @PathVariable LocalDate endDate) {
        List<DailyAverageDTO> dailyAverageDTOS = analyticsService.getAverageSpendingForACustomRange(userId, startDate, endDate);
        return new ResponseEntity<>(dailyAverageDTOS, HttpStatus.OK);
    }

    @GetMapping("/top-spending-days/user/{userId}")
    public ResponseEntity<List<TopSpendingDayDTO>> getTopSpendingDays(@PathVariable Long userId) {
        List<TopSpendingDayDTO> topSpendingDayDTOS = analyticsService.getTopSpendingDays(userId);
        return new ResponseEntity<>(topSpendingDayDTOS, HttpStatus.OK);
    }

}

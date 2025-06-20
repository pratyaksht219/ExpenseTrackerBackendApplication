package com.example.expensetracker.Analytics.Service.Impl;

import com.example.expensetracker.Analytics.DTOs.CategoryExpenseDTO;
import com.example.expensetracker.Analytics.DTOs.DailyAverageDTO;
import com.example.expensetracker.Analytics.DTOs.MonthlyExpenseDTO;
import com.example.expensetracker.Analytics.DTOs.TopSpendingDayDTO;
import com.example.expensetracker.Analytics.Service.AnalyticsService;
import com.example.expensetracker.Repository.ExpenseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class AnalyticsServiceImpl implements AnalyticsService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<MonthlyExpenseDTO> getMonthlyExpenses(Long userId, Integer month, Integer year) {
        List<Object[]> monthlyExpenseList = expenseRepository.getMonthlyExpenseSummary(userId, month, year);
        List<MonthlyExpenseDTO> monthlyExpenseDTOList = monthlyExpenseList
                .stream()
                .map(expense -> modelMapper.map(expense, MonthlyExpenseDTO.class))
                .toList();
        return monthlyExpenseDTOList;
    }

    @Override
    public List<DailyAverageDTO> getAverageSpendingForACustomRange(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> averageSpendingList = expenseRepository
                .getAverageSpendingForACustomRange(userId, startDate, endDate);
        List<DailyAverageDTO> averageSpendingDTOList= averageSpendingList.stream().map(
                expense -> modelMapper.map(expense, DailyAverageDTO.class)
        ).toList();
        return averageSpendingDTOList;
    }

    @Override
    public List<CategoryExpenseDTO> getCategoryWiseExpenses(Long userId) {
        List<Object[]> categoryWiseExpenses = expenseRepository.getCategoryWiseExpenses(userId);
        List<CategoryExpenseDTO> categoryExpenseDTOList = categoryWiseExpenses
                .stream()
                .map(expense-> modelMapper.map(expense, CategoryExpenseDTO.class)).toList();
        return categoryExpenseDTOList;
    }

    @Override
    public List<TopSpendingDayDTO> getTopSpendingDays(Long userId) {
        List<Object[]> topSpendingDays = expenseRepository.getTopSpendingDays(userId);
        List<TopSpendingDayDTO> topSpendingDayDTOList = topSpendingDays
                .stream()
                .map(expense->modelMapper.map(expense, TopSpendingDayDTO.class))
                .toList();

        return topSpendingDayDTOList;
    }
}

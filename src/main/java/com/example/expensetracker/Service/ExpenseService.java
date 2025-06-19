package com.example.expensetracker.Service;

import com.example.expensetracker.Payload.ExpenseRequestDTO;
import com.example.expensetracker.Payload.ExpenseResponseDTO;

import java.util.List;

public interface ExpenseService {
    ExpenseResponseDTO addExpense(ExpenseRequestDTO expenseRequestDTO);

    List<ExpenseResponseDTO> getAllExpensesByUser(Long userId);

    ExpenseResponseDTO updateExpense(Long expenseId, ExpenseRequestDTO expenseRequestDTO);

    ExpenseResponseDTO deleteExpense(Long expenseId);
}

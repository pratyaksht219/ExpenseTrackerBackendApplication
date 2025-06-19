package com.example.expensetracker.Controller;

import com.example.expensetracker.Payload.ExpenseRequestDTO;
import com.example.expensetracker.Payload.ExpenseResponseDTO;
import com.example.expensetracker.Service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;
    @PostMapping("/expenses")
    public ResponseEntity<ExpenseResponseDTO> addExpense(@RequestBody ExpenseRequestDTO expenseRequestDTO) {
        ExpenseResponseDTO addedExpense = expenseService.addExpense(expenseRequestDTO);
        return new ResponseEntity<>(addedExpense, HttpStatus.CREATED);
    }

    @GetMapping("/expenses/users/{userId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpensesByUser(@PathVariable Long userId) {
        List<ExpenseResponseDTO> expenseResponseDTOS = expenseService.getAllExpensesByUser(userId);
        return new ResponseEntity<>(expenseResponseDTOS, HttpStatus.OK);
    }

    @PutMapping("/expenses/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(@PathVariable Long expenseId, @RequestBody ExpenseRequestDTO expenseRequestDTO) {
        ExpenseResponseDTO updatedExpense = expenseService.updateExpense(expenseId, expenseRequestDTO);
        return new ResponseEntity<>(updatedExpense, HttpStatus.OK);
    }

    @DeleteMapping("/expenses/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> deleteExpense(@PathVariable Long expenseId) {
        ExpenseResponseDTO deletedExpense = expenseService.deleteExpense(expenseId);
        return new ResponseEntity<>(deletedExpense, HttpStatus.OK);
    }
}

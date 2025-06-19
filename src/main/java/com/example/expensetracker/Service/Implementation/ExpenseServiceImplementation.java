package com.example.expensetracker.Service.Implementation;

import com.example.expensetracker.Entity.Category;
import com.example.expensetracker.Entity.Expense;
import com.example.expensetracker.Entity.User;
import com.example.expensetracker.Payload.ExpenseRequestDTO;
import com.example.expensetracker.Payload.ExpenseResponseDTO;
import com.example.expensetracker.Repository.CategoryRepository;
import com.example.expensetracker.Repository.ExpenseRepository;
import com.example.expensetracker.Repository.UserRepository;
import com.example.expensetracker.Service.ExpenseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseServiceImplementation implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ExpenseResponseDTO addExpense(ExpenseRequestDTO expenseRequestDTO) {

        User newUser = userRepository.findById(expenseRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Invalid user ID"));
        Category newCategory = categoryRepository.findById(expenseRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Invalid category ID"));

        Expense newExpense = new Expense();
        newExpense.setAmount(expenseRequestDTO.getAmount());
        newExpense.setTitle(expenseRequestDTO.getTitle());
        newExpense.setDate(expenseRequestDTO.getDate());
        newExpense.setUser(newUser);
        newExpense.setCategory(newCategory);

        expenseRepository.save(newExpense);
        return modelMapper.map(newExpense, ExpenseResponseDTO.class);
    }

    @Override
    public List<ExpenseResponseDTO> getAllExpensesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("Invalid user ID"));
        List<Expense> userExpenses = expenseRepository.findByUser(user);
        if(userExpenses.isEmpty()){
            throw new RuntimeException("No expenses found for the particular user");
        }

        return userExpenses.stream()
                .map(expense->modelMapper.map(expense, ExpenseResponseDTO.class))
                .toList();
    }

    @Override
    public ExpenseResponseDTO updateExpense(Long expenseId, ExpenseRequestDTO expenseRequestDTO) {
        Expense existingExpense = expenseRepository.findById(expenseId)
                .orElseThrow(()-> new RuntimeException("Expense with expense_ID:"+expenseId+" does not exist"));
        Category newCategory = categoryRepository.findById(expenseRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Invalid category ID"));
        User newUser = userRepository.findById(expenseRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Invalid user ID"));



        Expense updatedExpense = new Expense();
        updatedExpense.setAmount(expenseRequestDTO.getAmount());
        updatedExpense.setTitle(expenseRequestDTO.getTitle());
        updatedExpense.setDate(expenseRequestDTO.getDate());
        updatedExpense.setUser(newUser);
        updatedExpense.setCategory(newCategory);
        expenseRepository.delete(existingExpense);
        expenseRepository.save(updatedExpense);
        return modelMapper.map(updatedExpense, ExpenseResponseDTO.class);
    }

    @Override
    public ExpenseResponseDTO deleteExpense(Long expenseId) {
        Expense existingExpense  = expenseRepository.findById(expenseId)
                .orElseThrow(()-> new RuntimeException("Expense with expense_ID:"+expenseId+" does not exist"));
        expenseRepository.delete(existingExpense);
        return modelMapper.map(existingExpense, ExpenseResponseDTO.class);
    }
}

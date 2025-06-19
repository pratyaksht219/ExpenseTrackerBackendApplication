package com.example.expensetracker.Payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponseDTO {
    private Long expenseId;
    private String title;
    private Double amount;
    private LocalDate date; // Changed to String for easier JSON serialization
    private String categoryName; // Added for convenience
//    private Long categoryId;
//    private Long userId;
//    private String userName; // Added for convenience
}

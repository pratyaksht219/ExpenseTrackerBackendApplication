package com.example.expensetracker.Payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseRequestDTO {
    private String title;
    private Double amount;
    private LocalDate date;
    private Long categoryId;
    private Long userId;
}

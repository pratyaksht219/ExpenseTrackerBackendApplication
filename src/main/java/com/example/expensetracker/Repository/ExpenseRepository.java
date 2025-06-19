package com.example.expensetracker.Repository;

import com.example.expensetracker.Entity.Expense;
import com.example.expensetracker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);

    @Query("SELECT MONTH(e.date), YEAR(e.date), SUM(e.amount) " +
            "FROM Expense e WHERE e.user.id = :userId " +
            "GROUP BY YEAR(e.date), MONTH(e.date)")

    List<Object[]> getMonthlyExpenseSummary(@Param("userId") Long userId);
}

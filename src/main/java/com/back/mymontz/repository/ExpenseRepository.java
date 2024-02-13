package com.back.mymontz.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.back.mymontz.model.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findByUserId(Long userId);
    List<Expense> findByDateBetweenAndUserId(LocalDate startDate, LocalDate endDate, Long usuarioId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.date = :date AND e.user.id = :id")
    Double getTotalAmountByDateAndUserId(@Param("date") LocalDate date, @Param("id") Long id);
    
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.date BETWEEN :startOfWeek AND :endOfWeek AND e.user.id = :id")
    Double getTotalAmountForWeekAndUserId(@Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek, @Param("id") Long id);
    
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.date BETWEEN :startOfMonth AND :endOfMonth AND e.user.id = :id")
    Double getTotalAmountForMonthAndUserId(@Param("startOfMonth") LocalDate startOfMonth, @Param("endOfMonth") LocalDate endOfMonth, @Param("id") Long id);
}

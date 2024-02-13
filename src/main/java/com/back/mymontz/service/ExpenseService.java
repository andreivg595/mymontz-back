package com.back.mymontz.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.back.mymontz.dto.ExpenseRequest;
import com.back.mymontz.model.Expense;

@Service
public interface ExpenseService {

	Expense createExpense(ExpenseRequest expenseRequest);
	
	List<Expense> getAllExpensesByUserId(Long id);
	
	List<Expense> getAllExpensesBetweenDatesAndUserId(Long id, LocalDate startDate, LocalDate endDate);
	
	Expense updateExpense(Long id, ExpenseRequest expenseRequest);
	
	void deleteExpenseById(Long id);
	
	double getTotalAmountByDateAndUserId(LocalDate date, Long id);
	
	double getTotalAmountForWeekAndUserId(LocalDate startOfWeek, LocalDate endOfWeek, Long id);
	
	double getTotalAmountForMonthAndUserId(LocalDate startOfMonth, LocalDate endOfMonth, Long id);
}

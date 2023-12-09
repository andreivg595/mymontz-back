package com.back.mymontz.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.back.mymontz.model.Expense;

@Service
public interface ExpenseService {

	Expense createExpense(Expense expense, MultipartFile file);
	
	List<Expense> getAllExpensesByUserId(Long id);
	
	List<Expense> getAllExpensesBetweenDatesAndUserId(Long id, LocalDate startDate, LocalDate endDate);
	
	Expense updateExpense(Long id, Expense expense, MultipartFile file);
	
	void deleteExpenseById(Long id);
}

package com.back.mymontz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.back.mymontz.model.ExpenseCategory;

@Service
public interface ExpenseCategoryService {

	ExpenseCategory createExpenseCategory(ExpenseCategory expenseCategory);
	
	List<ExpenseCategory> getAllExpenseCategories();
	
	ExpenseCategory getExpenseCategoryById(Long id);
	
	ExpenseCategory updateExpenseCategory(Long id, ExpenseCategory expenseCategory);
	
	void deleteExpenseCategoryById(Long id);
}

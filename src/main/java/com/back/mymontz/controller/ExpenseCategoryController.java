package com.back.mymontz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.mymontz.model.ExpenseCategory;
import com.back.mymontz.service.ExpenseCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ExpenseCategoryController {

	@Autowired
	private final ExpenseCategoryService expenseCategoryService;
	
	@PostMapping("/expense/category/create")
	public ExpenseCategory createExpenseCategory(@RequestBody ExpenseCategory expenseCategory) {
		return expenseCategoryService.createExpenseCategory(expenseCategory);
	}
	
	@GetMapping("/expense/categories")
	public List<ExpenseCategory> listExpenseCategories() {
		return expenseCategoryService.getAllExpenseCategories();
	}
	
	@GetMapping("/expense/category/{id}")
	public ExpenseCategory getExpenseCategoryById(@PathVariable Long id) {
		return expenseCategoryService.getExpenseCategoryById(id);
	}
	
	@PutMapping("/expense/category/update/{id}")
	public ExpenseCategory updateExpenseCategory(@PathVariable Long id, @RequestBody ExpenseCategory expenseCategory) {
		return expenseCategoryService.updateExpenseCategory(id, expenseCategory);
	}
	
	@DeleteMapping("/expense/category/{id}")
	public void deleteExpenseCategory(@PathVariable Long id) {
		expenseCategoryService.deleteExpenseCategoryById(id);
	}
}

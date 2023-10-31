package com.back.mymontz.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.back.mymontz.model.Expense;
import com.back.mymontz.service.ExpenseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ExpenseController {

	@Autowired
	private final ExpenseService expenseService;
	
	@PostMapping("/expense/create")
	public Expense createExpense(@RequestBody Expense expense) {
		return expenseService.createExpense(expense);
	}
	
	@GetMapping("/expense/expenses/{id}")
	public List<Expense> listAllExpenses(@PathVariable Long id) {
		return expenseService.getAllExpensesByUserId(id);
	}
	
	@GetMapping("/expense/expenses")
	public List<Expense> listAllExpensesBetweenDates(@RequestParam  Long id, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
		return expenseService.getAllExpensesBetweenDatesAndUserId(id, startDate, endDate);
	}
	
	@PutMapping("/expense/update/{id}")
	public Expense updateExpense(@PathVariable Long id, @RequestBody Expense expense) {
		return expenseService.updateExpense(id, expense);
	}
	
	@DeleteMapping("/expense/{id}")
	public void deleteExpenseCategory(@PathVariable Long id) {
		expenseService.deleteExpenseById(id);
	}
}

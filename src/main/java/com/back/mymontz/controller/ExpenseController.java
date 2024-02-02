package com.back.mymontz.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.back.mymontz.dto.ExpenseRequest;
import com.back.mymontz.model.Expense;
import com.back.mymontz.service.ExpenseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ExpenseController {

	@Autowired
	private final ExpenseService expenseService;

	@PostMapping("/expense/create")
	public ResponseEntity<Expense> createExpense(@ModelAttribute ExpenseRequest expenseRequest) {
	    Expense createdExpense = expenseService.createExpense(expenseRequest);
        return ResponseEntity.ok(createdExpense);
	}

	@GetMapping("/expense/expenses/{id}")
	public List<Expense> listAllExpenses(@PathVariable Long id) {
		return expenseService.getAllExpensesByUserId(id);
	}

	@GetMapping("/expense/expenses")
	public List<Expense> listAllExpensesBetweenDates(@RequestParam Long id, @RequestParam LocalDate startDate,
			@RequestParam LocalDate endDate) {
		return expenseService.getAllExpensesBetweenDatesAndUserId(id, startDate, endDate);
	}

	@PutMapping("/expense/update/{id}")
	public Expense updateExpense(@PathVariable Long id, @ModelAttribute ExpenseRequest expenseRequest) {
		return expenseService.updateExpense(id, expenseRequest);
	}

	@DeleteMapping("/expense/{id}")
	public void deleteExpenseCategory(@PathVariable Long id) {
		expenseService.deleteExpenseById(id);
	}
}

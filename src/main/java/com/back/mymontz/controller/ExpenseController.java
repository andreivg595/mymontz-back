package com.back.mymontz.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.back.mymontz.dto.ExpenseRequest;
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
	
	@GetMapping("/expense/amount/{date}/{id}")
    public Double getTotalExpenseForDateAndUser(@PathVariable String date, @PathVariable Long id) {
        LocalDate specificDate = LocalDate.parse(date);
        return expenseService.getTotalAmountByDateAndUserId(specificDate, id);
    }
	
	@GetMapping("/expense/amount/week/{date}/{id}")
	public Double getTotalExpenseForWeekAndUser(@PathVariable String date, @PathVariable Long id) {
        LocalDate currentDate = LocalDate.parse(date);
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	    LocalDate endOfWeek = startOfWeek.plusDays(6);
	    return expenseService.getTotalAmountForWeekAndUserId(startOfWeek, endOfWeek, id);
	}
	
	@GetMapping("/expense/amount/month/{date}/{id}")
	public Double getTotalAmountForMonthAndUserId(@PathVariable String date, @PathVariable Long id) {
        LocalDate currentDate = LocalDate.parse(date);
	    LocalDate startOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
	    LocalDate endOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth());
	    return expenseService.getTotalAmountForMonthAndUserId(startOfMonth, endOfMonth, id);
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

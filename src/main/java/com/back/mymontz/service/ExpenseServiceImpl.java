package com.back.mymontz.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.back.mymontz.exception.ResourceNotFoundException;
import com.back.mymontz.model.Expense;
import com.back.mymontz.model.ExpenseCategory;
import com.back.mymontz.model.User;
import com.back.mymontz.repository.ExpenseCategoryRepository;
import com.back.mymontz.repository.ExpenseRepository;
import com.back.mymontz.repository.UserRepository;

@Component
public class ExpenseServiceImpl implements ExpenseService {

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private ExpenseCategoryRepository expenseCategoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Expense createExpense(Expense expense) {
		User user = userRepository.findById(expense.getUser().getId())
				.orElseThrow(() -> new ResourceNotFoundException("User not exist with id: " + expense.getUser().getId()));

		ExpenseCategory expenseCategory = expenseCategoryRepository.findById(expense.getCategory().getId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Expense category not exists with id: " + expense.getCategory().getId()));

		expense.setUser(user);
		expense.setCategory(expenseCategory);

		return expenseRepository.save(expense);
	}

	@Override
	public List<Expense> getAllExpensesByUserId(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("User not exist with id: " + id);
		}
		
		return expenseRepository.findByUserId(id);
	}

	@Override
	public List<Expense> getAllExpensesBetweenDatesAndUserId(Long id, LocalDate startDate, LocalDate endDate) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("User not exist with id: " + id);
		}
		
		return expenseRepository.findByDateBetweenAndUserId(startDate, endDate, id);
	}

	@Override
	public Expense updateExpense(Long id, Expense expense) {
		Expense existingExpense = expenseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Expense not exists with id: " + id));
		
		User user = userRepository.findById(expense.getUser().getId())
				.orElseThrow(() -> new ResourceNotFoundException("User not exist with id: " + expense.getUser().getId()));

		ExpenseCategory expenseCategory = expenseCategoryRepository.findById(expense.getCategory().getId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Expense category not exists with id: " + expense.getCategory().getId()));
		
		existingExpense.setUser(user);
		existingExpense.setCategory(expenseCategory);
		existingExpense.setDate(expense.getDate());
		existingExpense.setAmount(expense.getAmount());
		existingExpense.setNote(expense.getNote());

        return expenseRepository.save(existingExpense);
	}

	@Override
	public void deleteExpenseById(Long id) {
		if (!expenseRepository.existsById(id)) {
			throw new ResourceNotFoundException("Expense not exists with id: " + id);
		}
		expenseRepository.deleteById(id);
	}

}

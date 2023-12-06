package com.back.mymontz.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.back.mymontz.config.JwtService;
import com.back.mymontz.exception.ConstraintException;
import com.back.mymontz.exception.CustomException;
import com.back.mymontz.exception.ResourceNotFoundException;
import com.back.mymontz.exception.UnauthorizedException;
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
	
	@Autowired
	private UserServiceImpl userService;

	@Override
	public Expense createExpense(Expense expense) {
		try {
			userService.checkAuthorization(expense.getUser().getId());
			User user = userRepository.findById(expense.getUser().getId()).orElseThrow(
					() -> new ResourceNotFoundException("User not exist with id: " + expense.getUser().getId()));

			ExpenseCategory expenseCategory = expenseCategoryRepository.findById(expense.getCategory().getId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Expense category not exists with id: " + expense.getCategory().getId()));

			expense.setUser(user);
			expense.setCategory(expenseCategory);

			return expenseRepository.save(expense);
		} catch (DataIntegrityViolationException e) {
			String msg = e.getMessage().substring(0, e.getMessage().indexOf("]") + 1);
			throw new ConstraintException(msg, e);
		} catch (NullPointerException e) {
			throw new CustomException(e.getMessage(), e, HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public List<Expense> getAllExpensesByUserId(Long id) {
		userService.checkAuthorization(id);

		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("User not exist with id: " + id);
		}

		return expenseRepository.findByUserId(id);
	}

	@Override
	public List<Expense> getAllExpensesBetweenDatesAndUserId(Long id, LocalDate startDate, LocalDate endDate) {
		userService.checkAuthorization(id);
		if (startDate.isAfter(endDate) || endDate.isBefore(startDate)) {
			Throwable throwable = new Throwable();
			throw new CustomException(
					"Wrong dates between start date (" + startDate + ") and end date (" + endDate + ")", throwable,
					HttpStatus.BAD_REQUEST);
		}
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("User not exist with id: " + id);
		}

		return expenseRepository.findByDateBetweenAndUserId(startDate, endDate, id);
	}

	@Override
	public Expense updateExpense(Long id, Expense expense) {
		try {
			Expense existingExpense = expenseRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Expense not exists with id: " + id));

			userService.checkAuthorization(existingExpense.getUser().getId());
			
			User user = userRepository.findById(expense.getUser().getId()).orElseThrow(
					() -> new ResourceNotFoundException("User not exist with id: " + expense.getUser().getId()));

			ExpenseCategory expenseCategory = expenseCategoryRepository.findById(expense.getCategory().getId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Expense category not exists with id: " + expense.getCategory().getId()));

			existingExpense.setUser(user);
			existingExpense.setCategory(expenseCategory);
			existingExpense.setDate(expense.getDate());
			existingExpense.setAmount(expense.getAmount());
			existingExpense.setNote(expense.getNote());

			return expenseRepository.save(existingExpense);
		} catch (DataIntegrityViolationException e) {
			String msg = e.getMessage().substring(0, e.getMessage().indexOf("]") + 1);
			throw new ConstraintException(msg, e);
		} catch (NullPointerException e) {
			throw new CustomException(e.getMessage(), e, HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteExpenseById(Long id) {
		Expense existingExpense = expenseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Expense not exists with id: " + id));
		
		userService.checkAuthorization(existingExpense.getUser().getId());

		expenseRepository.deleteById(id);
	}
}

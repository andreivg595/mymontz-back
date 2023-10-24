package com.back.mymontz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.back.mymontz.exception.ResourceNotFoundException;
import com.back.mymontz.model.ExpenseCategory;
import com.back.mymontz.repository.ExpenseCategoryRepository;

@Component
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

	@Autowired
	private ExpenseCategoryRepository expenseCategoryRepository;

	@Override
	public ExpenseCategory createExpenseCategory(ExpenseCategory expenseCategory) {
		if (expenseCategoryRepository.existsByType(expenseCategory.getType())) {
			throw new ResourceNotFoundException("Expense category duplicated of type: " + expenseCategory.getType());
		}

		return expenseCategoryRepository.save(expenseCategory);
	}

	@Override
	public List<ExpenseCategory> getAllExpenseCategories() {
		return expenseCategoryRepository.findAll();
	}

	@Override
	public ExpenseCategory getExpenseCategoryById(Long id) {
		return expenseCategoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Expense category not exists with id: " + id));
	}

	@Override
	public ExpenseCategory updateExpenseCategory(Long id, ExpenseCategory expenseCategory) {
		ExpenseCategory existingExpenseCategory = expenseCategoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Expense category not exists with id: " + id));

		if (!existingExpenseCategory.getType().equals(expenseCategory.getType())
				&& expenseCategoryRepository.existsByType(expenseCategory.getType())) {
			throw new ResourceNotFoundException("Expense category duplicated of type: " + expenseCategory.getType());
		}

		existingExpenseCategory.setType(expenseCategory.getType());

		return expenseCategoryRepository.save(existingExpenseCategory);
	}

	@Override
	public void deleteExpenseCategoryById(Long id) {
		if (!expenseCategoryRepository.existsById(id)) {
			throw new ResourceNotFoundException("Expense category not exists with id: " + id);
		}
		expenseCategoryRepository.deleteById(id);
	}
}

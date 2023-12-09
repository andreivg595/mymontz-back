package com.back.mymontz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.back.mymontz.exception.ConstraintException;
import com.back.mymontz.exception.DuplicateEntryException;
import com.back.mymontz.exception.ResourceNotFoundException;
import com.back.mymontz.model.ExpenseCategory;
import com.back.mymontz.repository.ExpenseCategoryRepository;

@Component
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

	@Autowired
	private ExpenseCategoryRepository expenseCategoryRepository;

	@Override
	public ExpenseCategory createExpenseCategory(ExpenseCategory expenseCategory) {
		try {
			if (expenseCategoryRepository.existsByType(expenseCategory.getType())) {
				throw new ResourceNotFoundException(
						"Expense category duplicated of type: " + expenseCategory.getType());
			}

			return expenseCategoryRepository.save(expenseCategory);
		} catch (DataIntegrityViolationException e) {
			String msg = e.getMessage().substring(0, e.getMessage().indexOf("]") + 1);
			throw new ConstraintException(msg, e);
		}
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
			throw new DuplicateEntryException("Expense category duplicated of type: " + expenseCategory.getType());
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

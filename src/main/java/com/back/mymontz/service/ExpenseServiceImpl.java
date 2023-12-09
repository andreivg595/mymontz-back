package com.back.mymontz.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.back.mymontz.exception.ConstraintException;
import com.back.mymontz.exception.CustomException;
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
	
	@Autowired
	private UserServiceImpl userService;

	@Override
	public Expense createExpense(Expense expense, MultipartFile file) {
		try {
			userService.checkAuthorization(expense.getUser().getId());
			User user = userRepository.findById(expense.getUser().getId()).orElseThrow(
					() -> new ResourceNotFoundException("User not exist with id: " + expense.getUser().getId()));

			ExpenseCategory expenseCategory = expenseCategoryRepository.findById(expense.getCategory().getId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Expense category not exists with id: " + expense.getCategory().getId()));
			
			if (file != null) {
				try {
					System.out.println("Original Image Byte Size - " + file.getBytes().length);
					expense.setImage(compressBytes(file.getBytes()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

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
		
		List<Expense> expenses;
		
		expenses = expenseRepository.findByUserId(id);

		for (Expense expense : expenses) {
			expense.setImage(decompressBytes(expense.getImage()));
		}
		
		return expenses;
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
		
		List<Expense> expenses;
		
		expenses = expenseRepository.findByDateBetweenAndUserId(startDate, endDate, id);

		for (Expense expense : expenses) {
			expense.setImage(decompressBytes(expense.getImage()));
		}

		return expenses;
	}

	@Override
	public Expense updateExpense(Long id, Expense expense, MultipartFile file) {
		try {
			Expense existingExpense = expenseRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Expense not exists with id: " + id));

			userService.checkAuthorization(expense.getUser().getId());
			
			User user = userRepository.findById(expense.getUser().getId()).orElseThrow(
					() -> new ResourceNotFoundException("User not exist with id: " + expense.getUser().getId()));

			ExpenseCategory expenseCategory = expenseCategoryRepository.findById(expense.getCategory().getId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Expense category not exists with id: " + expense.getCategory().getId()));

			if (file != null) {
				try {
					System.out.println("Original Image Byte Size - " + file.getBytes().length);
					existingExpense.setImage(compressBytes(file.getBytes()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

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
	
	// compress the image bytes before storing it in the database
	private static byte[] compressBytes(byte[] data) {
		Deflater deflater = new Deflater();
		deflater.setLevel(Deflater.BEST_COMPRESSION);
		deflater.setInput(data);
		deflater.finish();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[4*1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		try {
			outputStream.close();
		} catch (IOException e) {
		}
		System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
		return outputStream.toByteArray();
	}

	// uncompress the image bytes before returning it to the angular application
	private static byte[] decompressBytes(byte[] data) {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[4*1024];
		try {
			while (!inflater.finished()) {
				int count = inflater.inflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			outputStream.close();
		} catch (IOException ioe) {
		} catch (DataFormatException e) {
		}
		return outputStream.toByteArray();
	}
}

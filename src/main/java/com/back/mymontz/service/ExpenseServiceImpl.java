package com.back.mymontz.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.back.mymontz.dto.ExpenseRequest;
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
	public Expense createExpense(ExpenseRequest expenseRequest) {
		try {
			userService.checkAuthorization(expenseRequest.getUserId());
			User user = userRepository.findById(expenseRequest.getUserId()).orElseThrow(
					() -> new ResourceNotFoundException("User not exist with id: " + expenseRequest.getUserId()));

			ExpenseCategory expenseCategory = expenseCategoryRepository.findById(expenseRequest.getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Expense category not exists with id: " + expenseRequest.getCategoryId()));
			
			Expense expense = new Expense();
			
			if (expenseRequest.getImageFile() != null) {
				try {
					System.out.println("Original Image Byte Size - " + expenseRequest.getImageFile().getBytes().length);
					expense.setImage(compressBytes(expenseRequest.getImageFile().getBytes()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
	            LocalDate parsedDate = LocalDate.parse(expenseRequest.getDate());
	            expense.setDate(parsedDate);
	        } catch (DateTimeParseException e) {
	            throw new RuntimeException("Incorrect format. Accepted 'YYYY-MM-DD'");
	        }

			expense.setUser(user);
			expense.setCategory(expenseCategory);
			expense.setAmount(expenseRequest.getAmount());
	        expense.setNote(expenseRequest.getNote());

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
			if (expense.getImage() != null) {
				expense.setImage(decompressBytes(expense.getImage()));
			}
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
			if (expense.getImage() != null) {
				expense.setImage(decompressBytes(expense.getImage()));
			}
		}

		return expenses;
	}

	@Override
	public Expense updateExpense(Long id, ExpenseRequest expenseRequest) {
		try {
			Expense existingExpense = expenseRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Expense not exists with id: " + id));

			userService.checkAuthorization(expenseRequest.getUserId());
			
			User user = userRepository.findById(expenseRequest.getUserId()).orElseThrow(
					() -> new ResourceNotFoundException("User not exist with id: " + expenseRequest.getUserId()));

			ExpenseCategory expenseCategory = expenseCategoryRepository.findById(expenseRequest.getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Expense category not exists with id: " + expenseRequest.getCategoryId()));

			if (expenseRequest.getImageFile() != null) {
				try {
					System.out.println("Original Image Byte Size - " + expenseRequest.getImageFile().getBytes().length);
					existingExpense.setImage(compressBytes(expenseRequest.getImageFile().getBytes()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			existingExpense.setUser(user);
			existingExpense.setCategory(expenseCategory);
			//existingExpense.setDate(expenseRequest.getDate());
			existingExpense.setAmount(expenseRequest.getAmount());
			existingExpense.setNote(expenseRequest.getNote());

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

	@Override
	public double getTotalAmountByDateAndUserId(LocalDate date, Long id) {
		userService.checkAuthorization(id);
		userRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("User not exist with id: " + id));
		return expenseRepository.getTotalAmountByDateAndUserId(date, id);
	}

	@Override
	public double getTotalAmountForWeekAndUserId(LocalDate startOfWeek, LocalDate endOfWeek, Long id) {
		userService.checkAuthorization(id);
		userRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("User not exist with id: " + id));
		return expenseRepository.getTotalAmountForWeekAndUserId(startOfWeek, endOfWeek, id);
	}
	
	public double getTotalAmountForMonthAndUserId(LocalDate startOfMonth, LocalDate endOfMonth, Long id) {
		userService.checkAuthorization(id);
		userRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("User not exist with id: " + id));
		return expenseRepository.getTotalAmountForMonthAndUserId(startOfMonth, endOfMonth, id);
	}
}

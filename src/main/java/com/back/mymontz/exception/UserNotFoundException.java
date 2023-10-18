package com.back.mymontz.exception;

public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UserNotFoundException(Long id) {
        super("User not found with ID: " + id);
    }
}
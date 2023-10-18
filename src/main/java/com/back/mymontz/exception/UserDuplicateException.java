package com.back.mymontz.exception;

public class UserDuplicateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UserDuplicateException(String username) {
        super("User already exists with username: " + username);
    }
}
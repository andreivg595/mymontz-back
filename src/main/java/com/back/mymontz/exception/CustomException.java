package com.back.mymontz.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final String message;
    private final Throwable cause;
    private final HttpStatus httpStatus;

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
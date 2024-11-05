package com.example.demo.service.exception;

public class InvalidTokenException extends RuntimeException {
	private static final long serialVersionUID = 6786536955152192384L;

	public InvalidTokenException(Object e) {
		super("Invalid token: id " + e);
	}
	
}

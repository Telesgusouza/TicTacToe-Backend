package com.example.demo.service.exception;

public class TokenException extends RuntimeException {
	private static final long serialVersionUID = 1367142848321753959L;

	public TokenException(Object id) {
		super("Error with passed token. id " + id);
	}
	
}

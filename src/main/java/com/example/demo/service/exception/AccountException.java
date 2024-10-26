package com.example.demo.service.exception;

public class AccountException extends RuntimeException {
	private static final long serialVersionUID = 1367142848321753959L;

	public AccountException(Object id) {
		super("Error with passed token. id " + id);
	}

}

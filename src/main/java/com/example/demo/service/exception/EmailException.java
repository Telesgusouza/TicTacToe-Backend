package com.example.demo.service.exception;

public class EmailException extends RuntimeException {
	private static final long serialVersionUID = -6507693762614921161L;

	public EmailException(Object id) {
		super("Email Exception. id " + id);
	}

}

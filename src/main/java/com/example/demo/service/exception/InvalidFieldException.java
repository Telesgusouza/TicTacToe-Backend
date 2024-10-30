package com.example.demo.service.exception;

public class InvalidFieldException extends RuntimeException {
	private static final long serialVersionUID = 3864470702530291358L;

	public InvalidFieldException(Object id) {
		super("incorrect field. id " + id);
	}

}

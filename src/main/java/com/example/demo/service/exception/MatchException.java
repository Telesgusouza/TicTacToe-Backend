package com.example.demo.service.exception;

public class MatchException extends RuntimeException {
	private static final long serialVersionUID = -9199979453362087917L;

	public MatchException(Object id) {
		super("Error with match. id " + id);
	}

}

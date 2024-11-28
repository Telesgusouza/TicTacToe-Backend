package com.example.demo.service.exception;

public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 4382592954390171873L;

	public ResourceNotFoundException(Object id) {
		super("Resource not found. id " + id);
	}

}

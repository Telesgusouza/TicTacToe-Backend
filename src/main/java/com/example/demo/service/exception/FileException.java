package com.example.demo.service.exception;

public class FileException extends RuntimeException {
	private static final long serialVersionUID = -3831787137884166313L;

	public FileException(Object id) {
		super("error with the file" + id);
	}

}

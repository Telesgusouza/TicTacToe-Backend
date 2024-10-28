package com.example.demo.service.exception;

public class FileTypeException extends RuntimeException {
	private static final long serialVersionUID = -3831787137884166313L;

	public FileTypeException(Object id) {
		super("Error with the file. id " + id);
	}

}

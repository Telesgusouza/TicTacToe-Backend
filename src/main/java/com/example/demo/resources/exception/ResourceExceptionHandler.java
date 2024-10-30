package com.example.demo.resources.exception;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.service.exception.AccountException;
import com.example.demo.service.exception.FileException;
import com.example.demo.service.exception.FileTypeException;
import com.example.demo.service.exception.InvalidFieldException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResourceExceptionHandler {

	// Authentication
	@ExceptionHandler(AccountException.class)
	public ResponseEntity<StandardError> handleAccountException(AccountException e, HttpServletRequest request) {

		String error = "Account error";
		Integer status = 400; // Bad Request
		StandardError err = new StandardError(Instant.now(), status, error, e.getMessage(), request.getRequestURI());

		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<StandardError> handleAuthenticationException(AuthenticationException e,
			HttpServletRequest request) {

		String error = "Erro in authentication";
		Integer status = 400; // Bad Request
		StandardError err = new StandardError(Instant.now(), status, error, e.getMessage(), request.getRequestURI());

		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(FileTypeException.class)
	public ResponseEntity<StandardError> handleFileTypeException(FileTypeException e, HttpServletRequest request) {

		String error = "invalid type";
		Integer status = 400;
		StandardError err = new StandardError(Instant.now(), status, error, e.getMessage(), request.getRequestURI());

		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(FileException.class)
	public ResponseEntity<StandardError> handleFileException(FileException e, HttpServletRequest request) {

		String error = "unexpected error with file";
		Integer status = 400;
		StandardError err = new StandardError(Instant.now(), status, error, e.getMessage(), request.getRequestURI());

		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(InvalidFieldException.class)
	public ResponseEntity<StandardError> handleInvalidFieldException(InvalidFieldException e,
			HttpServletRequest request) {

		String error = "unexpected error with a field";
		Integer status = 400;
		StandardError err = new StandardError(Instant.now(), status, error, e.getMessage(), request.getRequestURI());

		return ResponseEntity.status(status).body(err);
	}

}

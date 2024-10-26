package com.example.demo.resources.exception;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.service.exception.AccountException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResourceExceptionHandler {

	@ExceptionHandler(AccountException.class)
	public ResponseEntity<StandardError> handleAccountException(AccountException e, HttpServletRequest request) {

		String error = "Erro na conta";
		Integer status = 400; // Bad Request
		StandardError err = new StandardError(Instant.now(), status, error, e.getMessage(), request.getRequestURI());

		return ResponseEntity.status(status).body(err);
	}

	// AuthenticationException
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<StandardError> handleAuthenticationException(AuthenticationException e,
			HttpServletRequest request) {

		String error = "Erro in authentication";
		Integer status = 400; // Bad Request
		StandardError err = new StandardError(Instant.now(), status, error, e.getMessage(), request.getRequestURI());

		return ResponseEntity.status(status).body(err);
	}

}

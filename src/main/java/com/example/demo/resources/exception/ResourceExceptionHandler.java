package com.example.demo.resources.exception;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.service.ResourceNotFoundException;
import com.example.demo.service.exception.TokenException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResourceExceptionHandler {

	@ExceptionHandler(TokenException.class)
	public ResponseEntity<StandardError> tokenException(ResourceNotFoundException e, HttpServletRequest request) {

		String error = "Unable to login";
		Integer status = 401;
		StandardError err = new StandardError(Instant.now(), status, error, e.getMessage(), request.getRequestURI());

		return ResponseEntity.status(status).body(null);
	}

}

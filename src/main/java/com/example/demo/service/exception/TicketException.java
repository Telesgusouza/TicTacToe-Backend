package com.example.demo.service.exception;

public class TicketException extends RuntimeException {
	private static final long serialVersionUID = 7777774872909859835L;

	public TicketException(Object id) {
		super("Ticket Exception. id " + id);
	}
	
}

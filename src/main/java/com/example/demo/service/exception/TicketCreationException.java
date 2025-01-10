package com.example.demo.service.exception;

public class TicketCreationException extends RuntimeException {
	private static final long serialVersionUID = 7777774872909859835L;

	public TicketCreationException(Object id) {
		super("Ticket Creation Exception. id " + id);
	}
	
}

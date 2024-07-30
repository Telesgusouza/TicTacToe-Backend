package com.example.demo.resources;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.TicketsService;

@RestController
@RequestMapping(value = "api/v1/ticket")
@CrossOrigin
public class TicketController {

	@Autowired
	private TicketsService ticketsService;

	@PostMapping(value = "/{id}")
	public Map<String, String> buildTicket(@PathVariable UUID id) {

		String ticket = ticketsService.buildAndSaveTicket(id);

		return Map.of("ticket", ticket);
	}

}

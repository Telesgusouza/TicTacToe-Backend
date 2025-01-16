package com.example.demo.resources;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.resources.exception.StandardError;
import com.example.demo.service.TicketsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
		name = "Ticket",
		description = "The ticket is a websockets authentication"
		)
@RestController
@RequestMapping(value = "api/v1/ticket")
public class TicketController {

	@Autowired
	private TicketsService ticketsService;
	
	@Operation(
			summary = "generate ticket",
			description = "operation responsible for generating the token for the user",
			responses = {
					
					@ApiResponse(
							responseCode = "200",
							description = "Ticket generated successfully",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = Map.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "id cannot be null",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = StandardError.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "match not found",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = StandardError.class)))
					
			}
			)
	@PostMapping(value = "/{id}")
	public Map<String, String> buildTicket(@PathVariable UUID id) {

		String ticket = ticketsService.buildAndSaveTicket(id);

		return Map.of("ticket", ticket);
	}

}

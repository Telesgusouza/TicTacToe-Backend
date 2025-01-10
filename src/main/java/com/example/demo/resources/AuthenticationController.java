
package com.example.demo.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RequestAuthDTO;
import com.example.demo.dto.RequestRegisterDTO;
import com.example.demo.dto.ResetPasswordDTO;
import com.example.demo.dto.ResponseTokenDTO;
import com.example.demo.dto.TicketDTO;
import com.example.demo.entity.Mail;
import com.example.demo.repository.EmailRepository;
import com.example.demo.resources.exception.StandardError;
import com.example.demo.service.AuthorizationService;
import com.example.demo.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "operations related to user registration and login")
public class AuthenticationController {

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private EmailService emailService;

	@Operation(summary = "log into account", description = "Resource to be able to log into our account", responses = {

			@ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseTokenDTO.class))),

			@ApiResponse(responseCode = "401", description = "Error logging into account", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class))),

			@ApiResponse(responseCode = "400", description = "Error incorrect password", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class))), }

	)
	@PostMapping("login")
	public ResponseEntity<ResponseTokenDTO> login(@RequestBody @Valid RequestAuthDTO data) {

		ResponseTokenDTO response = authorizationService.login(data);

		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Create new user/player", description = "Resource that registers our users in the db", responses = {
			@ApiResponse(responseCode = "201", description = "Success in registering user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseTokenDTO.class))),

			@ApiResponse(responseCode = "401", description = "Account already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class))),

			@ApiResponse(responseCode = "401", description = "Must have at least 6 characters and less than 50", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class))),

			@ApiResponse(responseCode = "401", description = "Error while generating token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class))), })
	@PostMapping("register")
	public ResponseEntity<ResponseTokenDTO> register(@RequestBody @Valid RequestRegisterDTO data) {

		ResponseTokenDTO response = authorizationService.register(data);

		return ResponseEntity.ok().body(response);
	}

	// emails // 
	@Operation(
			summary = "Send ticket by email",
			description = "The ticket will be sent by email",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Email sent successfully",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = Void.class
											))),
					
					@ApiResponse(
							responseCode = "400",
							description = "Invalid field",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "Error creating or saving ticket",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "failed to send email",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "Unknown error sending email",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class)))
					
					
			}
			)
	@PostMapping("/email_reset_password")
	public void emailResetPassword(@RequestBody Mail mail) {

		this.emailService.emailResetPassword(mail);
	}

	@Operation(
			summary = "verify token",
			description = "Checks whether the passed token is a valid token",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "is a valid token",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = Void.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "token cannot be is null",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "invalid ticket",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class)))
					
			})
	@PostMapping("/verify_ticket")
	public ResponseEntity<?> getTicket(@RequestBody TicketDTO ticket) {

		this.emailService.verifyTicket(ticket.ticket());

		return ResponseEntity.noContent().build();
	}

	@Operation(
			summary = "Change password",
			description = "Function that will change the user's password",
			responses = {
					
					@ApiResponse(
							responseCode = "200",
							description = "password changed successfully",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = Void.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "Password too short",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "Password cannot be null",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "token cannot be is null",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "invalid ticket",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "An error occurred while saving user data",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class)))
					
			})
	@PatchMapping("/pass_password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO password) {

		this.emailService.resetPassword(password);
		return ResponseEntity.noContent().build();
	}

}




































package com.example.demo.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RequestAuthDTO;
import com.example.demo.dto.RequestRegisterDTO;
import com.example.demo.dto.ResponseTokenDTO;
import com.example.demo.resources.exception.StandardError;
import com.example.demo.service.AuthorizationService;

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


	@Operation(
			summary = "log into account", 
			description = "Resource to be able to log into our account", 
			responses = {
			
					@ApiResponse(
							responseCode = "200", 
							description = "User logged in successfully", 
							content = @Content(
									mediaType = "application/json", 
									schema = @Schema(
											implementation = ResponseTokenDTO.class))),
				
					@ApiResponse(
							responseCode = "401",
							description = "Error logging into account",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class)
									)
							)
					
				}
					
			)
	@PostMapping("login")
	public ResponseEntity<ResponseTokenDTO> login(@RequestBody @Valid RequestAuthDTO data) {

		ResponseTokenDTO response = authorizationService.login(data);

		return ResponseEntity.ok().body(response);
	}


	@Operation(
			summary = "Create new user/player",
			description = "Resource that registers our users in the db",
			responses = {
					@ApiResponse(
							responseCode = "201",
							description = "Success in registering user",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = ResponseTokenDTO.class
											)
									)
							),
					
					@ApiResponse(
							responseCode = "401",
							description = "Account already exists",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class)
									)
							),
					
					@ApiResponse(
							responseCode = "401",
							description = "Error while generating token",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = StandardError.class)
									)
							),
			}
			)
	@PostMapping("register")
	public ResponseEntity<ResponseTokenDTO> register(@RequestBody @Valid RequestRegisterDTO data) {

		ResponseTokenDTO response = authorizationService.register(data);

		return ResponseEntity.ok().body(response);
	}

}

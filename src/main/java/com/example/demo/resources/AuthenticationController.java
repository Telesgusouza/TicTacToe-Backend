
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
import com.example.demo.service.AuthorizationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

	@Autowired
	private AuthorizationService authorizationService;

	@PostMapping("login")
	public ResponseEntity<ResponseTokenDTO> login(@RequestBody @Valid RequestAuthDTO data) {

		ResponseTokenDTO response = authorizationService.login(data);

		return ResponseEntity.ok().body(response);
	}

	@PostMapping("register")
	public ResponseEntity<?> register(@RequestBody @Valid RequestRegisterDTO data) {

		ResponseTokenDTO response = authorizationService.register(data);

		return ResponseEntity.ok().body(response);
	}

}

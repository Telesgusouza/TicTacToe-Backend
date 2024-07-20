package com.example.demo.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RequestAuthDTO;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository repo;

	@PostMapping("login")
	public ResponseEntity<?> login(@RequestBody @Valid RequestAuthDTO data) {

		var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
		var auth = authenticationManager.authenticate(usernamePassword);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("register")
	public ResponseEntity<?> register(@RequestBody @Valid RequestAuthDTO data) {

		if (this.repo.findByLogin(data.login()) != null)
			return ResponseEntity.badRequest().build();

		String encryptPassword = new BCryptPasswordEncoder().encode(data.password());
		User newUser = new User(null, data.login(), encryptPassword, UserRole.OUT_OF_START);

		this.repo.save(newUser);

		return ResponseEntity.noContent().build();
	}

}

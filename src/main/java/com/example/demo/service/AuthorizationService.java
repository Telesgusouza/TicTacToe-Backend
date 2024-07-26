package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.config.TokenService;
import com.example.demo.dto.RequestAuthDTO;
import com.example.demo.dto.ResponseTokenDTO;
import com.example.demo.entity.User;
import com.example.demo.enums.Player;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.UserRepository;

@Service
public class AuthorizationService implements UserDetailsService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository repo;

	@Autowired
	private TokenService tokenService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		return repo.findByLogin(username);
	}

	public ResponseTokenDTO login(RequestAuthDTO data) {
		var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
		var auth = authenticationManager.authenticate(usernamePassword);

		var token = tokenService.generateToken((User) auth.getPrincipal());

		return new ResponseTokenDTO(token);
	}

	public ResponseTokenDTO register(RequestAuthDTO data) {
		if (this.repo.findByLogin(data.login()) != null)
			throw new RuntimeException("account already exists");

		String encryptPassword = new BCryptPasswordEncoder().encode(data.password());
		User newUser = new User(null, data.login(), encryptPassword, UserRole.LOOKING_FOR_MATCH, Player.PLAYER_ONE); // OUT_OF_START

		User user = this.repo.save(newUser);
		var token = tokenService.generateToken(user);

		return new ResponseTokenDTO(token);
	}

}

package com.example.demo.service;

import java.util.Optional;
import java.util.UUID;

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
import com.example.demo.dto.RequestFriendsDTO;
import com.example.demo.dto.RequestRegisterDTO;
import com.example.demo.dto.ResponseTokenDTO;
import com.example.demo.entity.Friend;
import com.example.demo.entity.User;
import com.example.demo.enums.Player;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.FriendsRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.exception.AccountException;
import com.example.demo.service.exception.InvalidFieldException;

@Service
public class AuthorizationService implements UserDetailsService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository repo;

	@Autowired
	private FriendsRepository repoFriends;

	@Autowired
	private S3Service s3Service;

	@Autowired
	private TokenService tokenService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		return repo.findByLogin(username);
	}

	public ResponseTokenDTO login(RequestAuthDTO data) {

		var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());

		try {

			var auth = authenticationManager.authenticate(usernamePassword);

			var token = tokenService.generateToken((User) auth.getPrincipal());

			return new ResponseTokenDTO(token);

		} catch (Exception e) {

			if (this.repo.findByLogin(data.login()) != null) {
				throw new InvalidFieldException("incorrect password");
			} else {
				throw new AccountException("Authentication failed");
			}

		}

	}

	public ResponseTokenDTO register(RequestRegisterDTO data) {

		if (this.repo.findByLogin(data.login()) != null) {
			throw new InvalidFieldException("account already exists");
		}
		if (data.password().length() < 6 || data.password().length() >= 50) {
			throw new InvalidFieldException("must have at least 6 characters and less than 50");
		}

		String encryptPassword = new BCryptPasswordEncoder().encode(data.password());
		User newUser = new User(null, data.name(), data.login(), encryptPassword,

				UserRole.OUT_OF_START, Player.NO_PLAYER,

				0, 0, 0);

		User user = this.repo.save(newUser);
		var token = tokenService.generateToken(user);

		return new ResponseTokenDTO(token);
	}

	public User findById(UUID id) {
		return repo.findById(id).orElseThrow(() -> new AccountException("No user found"));
	}

	public Friend addToFriend(RequestFriendsDTO data, UUID id) {

		Optional<User> user = this.repo.findById(id);
		User field = user.orElseThrow(() -> new AccountException("user not found"));

		if (!field.getFriends().stream().anyMatch(f -> f.getIdPlayer().equals(data.anotherPlayer()))) {

			Friend newFriend = new Friend(null, data.name(), data.img(), data.anotherPlayer());
			newFriend.setPlayer_friend(field);

			field.getFriends().add(newFriend);

			this.repoFriends.save(newFriend);
			this.repo.save(field);

			return newFriend;
		} else {
			throw new InvalidFieldException("Friend already exists in the friends list");
		}

	}

	public void deleteAccount(User user) {

		this.s3Service.delete(user.getId());
		this.repo.delete(user);
	}

}

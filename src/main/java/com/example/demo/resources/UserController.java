package com.example.demo.resources;

import java.util.List;
import java.util.UUID;

import javax.security.auth.login.AccountException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RequestFriendsDTO;
import com.example.demo.dto.ResponseIDUserDTO;
import com.example.demo.dto.ResponseUser;
import com.example.demo.entity.Friend;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.resources.exception.StandardError;
import com.example.demo.service.AuthorizationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "User", description = "Contains operations to deal with user")
@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin
public class UserController {

	@Autowired
	private AuthorizationService repo;

	@Autowired
	private UserRepository repoUser;

	@Operation(summary = "get user ", description = "bring user information", responses = {
			@ApiResponse(responseCode = "200", description = "success in bringing data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseUser.class))),

			@ApiResponse(responseCode = "400", description = "error in bringing data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class))), })
	@GetMapping
	public ResponseEntity<ResponseUser> getUser(@AuthenticationPrincipal User user) throws AccountException {

		if (user == null) {
			throw new AccountException("account already exists");
		}

		ResponseUser obj = new ResponseUser(user.getName(), user.getLogin(), user.getRole(), user.getPlayer(),

				user.getNumberOfWins(), user.getNumberOfDefeats(), user.getNumberOfDraws());

		return ResponseEntity.ok().body(obj);
	}

	@Operation(summary = "get id user", description = "Bring the user id", responses = {
			@ApiResponse(responseCode = "200", description = "success in bringing id", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseIDUserDTO.class))),

			@ApiResponse(responseCode = "400", description = "Error in bringing id", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class))), })
	@GetMapping("/id")
	public ResponseEntity<ResponseIDUserDTO> getIdUser(@AuthenticationPrincipal User user) throws AccountException {

		if (user == null) {
			throw new AccountException("account already exists");
		}

		return ResponseEntity.ok().body(new ResponseIDUserDTO(user.getId()));
	}

	@Operation(summary = "get user for id", description = "Bring user data by id", responses = {
			@ApiResponse(responseCode = "200", description = "success in bringing data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseUser.class))),

			@ApiResponse(responseCode = "400", description = "Error in bringing data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class))), })
	@GetMapping("/{id}")
	public ResponseEntity<ResponseUser> getUserForID(@PathVariable UUID id) throws AccountException {
		User user = this.repo.findById(id);

		if (user == null) {
			throw new AccountException("account already exists");
		}

		ResponseUser player = new ResponseUser(user.getName(), user.getLogin(), user.getRole(), user.getPlayer(),
				user.getNumberOfWins(), user.getNumberOfDefeats(), user.getNumberOfDraws());

		return ResponseEntity.ok().body(player);
	}

	@Operation(summary = "get List friends", description = "Brings the user's friends list", responses = {
			@ApiResponse(responseCode = "200", description = "Success in bringing friends list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Friend.class))),

			@ApiResponse(responseCode = "400", description = "Error in bringing friends list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class))), })
	@GetMapping("/list_friends")
	public ResponseEntity<List<Friend>> getListFriends(@AuthenticationPrincipal User user) throws AccountException {

		if (user == null) {
			throw new AccountException("account already exists");
		}

		return ResponseEntity.ok().body(user.getFriends());
	}

	@Operation(summary = "Add new friend", description = "Add a new friend to the list", responses = {
			@ApiResponse(responseCode = "200", description = "Success in add new user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Friend.class))),

			@ApiResponse(responseCode = "400", description = "Error in add new user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardError.class))), })
	@PostMapping
	public ResponseEntity<Friend> addToFriend(@Valid @RequestBody RequestFriendsDTO data,
			@AuthenticationPrincipal User user) {

		Friend userSave = this.repo.addToFriend(data, user.getId());

		return ResponseEntity.ok().body(userSave);
	}
	
	@Operation(
			summary = "Delete user", 
			description = "delete user from database", 
			responses = {
				@ApiResponse(
						responseCode = "200", 
						description = "Success in deleter account", 
						content = @Content(
								mediaType = "application/json", 
								schema = @Schema(implementation = Void.class))),
	
				@ApiResponse(
						responseCode = "400", 
						description = "Error in deleter account", 
						content = @Content(
								mediaType = "application/json", 
								schema = @Schema(implementation = StandardError.class))), })
	
	@DeleteMapping
	public ResponseEntity<Friend> deleteAccount(@AuthenticationPrincipal User user) {
		
		this.repo.deleteAccount(user);
		return ResponseEntity.noContent().build();
	}

}




















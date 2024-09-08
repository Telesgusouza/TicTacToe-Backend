package com.example.demo.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RequestFriendsDTO;
import com.example.demo.dto.ResponseUser;
import com.example.demo.entity.Friend;
import com.example.demo.entity.User;
import com.example.demo.service.AuthorizationService;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*")
public class UserController {

	@Autowired
	private AuthorizationService repo;

	@GetMapping
	public ResponseEntity<ResponseUser> getUser(@AuthenticationPrincipal User user) {
		ResponseUser obj = new ResponseUser(user.getName(), user.getLogin(), user.getRole(), user.getPlayer(),

				user.getNumberOfWins(), user.getNumberOfDefeats(), user.getNumberOfDraws());

		return ResponseEntity.ok().body(obj);
	}

	@GetMapping("/list_friends")
	public ResponseEntity<List<Friend>> getListFriends(@AuthenticationPrincipal User user) {

		return ResponseEntity.ok().body(user.getFriends());
	}

	@PostMapping
	public ResponseEntity<User> addToFriend(@RequestBody RequestFriendsDTO data, @AuthenticationPrincipal User user) {
		
		User userSave = this.repo.addToFriend(data, user.getId());

		return ResponseEntity.ok().body(userSave);
	}

}

package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Match;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.MatchRepository;
import com.example.demo.repository.UserRepository;

@Service
public class MatchService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MatchRepository matchRepository;

	public User searchPlayer(UUID idUser) {
		return this.userRepository.findByRoleAndIdNot(UserRole.LOOKING_FOR_MATCH, idUser);
	}

	public Match createMatch(User user) {

		try {

			UUID playerOne;
			UUID playerTwo;

			user.setRole(UserRole.LOOKING_FOR_MATCH);
			this.userRepository.save(user);

			User anotherPlayer = searchPlayer(user.getId());

			if (anotherPlayer == null) {
				throw new RuntimeException("player not found");
			}

			Integer random = new Random().nextInt(2) + 1;
			playerOne = random == 1 ? user.getId() : anotherPlayer.getId();
			playerTwo = random == 1 ? anotherPlayer.getId() : user.getId();

			Match newMatch = new Match(null, LocalDateTime.now(), playerOne, playerTwo, 0, 0, 0);
			Match match = this.matchRepository.save(newMatch);

			return match;
		} catch (Exception e) {
			throw new RuntimeException("error when creating the match");
		}

	}

}

package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Board;
import com.example.demo.entity.Match;
import com.example.demo.entity.User;
import com.example.demo.enums.Player;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MatchRepository;
import com.example.demo.repository.UserRepository;

@Service
public class MatchService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BoardRepository boardRepository;

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

			user.setPlayer(Player.PLAYER_ONE);

			this.userRepository.save(user);

			User anotherPlayer = searchPlayer(user.getId());

			if (anotherPlayer == null) {
				throw new RuntimeException("player not found");
			}

			Integer random = new Random().nextInt(2) + 1;
			playerOne = random == 1 ? user.getId() : anotherPlayer.getId();
			playerTwo = random == 1 ? anotherPlayer.getId() : user.getId();

			this.userRepository.save(user);

			Match newMatch = new Match(null, LocalDateTime.now(), playerOne, playerTwo, 0, 0, 0);
			Match match = this.matchRepository.save(newMatch);

			return match;
		} catch (Exception e) {
			user.setRole(UserRole.OUT_OF_START);

			user.setPlayer(Player.PLAYER_ONE);

			this.userRepository.save(user);

			throw new RuntimeException("error when creating the match");
		}
	}

	public Match getMatch(UUID id) {
		return matchRepository.findById(id).orElseThrow(() -> new RuntimeException("match not found"));
	}

	private Board board() {

		Player noField = Player.NO_PLAYER;

		Board newBoard = new Board(null, List.of(noField, noField, noField), List.of(noField, noField, noField),
				List.of(noField, noField, noField));

		return this.boardRepository.save(newBoard);
	}

}

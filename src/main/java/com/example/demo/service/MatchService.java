package com.example.demo.service;

import java.util.List;
import java.util.Random;

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
	private BoardRepository boardRepository;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private UserRepository userRepository;

	public Match creatingMatch(User user) {
		try {
			user.setRole(UserRole.LOOKING_FOR_MATCH);
			this.userRepository.save(user); // Salva o estado atualizado do usuário

			User anotherPlayer = this.userRepository.findByRoleAndIdNot(UserRole.LOOKING_FOR_MATCH, user.getId());

			if (anotherPlayer == null || anotherPlayer.getId().equals(user.getId())) {

				user.setRole(UserRole.OUT_OF_START);
				user.setPlayer(Player.PLAYER_ONE);

				this.userRepository.save(user); // Salva o estado atualizado do usuário

				return null;
			}

			// Lógica para criar o jogo entre 'user' e 'anotherPlayer'
			Random random = new Random();
			int randomNumber = random.nextInt(2) + 1;

			Player playerOne = randomNumber == 1 ? Player.PLAYER_ONE : Player.NO_PLAYER;
			Player playerTwo = randomNumber == 2 ? Player.PLAYER_TWO : Player.NO_PLAYER;

			Match match = new Match();

			match.setPlayerOne(playerOne);
			match.setPlayerTwo(playerTwo);

			// Atualiza os usuários para refletir que eles agora estão em uma partida
			user.setRole(UserRole.ON_DEPARTURE);
			anotherPlayer.setRole(UserRole.ON_DEPARTURE);
			this.userRepository.save(user);
			this.userRepository.save(anotherPlayer);

			return match;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error creating match");
		}
	}

	private Board board() {

		Player noField = Player.NO_PLAYER;

		Board newBoard = new Board(null, List.of(noField, noField, noField), List.of(noField, noField, noField),
				List.of(noField, noField, noField));

		return this.boardRepository.save(newBoard);
	}

}

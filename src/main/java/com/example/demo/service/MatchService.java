package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.RequestMatchScoreboardDTO;
import com.example.demo.dto.ResponseUrlPhotoDTO;
import com.example.demo.entity.Board;
import com.example.demo.entity.Match;
import com.example.demo.entity.User;
import com.example.demo.enums.MatchScoreboardEnum;
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
	
	@Autowired
	private S3Service s3Service;

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

			// photos players
			ResponseUrlPhotoDTO photoPlayerOne = this.s3Service.getPhoto(playerOne);
			ResponseUrlPhotoDTO photoPlayerTwo = this.s3Service.getPhoto(playerTwo);
			
			Match newMatch = new Match(
					null, 
					
					LocalDateTime.now(), 
					
					playerOne, 
					playerTwo, 
					
					photoPlayerOne.photo(), 
					photoPlayerTwo.photo(),
					0, 0, 0, 0);
			Match match = this.matchRepository.save(newMatch);

			return match;
		} catch (Exception e) {
			user.setRole(UserRole.OUT_OF_START);

			user.setPlayer(Player.NO_PLAYER);

			this.userRepository.save(user);

			throw new RuntimeException("error when creating the match");
		}
	}

	public Match getMatch(UUID id) {
		return matchRepository.findById(id).orElseThrow(() -> new RuntimeException("match not found"));
	}

	public Match modifyMatchScoreboard(UUID id, RequestMatchScoreboardDTO data) {
		Optional<Match> requestMatch = this.matchRepository.findById(id);
		Match match = requestMatch.orElseThrow();

		switch (data.victory()) {

		case MatchScoreboardEnum.PLAYER_ONE: {
			match.setNumberOfWinsPlayerOne(match.getNumberOfWinsPlayerOne() + 1);
			match.setNumberOfMatches(match.getNumberOfMatches() + 1);

			addVictoryScore(match.getIdPlayerOne(), match.getIdPlayerTwo());

			break;
		}

		case MatchScoreboardEnum.PLAYER_TWO: {
			match.setNumberOfWinsPlayerTwo(match.getNumberOfWinsPlayerTwo() + 1);
			match.setNumberOfMatches(match.getNumberOfMatches() + 1);

			addVictoryScore(match.getIdPlayerTwo(), match.getIdPlayerOne());

			break;
		}

		default: {
			match.setNumberOfDraws(match.getNumberOfDraws() + 1);
			match.setNumberOfMatches(match.getNumberOfMatches() + 1);

			Optional<User> player = this.userRepository.findById(match.getIdPlayerOne());
			User playerOne = player.orElseThrow();
			playerOne.setNumberOfDraws(playerOne.getNumberOfDraws() + 1);

			Optional<User> anotherPlayer = this.userRepository.findById(match.getIdPlayerTwo());
			User playerTwo = anotherPlayer.orElseThrow();
			playerTwo.setNumberOfDraws(playerTwo.getNumberOfDraws() + 1);

			this.userRepository.saveAll(Arrays.asList(playerOne, playerTwo));
		}
		}

		return this.matchRepository.save(match);
	}

	private void addVictoryScore(UUID idPlayerOne, UUID idPlayerTwo) {
		Optional<User> player = this.userRepository.findById(idPlayerOne);
		User playerOne = player.orElseThrow();
		playerOne.setNumberOfWins(playerOne.getNumberOfWins() + 1);

		Optional<User> anotherPlayer = this.userRepository.findById(idPlayerTwo);
		User playerTwo = anotherPlayer.orElseThrow();
		playerTwo.setNumberOfDefeats(playerTwo.getNumberOfDefeats() + 1);

		this.userRepository.saveAll(Arrays.asList(playerOne, playerTwo));
	}

	private Board board() {

		Player noField = Player.NO_PLAYER;

		Board newBoard = new Board(null, List.of(noField, noField, noField), List.of(noField, noField, noField),
				List.of(noField, noField, noField));

		return this.boardRepository.save(newBoard);
	}

}

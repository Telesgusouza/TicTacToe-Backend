package com.example.demo.service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.RequestMatchScoreboardDTO;
import com.example.demo.entity.Match;
import com.example.demo.entity.User;
import com.example.demo.enums.MatchScoreboardEnum;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.MatchRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.exception.AccountException;
import com.example.demo.service.exception.InvalidFieldException;
import com.example.demo.service.exception.MatchException;

@Service
public class MatchService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MatchRepository matchRepository;

	public User searchPlayer(UUID idUser) {
		return this.userRepository.findByRoleAndIdNot(UserRole.LOOKING_FOR_MATCH, idUser);
	}

	public Match getMatch(UUID id) {

		if (id == null) {
			throw new InvalidFieldException("field cannot be null");
		}
		if (!matchRepository.findById(id).isPresent()) {
			throw new InvalidFieldException("invalid id");
		}

		Optional<Match> matchOptional = matchRepository.findById(id);
		Match match = matchOptional.orElseThrow(() -> new MatchException("match not found"));

		return match;
	}

	public Match modifyMatchScoreboard(UUID id, RequestMatchScoreboardDTO data) {

		if (id == null || !matchRepository.findById(id).isPresent()) {
			throw new InvalidFieldException("Invalid match ID");
		}
		if (data == null) {
			throw new InvalidFieldException("Request data cannot be null");
		}

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
		if (!userRepository.findById(idPlayerOne).isPresent() || !userRepository.findById(idPlayerTwo).isPresent()) {
			throw new AccountException("User not found");
		}

		Optional<User> player = this.userRepository.findById(idPlayerOne);
		User playerOne = player.orElseThrow();
		playerOne.setNumberOfWins(playerOne.getNumberOfWins() + 1);

		Optional<User> anotherPlayer = this.userRepository.findById(idPlayerTwo);
		User playerTwo = anotherPlayer.orElseThrow();
		playerTwo.setNumberOfDefeats(playerTwo.getNumberOfDefeats() + 1);

		this.userRepository.saveAll(Arrays.asList(playerOne, playerTwo));
	}

}
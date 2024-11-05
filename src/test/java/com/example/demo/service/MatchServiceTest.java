package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.dto.RequestMatchScoreboardDTO;
import com.example.demo.entity.Match;
import com.example.demo.entity.User;
import com.example.demo.enums.MatchScoreboardEnum;
import com.example.demo.enums.Player;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.MatchRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.exception.InvalidFieldException;
import com.example.demo.service.exception.MatchException;

@SpringBootTest(properties = {

		"AWS_ACESSKEY=aws_acesskey", "AWS_BUCKET=aws_bucket", "AWS_SECRET=aws_secret",

		"JWT_SECRET=my-secret-key-for-tests",

		"aws.acesskey=${AWS_ACESSKEY}", "aws.secrety=${AWS_SECRET}", "aws.bucket=${AWS_BUCKET}",

		"api.security.token.secret=${JWT_SECRET:my-secret-key}",

})
@AutoConfigureWebMvc
@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private MatchRepository matchRepository;

	@InjectMocks
	private MatchService matchService;

	@Test
	@DisplayName("Bringing the match data successfully")
	public void bringingMatchData() {
		UUID uuid = UUID.randomUUID();

		Optional<Match> matchOptional = Optional
				.of(new Match(uuid, LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID(), "", "", 0, 0, 0, 0));

		when(matchRepository.findById(uuid)).thenReturn(matchOptional);

		Match matchEntity = matchService.getMatch(uuid);

		assertNotNull(matchEntity);
		assertEquals(uuid, matchEntity.getId());
	}

	@Test
	@DisplayName("returns exception if the id to search for match is null")
	public void idIsNull() {
		assertThrows(InvalidFieldException.class, () -> matchService.getMatch(null));
	}

	@Test
	@DisplayName("Id is not associated with a match")
	public void matchNotFound() {
		UUID nonExistingUuid = UUID.randomUUID();

		when(matchRepository.findById(nonExistingUuid)).thenReturn(Optional.empty());

		assertThrows(MatchException.class, () -> matchService.getMatch(nonExistingUuid));
	}

	@Test
	@DisplayName("the Scoreboard must change with the victory of a player")
	public void modifyMatchScoreboard() {
		UUID uuid = UUID.randomUUID();
		UUID uuidUserOne = UUID.randomUUID();
		UUID uuidUserTwo = UUID.randomUUID();

		// match
		Optional<Match> matchOptional = Optional
				.of(new Match(uuid, LocalDateTime.now(), uuidUserOne, uuidUserTwo, "", "", 0, 0, 0, 0));
		Match match = matchOptional.orElseThrow();

		// scoreboard
		RequestMatchScoreboardDTO scoreboard = new RequestMatchScoreboardDTO(MatchScoreboardEnum.PLAYER_ONE);

		// users
		Optional<User> playerOneOptional = Optional.of(new User(uuidUserOne, "playerOne", "login1@gmail.com",
				"password", UserRole.ON_DEPARTURE, Player.PLAYER_ONE, 0, 0, 0));
		Optional<User> playerTwoOptional = Optional.of(new User(uuidUserTwo, "playerTwo", "login2@gmail.com",
				"password", UserRole.ON_DEPARTURE, Player.PLAYER_TWO, 0, 0, 0));
		User playerOne = playerOneOptional.orElseThrow();
		User playerTwo = playerTwoOptional.orElseThrow();

		when(matchRepository.findById(uuid)).thenReturn(matchOptional);
		when(userRepository.findById(uuidUserOne)).thenReturn(playerOneOptional);
		when(userRepository.findById(uuidUserTwo)).thenReturn(playerTwoOptional);
		when(userRepository.saveAll(Arrays.asList(playerOne, playerTwo)))
				.thenReturn(Arrays.asList(playerOne, playerTwo));

		when(matchRepository.save(match)).thenReturn(match);

		Match modifyScoreboard = matchService.modifyMatchScoreboard(uuid, scoreboard);

		assertNotNull(modifyScoreboard);

		// Verifique se os resultados foram atualizados corretamente
		assertEquals(1, modifyScoreboard.getNumberOfWinsPlayerOne());
		assertEquals(1, modifyScoreboard.getNumberOfMatches());
		assertEquals(1, playerOne.getNumberOfWins());
		assertEquals(1, playerTwo.getNumberOfDefeats());
	}

	@Test
	@DisplayName("the fields passed are invalid")
	public void invalidFieldsMatch() {
		UUID uuid = UUID.randomUUID();
		RequestMatchScoreboardDTO scoreboard = new RequestMatchScoreboardDTO(MatchScoreboardEnum.PLAYER_ONE);

		when(matchRepository.findById(uuid)).thenReturn(Optional.empty());

		assertThrows(InvalidFieldException.class, () -> matchService.modifyMatchScoreboard(null, scoreboard));
		assertThrows(InvalidFieldException.class, () -> matchService.modifyMatchScoreboard(uuid, null));
		assertThrows(InvalidFieldException.class, () -> matchService.modifyMatchScoreboard(uuid, scoreboard));

	}

}

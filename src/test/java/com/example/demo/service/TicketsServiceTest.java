package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.example.demo.entity.Match;
import com.example.demo.repository.MatchRepository;
import com.example.demo.service.exception.InvalidFieldException;

@SpringBootTest(properties = {

		"AWS_ACESSKEY=aws_acesskey", "AWS_BUCKET=aws_bucket", "AWS_SECRET=aws_secret",

		"JWT_SECRET=my-secret-key-for-tests",

		"aws.acesskey=${AWS_ACESSKEY}", "aws.secrety=${AWS_SECRET}", "aws.bucket=${AWS_BUCKET}",

		"api.security.token.secret=${JWT_SECRET:my-secret-key}",

})
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class TicketsServiceTest {

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@Mock
	private MatchRepository matchRepository;

	@InjectMocks
	private TicketsService ticketsService;

	@Test
	@DisplayName("must generate ticket")
	public void mustGenerateTicket() {

		// Arrange
		UUID idMatch = UUID.randomUUID();
		Match match = new Match(idMatch, LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID(), "", "", 0, 0, 0, 0);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(matchRepository.findById(any(UUID.class))).thenReturn(Optional.of(match));

		// Act
		String generatedTicket = ticketsService.buildAndSaveTicket(idMatch);

		// Assert
		assertNotNull(generatedTicket);
		assertNotEquals("", generatedTicket);

		// Verificar se o valor foi salvo no Redis
		verify(valueOperations).set(eq(generatedTicket), eq(idMatch.toString()), eq(Duration.ofSeconds(10L)));
	}

	@Test
	@DisplayName("must throw exception when idMatch is null")
	public void mustThrowExceptionWhenIdMatchIsNull() {
		assertThrows(InvalidFieldException.class, () -> ticketsService.buildAndSaveTicket(null));
	}

	@Test
	@DisplayName("must throw exception when match is not found")
	public void mustThrowExceptionWhenMatchIsNotFound() {

		UUID nnExistentIdMatch = UUID.randomUUID();
		when(matchRepository.findById(nnExistentIdMatch)).thenReturn(Optional.empty());

		assertThrows(InvalidFieldException.class, () -> ticketsService.buildAndSaveTicket(nnExistentIdMatch));

	}

}

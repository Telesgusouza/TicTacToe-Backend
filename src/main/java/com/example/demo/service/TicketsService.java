package com.example.demo.service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Match;
import com.example.demo.repository.MatchRepository;

@Service
public class TicketsService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private MatchRepository matchRepository;

	public String buildAndSaveTicket(UUID idMatch) {

		if (idMatch == null) {
			throw new RuntimeException("missing id match");
		}

		String ticket = UUID.randomUUID().toString();
		Optional<Match> match = matchRepository.findById(idMatch);
		String matchId = match.orElseThrow().getId().toString();

		redisTemplate.opsForValue().set(ticket, matchId, Duration.ofSeconds(10L));

		return ticket;

	}

	public Optional<String> getMatchByTicket(String ticket) {
		return Optional.ofNullable(redisTemplate.opsForValue().getAndDelete(ticket));
	}

}

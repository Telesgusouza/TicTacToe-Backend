package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ResponseUrlPhotoDTO;
import com.example.demo.entity.Match;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.event.MatchFoundEvent;
import com.example.demo.repository.MatchRepository;
import com.example.demo.repository.UserRepository;

@Service
public class MatchmakingService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private S3Service s3Service;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	private Queue<User> matchmakingQueue = new ConcurrentLinkedQueue<>();

	@Autowired
	private ApplicationContext applicationContext;

	public void startMatchmaking(User user) {

		user.setRole(UserRole.LOOKING_FOR_MATCH);
		userRepository.save(user);
		matchmakingQueue.offer(user);

	}

	@Scheduled(fixedRate = 5000) // Verifica a cada 5 segundos
	public void checkForMatch() {

		if (matchmakingQueue.size() >= 2) {
			User playerOne = matchmakingQueue.poll();
			User playerTwo = matchmakingQueue.poll();

			try {
				Match match = createMatch(playerOne, playerTwo);
				notifyPlayersOfMatch(playerOne, playerTwo, match.getId());
			} catch (Exception e) {
				// Se houver um erro, devolve os jogadores para a fila
				matchmakingQueue.offer(playerOne);
				matchmakingQueue.offer(playerTwo);
				System.err.println("Erro ao criar partida: " + e.getMessage());
			}
		}
	}

	// private void checkForMatch() {
//		if (matchmakingQueue.size() >= 2) {
//			User playerOne = matchmakingQueue.poll();
//			User playerTwo = matchmakingQueue.poll();
//
//			Match match = createMatch(playerOne, playerTwo);
//		}
//	}

	private Match createMatch(User playerOne, User playerTwo) {
		try {

			// photos players
			ResponseUrlPhotoDTO photoPlayerOne = this.s3Service.getPhoto(playerOne.getId());
			ResponseUrlPhotoDTO photoPlayerTwo = this.s3Service.getPhoto(playerTwo.getId());

			Match newMatch = new Match(null, LocalDateTime.now(),

					playerOne.getId(), playerTwo.getId(),

					photoPlayerOne.photo(), photoPlayerTwo.photo(),

					0, 0, 0, 0);

			return this.matchRepository.save(newMatch);
		} catch (Exception e) {
			throw new RuntimeException("error when creating the match");
		}
	}

	public void notifyPlayersOfMatch(User playerOne, User playerTwo, UUID matchId) {
		eventPublisher.publishEvent(new MatchFoundEvent(matchId, Arrays.asList(playerOne, playerTwo)));
	}
}

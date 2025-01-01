package com.example.demo.handler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.dto.RequestBoardDTO;
import com.example.demo.dto.ResponseWSBoardDTO;
import com.example.demo.entity.Board;
import com.example.demo.entity.Match;
import com.example.demo.enums.Player;
import com.example.demo.service.MatchService;
import com.example.demo.service.TicketsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

	private final TicketsService ticketsService;

	private Map<String, WebSocketSession> sessions;

	private Board board = new Board(null, Arrays.asList(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER),
			Arrays.asList(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER),
			Arrays.asList(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER));

	private Match match;

	@Autowired
	private MatchService matchService;

	private Player currentPlayer = Player.PLAYER_TWO;
	ObjectMapper mapper = new ObjectMapper();

	public WebSocketHandler(TicketsService ticketsService) {
		this.ticketsService = ticketsService;
		sessions = new ConcurrentHashMap<>();
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		System.out.println("[afterConnectionEstablished] session id " + session.getId());

		bringMatch(session);

		if (match.getIdPlayerOne().equals(match.getIdPlayerTwo())) {
			System.out.println("existe apenas um jogador");
			throw new RuntimeException("there is only one player");
		}

		Optional<String> ticket = ticketOf(session);

		if (ticket.isEmpty() || ticket.get().isBlank()) {
			close(session, CloseStatus.POLICY_VIOLATION);
			return;
		}

		Optional<String> matchId = ticketsService.getMatchByTicket(ticket.get());

		if (matchId.isEmpty()) {
			close(session, CloseStatus.POLICY_VIOLATION);
			return;
		}

		Optional<String> uriServer = Optional.ofNullable(session.getUri()).map(UriComponentsBuilder::fromUri)
				.map(UriComponentsBuilder::build).map(UriComponents::getQueryParams).map(it -> it.get("server"))
				.flatMap(it -> it.stream().findFirst()).map(String::trim);

		sessions.put(uriServer.toString(), session);

	}

	public WebSocketSession getSessionById(UUID userId) {
		return sessions.get(userId);
	}

	@SuppressWarnings("static-access")
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		String payload = (String) message.getPayload();

		if ("pong".equals(payload)) {

			ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

			Runnable task = () -> {
				try {
					session.sendMessage(new TextMessage("ping"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			};

			executorService.schedule(task, 4, TimeUnit.SECONDS);

			// Fechar o executorService após a execução da tarefa
			executorService.shutdown();
		}

		else if ("view board".equals(payload)) {

			ObjectMapper mapper = new ObjectMapper();

			ResponseWSBoardDTO data = new ResponseWSBoardDTO(board, currentPlayer);

			String boardString = mapper.writeValueAsString(data);
			session.sendMessage(new TextMessage(boardString));

		}

		else if ("reset board".equals(payload)) {

			board = new Board(null, Arrays.asList(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER),
					Arrays.asList(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER),
					Arrays.asList(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER));

			ResponseWSBoardDTO data = new ResponseWSBoardDTO(board, currentPlayer);
			broadcastUpdate(data);
			String resetBoard = mapper.writeValueAsString(data);

			session.sendMessage(new TextMessage(resetBoard));
			sessions.put(session.getId(), session);
		}

		else if ("close match".equals(payload)) {

			String msg = mapper.writeValueAsString("close match");

			sessions.values().removeIf(sessionValue -> {
				try {
					sessionValue.sendMessage(new TextMessage(msg));
					return false;
				} catch (IOException e) {
					System.err.println("Error sending update to session " + sessionValue.getId());
					return true;
				}
			});

			clearBoard();
			currentPlayer = Player.PLAYER_TWO;

			sessions.remove(session.getId());
			session.close();

		}

		else {

			RequestBoardDTO movement = mapper.readValue(payload, RequestBoardDTO.class);

			// vez do jogador errado
			if (currentPlayer != movement.player()) {
				System.out.println("não é sua vez de jogar");
				throw new RuntimeException("It's not this player's turn");
			}

			currentPlayer = switchPlayer(currentPlayer);

			String move = move(movement); // turnPlayer === currentPlayer

			session.sendMessage(new TextMessage(move));
			sessions.put(session.getId(), session);

		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);

		if (session.isOpen()) {

			String msg = mapper.writeValueAsString("match was ended");

			sessions.values().removeIf(sessionValue -> {
				try {
					sessionValue.sendMessage(new TextMessage(msg));
					return false;
				} catch (IOException e) {
					System.err.println("Error sending update to session " + sessionValue.getId());
					return true;
				}
			});

			session.sendMessage(new TextMessage(msg));
			sessions.put(session.getId(), session);
		}

		sessions.remove(session.getId());
		System.out.println("[afterConnectionClosed] session id " + session.getId());
		session.close();
	}

	@Scheduled(fixedRate = 60000) // Executa a cada minuto
	public void cleanInactiveSessions() {
		sessions.values().removeIf(session -> {
			try {
				session.isOpen();
				return false; // Sessão ativa, mantém
			} catch (RuntimeException e) {
				return true; // Sessão inativa, remove
			}
		});
	}

	private void broadcastUpdate(ResponseWSBoardDTO data) {
		String jsonData;

		try {
			jsonData = mapper.writeValueAsString(data);

			sessions.values().removeIf(session -> {
				try {
					session.sendMessage(new TextMessage(jsonData));
					return false;
				} catch (IOException e) {
					System.err.println("Error sending update to session " + session.getId());
					return true;
				}
			});

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String move(RequestBoardDTO movement) {

		try {
			List<Player> array;

			int column = movement.column();
			Player player = movement.player();

			switch (movement.row()) {
			case 0: {
				if (board.getRows_1().get(column) != Player.NO_PLAYER) {
					return mapper.writeValueAsString("field already filled in");
				}

				array = board.getRows_1();
				array.set(column, player);
				board.setRows_1(array);
				break;
			}
			case 1: {
				if (board.getRows_2().get(column) != Player.NO_PLAYER) {
					throw new RuntimeException("field already filled");
				}

				array = board.getRows_2();
				array.set(column, player);
				board.setRows_2(array);
				break;
			}
			case 2: {
				if (board.getRows_3().get(column) != Player.NO_PLAYER) {
					throw new RuntimeException("field already filled");
				}

				array = board.getRows_3();
				array.set(column, player);
				board.setRows_3(array);
				break;
			}
			default:
				throw new IllegalArgumentException("Valor inesperado: " + (movement.row() - 1));
			}

			ResponseWSBoardDTO data = new ResponseWSBoardDTO(board, currentPlayer);

			broadcastUpdate(data);

			// playerTurn
			return mapper.writeValueAsString(data);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	// OUTRAS FUNCOES

	private void clearBoard() {
		board = new Board(null, Arrays.asList(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER),
				Arrays.asList(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER),
				Arrays.asList(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER));
	}

	private void close(WebSocketSession session, CloseStatus status) {
		try {
			session.close(status);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Optional<String> ticketOf(WebSocketSession session) {

		return Optional.ofNullable(session.getUri()).map(UriComponentsBuilder::fromUri).map(UriComponentsBuilder::build)
				.map(UriComponents::getQueryParams).map(it -> it.get("ticket")).flatMap(it -> it.stream().findFirst())
				.map(String::trim);
	}

	private Optional<String> matchOf(WebSocketSession session) {
		return Optional.ofNullable(session.getUri())

				.map(UriComponentsBuilder::fromUri).map(UriComponentsBuilder::build).map(UriComponents::getQueryParams)

				.map(it -> it.get("server")).flatMap(it -> it.stream().findFirst()).map(String::trim);
	}

	private void bringMatch(WebSocketSession session) {

		Optional<String> uriMatch = matchOf(session);

		if (!uriMatch.isPresent()) {
			throw new RuntimeException("match not found");
		}

		String matchIdString = uriMatch.get();

		UUID matchId;
		try {
			matchId = UUID.fromString(matchIdString);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalid UUID format", e);
		}

		Match match = matchService.getMatch(matchId);

		this.match = match;

	}

	private Player switchPlayer(Player currentPlayer) {
		return currentPlayer == Player.PLAYER_ONE ? Player.PLAYER_TWO : Player.PLAYER_ONE;
	}

}
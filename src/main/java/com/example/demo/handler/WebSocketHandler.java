package com.example.demo.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.entity.Board;
import com.example.demo.enums.Player;
import com.example.demo.service.TicketsService;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

	private final TicketsService ticketsService;

	private Map<String, WebSocketSession> sessions;

	public WebSocketHandler(TicketsService ticketsService) {
		this.ticketsService = ticketsService;
		sessions = new ConcurrentHashMap<>();
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		System.out.println("[afterConnectionEstablished] session id " + session.getId());

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

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

		String payload = (String) message.getPayload();

		Board board = new Board(null, 
				List.of(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER),
				List.of(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER),
				List.of(Player.NO_PLAYER, Player.NO_PLAYER, Player.NO_PLAYER));

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

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		System.out.println("[afterConnectionClosed] session id " + session.getId());

		session.close();

	}

	private void close(WebSocketSession session, CloseStatus status) {
		try {
			session.close(status);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Optional<String> ticketOf(WebSocketSession session) {

		return Optional.ofNullable(session.getUri()).map(UriComponentsBuilder::fromUri).map(UriComponentsBuilder::build)
				.map(UriComponents::getQueryParams).map(it -> it.get("ticket")).flatMap(it -> it.stream().findFirst())
				.map(String::trim);

	}

}

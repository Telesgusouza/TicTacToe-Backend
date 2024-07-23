package com.example.demo.handler;

import java.io.IOException;
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

@Component
public class WebSocketHandler extends TextWebSocketHandler {

	private Map<String, WebSocketSession> sessions;

	public WebSocketHandler() {
		sessions = new ConcurrentHashMap<>();
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		System.out.println("[afterConnectionEstablished] session id " + session.getId());

		Optional<String> uriServer = Optional.ofNullable(session.getUri()).map(UriComponentsBuilder::fromUri)
				.map(UriComponentsBuilder::build).map(UriComponents::getQueryParams).map(it -> it.get("server"))
				.flatMap(it -> it.stream().findFirst()).map(String::trim);

		sessions.put(uriServer.toString(), session);

	}

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

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		System.out.println("[afterConnectionClosed] session id " + session.getId());

		session.close();

	}

}

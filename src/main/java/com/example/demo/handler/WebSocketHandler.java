package com.example.demo.handler;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		System.out.println("[afterConnectionEstablished] session id " + session.getId());

	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

		System.out.println("[handleMessage] session id " + message.getPayload());

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

			System.out.println("Enviado pong em resposta ao ping");
		}

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		System.out.println("[afterConnectionClosed] session id " + session.getId());

		session.close();

	}

}

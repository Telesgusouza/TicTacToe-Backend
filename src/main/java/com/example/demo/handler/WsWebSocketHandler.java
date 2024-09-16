package com.example.demo.handler;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.demo.entity.User;
import com.example.demo.event.MatchFoundEvent;

@Component
public class WsWebSocketHandler extends TextWebSocketHandler implements ApplicationListener<MatchFoundEvent> {

//	private Map<String, WebSocketSession> sessions;
	
	@Autowired
    private ApplicationContext applicationContext;

	private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

//	public void searchForMatch(User user) {
//	    // ... lógica de busca de match ...
//	    
//	    MatchFoundEvent event = new MatchFoundEvent(this, matchId, players);
//	    applicationContext.publishEvent(event); // Certifique-se de ter acesso ao ApplicationContext
//	}

	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("WebSocket connection established: " + session.getId());
		sessions.put(session.getId(), session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("WebSocket connection closed: " + session.getId());
		sessions.remove(session.getId());
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		System.out.println("Received message: " + message);
		// Processar a mensagem aqui
	}

	@Override
	public void onApplicationEvent(MatchFoundEvent event) {
		// Obter a sessão WebSocket aqui
//		WebSocketSession session = getWebSocketSession(event);
		WebSocketSession session = sessions.get(event.getPlayers().getFirst().getId().toString());

		System.out.println("==================================");
		System.out.println("CHEGA AQUI");

		if (session != null && session.isOpen()) {
			UUID matchId = event.getMatchId();
			List<User> players = event.getPlayers();

			System.out.println("primeiro IF (1° if)");

			for (User player : players) {
				System.out.println("primeiro FOR (1° for)");

				String payload = "match_found:" + matchId.toString() + ":" + player.getId().toString();
				try {
					session.sendMessage(new TextMessage(payload));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

//	private WebSocketSession getWebSocketSession(MatchFoundEvent event) {
	private WebSocketSession getWebSocketSession(User user) {
		// Implemente a logicagem necessária para obter a sessão WebSocket
		// Isso pode incluir buscar pelo usuário, pela partida, ou qualquer outra
		// identificação relevante
		// Por exemplo:

		// User user = event.getPlayer(); // Assumindo que o evento contém o jogador
//		User user = event.getPlayers().getFirst(); // Assumindo que o evento contém o jogador
//		return sessions.get(user.getId().toString());
		return sessions.get(user.getId().toString());
	}

}

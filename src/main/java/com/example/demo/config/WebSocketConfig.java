package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.demo.handler.WebSocketHandler;
import com.example.demo.handler.WsWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private WebSocketHandler webSocketHandler;
	
	@Autowired
	private WsWebSocketHandler wsWebSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		registry.addHandler(webSocketHandler, "/match").setAllowedOrigins("*");
		registry.addHandler(wsWebSocketHandler, "/ws").setAllowedOrigins("*");

	}

}

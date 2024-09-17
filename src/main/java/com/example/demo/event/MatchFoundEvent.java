package com.example.demo.event;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import com.example.demo.entity.User;

public class MatchFoundEvent extends ApplicationEvent {
	private static final long serialVersionUID = 157301338970648229L;
	
	private final UUID matchId;
	private final List<User> players;

	public MatchFoundEvent(UUID matchId, List<User> players) {
		super(players);
		this.matchId = matchId;
		this.players = players;
	}

	public UUID getMatchId() {
		return matchId;
	}

	public List<User> getPlayers() {
		return players;
	}

	@Override
	public String toString() {
		return "MatchFoundEvent [matchId=" + matchId + ", players=" + players + "]";
	}

}

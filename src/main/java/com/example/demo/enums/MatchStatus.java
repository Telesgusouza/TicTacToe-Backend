package com.example.demo.enums;

public enum MatchStatus {
	MATCH_STARTED("match started"), // partida iniciada
	MATCH_FINISHED("match finished"); // partida finalizada

	private String role;

	MatchStatus(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

}

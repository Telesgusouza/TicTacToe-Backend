package com.example.demo.enums;

public enum Player {
	NO_PLAYER("no player"),

	PLAYER_ONE("player one"), 
	PLAYER_TWO("player two");

	String role;

	Player(String role) {
		this.role = role;
	}

	public String getRole() {
		return this.role;
	}
}

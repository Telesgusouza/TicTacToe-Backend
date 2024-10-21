package com.example.demo.enums;

public enum UserRole {
	OUT_OF_START("out of start"), // fora de partida
	LOOKING_FOR_MATCH("looking for match"), // procurando partida
	ON_DEPARTURE("on departure"); // em partida

	private String role;

	UserRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return this.role;
	}
}

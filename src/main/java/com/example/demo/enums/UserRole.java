package com.example.demo.enums;


public enum UserRole {
	OUT_OF_START("out of start"), 
	LOOKING_FOR_MATCH("looking for match"), 
	ON_DEPARTURE("on departure");

	private String role;

	UserRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return this.role;
	}

}

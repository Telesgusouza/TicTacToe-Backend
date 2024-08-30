package com.example.demo.dto;

import com.example.demo.enums.Player;
import com.example.demo.enums.UserRole;

public record ResponseUser(
		String name, 
		String login, 
		UserRole role,
		Player player,

		Integer numberOfWins, 
		Integer numberOfDefeats, 
		Integer numberOfDraws

) {

}

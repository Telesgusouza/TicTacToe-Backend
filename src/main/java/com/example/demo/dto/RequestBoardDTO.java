package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.enums.Player;

/*
 
 id === id do jogador
 player == se é p1 ou p2
  
 */
public record RequestBoardDTO(Integer row, Integer column, UUID id, Player player) {

}

package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.enums.Player;

public record RequestBoardDTO(Integer row, Integer column, UUID id, Player player) {

}

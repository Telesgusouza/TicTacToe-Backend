package com.example.demo.dto;

import com.example.demo.entity.Board;
import com.example.demo.enums.Player;

public record ResponseWSBoardDTO(Board board, Player PlayerSTurn) {

}

package com.example.demo.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import com.example.demo.enums.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_board")
public class Board implements Serializable {
	private static final long serialVersionUID = 1338149900643370484L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private Player[] column;
	private Player[] rows;

	public Board() {
	}

	public Board(UUID id, Player[] column, Player[] rows) {
		super();
		this.id = id;
		this.column = column;
		this.rows = rows;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Player[] getColumn() {
		return column;
	}

	public void setColumn(Player[] column) {
		this.column = column;
	}

	public Player[] getRows() {
		return rows;
	}

	public void setRows(Player[] rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "Board [id=" + id + ", column=" + Arrays.toString(column) + ", rows=" + Arrays.toString(rows) + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Board other = (Board) obj;
		return Objects.equals(id, other.id);
	}

}

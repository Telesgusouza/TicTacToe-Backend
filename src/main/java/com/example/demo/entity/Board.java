package com.example.demo.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.example.demo.enums.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Board implements Serializable {
	private static final long serialVersionUID = 1338149900643370484L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

//	@Size(min = 3, max = 3)
	private List<Player> rows_1;
//	@Size(min = 3, max = 3)
	private List<Player> rows_2;
//	@Size(min = 3, max = 3)
	private List<Player> rows_3;

	public Board() {
	}

	public Board(UUID id, List<Player> rows_1, List<Player> rows_2, List<Player> rows_3) {
		super();
		this.id = id;
		this.rows_1 = rows_1;
		this.rows_2 = rows_2;
		this.rows_3 = rows_3;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public List<Player> getRows_1() {
		return rows_1;
	}

	public void setRows_1(List<Player> rows_1) {
		this.rows_1 = rows_1;
	}

	public List<Player> getRows_2() {
		return rows_2;
	}

	public void setRows_2(List<Player> rows_2) {
		this.rows_2 = rows_2;
	}

	public List<Player> getRows_3() {
		return rows_3;
	}

	public void setRows_3(List<Player> rows_3) {
		this.rows_3 = rows_3;
	}
	
	

	@Override
	public String toString() {
		return "Board [id=" + id + ", rows_1=" + rows_1 + ", rows_2=" + rows_2 + ", rows_3=" + rows_3 + "]";
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

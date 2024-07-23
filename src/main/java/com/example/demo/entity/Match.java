package com.example.demo.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.example.demo.enums.MatchStatus;
import com.example.demo.enums.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_match")
public class Match implements Serializable {
	private static final long serialVersionUID = -403386033926941713L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private Board board;

	private Player playerOne;
	private Player playerTwo;

	private Integer numberOfWinsPlayerOne;
	private Integer numberOfWinsPlayerTwo;

	private Integer numberOfMatches;

	private Player whoseTurnIsIt;

	private MatchStatus matchStatus;

	public Match() {
	}

	public Match(UUID id, Board board, Player playerOne, Player playerTwo, Integer numberOfWinsPlayerOne,
			Integer numberOfWinsPlayerTwo, Integer numberOfMatches, Player whoseTurnIsIt, MatchStatus matchStatus) {
		super();
		this.id = id;
		this.board = board;
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
		this.numberOfWinsPlayerOne = numberOfWinsPlayerOne;
		this.numberOfWinsPlayerTwo = numberOfWinsPlayerTwo;
		this.numberOfMatches = numberOfMatches;
		this.whoseTurnIsIt = whoseTurnIsIt;
		this.matchStatus = matchStatus;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Player getPlayerOne() {
		return playerOne;
	}

	public void setPlayerOne(Player playerOne) {
		this.playerOne = playerOne;
	}

	public Player getPlayerTwo() {
		return playerTwo;
	}

	public void setPlayerTwo(Player playerTwo) {
		this.playerTwo = playerTwo;
	}

	public Integer getNumberOfWinsPlayerOne() {
		return numberOfWinsPlayerOne;
	}

	public void setNumberOfWinsPlayerOne(Integer numberOfWinsPlayerOne) {
		this.numberOfWinsPlayerOne = numberOfWinsPlayerOne;
	}

	public Integer getNumberOfWinsPlayerTwo() {
		return numberOfWinsPlayerTwo;
	}

	public void setNumberOfWinsPlayerTwo(Integer numberOfWinsPlayerTwo) {
		this.numberOfWinsPlayerTwo = numberOfWinsPlayerTwo;
	}

	public Integer getNumberOfMatches() {
		return numberOfMatches;
	}

	public void setNumberOfMatches(Integer numberOfMatches) {
		this.numberOfMatches = numberOfMatches;
	}

	public Player getWhoseTurnIsIt() {
		return whoseTurnIsIt;
	}

	public void setWhoseTurnIsIt(Player whoseTurnIsIt) {
		this.whoseTurnIsIt = whoseTurnIsIt;
	}

	public MatchStatus getMatchStatus() {
		return matchStatus;
	}

	public void setMatchStatus(MatchStatus matchStatus) {
		this.matchStatus = matchStatus;
	}

	@Override
	public String toString() {
		return "Match [id=" + id + ", board=" + board + ", playerOne=" + playerOne + ", playerTwo=" + playerTwo
				+ ", numberOfWinsPlayerOne=" + numberOfWinsPlayerOne + ", numberOfWinsPlayerTwo="
				+ numberOfWinsPlayerTwo + ", numberOfMatches=" + numberOfMatches + ", whoseTurnIsIt=" + whoseTurnIsIt
				+ ", matchStatus=" + matchStatus + "]";
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
		Match other = (Match) obj;
		return Objects.equals(id, other.id);
	}

}

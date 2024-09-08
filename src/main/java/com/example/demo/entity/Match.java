package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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

	private LocalDateTime matchCreationDate;

	private UUID idPlayerOne;
	private UUID idPlayerTwo;

	private String photoPlayerOne;
	private String photoPlayerTwo;

	private Integer numberOfWinsPlayerOne;
	private Integer numberOfWinsPlayerTwo;

	private Integer numberOfDraws;

	private Integer numberOfMatches;

	public Match() {
	}

	public Match(UUID id, LocalDateTime matchCreationDate, UUID idPlayerOne, UUID idPlayerTwo, String photoPlayerOne,
			String photoPlayerTwo, Integer numberOfWinsPlayerOne, Integer numberOfWinsPlayerTwo, Integer numberOfDraws,
			Integer numberOfMatches) {
		super();
		this.id = id;
		
		this.matchCreationDate = matchCreationDate;
		
		this.idPlayerOne = idPlayerOne;
		this.idPlayerTwo = idPlayerTwo;
		
		this.photoPlayerOne = photoPlayerOne;
		this.photoPlayerTwo = photoPlayerTwo;
		
		this.numberOfWinsPlayerOne = numberOfWinsPlayerOne;
		this.numberOfWinsPlayerTwo = numberOfWinsPlayerTwo;
		this.numberOfDraws = numberOfDraws;
		this.numberOfMatches = numberOfMatches;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public LocalDateTime getMatchCreationDate() {
		return matchCreationDate;
	}

	public void setMatchCreationDate(LocalDateTime matchCreationDate) {
		this.matchCreationDate = matchCreationDate;
	}

	public UUID getIdPlayerOne() {
		return idPlayerOne;
	}

	public void setIdPlayerOne(UUID idPlayerOne) {
		this.idPlayerOne = idPlayerOne;
	}

	public UUID getIdPlayerTwo() {
		return idPlayerTwo;
	}

	public void setIdPlayerTwo(UUID idPlayerTwo) {
		this.idPlayerTwo = idPlayerTwo;
	}

	public String getPhotoPlayerOne() {
		return photoPlayerOne;
	}

	public void setPhotoPlayerOne(String photoPlayerOne) {
		this.photoPlayerOne = photoPlayerOne;
	}

	public String getPhotoPlayerTwo() {
		return photoPlayerTwo;
	}

	public void setPhotoPlayerTwo(String photoPlayerTwo) {
		this.photoPlayerTwo = photoPlayerTwo;
	}

	public Integer getNumberOfDraws() {
		return numberOfDraws == null ? 0 : numberOfDraws;
	}

	public void setNumberOfDraws(Integer numberOfDraws) {
		this.numberOfDraws = numberOfDraws;
	}

	public Integer getNumberOfWinsPlayerOne() {
		return numberOfWinsPlayerOne == null ? 0 : numberOfWinsPlayerOne;
	}

	public void setNumberOfWinsPlayerOne(Integer numberOfWinsPlayerOne) {
		this.numberOfWinsPlayerOne = numberOfWinsPlayerOne;
	}

	public Integer getNumberOfWinsPlayerTwo() {
		return numberOfWinsPlayerTwo == null ? 0 : numberOfWinsPlayerTwo;
	}

	public void setNumberOfWinsPlayerTwo(Integer numberOfWinsPlayerTwo) {
		this.numberOfWinsPlayerTwo = numberOfWinsPlayerTwo;
	}

	public Integer getNumberOfMatches() {
		return numberOfMatches == null ? 0 : numberOfMatches;
	}

	public void setNumberOfMatches(Integer numberOfMatches) {
		this.numberOfMatches = numberOfMatches;
	}

	@Override
	public String toString() {
		return "Match [id=" + id + ", matchCreationDate=" + matchCreationDate + ", idPlayerOne=" + idPlayerOne
				+ ", idPlayerTwo=" + idPlayerTwo + ", numberOfWinsPlayerOne=" + numberOfWinsPlayerOne
				+ ", numberOfWinsPlayerTwo=" + numberOfWinsPlayerTwo + ", numberOfDraws=" + numberOfDraws
				+ ", numberOfMatches=" + numberOfMatches + "]";
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

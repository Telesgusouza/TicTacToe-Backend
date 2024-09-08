package com.example.demo.entity;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_friend")
public class Friend {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private String name;
	private String img;
	private UUID idPlayer;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User player_friend;
	
	public Friend() {}

	public Friend(UUID id, String name, String img, UUID idPlayer) {
		super();
		this.id = id;
		this.name = name;
		this.img = img;
		this.idPlayer = idPlayer;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public UUID getIdPlayer() {
		return idPlayer;
	}

	public void setIdPlayer(UUID idPlayer) {
		this.idPlayer = idPlayer;
	}

	public void setPlayer_friend(User player_friend) {
		this.player_friend = player_friend;
	}

	@Override
	public String toString() {
		return "Friends [id=" + id + ", name=" + name + ", img=" + img + ", idPlayer=" + idPlayer + "]";
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
		Friend other = (Friend) obj;
		return Objects.equals(id, other.id);
	}

}

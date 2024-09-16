package com.example.demo.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.demo.enums.Player;
import com.example.demo.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

@Table(name = "tb_users")
@Entity(name = "tb_users")
@CrossOrigin(origins = "*")
public class User implements UserDetails {
	private static final long serialVersionUID = -2378536838878240518L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private String name;

	@Email
	@Column(nullable = false)
	private String login;

	@Column(nullable = false, unique = true)
	private String password;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Enumerated(EnumType.STRING)
	private Player player = Player.NO_PLAYER;

	private Integer numberOfWins;
	private Integer numberOfDefeats;
	private Integer numberOfDraws;

	@OneToMany(mappedBy = "player_friend", fetch = FetchType.EAGER)
	private List<Friend> friends = List.of();

	public User() {
	}

	public User(UUID id, String name, @Email String login, String password, UserRole role, Player player,
			Integer numberOfWins, Integer numberOfDefeats, Integer numberOfDraws) {
		super();
		this.id = id;

		this.name = name;
		this.login = login;
		this.password = password;

		this.role = role;
		this.player = player;

		this.numberOfWins = numberOfWins;
		this.numberOfDefeats = numberOfDefeats;
		this.numberOfDraws = numberOfDraws;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (this.role == UserRole.OUT_OF_START)
			return Arrays.asList(new SimpleGrantedAuthority("ROLE_OUT_OF_START"));

		else if (this.role == UserRole.LOOKING_FOR_MATCH)
			return Arrays.asList(new SimpleGrantedAuthority("ROLE_LOOKING_FOR_MATCH"));

		else if (this.role == UserRole.ON_DEPARTURE)
			return Arrays.asList(new SimpleGrantedAuthority("ROLE_ON_DEPARTURE"));

		return Arrays.asList(new SimpleGrantedAuthority("ROLE_OUT_OF_START"));
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.login;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getNumberOfWins() {
		return numberOfWins;
	}

	public void setNumberOfWins(Integer numberOfWins) {
		this.numberOfWins = numberOfWins;
	}

	public Integer getNumberOfDefeats() {
		return numberOfDefeats;
	}

	public void setNumberOfDefeats(Integer numberOfDefeats) {
		this.numberOfDefeats = numberOfDefeats;
	}

	public Integer getNumberOfDraws() {
		return numberOfDraws;
	}

	public void setNumberOfDraws(Integer numberOfDraws) {
		this.numberOfDraws = numberOfDraws;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Friend> getFriends() {
		return friends;
	}

	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", login=" + login + ", password=" + password + ", role=" + role
				+ ", player=" + player + ", numberOfWins=" + numberOfWins + ", numberOfDefeats=" + numberOfDefeats
				+ ", numberOfDraws=" + numberOfDraws + ", friends=" + friends + "]";
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
		User other = (User) obj;
		return Objects.equals(id, other.id);
	}
}

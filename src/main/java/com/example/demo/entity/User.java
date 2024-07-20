package com.example.demo.entity;

import java.util.UUID;

import com.example.demo.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;

public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Email
	@Column(nullable = false)
	private String login;

	@Column(nullable = false, unique = true)
	private String password;

	@Enumerated(EnumType.STRING)
	private UserRole role;

}

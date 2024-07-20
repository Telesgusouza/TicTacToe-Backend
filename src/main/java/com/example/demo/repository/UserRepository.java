package com.example.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

	UserDetails findByLogin(String login);

}

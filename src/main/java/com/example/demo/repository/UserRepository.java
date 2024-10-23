package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;

public interface UserRepository extends JpaRepository<User, UUID> {

	UserDetails findByLogin(String login);

	User findByRoleAndIdNot(UserRole role, UUID userId);
	
	List<User> findAllByRole(UserRole role);

}

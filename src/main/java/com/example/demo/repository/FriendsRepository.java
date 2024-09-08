package com.example.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Friend;

public interface FriendsRepository extends JpaRepository<Friend, UUID> {

}

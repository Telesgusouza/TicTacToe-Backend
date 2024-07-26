package com.example.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Match;

public interface MatchRepository extends JpaRepository<Match, UUID> {

}

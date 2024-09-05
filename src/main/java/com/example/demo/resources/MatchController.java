package com.example.demo.resources;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RequestMatchScoreboardDTO;
import com.example.demo.entity.Match;
import com.example.demo.entity.User;
import com.example.demo.service.MatchService;

@RestController
@RequestMapping("/api/v1/match")
@CrossOrigin(origins = "*")
public class MatchController {

	// aqui criaremos as partidas porém precisaremos de um ticket para ter acesso

	@Autowired
	private MatchService repo;

	@GetMapping("/{id}")
	public ResponseEntity<Match> getMatch(@PathVariable UUID id) {

		Match match = this.repo.getMatch(id);
		return ResponseEntity.ok().body(match);
	}

	@PostMapping
	public ResponseEntity<Match> postMatch(@AuthenticationPrincipal User user) {

		Match match = this.repo.createMatch(user);
		return ResponseEntity.ok().body(match);
	}

	@PostMapping("/{id}")
	public ResponseEntity<Match> modifyMatchScoreboard(@PathVariable UUID id,
			@RequestBody RequestMatchScoreboardDTO data) {

		Match match = this.repo.modifyMatchScoreboard(id, data);
		return ResponseEntity.ok().body(match);
	}

}

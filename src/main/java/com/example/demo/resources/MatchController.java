package com.example.demo.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Match;
import com.example.demo.entity.User;
import com.example.demo.service.MatchService;

@RestController
@RequestMapping(value = "/api/v1/match")
public class MatchController {

	@Autowired
	private MatchService repo = new MatchService();

	@PostMapping
	public ResponseEntity<Match> postMethodName(@AuthenticationPrincipal User user) {


		Match match = this.repo.creatingMatch(user);

		return ResponseEntity.ok().body(match);
	}

}

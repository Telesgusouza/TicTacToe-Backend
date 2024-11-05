package com.example.demo.resources;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RequestMatchScoreboardDTO;
import com.example.demo.entity.Match;
import com.example.demo.resources.exception.StandardError;
import com.example.demo.service.MatchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
		name = "Match",
		description = "operations to manipulate the matches"
		)
@RestController
@RequestMapping("/api/v1/match")
@CrossOrigin(origins = "*")
public class MatchController {

	@Autowired
	private MatchService repo;

	@Operation(
			summary = "get data match",
			description = "operation to bring match data",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Success bringing data match",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = Match.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "Error bringing data match",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = StandardError.class)))
			}
			)
	@GetMapping("/{id}")
	public ResponseEntity<Match> getMatch(@PathVariable UUID id) {

		Match match = this.repo.getMatch(id);
		return ResponseEntity.ok().body(match);
	}

	@Operation(
			summary = "modify match score",
			description = "Change the score of the match",
			responses = {
					
					@ApiResponse(
							responseCode = "200",
							description = "Success changes data on the scoreboard",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = Match.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "id is not null",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = StandardError.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "match not found ",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = StandardError.class))), 
					
					@ApiResponse(
							responseCode = "403",
							description = "user not found",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = StandardError.class))),
					
			}
			)
	@PostMapping("/{id}")
	public ResponseEntity<Match> modifyMatchScoreboard(@PathVariable UUID id,
			@RequestBody RequestMatchScoreboardDTO data) {

		Match match = this.repo.modifyMatchScoreboard(id, data);
		return ResponseEntity.ok().body(match);
	}

}





























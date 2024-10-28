package com.example.demo.resources;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ResponseUrlPhotoDTO;
import com.example.demo.dto.ResultResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.resources.exception.StandardError;
import com.example.demo.service.S3Service;
import com.example.demo.service.exception.AccountException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/file")
@CrossOrigin(origins = "*")
@Tag(name = "Files", description = "operations to manipulate the user's photo")
public class FileController {

	@Autowired
	private S3Service s3Service;

	@Operation(summary = "upload photo", description = "feature to upload the user's photo",

			responses = {

					@ApiResponse(
							responseCode = "201", 
							description = "Successful photo upload", 
							content = @Content(
									mediaType = "application/json", 
									schema = @Schema(
											implementation = ResultResponseDTO.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "File Error",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = ResultResponseDTO.class))),
					
					@ApiResponse(
							responseCode = "400",
							description = "account already exist",
							content = @Content(
										mediaType = "application/json",
										schema = @Schema(
												implementation = StandardError.class))),

			})
	@PostMapping
	public ResultResponseDTO upload(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal User user) {
		
		if (user == null) {
			throw new AccountException("account already exists");
		}

		return s3Service.upload(file, user.getId());
	}

	@DeleteMapping
	public ResultResponseDTO deleteFile(@AuthenticationPrincipal User user) {
		return s3Service.delete(user.getId());
	}

	@GetMapping("/{id}")
	public ResponseUrlPhotoDTO getPhoto(@PathVariable UUID id) {

		return s3Service.getPhoto(id);
	}

	@GetMapping
	public ResponseUrlPhotoDTO getPhoto(@AuthenticationPrincipal User user) {

		if (user == null) {
			throw new RuntimeException("Invalid token or unauthenticated user");
		}

		return s3Service.getPhoto(user.getId());
	}

}
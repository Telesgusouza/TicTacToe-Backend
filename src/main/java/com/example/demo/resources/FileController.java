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
import com.example.demo.service.S3Service;

@RestController
@RequestMapping("/api/v1/file")
@CrossOrigin(origins = "*")
public class FileController {

	@Autowired
	private S3Service s3Service;

	@PostMapping
	public ResultResponseDTO upload(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal User user) {

		if (user == null) {
			throw new RuntimeException("Invalid token or unauthenticated user");
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
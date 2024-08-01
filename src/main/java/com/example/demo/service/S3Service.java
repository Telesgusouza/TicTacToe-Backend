package com.example.demo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.example.demo.dto.MetadataResponseDTO;
import com.example.demo.dto.ResponseUrlPhotoDTO;
import com.example.demo.dto.ResultResponseDTO;

@Service
public class S3Service {

	@Value("${aws.bucket}")
	private String bucketName;

	private final AmazonS3 s3Client;

	public S3Service(AmazonS3 s3Client) {
		this.s3Client = s3Client;
	}

	public ResultResponseDTO upload(MultipartFile file, UUID filename) {

		String contentType = file.getContentType();
		if (contentType == null || !isImage(contentType)) {
			MetadataResponseDTO metadataResponse = new MetadataResponseDTO("400", "invalid field", "0");
			ResultResponseDTO response = new ResultResponseDTO(metadataResponse, null);

			throw new RuntimeException("invalid typé file");
		}

		if (file == null || file.isEmpty()) {
			MetadataResponseDTO metadadaResponse = new MetadataResponseDTO("400", "invalid field", "0");
			ResultResponseDTO response = new ResultResponseDTO(metadadaResponse, null);

			throw new RuntimeException("invalid field file");
		}

		try {
			File fileSave = convertMultiPartToFile(file);
			s3Client.putObject(bucketName, "" + filename, fileSave);

			MetadataResponseDTO metadaResponse = new MetadataResponseDTO("200", "file upload successfully in aws", "1");
			ResultResponseDTO response = new ResultResponseDTO(metadaResponse, filename);

			return response;

		} catch (Exception e) {
			MetadataResponseDTO metadataResponse = new MetadataResponseDTO("400", "failed to upload file in s3 bucket",
					"0");
			ResultResponseDTO response = new ResultResponseDTO(metadataResponse, null);

			return response;
		}
	}

	public boolean isImage(String contentType) {
		return contentType.equals(MediaType.IMAGE_JPEG_VALUE) || contentType.equals(MediaType.IMAGE_PNG_VALUE);
	}

	public ResultResponseDTO delete(UUID id) {
		try {

			s3Client.deleteObject(bucketName, "" + id);

			MetadataResponseDTO metadataResponse = new MetadataResponseDTO("200", "file delete successfully", "1");
			ResultResponseDTO response = new ResultResponseDTO(metadataResponse, "" + id);

			return response;

		} catch (Exception e) {
			MetadataResponseDTO metadataResponse = new MetadataResponseDTO("400", "failed to delete file", "0");
			ResultResponseDTO response = new ResultResponseDTO(metadataResponse, null);
			return response;
		}
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);

		fos.write(file.getBytes());
		fos.close();

		return convFile;
	}

	public ResponseUrlPhotoDTO getPhoto(UUID id) {
		try {

			return new ResponseUrlPhotoDTO("" + s3Client.getUrl(bucketName, "" + id));

		} catch (Exception e) {
			throw new RuntimeException("Error get photo", e);
		}
	}

}

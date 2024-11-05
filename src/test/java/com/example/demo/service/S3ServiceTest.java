package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.example.demo.dto.ResultResponseDTO;

import jakarta.annotation.PostConstruct;

// VOLTAR NESSE TESTE DEPOIS

@SpringBootTest(properties = { "AWS_ACESSKEY=aws_acesskey", "AWS_BUCKET=aws_bucket", "AWS_SECRET=aws_secret",

		"JWT_SECRET=my-secret-key-for-tests",

		"aws.acesskey=${AWS_ACESSKEY}", "aws.secrety=${AWS_SECRET}", "aws.bucket=${AWS_BUCKET}",

		"api.security.token.secret=${JWT_SECRET:my-secret-key}" })
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

	@Value("${aws.bucket}")
	private String bucketName;

	@Autowired
	private S3Service s3Service;

	@MockBean
	private AmazonS3 s3Client;

	@Mock
	private MultipartFile multipartFile;

	@PostConstruct
	public void init() {
		System.out.println("Bucket name initialized: " + bucketName);
	}

	@Test
	public void uploadFile_SuccessfulUpload() throws Exception {
		// Arrange
		UUID filename = UUID.randomUUID();
		File tempFile = File.createTempFile("test", ".jpg");
		InputStream inputStream = new FileInputStream(tempFile);

		when(multipartFile.getContentType()).thenReturn("image/jpeg");
		when(multipartFile.getInputStream()).thenReturn(inputStream);
		when(s3Client.putObject(anyString(), anyString(), any(File.class))).thenReturn(null);

		// Act
		try {
			ResultResponseDTO result = s3Service.upload(multipartFile, filename);

			// Assert
			assertNotNull(result);

			// Verificar se o método putObject foi chamado com os parâmetros corretos
			ArgumentCaptor<File> fileArgumentCaptor = ArgumentCaptor.forClass(File.class);
			verify(s3Client).putObject(eq("aws_bucket"), eq(filename.toString()), fileArgumentCaptor.capture());
			File capturedFile = fileArgumentCaptor.getValue();

			assertNotNull(capturedFile);
			assertTrue(capturedFile.exists());
			assertTrue(capturedFile.length() > 0);
		} catch (Throwable t) {
			System.out.println("Caught exception: " + t.getClass().getName() + ": " + t.getMessage());
			t.printStackTrace();
			fail("An unexpected exception was thrown: " + t.getMessage());
		}

	}

}

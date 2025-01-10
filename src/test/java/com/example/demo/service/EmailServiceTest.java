package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.thymeleaf.TemplateEngine;

import com.example.demo.dto.ResetPasswordDTO;
import com.example.demo.entity.Mail;
import com.example.demo.entity.User;
import com.example.demo.enums.Player;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.exception.InvalidFieldException;
import com.example.demo.service.exception.InvalidTokenException;
import com.example.demo.service.exception.TicketCreationException;
import com.example.demo.service.exception.TicketException;

@SpringBootTest(properties = {

		"AWS_ACESSKEY=aws_acesskey", "AWS_BUCKET=aws_bucket", "AWS_SECRET=aws_secret",

		"JWT_SECRET=my-secret-key-for-tests",

		"aws.acesskey=${AWS_ACESSKEY}", "aws.secrety=${AWS_SECRET}", "aws.bucket=${AWS_BUCKET}",

		"api.security.token.secret=${JWT_SECRET:my-secret-key}",

})
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

	@Mock
	private JavaMailSender mailSender;

	@Mock
	private TemplateEngine templateEngine;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private EmailService emailService;
	
	@Test
	@DisplayName("Email is null")
	public void EmailIsNull() {
		Mail mail = new Mail();
		mail.setTo(null);

		assertThrows(InvalidFieldException.class, () -> this.emailService.emailResetPassword(mail));
	}

	@Test
	@DisplayName("Error creating ticket")
	public void ErrorCreatingTicket() {
		String email = "test@gmail.com";

		User user = new User(UUID.randomUUID(), "test_name", email, "password_123", UserRole.OUT_OF_START,
				Player.PLAYER_ONE, 0, 0, 0);

		Mail mail = new Mail();
		mail.setTo(email);

		when(this.userRepository.findByLogin(email)).thenReturn(null);

		assertThrows(TicketCreationException.class, () -> this.emailService.emailResetPassword(mail));

	}
	
	@Test
	@DisplayName("verifyTicket() very short ticket")
	public void verifyTicket_VeryShortTicket() {
		assertThrows(TicketException.class, () -> this.emailService.verifyTicket("123"));
	}
	
	@Test
	@DisplayName("resetPassword() very short ticket")
	public void resetPassword_VeryShortTicket() {
		
		assertThrows(TicketException.class, () -> this.emailService.resetPassword(new ResetPasswordDTO("test@gmail.com", "12345", "password_123")));
	}
	
	@Test
	@DisplayName("resetPassword() wrong password passed")
	public void resetPassword_WrongPasswordPassed() {
		
		assertThrows(InvalidFieldException.class, () -> this.emailService.resetPassword(new ResetPasswordDTO("test@gmail.com", "123456", "12345")));
	}	
	
//	ATENÇÃO
//	Não foi possivel concluir os teste que utilizam redis
//	
//	ATTENTION
//	Unable to complete tests using redis
}

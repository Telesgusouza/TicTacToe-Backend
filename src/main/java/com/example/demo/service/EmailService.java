package com.example.demo.service;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.demo.dto.ResetPasswordDTO;
import com.example.demo.entity.Mail;
import com.example.demo.entity.User;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.exception.EmailException;
import com.example.demo.service.exception.InvalidFieldException;
import com.example.demo.service.exception.ResourceNotFoundException;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService implements EmailRepository {

	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private UserRepository userRepository;

	public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
		this.mailSender = mailSender;
		this.templateEngine = templateEngine;
	}

	@Async
	public void emailResetPassword(Mail mail) {

		try {

			Context context = new Context();
			String ticket = buildAndSaveTicket(mail.getTo());

			context.setVariable("ticket", ticket);
			String process = templateEngine.process("ResetPasswordUser", context);

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			try {

				helper.setSubject("Resetar senha");
				helper.setFrom("gustavo.teles711@gmail.com");
				helper.setText(process, true);

				helper.setTo(mail.getTo());

				mailSender.send(message);

			} catch (MessagingException e) {
				e.printStackTrace();
				throw new EmailException("failed to send email");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceNotFoundException("Unknown error sending email");
		}

	}

	// criaremos o ticket
	private String buildAndSaveTicket(String email) {
		if (email == null) {
			throw new InvalidFieldException("Email cannot be null");
		}

		try {
			// Obter o ticket anterior
			String oldTicket = redisTemplate.opsForValue().getAndDelete(email);

			// Gerar novo ticket
			String newTicket = generateRandomString(6);

			User user = (User) userRepository.findByLogin(email);
			String userLogin = user.getLogin();

			// Salvar novo ticket com tempo de expiração
			redisTemplate.opsForValue().set(newTicket, userLogin, Duration.ofHours(2));

			return newTicket;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new ResourceNotFoundException("Error creating or saving ticket");
		}
	}

	private static String generateRandomString(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder(length);
		Random random = new Random(System.nanoTime());

		for (int i = 0; i < length; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}

		return sb.toString();
	}

	// veremos o ticket
	public Optional<String> getViewTicket(String email) {

		return Optional.ofNullable(redisTemplate.opsForValue().get(email));
	}

	public void verifyTicket(String ticket) {

		if (ticket.length() <= 0) {
			throw new InvalidFieldException("token cannot be is null");
		}

		Optional<String> optionalTicket = Optional.ofNullable(redisTemplate.opsForValue().get(ticket));
		optionalTicket.orElseThrow(() -> new ResourceNotFoundException("invalid ticket"));
	}

	// deletaremos o ticket
	public Optional<String> getResetByTicket(String ticket) {
		return Optional.ofNullable(redisTemplate.opsForValue().getAndDelete(ticket));
	}

	public void resetPassword(ResetPasswordDTO data) {
		if (data.password().length() < 6) {
			throw new InvalidFieldException("Password too short");
		}

		if (data.password() == null) {
			throw new InvalidFieldException("Password cannot be null");
		}

		verifyTicket(data.ticket());

		try {
			String passwordEncript = new BCryptPasswordEncoder().encode(data.password());
			User user = (User) this.userRepository.findByLogin(data.email());

			user.setPassword(passwordEncript);
			this.userRepository.save(user);

			getResetByTicket(data.ticket());
		} catch (RuntimeException e) {
			e.printStackTrace();
			;
			new ResourceNotFoundException("An error occurred while saving user data");
		}

	}

}

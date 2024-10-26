package com.example.demo.config;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.entity.User;
import com.example.demo.service.exception.AccountException;

@Service
public class TokenService {

	@Value("${JWT_SECRET:my-secret-key}")
	private String secret;

	public String generateToken(User user) {

		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			String token = JWT.create().withIssuer("auth-api").withSubject(user.getLogin())
					.withExpiresAt(genExpirationDate()).sign(algorithm);
			return token;
		} catch (Exception e) {
			
			throw new AccountException("Error while generating token " + e);
		}
	}

	public String validateToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			return JWT.require(algorithm).withIssuer("auth-api").build().verify(token).getSubject();

		} catch (Exception e) {
			return "";
		}
	}

	private Instant genExpirationDate() {
		return LocalDateTime.now().plusMonths(3).toInstant(ZoneOffset.of("-03:00"));
	}

}

package com.example.demo.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demo.repository.UserRepository;
import com.example.demo.service.exception.InvalidTokenException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UserRepository userRepository;

	private final ResponseEntityExceptionHandler exceptionHandler;

	public SecurityFilter(ResponseEntityExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		var token = this.recoverToken(request);

		if (token != null) {
			try {
				var login = tokenService.validateToken(token);
				UserDetails user = userRepository.findByLogin(login);

				var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception e) {

				throw new InvalidTokenException("Error processing token");
			}
		}

		filterChain.doFilter(request, response);

	}

	private String recoverToken(HttpServletRequest request) {

		var authHeader = request.getHeader("Authorization");
		if (authHeader == null)
			return null;

		return authHeader.replace("Bearer ", "");
	}

}

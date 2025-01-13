package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.example.demo.config.TokenService;
import com.example.demo.dto.RequestAuthDTO;
import com.example.demo.dto.RequestRegisterDTO;
import com.example.demo.dto.ResponseTokenDTO;
import com.example.demo.entity.User;
import com.example.demo.enums.Player;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.exception.AccountException;
import com.example.demo.service.exception.InvalidFieldException;

@SpringBootTest(properties = {

		"AWS_ACESSKEY=aws_acesskey", "AWS_BUCKET=aws_bucket", "AWS_SECRET=aws_secret",

		"JWT_SECRET=my-secret-key-for-tests",

		"aws.acesskey=${AWS_ACESSKEY}", "aws.secrety=${AWS_SECRET}", "aws.bucket=${AWS_BUCKET}",

		"api.security.token.secret=${JWT_SECRET:my-secret-key}",

})
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserRepository userRepository;

	@Mock
	private TokenService tokenService;

	@InjectMocks
	private AuthorizationService authorizationService;

	@Autowired
	private AuthorizationService authService;

	@DisplayName("Must connect our user")
	public void mustLogIn() throws Exception {

		User user = new User(UUID.randomUUID(), "testName", "test@gmail.com", "password123", UserRole.OUT_OF_START,
				Player.PLAYER_ONE, 0, 0, 0);

		RequestAuthDTO userAuth = new RequestAuthDTO("test@gmail.com", "password123");
		var usernamePassword = new UsernamePasswordAuthenticationToken(userAuth.login(), userAuth.password());

		when(authenticationManager.authenticate(usernamePassword)).thenReturn(usernamePassword);
		when(tokenService.generateToken(user)).thenReturn("token_123");

		var auth = authService.login(userAuth);

		assertNotNull(auth);

	}

	@Test
	@DisplayName("Must throw exception on password, upon login")
	public void incorrectPasswordLogin() {
		User existingUser = new User(null, null, "django@gmail.com", "111111", null, null, null, null, null);

		RequestAuthDTO inputUser = new RequestAuthDTO("django@gmail.com", "111112");

		Mockito.when(userRepository.findByLogin(Mockito.eq("django@gmail.com"))).thenReturn(existingUser);

		assertThrows(InvalidFieldException.class, () -> authorizationService.login(inputUser));
	}

	@Test
	@DisplayName("Authentication fails")
	public void authenticationFailed() {

		RequestAuthDTO user = new RequestAuthDTO("django@gmail.com", "111112");
		Mockito.when(userRepository.findByLogin(Mockito.eq("django@gmail.com"))).thenReturn(null);

		assertThrows(AccountException.class, () -> authorizationService.login(user));
	}

	// register
	@Test
	@DisplayName("Success record")
	public void RegisterSuccess() throws Exception {
		RequestRegisterDTO request = new RequestRegisterDTO("test@example.com", "password123", "John Doe");

		User expectedUser = new User(null, "John Doe", "test@example.com", "encrypted_password", UserRole.OUT_OF_START,
				Player.NO_PLAYER, 0, 0, 0);

		String mockToken = "";
		when(userRepository.findByLogin(request.login())).thenReturn(null);
		when(userRepository.save(any(User.class))).thenReturn(expectedUser);
		when(tokenService.generateToken(any(User.class))).thenReturn(mockToken);

		ResponseTokenDTO result = authorizationService.register(request);

		assertNotNull(result);
		assertEquals(mockToken, result.token());

		verify(tokenService).generateToken(Mockito.eq(expectedUser));
	}

	@Test
	@DisplayName("account already exists in the database")
	public void accountAlreadyExists() {

		RequestRegisterDTO request = new RequestRegisterDTO("test@example.com", "password123", "John Doe");
		User expectedUser = new User(null, "John Doe", "test@example.com", "encrypted_password", UserRole.OUT_OF_START,
				Player.NO_PLAYER, 0, 0, 0);

		when(userRepository.findByLogin(request.login())).thenReturn(expectedUser);

		assertThrows(InvalidFieldException.class, () -> authorizationService.register(request));

	}

	@Test
	@DisplayName("Password too short")
	public void passwordTooShort() {

		RequestRegisterDTO request = new RequestRegisterDTO("test@gmail.com", "11111", "teste");

		when(userRepository.findByLogin(request.login())).thenReturn(null);

		assertThrows(InvalidFieldException.class, () -> authorizationService.register(request));
	}

	@Test
	@DisplayName("Password too long")
	public void passwordTooLong() {

		RequestRegisterDTO request = new RequestRegisterDTO("test@gmail.com",
				"1111111111111111111111111111111111111111111111111111", "teste");

		when(userRepository.findByLogin(request.login())).thenReturn(null);

		assertThrows(InvalidFieldException.class, () -> authorizationService.register(request));
	}

}

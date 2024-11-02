package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	private TokenService tokenService; // Adicionado mock para TokenService

	@InjectMocks
	private AuthorizationService authorizationService;

	@Autowired
	private AuthorizationService authService;

	@Test
	@DisplayName("Deve conectar nosso usuário")
	public void mustLogIn() throws Exception {

		RequestAuthDTO user = new RequestAuthDTO("django@gmail.com", "111111");
		var auth = authService.login(user);

		assertNotNull(user);

	}

	// anotar amanhã
	@Test
	@DisplayName("Deve lançar exceção sobre a senha, no login")
	public void incorrectPasswordLogin() {
		// Arrange
		User existingUser = new User(null, null, "django@gmail.com", "111111", null, null, null, null, null); // Senha
																												// correta
		RequestAuthDTO inputUser = new RequestAuthDTO("django@gmail.com", "111112");

		Mockito.when(userRepository.findByLogin(Mockito.eq("django@gmail.com"))).thenReturn(existingUser);

		// Act & Assert
		assertThrows(InvalidFieldException.class, () -> authorizationService.login(inputUser));
	}

	@Test
	@DisplayName("Authenticação falha")
	public void authenticationFailed() {

		RequestAuthDTO user = new RequestAuthDTO("django@gmail.com", "111112");
		Mockito.when(userRepository.findByLogin(Mockito.eq("django@gmail.com"))).thenReturn(null); // Simula usuário não
																									// encontrado
		// Act & Assert
		assertThrows(AccountException.class, () -> authorizationService.login(user));
	}

	// register
	@Test
	@DisplayName("Registro de sucesso")
	public void RegisterSuccess() throws Exception {
		// Arrange
		RequestRegisterDTO request = new RequestRegisterDTO("test@example.com", "password123", "John Doe");

		User expectedUser = new User(null, "John Doe", "test@example.com", "encrypted_password", UserRole.OUT_OF_START,
				Player.NO_PLAYER, 0, 0, 0);

		String mockToken = "";
		when(userRepository.findByLogin(request.login())).thenReturn(null);
		when(userRepository.save(any(User.class))).thenReturn(expectedUser);
		when(tokenService.generateToken(any(User.class))).thenReturn(mockToken);

		// Act
		ResponseTokenDTO result = authorizationService.register(request);

		// Assert
		assertNotNull(result);
		assertEquals(mockToken, result.token());

		verify(tokenService).generateToken(Mockito.eq(expectedUser));
	}

	@Test
	public void incorrectPasswordRegister() {

		RequestRegisterDTO request = new RequestRegisterDTO("test@example.com", "password123", "John Doe");
		User expectedUser = new User(null, "John Doe", "test@example.com", "encrypted_password", UserRole.OUT_OF_START,
				Player.NO_PLAYER, 0, 0, 0);
		String mockToken = "";

		when(userRepository.findByLogin(request.login())).thenReturn(expectedUser);

		assertThrows(InvalidFieldException.class, () -> authorizationService.register(request));

	}

}

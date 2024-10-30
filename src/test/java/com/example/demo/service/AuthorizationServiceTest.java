package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;

import com.example.demo.dto.RequestAuthDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.exception.AccountException;
import com.example.demo.service.exception.InvalidFieldException;

@SpringBootTest(properties = {

		"AWS_ACESSKEY=aws_acesskey", "AWS_BUCKET=aws_bucket", "AWS_SECRET=aws_secret",

		"JWT_SECRET=my-secret-key-for-tests",

		"aws.acesskey=${AWS_ACESSKEY}", "aws.secrety=${AWS_SECRET}", "aws.bucket=${AWS_BUCKET}",

})
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {
//
//	@Autowired
//	private AuthorizationService authorizationService;

	@Mock
	private AuthenticationManager authenticationManager;
	
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

//	@Mock
//	private UserRepository userRepository;

	@Test
	@DisplayName("Deve conectar nosso usuario")
	public void mustLogIn() {
		try {
			RequestAuthDTO user = new RequestAuthDTO("django@gmail.com", "111111");
			var auth = authorizationService.login(user);

			assertNotNull(auth);
			assertNotNull(auth.token());
		} catch (Exception e) {
			fail("Erro ao gerar token: " + e.getMessage());
		}
	}

//	@Test
//	@DisplayName("Deve lançar exceção sobre a senha")
//	public void incorrectPassword() {
//
//        RequestAuthDTO user = new RequestAuthDTO("django@gmail.com", "111111");
//        
//        Mockito.when(authorizationService.login(user))
//               .thenReturn(new RequestAuthDTO("django@gmail.com", "111112")); // Simula usuário não encontrado
//        
////		RequestAuthDTO user = new RequestAuthDTO("django@gmail.com", "111112");
//		assertThrows(InvalidFieldException.class, () -> authorizationService.login(user));
//
//	}
//	

    @Test
    @DisplayName("Deve lançar exceção sobre a senha")
    public void incorrectPassword() {
        // Arrange
        User existingUser = new User(null, null, "django@gmail.com", "111111", null, null, null, null, null); // Senha correta
    	RequestAuthDTO inputUser = new RequestAuthDTO("django@gmail.com", "111112");
    	
        Mockito.when(userRepository.findByLogin(Mockito.eq("django@gmail.com")))
               .thenReturn(existingUser);

        // Act & Assert
        assertThrows(InvalidFieldException.class, () -> authorizationService.login(inputUser));
    }
	
	@Test
	public void authenticationFailed() {

        RequestAuthDTO user = new RequestAuthDTO("django@gmail.com", "111112");
        Mockito.when(userRepository.findByLogin(Mockito.eq("django@gmail.com")))
               .thenReturn(null); // Simula usuário não encontrado

        // Act & Assert
        assertThrows(AccountException.class, () -> authorizationService.login(user));
	}
	
}
















package br.com.hyperativa.card.resource;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.hyperativa.card.exception.handler.CustomExceptionHandler;
import br.com.hyperativa.card.model.record.LoginRequest;
import br.com.hyperativa.card.service.SecurityService;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
	
	private MockMvc mockMvc;

	@Mock
	private SecurityService securityService;
	
    @InjectMocks
    private AuthController authController;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

    }
    
    @Test
    void testAuthenticateUser_Success() throws Exception {
        String username = "john";
        String password = "secret";
        String expectedToken = "mocked-jwt-token";

        LoginRequest loginRequest = new LoginRequest(username, password);

        when(securityService.login(username, password)).thenReturn(expectedToken);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }
    
    @Test
    void authenticateUser_withInvalidCredentials_returnsUnauthorized() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("invalidUser", "wrongPassword");

        when(securityService.login("invalidUser", "wrongPassword"))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }


	@Test
	void authenticateUser_withEmptyUsername_returnsBadRequest() throws Exception {
		
		String username = "";
		String password = "password";

		LoginRequest loginRequest = new LoginRequest(username, password);
		
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isBadRequest());
	}

	@Test
	void authenticateUser_withEmptyPassword_returnsBadRequest() throws Exception {
		
		String username = "username";
		String password = "";

		LoginRequest loginRequest = new LoginRequest(username, password);

		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isBadRequest());
	}


	@Test
	void authenticateUser_withMissingPassword_returnsBadRequest() throws Exception {
		
		String username = "";

		LoginRequest loginRequest = new LoginRequest(username, null);
		
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isBadRequest());
	}

}
package br.com.hyperativa.card.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import br.com.hyperativa.card.commons.Constants;
import br.com.hyperativa.card.model.dto.ApiResponse;
import br.com.hyperativa.card.security.JwtTokenProvider;
import br.com.hyperativa.card.service.impl.SecurityServiceImpl;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private SecurityServiceImpl securityService;

    @Test
    void loginSuccessfully() {
    	String username = "user";
        String password = "password";
        Authentication authentication = mock(Authentication.class);
        String jwt = "jwtToken";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(jwt);

        String result = securityService.login(username, password);

        assertEquals(jwt, result);
    }

    @Test
    void loginAuthenticationFails() {
        String username = "user";
        String password = "password";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Authentication failed"));

        assertThrows(RuntimeException.class, () -> securityService.login(username, password));
    }

    @Test
    void validateTokenSuccessfully() {
        String authHeader = "Bearer validToken";
        String token = "validToken";
        String username = "user";

        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getUsernameFromJWT(token)).thenReturn(username);

        ApiResponse result = securityService.validateToken(authHeader);

        assertTrue(result.getSuccess());
        assertEquals(Constants.TOKEN_VALID_FOR_USER + username, result.getMessage());
    }

    @Test
    void validateTokenMissingAuthHeader() {
        String authHeader = null;

        ApiResponse result = securityService.validateToken(authHeader);

        assertFalse(result.getSuccess());
        assertEquals(Constants.MISSING_AUTHORIZATION_HEADER, result.getMessage());
    }

    @Test
    void validateTokenInvalidOrExpired() {
        String authHeader = "Bearer invalidToken";
        String token = "invalidToken";

        when(tokenProvider.validateToken(token)).thenReturn(false);

        ApiResponse result = securityService.validateToken(authHeader);

        assertFalse(result.getSuccess());
        assertEquals(Constants.INVALID_OR_EXPIRED_TOKEN, result.getMessage());
    }
}
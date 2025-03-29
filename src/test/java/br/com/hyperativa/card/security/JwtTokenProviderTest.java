package br.com.hyperativa.card.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

@SpringBootTest(properties = {
	    "app.jwtSecret=1234567890123456789012345678901234567890123456789012345678901234",
	    "app.jwtExpirationInMs=60000"
	})
class JwtTokenProviderTest {
	

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @Test
    void generateTokenSuccessfully() {
        String username = "user";
        when(authentication.getName()).thenReturn(username);

        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
    }

    @Test
    void getUsernameFromJWTSuccessfully() {
        String username = "user";
        when(authentication.getName()).thenReturn(username);
        String token = jwtTokenProvider.generateToken(authentication);

        String extractedUsername = jwtTokenProvider.getUsernameFromJWT(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void validateTokenValid() {
        String username = "user";
        when(authentication.getName()).thenReturn(username);
        String token = jwtTokenProvider.generateToken(authentication);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateTokenInvalid() {
        String invalidToken = "invalidToken";

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

}

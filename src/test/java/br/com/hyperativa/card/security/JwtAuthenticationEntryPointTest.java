package br.com.hyperativa.card.security;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    void commenceUnauthorizedError() throws IOException, ServletException {
        String errorMessage = "Unauthorized error";
        when(authException.getMessage()).thenReturn(errorMessage);

        jwtAuthenticationEntryPoint.commence(request, response, authException);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
    }

    @Test
    void commenceNullExceptionMessage() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn(null);

        jwtAuthenticationEntryPoint.commence(request, response, authException);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, null);
    }
}
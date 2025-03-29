package br.com.hyperativa.card.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.hyperativa.card.service.impl.CustomUserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void doFilterInternalValidToken() throws ServletException, IOException {
    	String jwt = "validToken";
        String username = "user";
        UserDetails userDetails = mock(UserDetails.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(tokenProvider.validateToken(jwt)).thenReturn(true);
        when(tokenProvider.getUsernameFromJWT(jwt)).thenReturn(username);
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(tokenProvider).validateToken(jwt);
        verify(tokenProvider).getUsernameFromJWT(jwt);
        verify(customUserDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalInvalidToken() throws ServletException, IOException {
        String jwt = "invalidToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(tokenProvider.validateToken(jwt)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(tokenProvider).validateToken(jwt);
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    void doFilterInternalNoToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalException() throws ServletException, IOException {
        String jwt = "validToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(tokenProvider.validateToken(jwt)).thenThrow(new RuntimeException("Token validation error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}

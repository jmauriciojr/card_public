package br.com.hyperativa.card.service.impl;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.hyperativa.card.commons.Constants;
import br.com.hyperativa.card.model.dto.ApiResponse;
import br.com.hyperativa.card.security.JwtTokenProvider;
import br.com.hyperativa.card.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    public String login(String username, String password) {
        log.info("method=login, step=starting, username={}", username);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        String jwt = tokenProvider.generateToken(authentication);
        
        log.info("method=login, step=finished, username={}", username);
        return jwt;
    }
    
    public ApiResponse validateToken(String authHeader) {
        log.info("method=validateToken, step=starting");

        if (this.validateAuthHeader(authHeader)) {
	        log.error("method=validateToken, step=finished, message={}", Constants.MISSING_AUTHORIZATION_HEADER);
            return new ApiResponse(false, Constants.MISSING_AUTHORIZATION_HEADER);
        }
        
        String token = authHeader.substring(7);
        if (tokenProvider.validateToken(token)) {
            String username = this.getUsernameFromToken(token);
            log.info("method=validateToken, step=finished, username={}", username);

            return new ApiResponse(true, Constants.TOKEN_VALID_FOR_USER + username);
        } else {
	        log.error("method=validateToken, step=finished, message={}", Constants.INVALID_OR_EXPIRED_TOKEN);
	        
            return new ApiResponse(false, Constants.INVALID_OR_EXPIRED_TOKEN);
        }
    	
    }

	private boolean validateAuthHeader(String authHeader) {
		return (authHeader == null || !authHeader.startsWith(Constants.TOKEN_PREFIX));
	}
    
    private String getUsernameFromToken(String token) {
        return tokenProvider.getUsernameFromJWT(token);
    }
}
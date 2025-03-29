package br.com.hyperativa.card.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.hyperativa.card.model.dto.JwtAuthenticationResponse;
import br.com.hyperativa.card.model.record.LoginRequest;
import br.com.hyperativa.card.service.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SecurityService securityService;

	@Operation(summary = "Login user")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("method=authenticateUser, step=starting, username={}", loginRequest.username());

        String token = securityService.login(loginRequest.username(), loginRequest.password());
        
        log.info("method=authenticateUser, step=finishing, token={}", token);
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }
}
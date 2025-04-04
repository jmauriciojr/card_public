package br.com.hyperativa.card.model.dto;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {

	private String accessToken;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}

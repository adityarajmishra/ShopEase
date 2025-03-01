package com.shopease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for returning JWT authentication response.
 */
@Data
@AllArgsConstructor
public class JwtAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public JwtAuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
package com.trading.backend.authservice.dto.response;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    @JsonProperty("token")
    private String accessToken;
    
    @JsonProperty("refreshToken")
    private String refreshToken;
    
    @JsonProperty("tokenType")
    @Builder.Default
    private String tokenType = "Bearer";
    
    @JsonProperty("expiresIn")
    private long expiresIn;
    
    private UserInfo user;
}

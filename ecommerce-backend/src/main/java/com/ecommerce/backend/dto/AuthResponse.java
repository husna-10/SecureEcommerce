package com.ecommerce.backend.dto;

import com.ecommerce.backend.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication response with JWT token")
public class AuthResponse {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;

    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Token expiration time in milliseconds", example = "86400000")
    private Long expiresIn;

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "Username", example = "john_doe")
    private String username;

    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @Schema(description = "User's full name", example = "John Doe")
    private String fullName;

    @Schema(description = "User role", example = "USER")
    private Role role;

    @Schema(description = "Login timestamp", example = "2023-12-01T10:30:00")
    private LocalDateTime loginTime;
}
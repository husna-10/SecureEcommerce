package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.AuthLoginRequest;
import com.ecommerce.backend.dto.AuthResponse;
import com.ecommerce.backend.exception.CustomExceptions.*;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.security.CustomUserDetailsService;
import com.ecommerce.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse login(AuthLoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsernameOrEmail());

        try {
            // Find user first to check account status
            User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

            // Check if account is locked
            if (!user.isAccountNonLocked()) {
                log.warn("Login attempt for locked account: {}", user.getUsername());
                throw new AccountLockedException("Account is temporarily locked due to failed login attempts");
            }

            // Check if account is enabled
            if (!user.isEnabled()) {
                log.warn("Login attempt for disabled account: {}", user.getUsername());
                throw new DisabledException("Account is disabled");
            }

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Reset failed attempts on successful authentication
            user.resetFailedLoginAttempts();
            userRepository.save(user);

            // Generate tokens
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            log.info("Successful login for user: {}", user.getUsername());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationTime())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .loginTime(LocalDateTime.now())
                    .build();

        } catch (BadCredentialsException e) {
            // Handle failed authentication
            handleFailedLogin(request.getUsernameOrEmail());
            log.warn("Failed login attempt for user: {}", request.getUsernameOrEmail());
            throw new UnauthorizedException("Invalid credentials");
        } catch (LockedException e) {
            log.warn("Login attempt for locked account: {}", request.getUsernameOrEmail());
            throw new AccountLockedException("Account is temporarily locked");
        } catch (DisabledException e) {
            log.warn("Login attempt for disabled account: {}", request.getUsernameOrEmail());
            throw new UnauthorizedException("Account is disabled");
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        log.debug("Token refresh attempt");

        try {
            // Validate refresh token
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new InvalidTokenException("Invalid refresh token");
            }

            // Check if it's actually a refresh token
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                throw new InvalidTokenException("Token is not a refresh token");
            }

            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Generate new access token
            String newAccessToken = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            User user = (User) userDetails;

            log.info("Token refreshed for user: {}", user.getUsername());

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationTime())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .loginTime(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new InvalidTokenException("Failed to refresh token");
        }
    }

    @Transactional
    private void handleFailedLogin(String usernameOrEmail) {
        userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .ifPresent(user -> {
                    user.incrementFailedLoginAttempts();
                    userRepository.save(user);
                    
                    log.warn("Failed login attempt #{} for user: {}", 
                            user.getFailedLoginAttempts(), user.getUsername());
                });
    }

    public void logout(String token) {
        // In a production system, you'd maintain a blacklist of tokens
        // For now, we'll just log the logout
        try {
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                log.info("User logged out: {}", username);
            }
        } catch (Exception e) {
            log.debug("Logout with invalid token attempted");
        }
    }
}
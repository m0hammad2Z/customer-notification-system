package com.digitinarytask.customer.service.security;

import com.digitinarytask.customer.dto.security.AuthResponse;
import com.digitinarytask.customer.dto.security.LoginRequest;
import com.digitinarytask.customer.dto.security.RefreshTokenRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Service class for handling authentication-related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Authenticates a user and generates JWT tokens.
     *
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails user = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    /**
     * Refreshes the JWT access token using a refresh token.
     *
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String username = jwtService.extractUsername(request.getRefreshToken());
        UserDetails user = userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(request.getRefreshToken(), user)) {
            String accessToken = jwtService.generateToken(user);
            return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .build();
        }

        throw new RuntimeException("Invalid refresh token");
    }

    /**
     * Logs out the current user by clearing the security context.
     *
     */
    public void logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
    }
}

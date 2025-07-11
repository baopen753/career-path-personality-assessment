package org.swd392.quizzes.service;

import org.swd392.quizzes.dto.external.ApiResponse;
import org.swd392.quizzes.dto.external.IntrospectRequest;
import org.swd392.quizzes.dto.external.TokenValidationResponse;
import org.swd392.quizzes.dto.external.UserResponse;
import org.swd392.quizzes.repository.httpclient.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthServiceClient authServiceClient;

    /**
     * Validate JWT token and get user ID
     */
    public String extractUserIdFromToken(String authorizationHeader) {
        try {
            log.info("Validating token with auth service");

            // First validate the token using introspect
            String token = extractTokenFromHeader(authorizationHeader);
            IntrospectRequest introspectRequest = IntrospectRequest.builder()
                    .token(token)
                    .build();

            ApiResponse<TokenValidationResponse> validationResponse = authServiceClient.introspectToken(introspectRequest);
            TokenValidationResponse validation = validationResponse.getResult();

            if (validation == null || !validation.isValid()) {
                throw new RuntimeException("Invalid or expired token");
            }

            // If token is valid, get user info
            ApiResponse<UserResponse> userResponse = authServiceClient.getCurrentUser(authorizationHeader);
            UserResponse user = userResponse.getResult();

            if (user == null) {
                throw new RuntimeException("Auth service returned null user info");
            }

            log.info("Token validated successfully for user: {}", user.getId());
            return user.getId();
        } catch (Exception e) {
            log.error("Token validation failed", e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Get current user information from JWT token
     */
    public UserResponse getCurrentUser(String authorizationHeader) {
        try {
            log.info("Fetching current user info from auth service");
            ApiResponse<UserResponse> apiResponse = authServiceClient.getCurrentUser(authorizationHeader);

            UserResponse response = apiResponse.getResult();
            if (response == null) {
                throw new RuntimeException("Auth service returned null user info");
            }

            return response;
        } catch (Exception e) {
            log.error("Failed to get current user from auth service", e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Extract token from Authorization header by removing "Bearer " prefix
     */
    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header format");
        }
        return authorizationHeader.substring(7); // Remove "Bearer " prefix
    }
}


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
     * Validate JWT token and get user ID (returns as String for compatibility)
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
            // Convert Long to String for backward compatibility
            return user.getId().toString();
        } catch (Exception e) {
            log.error("Token validation failed", e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Extract Bearer token from Authorization header
     */
    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header format");
        }
        return authorizationHeader.substring(7); // Remove "Bearer " prefix
    }

    /**
     * Get current user information
     */
    public UserResponse getCurrentUser(String authorizationHeader) {
        try {
            ApiResponse<UserResponse> response = authServiceClient.getCurrentUser(authorizationHeader);
            return response.getResult();
        } catch (Exception e) {
            log.error("Failed to get current user", e);
            throw new RuntimeException("Failed to get user information: " + e.getMessage());
        }
    }

    /**
     * Alternative method: Extract user ID directly from X-User-Id header (set by API Gateway)
     * This is more efficient as it doesn't require calling user-service
     */
    public String extractUserIdFromHeaders(String authorizationHeader, String userIdHeader) {
        try {
            // First try to get from X-User-Id header (set by API Gateway)
            if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
                log.info("Using user ID from X-User-Id header: {}", userIdHeader);
                return userIdHeader;
            }

            // Fallback to token validation via user-service
            log.info("X-User-Id header not found, validating token with user-service");
            return extractUserIdFromToken(authorizationHeader);

        } catch (Exception e) {
            log.error("Failed to extract user ID from headers or token", e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
}

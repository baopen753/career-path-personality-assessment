package org.swd392.quizzes.repository.httpclient;

import org.swd392.quizzes.dto.external.*;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceClientFallback implements AuthServiceClient {
    @Override
    public ApiResponse<UserResponse> getUserByEmail(String email, String authorizationHeader) {
        return ApiResponse.<UserResponse>builder()
                .code(503)
                .message("Auth service is currently unavailable")
                .result(null)
                .build();
    }
}

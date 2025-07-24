package org.swd392.quizzes.repository.httpclient;

import org.swd392.quizzes.dto.external.*;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceClientFallback implements AuthServiceClient {
    @Override
    public ApiResponse<UserResponseDto> getUserByEmail(String email) {
        return ApiResponse.<UserResponseDto>builder()
                .code(503)
                .message("Auth service is currently unavailable")
                .result(null)
                .build();
    }
    @Override
    public ApiResponse<UserResponseDto> getUser(Long id) {
        return ApiResponse.<UserResponseDto>builder()
                .code(503)
                .message("Auth service is currently unavailable")
                .result(null)
                .build();
    }
}

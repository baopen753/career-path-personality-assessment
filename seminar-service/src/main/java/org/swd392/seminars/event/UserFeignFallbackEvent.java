package org.swd392.seminars.event;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.swd392.seminars.dto.ApiResponse;
import org.swd392.seminars.dto.UserInfoDto;
import org.swd392.seminars.service.client.UserFeignClient;

@Component
public class UserFeignFallbackEvent implements UserFeignClient {

    @Override
    public ResponseEntity<ApiResponse<UserInfoDto>> getUserDetails(Long id) {
        // Log the fallback or return a default response
        ApiResponse<UserInfoDto> fallbackResponse = ApiResponse.<UserInfoDto>builder()
                .code(503)
                .message("User service is unavailable. Fallback response triggered.")
                .result(null)
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }
}

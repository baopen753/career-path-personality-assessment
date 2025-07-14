package org.swd392.quizzes.repository.httpclient;

import org.swd392.quizzes.dto.external.ApiResponse;
import org.swd392.quizzes.dto.external.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user", fallback = AuthServiceClientFallback.class)
public interface AuthServiceClient {
    @GetMapping(value = "/users/by-email", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserResponse> getUserByEmail(@RequestParam("email") String email, @RequestHeader("Authorization") String authorizationHeader);
}

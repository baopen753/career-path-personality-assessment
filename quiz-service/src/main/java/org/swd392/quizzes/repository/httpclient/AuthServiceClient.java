package org.swd392.quizzes.repository.httpclient;

import org.swd392.quizzes.dto.external.ApiResponse;
import org.swd392.quizzes.dto.external.IntrospectRequest;
import org.swd392.quizzes.dto.external.TokenValidationResponse;
import org.swd392.quizzes.dto.external.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-service", fallback = AuthServiceClientFallback.class)
public interface AuthServiceClient {

    @GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserResponse> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader);

    @PostMapping(value = "/auth/introspect", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<TokenValidationResponse> introspectToken(@RequestBody IntrospectRequest request);

    @GetMapping(value = "/users/by-email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserResponse> getUserByEmail(@PathVariable("email") String email, @RequestHeader("Authorization") String authorizationHeader);
}

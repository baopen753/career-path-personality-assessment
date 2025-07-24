package org.swd392.quizzes.repository.httpclient;

import org.swd392.quizzes.dto.external.ApiResponse;
import org.swd392.quizzes.dto.external.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user")
public interface AuthServiceClient {
    @GetMapping(value = "/user/api/users/by-email", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserResponseDto> getUserByEmail(@RequestParam("email") String email);

    @GetMapping(value = "/user/api/users/userid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserResponseDto> getUser(@PathVariable("id") Long id);
}

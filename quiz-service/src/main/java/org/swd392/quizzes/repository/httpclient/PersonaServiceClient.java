package org.swd392.quizzes.repository.httpclient;

import org.swd392.quizzes.dto.external.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "persona-service")
public interface PersonaServiceClient {

    @GetMapping(value = "/profiles/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ProfileResponse getProfile(@PathVariable("userId") String userId);

    @PostMapping(value = "/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
    ProfileResponse createProfile(@RequestBody ProfileCreationRequest request);

    @PutMapping(value = "/profiles/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ProfileResponse updateProfile(@PathVariable("userId") String userId, @RequestBody ProfileUpdateRequest request);

    @PutMapping(value = "/profiles/{userId}/personality", produces = MediaType.APPLICATION_JSON_VALUE)
    ProfileResponse updatePersonality(@PathVariable("userId") String userId, @RequestBody PersonalityUpdateRequest request);
}

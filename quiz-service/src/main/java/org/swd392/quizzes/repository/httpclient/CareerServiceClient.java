package org.swd392.quizzes.repository.httpclient;

import org.swd392.quizzes.dto.external.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "career-service", fallback = CareerServiceClientFallback.class)
public interface CareerServiceClient {
    @GetMapping(value = "/careers/search", params = "careerNames", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<List<CareerRecommendationResponse>> searchCareersByName(@RequestParam("careerNames") List<String> careerNames);

    @GetMapping(value = "/careers/personality/{personalityType}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<List<CareerRecommendationResponse>> getCareersByPersonality(@PathVariable("personalityType") String personalityType);

    @GetMapping("/careers")
    ApiResponse<List<CareerRecommendationResponse>> getAllCareers();
}

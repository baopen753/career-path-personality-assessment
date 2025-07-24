package org.swd392.quizzes.repository.httpclient;

import org.swd392.quizzes.dto.external.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "career", fallback = CareerServiceClientFallback.class)
public interface CareerServiceClient {
    @GetMapping(value = "/career/careers/search", params = "careerNames", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<List<CareerRecommendationResponse>> searchCareersByName(@RequestParam("careerNames") List<String> careerNames);

    @GetMapping(value = "/career/careers/personality/{personalityType}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<List<CareerRecommendationResponse>> getCareersByPersonality(@PathVariable("personalityType") String personalityType);

    @GetMapping("/career/careers")
    ApiResponse<List<CareerRecommendationResponse>> getAllCareers();
}

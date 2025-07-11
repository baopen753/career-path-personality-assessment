package org.swd392.quizzes.repository.httpclient;

import org.swd392.quizzes.dto.external.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "university-service", fallback = UniversityServiceClientFallback.class)
public interface UniversityServiceClient {
    @GetMapping("/universities/by-programs")
    ApiResponse<List<UniversityRecommendationResponse>> getUniversitiesByPrograms(@RequestParam("programs") List<String> programs);

    @GetMapping("/universities/search")
    ApiResponse<List<UniversityRecommendationResponse>> searchUniversities(@RequestParam("major") String major);

    @GetMapping("/universities")
    ApiResponse<List<UniversityRecommendationResponse>> getAllUniversities();
}

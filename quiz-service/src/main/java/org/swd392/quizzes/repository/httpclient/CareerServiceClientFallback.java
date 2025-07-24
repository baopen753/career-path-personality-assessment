package org.swd392.quizzes.repository.httpclient;

import org.swd392.quizzes.dto.external.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CareerServiceClientFallback implements CareerServiceClient {

    @Override
    public ApiResponse<List<CareerRecommendationResponse>> searchCareersByName(List<String> careerNames) {
        return ApiResponse.<List<CareerRecommendationResponse>>builder()
                .code(503)
                .message("Career service is currently unavailable")
                .result(new ArrayList<>())
                .build();
    }

    @Override
    public ApiResponse<List<CareerRecommendationResponse>> getCareersByPersonality(String personalityType) {
        return ApiResponse.<List<CareerRecommendationResponse>>builder()
                .code(503)
                .message("Career service is currently unavailable")
                .result(new ArrayList<>())
                .build();
    }

    @Override
    public ApiResponse<List<CareerRecommendationResponse>> getAllCareers() {
        return ApiResponse.<List<CareerRecommendationResponse>>builder()
                .code(503)
                .message("Career service is currently unavailable")
                .result(new ArrayList<>())
                .build();
    }
}

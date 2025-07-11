package org.swd392.quizzes.service;

import org.swd392.quizzes.dto.PersonalityResultDTO;
import org.swd392.quizzes.dto.external.*;
import org.swd392.quizzes.entity.PersonalityStandard;
import org.swd392.quizzes.repository.PersonalityStandardRepository;
import org.swd392.quizzes.repository.httpclient.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MicroserviceIntegrationService {

    private final AuthServiceClient authServiceClient;
    private final CareerServiceClient careerServiceClient;
    private final UniversityServiceClient universityServiceClient;
    private final PersonalityStandardRepository personalityStandardRepository;

    /**
     * Complete personality result enrichment with career and university recommendations
     */
    public PersonalityResultDTO enrichPersonalityResult(PersonalityResultDTO result) {
        try {
            log.info("Enriching personality result with recommendations for type: {}", result.getPersonalityCode());

            // Get personality standard to access career_mapping_personality
            Optional<PersonalityStandard> standardOpt = personalityStandardRepository
                .findByPersonalityCode(result.getPersonalityCode());

            if (standardOpt.isPresent()) {
                PersonalityStandard standard = standardOpt.get();
                String careerMappings = standard.getCareerMappingPersonality();

                if (careerMappings != null && !careerMappings.isEmpty()) {
                    // Split career mappings and clean the data
                    List<String> mappedCareers = Arrays.stream(careerMappings.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                    log.info("Searching for careers matching: {}", mappedCareers);

                    // Get career recommendations based on mapped careers
                    ApiResponse<List<CareerRecommendationResponse>> careersResponse = careerServiceClient.searchCareersByName(mappedCareers);

                    if (careersResponse == null || careersResponse.getResult() == null || careersResponse.getResult().isEmpty()) {
                        log.warn("No careers found matching the personality mapping");
                        setDefaultRecommendations(result);
                        return result;
                    }

                    List<CareerRecommendationResponse> careers = careersResponse.getResult();

                    // Extract career names for matching with universities
                    List<String> careerNames = careers.stream()
                        .map(CareerRecommendationResponse::getName)
                        .collect(Collectors.toList());

                    log.info("Found {} matching careers, searching universities with majors: {}",
                            careers.size(), careerNames);

                    // Find universities that offer these careers as majors
                    ApiResponse<List<UniversityRecommendationResponse>> universitiesResponse = universityServiceClient
                        .getUniversitiesByPrograms(careerNames);

                    if (universitiesResponse == null || universitiesResponse.getResult() == null) {
                        log.warn("Failed to get university recommendations");
                        result.setUniversityRecommendations("University recommendations are currently unavailable");
                        return result;
                    }

                    List<UniversityRecommendationResponse> universities = universitiesResponse.getResult();

                    // Format recommendations for storage
                    String formattedCareerRecommendations = formatCareerRecommendations(careers);
                    String formattedUniversityRecommendations = formatUniversityRecommendations(universities);

                    // Update result with formatted recommendations
                    result.setCareerRecommendations(formattedCareerRecommendations);
                    result.setUniversityRecommendations(formattedUniversityRecommendations);

                    log.info("Successfully enriched personality result with {} careers and {} universities",
                            careers.size(), universities.size());
                } else {
                    log.warn("No career mappings found for personality type: {}", result.getPersonalityCode());
                    setDefaultRecommendations(result);
                }
            } else {
                log.warn("No personality standard found for type: {}", result.getPersonalityCode());
                setDefaultRecommendations(result);
            }

            return result;

        } catch (Exception e) {
            log.error("Failed to enrich personality result", e);
            setDefaultRecommendations(result);
            return result;
        }
    }

    private String formatCareerRecommendations(List<CareerRecommendationResponse> careers) {
        if (careers.isEmpty()) {
            return "No specific career recommendations available for this personality type.";
        }

        return careers.stream()
            .map(career -> String.format("Career: %s\n- Description: %s",
                career.getName(),
                career.getDescription()))
            .collect(Collectors.joining("\n\n"));
    }

    private String formatUniversityRecommendations(List<UniversityRecommendationResponse> universities) {
        if (universities.isEmpty()) {
            return "No specific university recommendations available for these career paths.";
        }

        return universities.stream()
            .map(uni -> String.format("University: %s\n- Location: %s\n- Major: %s\n- Contact: %s\n- Description: %s",
                uni.getName(),
                uni.getLocation(),
                uni.getMajor(),
                uni.getHotline(),
                uni.getDescription()))
            .collect(Collectors.joining("\n\n"));
    }

    private void setDefaultRecommendations(PersonalityResultDTO result) {
        result.setCareerRecommendations("Career recommendations are currently unavailable. Please consult with a career advisor.");
        result.setUniversityRecommendations("University recommendations are currently unavailable. Please check back later.");
    }

    /**
     * Get user information from auth service via JWT token
     */
    public UserResponse getCurrentUserInfo(String authorizationHeader) {
        try {
            log.info("Fetching user info from auth service");
            ApiResponse<UserResponse> apiResponse = authServiceClient.getCurrentUser(authorizationHeader);

            UserResponse response = apiResponse.getResult();
            if (response == null) {
                throw new RuntimeException("Auth service returned null user info");
            }

            return response;
        } catch (Exception e) {
            log.error("Failed to get user info from auth service", e);
            throw new RuntimeException("Failed to authenticate user: " + e.getMessage());
        }
    }
}

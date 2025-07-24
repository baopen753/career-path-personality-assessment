package org.swd392.quizzes.service.Imp;

import org.swd392.quizzes.dto.PersonalityResultDTO;
import org.swd392.quizzes.dto.external.*;
import org.swd392.quizzes.entity.PersonalityStandard;
import org.swd392.quizzes.repository.PersonalityStandardRepository;
import org.swd392.quizzes.repository.httpclient.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MicroserviceIntegrationServiceImp {

    private final CareerServiceClient careerServiceClient;
    private final UniversityServiceClient universityServiceClient;
    private final PersonalityStandardRepository personalityStandardRepository;

    public PersonalityResultDTO enrichPersonalityResult(PersonalityResultDTO result) {
        try {
            //get personality standard để acces vào career_mapping_personality
            Optional<PersonalityStandard> standardOpt = personalityStandardRepository
                    .findByPersonalityCode(result.getPersonalityCode());

            if (standardOpt.isPresent()) {
                PersonalityStandard standard = standardOpt.get();
                String careerMappings = standard.getCareerMappingPersonality();

                if (careerMappings != null && !careerMappings.isEmpty()) {
                    // Split career mappings và clean data
                    List<String> mappedCareers = Arrays.stream(careerMappings.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());

                    ApiResponse<List<CareerRecommendationResponse>> careersResponse =
                            careerServiceClient.searchCareersByName(mappedCareers);

                    if (careersResponse == null || careersResponse.getResult() == null || careersResponse.getResult().isEmpty()) {
                        log.warn("No careers found matching the personality mapping");
                        setDefaultRecommendations(result);
                        return result;
                    }

                    List<CareerRecommendationResponse> careers = careersResponse.getResult();

                    List<String> formattedCareers = formatCareerRecommendations(careers);
                    result.setCareerRecommendations(formattedCareers);

                    List<String> careerNames = careers.stream()
                            .map(CareerRecommendationResponse::getName)
                            .collect(Collectors.toList());

                    ApiResponse<List<UniversityRecommendationResponse>> universitiesResponse = universityServiceClient
                            .getUniversitiesByPrograms(careerNames);

                    if (universitiesResponse == null || universitiesResponse.getResult() == null) {
                        log.warn("Failed to get university recommendations");
                        result.setUniversityRecommendations(Collections.singletonList(
                                "University recommendations are currently unavailable"
                        ));
                        return result;
                    }

                    List<UniversityRecommendationResponse> universities = universitiesResponse.getResult();
                    List<String> formattedUniversities = formatUniversityRecommendations(universities);
                    result.setUniversityRecommendations(formattedUniversities);

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

    private List<String> formatCareerRecommendations(List<CareerRecommendationResponse> careers) {
        if (careers == null || careers.isEmpty()) {
            return Collections.singletonList("No specific career recommendations available for this personality type.");
        }

        return careers.stream()
                .map(career -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("Career: %s", career.getName())).append("\n");

                    if (career.getDescription() != null && !career.getDescription().isEmpty()) {
                        sb.append(String.format("- Description: %s", career.getDescription())).append("\n");
                    }

                    return sb.toString().trim();
                })
                .collect(Collectors.toList());
    }

    private List<String> formatUniversityRecommendations(List<UniversityRecommendationResponse> universities) {
        if (universities == null || universities.isEmpty()) {
            return Collections.singletonList("No specific university recommendations available for these career paths.");
        }

        return universities.stream()
                .map(uni -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("University: %s", uni.getName())).append("\n");

                    if (uni.getLocation() != null && !uni.getLocation().isEmpty()) {
                        sb.append(String.format("- Location: %s", uni.getLocation())).append("\n");
                    }

                    if (uni.getMajor() != null && !uni.getMajor().isEmpty()) {
                        sb.append(String.format("- Major: %s", uni.getMajor())).append("\n");
                    }

                    if (uni.getHotline() != null && !uni.getHotline().isEmpty()) {
                        sb.append(String.format("- Contact: %s", uni.getHotline())).append("\n");
                    }

                    if (uni.getDescription() != null && !uni.getDescription().isEmpty()) {
                        sb.append(String.format("- Description: %s", uni.getDescription())).append("\n");
                    }

                    return sb.toString().trim();
                })
                .collect(Collectors.toList());
    }

    private void setDefaultRecommendations(PersonalityResultDTO result) {
        result.setCareerRecommendations(Collections.singletonList(
                "Career: General Career Advice\n" +
                        "- Description: We couldn't find specific career recommendations for your personality type. " +
                        "Consider speaking with a career advisor for personalized guidance."
        ));

        result.setUniversityRecommendations(Collections.singletonList(
                "University: General University Information\n" +
                        "- Description: Specific university recommendations are not available at this time. " +
                        "Please check back later or contact university admissions offices for more information."
        ));
    }
}

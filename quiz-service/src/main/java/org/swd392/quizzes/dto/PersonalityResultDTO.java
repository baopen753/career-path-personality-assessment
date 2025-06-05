package org.swd392.quizzes.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalityResultDTO {
    private String personalityCode;
    private String nickname;
    private String keyTraits;
    private String description;
    private String careerRecommendations;
    private Map<String, Integer> scores; // dimension -> score
    private String universityRecommendations;
}


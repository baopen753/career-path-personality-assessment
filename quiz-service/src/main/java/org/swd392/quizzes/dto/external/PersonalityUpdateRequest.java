package org.swd392.quizzes.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalityUpdateRequest {
    private String personalityType;
    private String personalityDescription;
    private String keyTraits;
    private String careerRecommendations;
    private String universityRecommendations;
}

package org.swd392.quizzes.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramRecommendationResponse {
    private String id;
    private String name;
    private String degree;
    private String university;
    private String duration;
    private List<String> compatiblePersonalities;
    private String description;
}

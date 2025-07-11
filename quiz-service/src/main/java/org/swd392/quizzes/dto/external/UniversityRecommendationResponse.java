package org.swd392.quizzes.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniversityRecommendationResponse {
    private String id;
    private String name;
    private String major;
    private String hotline;
    private String location;
    private String description;
    private String picture;
}

package org.swd392.quizzes.dto;
import org.swd392.quizzes.entity.PersonalityStandard;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalityStandardDTO {
    private Long id;

    @NotNull(message = "Standard type is required")
    private PersonalityStandard.StandardType standard;

    @NotBlank(message = "Personality code is required")
    private String personalityCode;

    private String nickname;
    private String keyTraits;
    private String description;
    private String careerMappingPersonality;
}

package org.swd392.quizzes.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionRequestDTO {
    @NotBlank(message = "Question content is required")
    private String content;

    private Integer orderNumber;
    private String dimension;

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    @Valid
    private List<QuizOptionsRequestDTO> options;
}

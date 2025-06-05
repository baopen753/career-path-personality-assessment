package org.swd392.quizzes.dto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDTO {
    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Answers are required")
    private Map<Long, Long> answers; // questionId -> optionId
}


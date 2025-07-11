package org.swd392.quizzes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDTO {
    private Long id;
    private String resultType;
    private LocalDateTime timeSubmit;
    private Integer attemptOrder;
    private String resultJson;
    private Long quizId;
    private String userId; // Changed from Long to String to match auth-service UUID
    private Long personalityId;
}

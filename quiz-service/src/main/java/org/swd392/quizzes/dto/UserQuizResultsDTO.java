package org.swd392.quizzes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuizResultsDTO {
    private String userId;
    private String email;
    private int totalQuizzesTaken;
    private LocalDateTime firstQuizDate;
    private LocalDateTime lastQuizDate;
    private List<QuizResultSummaryDTO> quizResults;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuizResultSummaryDTO {
        private Long resultId;
        private Long quizId;
        private String quizTitle;
        private String resultType;
        private String personalityCode;
        private String personalityName;
        private String personalityDescription;
        private Integer attemptOrder;
        private LocalDateTime timeSubmit;
        private String resultJson;
    }
}

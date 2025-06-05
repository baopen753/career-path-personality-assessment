package org.swd392.quizzes.dto;

import org.swd392.quizzes.dto.QuizQuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDTO {
    private Long id;
    private String title;
    private Long categoryId;
    private String description;
    private Integer questionQuality;
    private String categoryName;
    private List<QuizQuestionDTO> questions;
}


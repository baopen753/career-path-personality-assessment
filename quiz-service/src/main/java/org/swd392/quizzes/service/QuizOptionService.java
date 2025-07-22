package org.swd392.quizzes.service;

import org.swd392.quizzes.dto.QuizOptionsDTO;
import org.swd392.quizzes.entity.QuizOptions;


import java.util.List;

public interface QuizOptionService {
    List<QuizOptionsDTO> getOptionsByQuestionId(Long questionId);
    List<QuizOptionsDTO> getOptionsByQuestionIds(List<Long> questionIds);
    QuizOptionsDTO getOptionById(Long id);
    List<QuizOptionsDTO> getOptionsByTargetTrait(String targetTrait);
    List<QuizOptionsDTO> getOptionsByScoreValue(QuizOptions.ScoreValue scoreValue);
    List<QuizOptionsDTO> getOptionsGroupedByTargetTrait(Long questionId, String targetTrait);
    QuizOptionsDTO createOption(QuizOptionsDTO optionDTO);
    List<QuizOptionsDTO> createOptions(List<QuizOptionsDTO> optionDTOs);
    QuizOptionsDTO updateOption(Long id, QuizOptionsDTO optionDTO);
    void deleteOptionsByQuestionId(Long questionId);
    long countOptionsByQuestionId(Long questionId);
    boolean optionExists(Long id);
}

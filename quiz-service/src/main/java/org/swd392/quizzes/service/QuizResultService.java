package org.swd392.quizzes.service;

import org.swd392.quizzes.dto.PersonalityResultDTO;
import org.swd392.quizzes.dto.QuizResultDTO;
import org.swd392.quizzes.dto.QuizSubmissionDTO;
import org.swd392.quizzes.dto.UserQuizResultsDTO;


public interface QuizResultService {
    PersonalityResultDTO submitQuizResultWithMicroservices(QuizSubmissionDTO submission, Long userId);
    UserQuizResultsDTO getMyQuizResults(Long userId);
    UserQuizResultsDTO getUserResultByEmail(String email, Long parentId);
    QuizResultDTO getResultById(Long resultId);
}

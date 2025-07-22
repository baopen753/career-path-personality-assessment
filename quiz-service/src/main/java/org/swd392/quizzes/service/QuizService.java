package org.swd392.quizzes.service;

import org.swd392.quizzes.dto.QuizDTO;
import org.swd392.quizzes.dto.QuizRequestDTO;

import java.util.List;

public interface QuizService {
    List<QuizDTO> getAllQuiz();
    QuizDTO getQuizById(Long id);
    QuizDTO getQuizWithQuestions(Long id);
    List<QuizDTO> getQuizByCategory(Long categoryId);
    QuizDTO updateQuiz(Long id, QuizRequestDTO quizRequestDTO);
}

package org.swd392.quizzes.service;

import org.swd392.quizzes.dto.QuizDTO;
import org.swd392.quizzes.dto.QuizRequestDTO;
import org.swd392.quizzes.entity.Category;
import org.swd392.quizzes.entity.Quiz;
import org.swd392.quizzes.exception.CategoryNotFoundException;
import org.swd392.quizzes.exception.QuizNotFoundException;
import org.swd392.quizzes.repository.CategoryRepository;
import org.swd392.quizzes.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<QuizDTO> getAllQuiz() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));
        return convertToDTO(quiz);
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizWithQuestions(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));
        QuizDTO quizDTO = convertToDTO(quiz);
        // Questions will be loaded through the relationship
        return quizDTO;
    }

    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        List<Quiz> quizzes = quizRepository.findByCategoryId(categoryId);
        return quizzes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public QuizDTO createQuiz(QuizRequestDTO quizRequestDTO) {
        Category category = categoryRepository.findById(quizRequestDTO.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(quizRequestDTO.getCategoryId()));

        Quiz quiz = Quiz.builder()
                .title(quizRequestDTO.getTitle())
                .category(category)
                .description(quizRequestDTO.getDescription())
                .questionQuality(quizRequestDTO.getQuestionQuality())
                .build();

        Quiz savedQuiz = quizRepository.save(quiz);
        return convertToDTO(savedQuiz);
    }

    public QuizDTO updateQuiz(Long id, QuizRequestDTO quizRequestDTO) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        Category category = categoryRepository.findById(quizRequestDTO.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(quizRequestDTO.getCategoryId()));

        existingQuiz.setTitle(quizRequestDTO.getTitle());
        existingQuiz.setCategory(category);
        existingQuiz.setDescription(quizRequestDTO.getDescription());
        existingQuiz.setQuestionQuality(quizRequestDTO.getQuestionQuality());

        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        return convertToDTO(updatedQuiz);
    }

    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new QuizNotFoundException(id);
        }
        quizRepository.deleteById(id);
    }

    private QuizDTO convertToDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .categoryId(quiz.getCategory().getId())
                .categoryName(quiz.getCategory().getName())
                .description(quiz.getDescription())
                .questionQuality(quiz.getQuestionQuality())
                .build();
    }
}


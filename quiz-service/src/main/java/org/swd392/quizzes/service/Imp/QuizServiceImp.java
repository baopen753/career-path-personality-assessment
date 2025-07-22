package org.swd392.quizzes.service.Imp;

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
import org.swd392.quizzes.service.QuizService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImp implements QuizService {

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

    public QuizDTO updateQuiz(Long id, QuizRequestDTO quizRequestDTO) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        Category category = categoryRepository.findById(quizRequestDTO.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(quizRequestDTO.getCategoryId()));

        existingQuiz.setTitle(quizRequestDTO.getTitle());
        existingQuiz.setCategory(category);
        existingQuiz.setDescription(quizRequestDTO.getDescription());
        existingQuiz.setQuestionQuantity(quizRequestDTO.getQuestionQuantity());

        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        return convertToDTO(updatedQuiz);
    }

    private QuizDTO convertToDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .categoryId(quiz.getCategory().getId())
                .categoryName(quiz.getCategory().getName())
                .description(quiz.getDescription())
                .questionQuantity(quiz.getQuestionQuantity())
                .build();
    }
}


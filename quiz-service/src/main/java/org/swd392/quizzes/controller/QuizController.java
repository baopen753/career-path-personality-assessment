package org.swd392.quizzes.controller;

import org.swd392.quizzes.dto.QuizDTO;
import org.swd392.quizzes.dto.QuizRequestDTO;
import org.swd392.quizzes.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<List<QuizDTO>> getAllQuiz() {
        return executeServiceCall(() -> {
            List<QuizDTO> quizzes = quizService.getAllQuiz();
            return ResponseEntity.ok(quizzes);
        });
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuizById(
            @PathVariable Long id) {
        return executeServiceCall(() -> {
            QuizDTO quiz = quizService.getQuizById(id);
            return ResponseEntity.ok(quiz);
        });
    }

    @GetMapping("/{id}/with-questions")
    public ResponseEntity<QuizDTO> getQuizWithQuestions(
            @PathVariable Long id) {
        return executeServiceCall(() -> {
            QuizDTO quiz = quizService.getQuizWithQuestions(id);
            return ResponseEntity.ok(quiz);
        });
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<QuizDTO>> getQuizByCategory(
            @PathVariable Long categoryId) {
        return executeServiceCall(() -> {
            List<QuizDTO> quizzes = quizService.getQuizByCategory(categoryId);
            return ResponseEntity.ok(quizzes);
        });
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizDTO> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizRequestDTO quizRequestDTO) {
        return executeServiceCall(() -> {
            QuizDTO updatedQuiz = quizService.updateQuiz(id, quizRequestDTO);
            return ResponseEntity.ok(updatedQuiz);
        });
    }

    private <T> T executeServiceCall(java.util.function.Supplier<T> serviceCall) {
        return serviceCall.get();
    }
}
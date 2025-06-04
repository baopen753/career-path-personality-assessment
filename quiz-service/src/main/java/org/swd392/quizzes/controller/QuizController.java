package org.swd392.quizzes.controller;


import org.swd392.quizzes.dto.QuizDTO;
import org.swd392.quizzes.dto.QuizRequestDTO;
import org.swd392.quizzes.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Tag(name = "Quiz Management", description = "API for managing quizzes")
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    @Operation(summary = "Get all quizzes", description = "Retrieve all available quizzes")
    public ResponseEntity<List<QuizDTO>> getAllQuiz() {
        return executeServiceCall(() -> {
            List<QuizDTO> quizzes = quizService.getAllQuiz();
            return ResponseEntity.ok(quizzes);
        });
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID", description = "Retrieve a specific quiz by its ID")
    public ResponseEntity<QuizDTO> getQuizById(
            @Parameter(description = "Quiz ID") @PathVariable Long id) {
        return executeServiceCall(() -> {
            QuizDTO quiz = quizService.getQuizById(id);
            return ResponseEntity.ok(quiz);
        });
    }

    @GetMapping("/{id}/with-questions")
    @Operation(summary = "Get quiz with questions", description = "Retrieve a quiz with all its questions and options")
    public ResponseEntity<QuizDTO> getQuizWithQuestions(
            @Parameter(description = "Quiz ID") @PathVariable Long id) {
        return executeServiceCall(() -> {
            QuizDTO quiz = quizService.getQuizWithQuestions(id);
            return ResponseEntity.ok(quiz);
        });
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get quizzes by category", description = "Retrieve all quizzes for a specific category")
    public ResponseEntity<List<QuizDTO>> getQuizByCategory(
            @Parameter(description = "Category ID") @PathVariable Long categoryId) {
        return executeServiceCall(() -> {
            List<QuizDTO> quizzes = quizService.getQuizByCategory(categoryId);
            return ResponseEntity.ok(quizzes);
        });
    }

    @PostMapping
    @Operation(summary = "Create new quiz", description = "Create a new quiz")
    public ResponseEntity<QuizDTO> createQuiz(@Valid @RequestBody QuizRequestDTO quizRequestDTO) {
        return executeServiceCall(() -> {
            QuizDTO createdQuiz = quizService.createQuiz(quizRequestDTO);
            return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
        });
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update quiz", description = "Update an existing quiz")
    public ResponseEntity<QuizDTO> updateQuiz(
            @Parameter(description = "Quiz ID") @PathVariable Long id,
            @Valid @RequestBody QuizRequestDTO quizRequestDTO) {
        return executeServiceCall(() -> {
            QuizDTO updatedQuiz = quizService.updateQuiz(id, quizRequestDTO);
            return ResponseEntity.ok(updatedQuiz);
        });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quiz", description = "Delete a quiz by ID")
    public ResponseEntity<Void> deleteQuiz(
            @Parameter(description = "Quiz ID") @PathVariable Long id) {
        return executeServiceCall(() -> {
            quizService.deleteQuiz(id);
            return ResponseEntity.noContent().build();
        });
    }

    private <T> T executeServiceCall(java.util.function.Supplier<T> serviceCall) {
        return serviceCall.get();
    }
}
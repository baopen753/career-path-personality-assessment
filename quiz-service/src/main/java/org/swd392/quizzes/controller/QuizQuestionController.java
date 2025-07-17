package org.swd392.quizzes.controller;

import org.swd392.quizzes.dto.QuizQuestionDTO;
import org.swd392.quizzes.service.QuizQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz-questions")
@RequiredArgsConstructor
@Slf4j
public class QuizQuestionController {

    private final QuizQuestionService quizQuestionService;

    //Get all questions for a specific quiz
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizQuestionDTO>> getQuestionsByQuizId(@PathVariable Long quizId) {
        log.info("Fetching questions for quiz ID: {}", quizId);

        List<QuizQuestionDTO> questions = quizQuestionService.getQuestionsByQuizId(quizId);
        return ResponseEntity.ok(questions);
    }

    //Get a specific question by ID
    @GetMapping("/{id}")
    public ResponseEntity<QuizQuestionDTO> getQuestionById(@PathVariable Long id) {
        log.info("Fetching question with ID: {}", id);

        QuizQuestionDTO question = quizQuestionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    //Get questions by dimension
    @GetMapping("/dimension/{dimension}")
    public ResponseEntity<List<QuizQuestionDTO>> getQuestionsByDimension(@PathVariable String dimension) {
        log.info("Fetching questions for dimension: {}", dimension);

        List<QuizQuestionDTO> questions = quizQuestionService.getQuestionsByDimension(dimension);
        return ResponseEntity.ok(questions);
    }

    //Create a new quiz question (Admin only)
    @PostMapping
    public ResponseEntity<QuizQuestionDTO> createQuestion(@Valid @RequestBody QuizQuestionDTO questionDTO) {
        log.info("Creating new question for quiz ID: {}", questionDTO.getQuizId());

        QuizQuestionDTO createdQuestion = quizQuestionService.createQuestion(questionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }

    //Update an existing quiz question (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<QuizQuestionDTO> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuizQuestionDTO questionDTO) {
        log.info("Updating question with ID: {}", id);

        QuizQuestionDTO updatedQuestion = quizQuestionService.updateQuestion(id, questionDTO);
        return ResponseEntity.ok(updatedQuestion);
    }

    //Delete a quiz question (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        log.info("Deleting question with ID: {}", id);

        quizQuestionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    //Get questions by multiple dimensions (for analytics)
    @GetMapping("/dimensions")
    public ResponseEntity<List<QuizQuestionDTO>> getQuestionsByDimensions(
            @RequestParam List<String> dimensions) {
        log.info("Fetching questions for dimensions: {}", dimensions);

        List<QuizQuestionDTO> questions = dimensions.stream()
                .flatMap(dimension -> quizQuestionService.getQuestionsByDimension(dimension).stream())
                .distinct()
                .toList();

        return ResponseEntity.ok(questions);
    }

    //Exception handler for this controller
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error in QuizQuestionController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing the request: " + e.getMessage());
    }
}
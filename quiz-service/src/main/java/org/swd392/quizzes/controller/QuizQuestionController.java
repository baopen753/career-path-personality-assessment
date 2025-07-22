package org.swd392.quizzes.controller;

import org.swd392.quizzes.dto.QuizQuestionDTO;
import org.swd392.quizzes.service.Imp.QuizQuestionServiceImp;
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

    private final QuizQuestionServiceImp quizQuestionService;

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizQuestionDTO>> getQuestionsByQuizId(@PathVariable Long quizId) {
        log.info("Fetching questions for quiz ID: {}", quizId);

        List<QuizQuestionDTO> questions = quizQuestionService.getQuestionsByQuizId(quizId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizQuestionDTO> getQuestionById(@PathVariable Long id) {
        log.info("Fetching question with ID: {}", id);

        QuizQuestionDTO question = quizQuestionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @GetMapping("/dimension/{dimension}")
    public ResponseEntity<List<QuizQuestionDTO>> getQuestionsByDimension(@PathVariable String dimension) {
        log.info("Fetching questions for dimension: {}", dimension);

        List<QuizQuestionDTO> questions = quizQuestionService.getQuestionsByDimension(dimension);
        return ResponseEntity.ok(questions);
    }

    @PostMapping
    public ResponseEntity<QuizQuestionDTO> createQuestion(@Valid @RequestBody QuizQuestionDTO questionDTO) {
        log.info("Creating new question for quiz ID: {}", questionDTO.getQuizId());

        QuizQuestionDTO createdQuestion = quizQuestionService.createQuestion(questionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizQuestionDTO> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuizQuestionDTO questionDTO) {
        log.info("Updating question with ID: {}", id);

        QuizQuestionDTO updatedQuestion = quizQuestionService.updateQuestion(id, questionDTO);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        log.info("Deleting question with ID: {}", id);

        quizQuestionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error in QuizQuestionController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing the request: " + e.getMessage());
    }
}
package org.swd392.quizzes.controller;

import org.swd392.quizzes.dto.QuizQuestionDTO;
import org.swd392.quizzes.service.QuizQuestionService;
import org.swd392.quizzes.service.QuizQuestionService.QuestionStatisticsDTO;
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

//Reorder questions within a quiz (Admin only)
    @PutMapping("/quiz/{quizId}/reorder")
    public ResponseEntity<List<QuizQuestionDTO>> reorderQuestions(
            @PathVariable Long quizId,
            @RequestBody List<Long> questionIds) {
        log.info("Reordering questions for quiz ID: {}", quizId);

        List<QuizQuestionDTO> reorderedQuestions = quizQuestionService.reorderQuestions(quizId, questionIds);
        return ResponseEntity.ok(reorderedQuestions);
    }

 //Duplicate a question (Admin only)
    @PostMapping("/{questionId}/duplicate")
    public ResponseEntity<QuizQuestionDTO> duplicateQuestion(
            @PathVariable Long questionId,
            @RequestParam Long targetQuizId) {
        log.info("Duplicating question ID: {} to quiz ID: {}", questionId, targetQuizId);

        QuizQuestionDTO duplicatedQuestion = quizQuestionService.duplicateQuestion(questionId, targetQuizId);
        return ResponseEntity.status(HttpStatus.CREATED).body(duplicatedQuestion);
    }

//Get question statistics for a quiz (Admin dashboard)
    @GetMapping("/quiz/{quizId}/statistics")
    public ResponseEntity<QuestionStatisticsDTO> getQuestionStatistics(@PathVariable Long quizId) {
        log.info("Fetching question statistics for quiz: {}", quizId);

        QuestionStatisticsDTO statistics = quizQuestionService.getQuestionStatistics(quizId);
        return ResponseEntity.ok(statistics);
    }

 //Bulk create questions (Admin only)
    @PostMapping("/bulk")
    public ResponseEntity<List<QuizQuestionDTO>> bulkCreateQuestions(
            @Valid @RequestBody List<QuizQuestionDTO> questionDTOs) {
        log.info("Bulk creating {} questions", questionDTOs.size());

        List<QuizQuestionDTO> createdQuestions = questionDTOs.stream()
                .map(quizQuestionService::createQuestion)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestions);
    }

//Bulk delete questions (Admin only)
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> bulkDeleteQuestions(@RequestBody List<Long> questionIds) {
        log.info("Bulk deleting questions: {}", questionIds);

        for (Long id : questionIds) {
            try {
                quizQuestionService.deleteQuestion(id);
            } catch (Exception e) {
                log.warn("Failed to delete question with ID: {}", id, e);
                // Continue with other deletions
            }
        }

        return ResponseEntity.noContent().build();
    }

//Copy all questions from one quiz to another (Admin only)
    @PostMapping("/quiz/{sourceQuizId}/copy-to/{targetQuizId}")
    public ResponseEntity<List<QuizQuestionDTO>> copyQuestionsToQuiz(
            @PathVariable Long sourceQuizId,
            @PathVariable Long targetQuizId) {
        log.info("Copying questions from quiz {} to quiz {}", sourceQuizId, targetQuizId);

        List<QuizQuestionDTO> sourceQuestions = quizQuestionService.getQuestionsByQuizId(sourceQuizId);
        List<QuizQuestionDTO> copiedQuestions = sourceQuestions.stream()
                .map(question -> quizQuestionService.duplicateQuestion(question.getId(), targetQuizId))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(copiedQuestions);
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

//Validate question content (utility endpoint)
    @PostMapping("/validate")
    public ResponseEntity<QuestionValidationResponse> validateQuestion(
            @RequestBody QuizQuestionDTO questionDTO) {
        log.info("Validating question content");

        try {
            QuestionValidationResponse response = new QuestionValidationResponse();

            if (questionDTO.getContent() == null || questionDTO.getContent().trim().isEmpty()) {
                response.setValid(false);
                response.addError("Question content cannot be empty");
            }

            if (questionDTO.getDimension() == null || questionDTO.getDimension().trim().isEmpty()) {
                response.setValid(false);
                response.addError("Question dimension cannot be empty");
            }

            if (questionDTO.getOptions() == null || questionDTO.getOptions().isEmpty()) {
                response.setValid(false);
                response.addError("Question must have at least one option");
            }

            if (response.getErrors().isEmpty()) {
                response.setValid(true);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error validating question", e);
            QuestionValidationResponse response = new QuestionValidationResponse();
            response.setValid(false);
            response.addError("Validation failed: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

//Exception handler for this controller
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error in QuizQuestionController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing the request: " + e.getMessage());
    }

//Inner class for question validation response
    public static class QuestionValidationResponse {
        private boolean valid = true;
        private List<String> errors = new java.util.ArrayList<>();

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }

        public void addError(String error) { this.errors.add(error); }
    }
}
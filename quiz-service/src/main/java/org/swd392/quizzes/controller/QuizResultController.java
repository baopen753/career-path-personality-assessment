package org.swd392.quizzes.controller;

import org.swd392.quizzes.dto.PersonalityResultDTO;
import org.swd392.quizzes.dto.QuizResultDTO;
import org.swd392.quizzes.dto.QuizSubmissionDTO;
import org.swd392.quizzes.dto.UserQuizResultsDTO;
import org.swd392.quizzes.service.QuizResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quiz-results")
@RequiredArgsConstructor
@Slf4j
public class QuizResultController {

    private final QuizResultService quizResultService;

    @PostMapping("/submit")
    public ResponseEntity<PersonalityResultDTO> submitQuiz(
            @Valid @RequestBody QuizSubmissionDTO submission,
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Received quiz submission request for user: {} and quiz: {}",
                userId, submission.getQuizId());

        PersonalityResultDTO result = quizResultService.submitQuizResultWithMicroservices(
                submission, userId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/me")
    public ResponseEntity<UserQuizResultsDTO> getMyResults(
            @RequestHeader("X-User-Id") Long userId) { // Thêm header này
        log.info("Fetching quiz results for authenticated user");

        try {
            UserQuizResultsDTO results = quizResultService.getMyQuizResults(userId);
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            log.error("Failed to get user results", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Get all quiz results for a user by their email address
    @GetMapping("/user/by-email")
    public ResponseEntity<UserQuizResultsDTO> getUserResultsByEmail(
            @RequestParam("email") String email,
            @RequestHeader("X-User-Id") Long parentId) {
        log.info("Parent {} requesting quiz results for student {}", parentId, email);

        UserQuizResultsDTO results = quizResultService.getUserResultByEmail(email, parentId);
        return ResponseEntity.ok(results);
    }

    //Get specific quiz result by ID
    @GetMapping("/{resultId}")
    public ResponseEntity<QuizResultDTO> getResultById(@PathVariable Long resultId) {
        log.info("Fetching quiz result with ID: {}", resultId);

        QuizResultDTO result = quizResultService.getResultById(resultId);
        return ResponseEntity.ok(result);
    }

    //Exception handler for this controller
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error in QuizResultController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing the request: " + e.getMessage());
    }
}

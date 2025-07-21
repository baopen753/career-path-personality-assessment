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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/quiz-results")
@RequiredArgsConstructor
@Slf4j
public class QuizResultController {

    private final QuizResultService quizResultService;

//Submit quiz and get personality result
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

//Get all quiz results for the authenticated user
@GetMapping("/user/me")
public ResponseEntity<UserQuizResultsDTO> getMyResults(
        @RequestHeader("X-User-Id") Long userId) {
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
            @RequestParam ("email") String email,
            @RequestHeader("X-User-Id") String parentId) {
        log.info("Parent {} requesting quiz results for student {}",parentId, email);

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

    //Get quiz results for specific quiz and user
    @GetMapping("/quiz/{quizId}/user/{userId}")
    public ResponseEntity<List<QuizResultDTO>> getResultsByQuizAndUser(
            @PathVariable Long quizId,
            @PathVariable Long userId) {
        log.info("Fetching quiz results for quiz: {} and user: {}", quizId, userId);

        List<QuizResultDTO> results = quizResultService.getResultsByQuizAndUser(quizId, userId);
        return ResponseEntity.ok(results);
    }

    //Get latest quiz result for user and quiz
    @GetMapping("/quiz/{quizId}/user/{userId}/latest")
    public ResponseEntity<QuizResultDTO> getLatestResult(
            @PathVariable Long quizId,
            @PathVariable Long userId) {
        log.info("Fetching latest quiz result for quiz: {} and user: {}", quizId, userId);

        Optional<QuizResultDTO> result = quizResultService.getLatestResultByQuizAndUser(quizId, userId);

        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Get quiz attempt count for a user and quiz
    @GetMapping("/quiz/{quizId}/user/{userId}/attempts")
    public ResponseEntity<Integer> getAttemptCount(
            @PathVariable Long quizId,
            @PathVariable Long userId) {
        log.info("Fetching attempt count for quiz: {} and user: {}", quizId, userId);

        Integer attemptCount = quizResultService.getQuizAttemptCount(userId, quizId);
        return ResponseEntity.ok(attemptCount);
    }

    //Check if user can attempt quiz
    @GetMapping("/quiz/{quizId}/user/{userId}/can-attempt")
    public ResponseEntity<Boolean> canUserAttemptQuiz(
            @PathVariable Long quizId,
            @PathVariable Long userId) {
        log.info("Checking if user: {} can attempt quiz: {}", userId, quizId);

        boolean canAttempt = quizResultService.canUserAttemptQuiz(userId, quizId);
        return ResponseEntity.ok(canAttempt);
    }

    //Delete quiz result (Admin only)
    @DeleteMapping("/{resultId}")
    public ResponseEntity<Void> deleteQuizResult(@PathVariable Long resultId) {
        log.info("Deleting quiz result with ID: {}", resultId);

        quizResultService.deleteQuizResult(resultId);
        return ResponseEntity.noContent().build();
    }

    //Exception handler for this controller
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error in QuizResultController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing the request: " + e.getMessage());
    }
}

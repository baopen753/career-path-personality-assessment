package org.swd392.quizzes.config;
import org.swd392.quizzes.dto.PersonalityResultDTO;
import org.swd392.quizzes.dto.QuizResultDTO;
import org.swd392.quizzes.dto.QuizSubmissionDTO;
import org.swd392.quizzes.service.QuizResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/quiz-results")
@RequiredArgsConstructor
@Tag(name = "Quiz Results", description = "APIs for managing quiz results and submissions")
public class QuizResultController {

    private final QuizResultService quizResultService;

    @PostMapping("/submit")
    @Operation(summary = "Submit quiz answers", description = "Submit quiz answers and calculate personality result")
    public ResponseEntity<PersonalityResultDTO> submitQuizResult(@Valid @RequestBody QuizSubmissionDTO submission) {
        PersonalityResultDTO result = quizResultService.submitQuizResult(submission);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user quiz results", description = "Retrieve all quiz results for a specific user")
    public ResponseEntity<List<QuizResultDTO>> getResultsByUserId(@PathVariable Long userId) {
        List<QuizResultDTO> results = quizResultService.getResultsByUserId(userId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz result by ID", description = "Retrieve a specific quiz result by its ID")
    public ResponseEntity<QuizResultDTO> getResultById(@PathVariable Long id) {
        QuizResultDTO result = quizResultService.getResultById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}/quiz/{quizId}/attempts")
    @Operation(summary = "Get quiz attempt count", description = "Get the number of attempts for a specific quiz by user")
    public ResponseEntity<Integer> getQuizAttemptCount(@PathVariable Long userId, @PathVariable Long quizId) {
        Integer attemptCount = quizResultService.getQuizAttemptCount(userId, quizId);
        return ResponseEntity.ok(attemptCount);
    }
}
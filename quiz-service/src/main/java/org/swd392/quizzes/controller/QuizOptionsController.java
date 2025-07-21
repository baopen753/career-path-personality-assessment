package org.swd392.quizzes.controller;

import org.swd392.quizzes.dto.QuizOptionsDTO;
import org.swd392.quizzes.entity.QuizOptions;
import org.swd392.quizzes.service.QuizOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quiz-options")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Quiz Options", description = "API for managing quiz options")
public class QuizOptionsController {

    private final QuizOptionService quizOptionService;

    @Operation(summary = "Get options by question ID", description = "Retrieve all options for a specific quiz question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Options retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz question not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<QuizOptionsDTO>> getOptionsByQuestionId(
            @Parameter(description = "ID of the quiz question", required = true)
            @PathVariable Long questionId) {

        log.info("GET /api/v1/quiz-options/question/{} - Fetching options for question", questionId);

        List<QuizOptionsDTO> options = quizOptionService.getOptionsByQuestionId(questionId);

        log.info("Successfully retrieved {} options for question {}", options.size(), questionId);
        return ResponseEntity.ok(options);
    }

    @Operation(summary = "Get options by multiple question IDs", description = "Retrieve options for multiple quiz questions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Options retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "One or more quiz questions not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/questions")
    public ResponseEntity<List<QuizOptionsDTO>> getOptionsByQuestionIds(
            @Parameter(description = "List of question IDs", required = true)
            @RequestParam List<Long> questionIds) {

        log.info("GET /api/v1/quiz-options/questions - Fetching options for {} questions", questionIds.size());

        List<QuizOptionsDTO> options = quizOptionService.getOptionsByQuestionIds(questionIds);

        log.info("Successfully retrieved {} options for {} questions", options.size(), questionIds.size());
        return ResponseEntity.ok(options);
    }

    @Operation(summary = "Get option by ID", description = "Retrieve a specific quiz option by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Option retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz option not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<QuizOptionsDTO> getOptionById(
            @Parameter(description = "ID of the quiz option", required = true)
            @PathVariable Long id) {

        log.info("GET /api/v1/quiz-options/{} - Fetching option", id);

        QuizOptionsDTO option = quizOptionService.getOptionById(id);

        log.info("Successfully retrieved option {}", id);
        return ResponseEntity.ok(option);
    }

    @Operation(summary = "Get options by target trait", description = "Retrieve options filtered by target trait")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Options retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid target trait"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/target-trait/{targetTrait}")
    public ResponseEntity<List<QuizOptionsDTO>> getOptionsByTargetTrait(
            @Parameter(description = "Target trait to filter by", required = true)
            @PathVariable String targetTrait) {

        log.info("GET /api/v1/quiz-options/target-trait/{} - Fetching options", targetTrait);

        List<QuizOptionsDTO> options = quizOptionService.getOptionsByTargetTrait(targetTrait);

        log.info("Successfully retrieved {} options for target trait {}", options.size(), targetTrait);
        return ResponseEntity.ok(options);
    }

    @Operation(summary = "Get options by score value", description = "Retrieve options filtered by score value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Options retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid score value"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/score-value/{scoreValue}")
    public ResponseEntity<List<QuizOptionsDTO>> getOptionsByScoreValue(
            @Parameter(description = "Score value to filter by", required = true)
            @PathVariable QuizOptions.ScoreValue scoreValue) {

        log.info("GET /api/v1/quiz-options/score-value/{} - Fetching options", scoreValue);

        List<QuizOptionsDTO> options = quizOptionService.getOptionsByScoreValue(scoreValue);

        log.info("Successfully retrieved {} options for score value {}", options.size(), scoreValue);
        return ResponseEntity.ok(options);
    }

    @Operation(summary = "Get options by question and target trait", description = "Retrieve options for a specific question and target trait")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Options retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "404", description = "Quiz question not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/question/{questionId}/target-trait/{targetTrait}")
    public ResponseEntity<List<QuizOptionsDTO>> getOptionsGroupedByTargetTrait(
            @Parameter(description = "ID of the quiz question", required = true)
            @PathVariable Long questionId,
            @Parameter(description = "Target trait to filter by", required = true)
            @PathVariable String targetTrait) {

        log.info("GET /api/v1/quiz-options/question/{}/target-trait/{} - Fetching options", questionId, targetTrait);

        List<QuizOptionsDTO> options = quizOptionService.getOptionsGroupedByTargetTrait(questionId, targetTrait);

        log.info("Successfully retrieved {} options for question {} and target trait {}",
                options.size(), questionId, targetTrait);
        return ResponseEntity.ok(options);
    }

    @Operation(summary = "Create a new quiz option", description = "Create a single quiz option")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Option created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Quiz question not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<QuizOptionsDTO> createOption(
            @Parameter(description = "Quiz option data", required = true)
            @Valid @RequestBody QuizOptionsDTO optionDTO) {

        log.info("POST /api/v1/quiz-options - Creating new option for question {}", optionDTO.getQuestionId());

        QuizOptionsDTO createdOption = quizOptionService.createOption(optionDTO);

        log.info("Successfully created option with ID: {}", createdOption.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOption);
    }

    @Operation(summary = "Create multiple quiz options", description = "Create multiple quiz options at once")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Options created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "One or more quiz questions not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/bulk")
    public ResponseEntity<List<QuizOptionsDTO>> createOptions(
            @Parameter(description = "List of quiz option data", required = true)
            @Valid @RequestBody List<QuizOptionsDTO> optionDTOs) {

        log.info("POST /api/v1/quiz-options/bulk - Creating {} new options", optionDTOs.size());

        List<QuizOptionsDTO> createdOptions = quizOptionService.createOptions(optionDTOs);

        log.info("Successfully created {} options", createdOptions.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOptions);
    }

    @Operation(summary = "Update a quiz option", description = "Update an existing quiz option")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Option updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Quiz option or question not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<QuizOptionsDTO> updateOption(
            @Parameter(description = "ID of the quiz option to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated quiz option data", required = true)
            @Valid @RequestBody QuizOptionsDTO optionDTO) {

        log.info("PUT /api/v1/quiz-options/{} - Updating option", id);

        QuizOptionsDTO updatedOption = quizOptionService.updateOption(id, optionDTO);

        log.info("Successfully updated option with ID: {}", id);
        return ResponseEntity.ok(updatedOption);
    }

    @Operation(summary = "Delete a quiz option", description = "Delete a specific quiz option")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Option deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz option not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOption(
            @Parameter(description = "ID of the quiz option to delete", required = true)
            @PathVariable Long id) {

        log.info("DELETE /api/v1/quiz-options/{} - Deleting option", id);

        quizOptionService.deleteOption(id);

        log.info("Successfully deleted option with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all options for a question", description = "Delete all options associated with a specific quiz question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Options deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz question not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<Void> deleteOptionsByQuestionId(
            @Parameter(description = "ID of the quiz question", required = true)
            @PathVariable Long questionId) {

        log.info("DELETE /api/v1/quiz-options/question/{} - Deleting all options for question", questionId);

        quizOptionService.deleteOptionsByQuestionId(questionId);

        log.info("Successfully deleted all options for question {}", questionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Count options for a question", description = "Get the count of options for a specific quiz question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz question not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/question/{questionId}/count")
    public ResponseEntity<Map<String, Long>> countOptionsByQuestionId(
            @Parameter(description = "ID of the quiz question", required = true)
            @PathVariable Long questionId) {

        log.info("GET /api/v1/quiz-options/question/{}/count - Counting options", questionId);

        long count = quizOptionService.countOptionsByQuestionId(questionId);

        log.info("Found {} options for question {}", count, questionId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @Operation(summary = "Check if option exists", description = "Check if a quiz option exists by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check completed successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Boolean>> optionExists(
            @Parameter(description = "ID of the quiz option", required = true)
            @PathVariable Long id) {

        log.info("GET /api/v1/quiz-options/{}/exists - Checking if option exists", id);

        boolean exists = quizOptionService.optionExists(id);

        log.info("Option {} exists: {}", id, exists);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @Operation(summary = "Get available score values", description = "Get all available score values for quiz options")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Score values retrieved successfully")
    })
    @GetMapping("/score-values")
    public ResponseEntity<List<QuizOptions.ScoreValue>> getAvailableScoreValues() {
        log.info("GET /api/v1/quiz-options/score-values - Fetching available score values");

        List<QuizOptions.ScoreValue> scoreValues = List.of(QuizOptions.ScoreValue.values());

        log.info("Retrieved {} available score values", scoreValues.size());
        return ResponseEntity.ok(scoreValues);
    }
}

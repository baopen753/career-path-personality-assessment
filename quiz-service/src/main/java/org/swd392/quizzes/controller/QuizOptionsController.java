package org.swd392.quizzes.controller;

import org.swd392.quizzes.dto.QuizOptionsDTO;
import org.swd392.quizzes.entity.QuizOptions;
import org.swd392.quizzes.service.Imp.QuizOptionServiceImp;
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
public class QuizOptionsController {

    private final QuizOptionServiceImp quizOptionService;

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<QuizOptionsDTO>> getOptionsByQuestionId(
            @PathVariable Long questionId) {

        List<QuizOptionsDTO> options = quizOptionService.getOptionsByQuestionId(questionId);

        log.info("Successfully retrieved {} options for question {}", options.size(), questionId);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuizOptionsDTO>> getOptionsByQuestionIds(
            @RequestParam List<Long> questionIds) {

        List<QuizOptionsDTO> options = quizOptionService.getOptionsByQuestionIds(questionIds);

        log.info("Successfully retrieved {} options for {} questions", options.size(), questionIds.size());
        return ResponseEntity.ok(options);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizOptionsDTO> getOptionById(
            @PathVariable Long id) {

        QuizOptionsDTO option = quizOptionService.getOptionById(id);

        log.info("Successfully retrieved option {}", id);
        return ResponseEntity.ok(option);
    }

    @GetMapping("/target-trait/{targetTrait}")
    public ResponseEntity<List<QuizOptionsDTO>> getOptionsByTargetTrait(
            @PathVariable String targetTrait) {

        List<QuizOptionsDTO> options = quizOptionService.getOptionsByTargetTrait(targetTrait);

        log.info("Successfully retrieved {} options for target trait {}", options.size(), targetTrait);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/score-value/{scoreValue}")
    public ResponseEntity<List<QuizOptionsDTO>> getOptionsByScoreValue(
            @PathVariable QuizOptions.ScoreValue scoreValue) {

        List<QuizOptionsDTO> options = quizOptionService.getOptionsByScoreValue(scoreValue);

        log.info("Successfully retrieved {} options for score value {}", options.size(), scoreValue);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/question/{questionId}/target-trait/{targetTrait}")
    public ResponseEntity<List<QuizOptionsDTO>> getOptionsGroupedByTargetTrait(
            @PathVariable Long questionId,
            @PathVariable String targetTrait) {

        List<QuizOptionsDTO> options = quizOptionService.getOptionsGroupedByTargetTrait(questionId, targetTrait);

        log.info("Successfully retrieved {} options for question {} and target trait {}",
                options.size(), questionId, targetTrait);
        return ResponseEntity.ok(options);
    }

    @PostMapping
    public ResponseEntity<QuizOptionsDTO> createOption(
            @Valid @RequestBody QuizOptionsDTO optionDTO) {

        QuizOptionsDTO createdOption = quizOptionService.createOption(optionDTO);

        log.info("Successfully created option with ID: {}", createdOption.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOption);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<QuizOptionsDTO>> createOptions(
            @Valid @RequestBody List<QuizOptionsDTO> optionDTOs) {

        List<QuizOptionsDTO> createdOptions = quizOptionService.createOptions(optionDTOs);

        log.info("Successfully created {} options", createdOptions.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOptions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizOptionsDTO> updateOption(
            @PathVariable Long id,
            @Valid @RequestBody QuizOptionsDTO optionDTO) {

        QuizOptionsDTO updatedOption = quizOptionService.updateOption(id, optionDTO);

        log.info("Successfully updated option with ID: {}", id);
        return ResponseEntity.ok(updatedOption);
    }

    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<Void> deleteOptionsByQuestionId(
            @PathVariable Long questionId) {

        quizOptionService.deleteOptionsByQuestionId(questionId);

        log.info("Successfully deleted all options for question {}", questionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/question/{questionId}/count")
    public ResponseEntity<Map<String, Long>> countOptionsByQuestionId(
            @PathVariable Long questionId) {

        long count = quizOptionService.countOptionsByQuestionId(questionId);

        log.info("Found {} options for question {}", count, questionId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Boolean>> optionExists(
            @PathVariable Long id) {

        boolean exists = quizOptionService.optionExists(id);

        log.info("Option {} exists: {}", id, exists);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/score-values")
    public ResponseEntity<List<QuizOptions.ScoreValue>> getAvailableScoreValues() {

        List<QuizOptions.ScoreValue> scoreValues = List.of(QuizOptions.ScoreValue.values());

        log.info("Retrieved {} available score values", scoreValues.size());
        return ResponseEntity.ok(scoreValues);
    }
}

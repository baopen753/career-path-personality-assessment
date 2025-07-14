package org.swd392.quizzes.service;

import org.swd392.quizzes.dto.PersonalityResultDTO;
import org.swd392.quizzes.dto.QuizSubmissionDTO;
import org.swd392.quizzes.entity.PersonalityStandard;
import org.swd392.quizzes.entity.Quiz;
import org.swd392.quizzes.entity.QuizOptions;
import org.swd392.quizzes.entity.QuizQuestion;
import org.swd392.quizzes.exception.InvalidQuizSubmissionException;
import org.swd392.quizzes.exception.QuizNotFoundException;
import org.swd392.quizzes.repository.PersonalityStandardRepository;
import org.swd392.quizzes.repository.QuizOptionsRepository;
import org.swd392.quizzes.repository.QuizQuestionRepository;
import org.swd392.quizzes.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalityCalculationService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizOptionsRepository quizOptionsRepository;
    private final PersonalityStandardRepository personalityStandardRepository;

    /**
     * Main method to calculate personality based on quiz submission
     */
    public PersonalityResultDTO calculatePersonality(QuizSubmissionDTO submission) {
        log.info("Starting personality calculation for quiz: {} and user: {}",
                submission.getQuizId(), submission.getUserId());

        // Validate submission
        validateSubmission(submission);

        // Get quiz and determine personality type
        Quiz quiz = quizRepository.findById(submission.getQuizId())
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + submission.getQuizId()));

        // Get all questions for the quiz
        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByOrderNumber(submission.getQuizId());

        // Calculate scores based on answers
        Map<String, Integer> dimensionScores = calculateDimensionScores(questions, submission.getAnswers(), quiz);

        // Determine personality type based on quiz category/type
        PersonalityResultDTO result;
        if (isDISCQuiz(quiz)) {
            result = calculateDISCPersonality(dimensionScores);
        } else if (isMBTIQuiz(quiz)) {
            result = calculateMBTIPersonality(dimensionScores);
        } else {
            throw new InvalidQuizSubmissionException("Unsupported quiz type for personality calculation");
        }

        // Enrich result with personality standard information
        enrichWithPersonalityStandard(result);

        log.info("Personality calculation completed. Result: {}", result.getPersonalityCode());
        return result;
    }

    /**
     * Calculate DISC personality type
     */
    public PersonalityResultDTO calculateDISCPersonality(Map<String, Integer> scores) {
        log.debug("Calculating DISC personality with scores: {}", scores);

        // Calculate DISC scores by accumulating positive values (2 points) for each dimension
        Map<String, Integer> discScores = new HashMap<>();
        discScores.put("D", 0);
        discScores.put("I", 0);
        discScores.put("S", 0);
        discScores.put("C", 0);

        // Update scores based on the provided answers
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String trait = entry.getKey();
            Integer value = entry.getValue();
            if (discScores.containsKey(trait)) {
                // For DISC, we use ScoreValue.DISC_TWO (2 points) for strong agreement
                discScores.put(trait, discScores.get(trait) + value);
            }
        }

        // Find the dominant trait
        String personalityCode = determineDISCType(
                discScores.get("D"),
                discScores.get("I"),
                discScores.get("S"),
                discScores.get("C")
        );

        PersonalityResultDTO result = new PersonalityResultDTO();
        result.setPersonalityCode(personalityCode);
        result.setScores(discScores);

        return result;
    }

    /**
     * Calculate MBTI personality type
     */
    public PersonalityResultDTO calculateMBTIPersonality(Map<String, Integer> scores) {
        log.debug("Calculating MBTI personality with scores: {}", scores);

        // MBTI dimensions - initialize with 0
        int extraversion = scores.getOrDefault("E", 0);
        int introversion = scores.getOrDefault("I", 0);
        int sensing = scores.getOrDefault("S", 0);
        int intuition = scores.getOrDefault("N", 0);
        int thinking = scores.getOrDefault("T", 0);
        int feeling = scores.getOrDefault("F", 0);
        int judging = scores.getOrDefault("J", 0);
        int perceiving = scores.getOrDefault("P", 0);

        log.debug("MBTI raw scores - E: {}, I: {}, S: {}, N: {}, T: {}, F: {}, J: {}, P: {}",
                extraversion, introversion, sensing, intuition, thinking, feeling, judging, perceiving);

        // Determine each dimension
        String personalityCode = determineMBTIType(
                extraversion, introversion,
                sensing, intuition,
                thinking, feeling,
                judging, perceiving
        );

        PersonalityResultDTO result = new PersonalityResultDTO();
        result.setPersonalityCode(personalityCode);
        result.setScores(Map.of(
                "E", extraversion, "I", introversion,
                "S", sensing, "N", intuition,
                "T", thinking, "F", feeling,
                "J", judging, "P", perceiving
        ));

        return result;
    }

    /**
     * Calculate dimension scores based on user answers
     * Fixed to handle both DISC and MBTI properly
     */
    private Map<String, Integer> calculateDimensionScores(List<QuizQuestion> questions, Map<Long, Long> answers, Quiz quiz) {
        Map<String, Integer> dimensionScores = new HashMap<>();
        boolean isMBTI = isMBTIQuiz(quiz);
        boolean isDISC = isDISCQuiz(quiz);

        // Initialize scores for MBTI
        if (isMBTI) {
            dimensionScores.put("E", 0);
            dimensionScores.put("I", 0);
            dimensionScores.put("S", 0);
            dimensionScores.put("N", 0);
            dimensionScores.put("T", 0);
            dimensionScores.put("F", 0);
            dimensionScores.put("J", 0);
            dimensionScores.put("P", 0);
        } else if (isDISC) {
            dimensionScores.put("D", 0);
            dimensionScores.put("I", 0);
            dimensionScores.put("S", 0);
            dimensionScores.put("C", 0);
        }

        for (QuizQuestion question : questions) {
            Long selectedOptionId = answers.get(question.getId());
            if (selectedOptionId == null) {
                continue; // Skip unanswered questions
            }

            QuizOptions selectedOption = quizOptionsRepository.findById(selectedOptionId)
                    .orElse(null);

            if (selectedOption != null && selectedOption.getQuestionId().equals(question.getId())) {
                String targetTrait = selectedOption.getTargetTrait();

                if (targetTrait != null && !targetTrait.isEmpty()) {
                    int scoreValue;

                    if (isMBTI) {
                        // For MBTI: use the raw score values including negative
                        scoreValue = getMBTIScoreValue(selectedOption.getScoreValue());
                    } else {
                        // For DISC: use the existing logic
                        scoreValue = getDISCScoreValue(selectedOption.getScoreValue());
                    }

                    // Add score to the dimension
                    dimensionScores.merge(targetTrait, scoreValue, Integer::sum);

                    log.debug("Question {}: Selected option targets '{}' with score {}",
                            question.getId(), targetTrait, scoreValue);
                }
            }
        }

        log.debug("Final calculated dimension scores: {}", dimensionScores);
        return dimensionScores;
    }

    /**
     * Get MBTI score value (supports negative values)
     */
    private int getMBTIScoreValue(QuizOptions.ScoreValue scoreValue) {
        if (scoreValue == null) return 0;

        switch (scoreValue) {
            case NEGATIVE_ONE:
                return -1;
            case ZERO:
                return 0;
            case POSITIVE_ONE:
                return 1;
            case DISC_TWO:
                return 1; // Treat as positive for MBTI if accidentally used
            default:
                return 0;
        }
    }

    /**
     * Get DISC score value (only positive values)
     */
    private int getDISCScoreValue(QuizOptions.ScoreValue scoreValue) {
        if (scoreValue == null) return 0;

        switch (scoreValue) {
            case DISC_TWO:
                return 2;  // Strong agreement
            case POSITIVE_ONE:
                return 1;  // Moderate agreement
            case ZERO:
                return 0;  // Neutral
            case NEGATIVE_ONE:
                return 0;  // Disagreement (counts as 0 in DISC)
            default:
                return 0;
        }
    }

    /**
     * Determine DISC personality type based on scores
     */
    private String determineDISCType(int d, int i, int s, int c) {
        // Find the highest score
        Map<String, Integer> scores = Map.of("D", d, "I", i, "S", s, "C", c);
        String dominantType = scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("D");

        // For more complex DISC combinations, you might want to consider secondary traits
        // For now, returning the dominant type
        return dominantType;
    }

    /**
     * Determine MBTI personality type based on scores
     * Fixed to handle negative values properly
     */
    private String determineMBTIType(int e, int i, int s, int n, int t, int f, int j, int p) {
        // Check if all scores are completely neutral - return ISFJ as default
        if (e == 0 && i == 0 && s == 0 && n == 0 && t == 0 && f == 0 && j == 0 && p == 0) {
            log.warn("All MBTI scores are neutral. Returning ISFJ as default result.");
            return "ISFJ";
        }

        StringBuilder personalityCode = new StringBuilder();

        // E vs I - if tied, default to I (Introversion)
        personalityCode.append(Math.abs(e) > Math.abs(i) ? "E" :
                Math.abs(i) > Math.abs(e) ? "I" : "I");

        // S vs N - if tied, default to S (Sensing)
        personalityCode.append(Math.abs(s) > Math.abs(n) ? "S" :
                Math.abs(n) > Math.abs(s) ? "N" : "S");

        // T vs F - if tied, default to F (Feeling)
        personalityCode.append(Math.abs(t) > Math.abs(f) ? "T" :
                Math.abs(f) > Math.abs(t) ? "F" : "F");

        // J vs P - if tied, default to J (Judging)
        personalityCode.append(Math.abs(j) > Math.abs(p) ? "J" :
                Math.abs(p) > Math.abs(j) ? "P" : "J");

        String result = personalityCode.toString();
        log.debug("MBTI calculation: E({})|I({}) -> {}, S({})|N({}) -> {}, T({})|F({}) -> {}, J({})|P({}) -> {} = Final: {}",
                e, i, result.charAt(0),
                s, n, result.charAt(1),
                t, f, result.charAt(2),
                j, p, result.charAt(3),
                result);

        return result;
    }

    /**
     * Enrich personality result with standard information
     */
    private void enrichWithPersonalityStandard(PersonalityResultDTO result) {
        Optional<PersonalityStandard> standardOpt = personalityStandardRepository
                .findByPersonalityCode(result.getPersonalityCode());

        if (standardOpt.isPresent()) {
            PersonalityStandard standard = standardOpt.get();
            result.setNickname(standard.getNickname());
            result.setKeyTraits(standard.getKeyTraits());
            result.setDescription(standard.getDescription());
            result.setCareerRecommendations(standard.getCareerMappingPersonality());
        } else {
            log.warn("No personality standard found for code: {}", result.getPersonalityCode());
            result.setNickname("Unknown");
            result.setKeyTraits("No traits available");
            result.setDescription("No description available");
            result.setCareerRecommendations("No career recommendations available");
        }
    }

    /**
     * Validate quiz submission
     */
    private void validateSubmission(QuizSubmissionDTO submission) {
        if (submission == null) {
            throw new InvalidQuizSubmissionException("Quiz submission cannot be null");
        }

        if (submission.getQuizId() == null) {
            throw new InvalidQuizSubmissionException("Quiz ID cannot be null");
        }

        if (submission.getUserId() == null) {
            throw new InvalidQuizSubmissionException("User ID cannot be null");
        }

        if (submission.getAnswers() == null || submission.getAnswers().isEmpty()) {
            throw new InvalidQuizSubmissionException("Quiz answers cannot be null or empty");
        }

        log.debug("Quiz submission validation passed for quiz: {} and user: {}",
                submission.getQuizId(), submission.getUserId());
    }

    /**
     * Check if quiz is DISC type
     */
    private boolean isDISCQuiz(Quiz quiz) {
        // You can implement this based on category or quiz metadata
        // For now, checking if category contains "DISC" or similar logic
        return quiz.getTitle().toUpperCase().contains("DISC") ||
                quiz.getDescription().toUpperCase().contains("DISC");
    }

    /**
     * Check if quiz is MBTI type
     */
    private boolean isMBTIQuiz(Quiz quiz) {
        // You can implement this based on category or quiz metadata
        return quiz.getTitle().toUpperCase().contains("MBTI") ||
                quiz.getDescription().toUpperCase().contains("MBTI") ||
                quiz.getTitle().toUpperCase().contains("MYERS");
    }

    /**
     * Convert score value enum to integer, with special handling for DISC
     * @deprecated Use getMBTIScoreValue() or getDISCScoreValue() instead
     */
    @Deprecated
    private int getScoreValue(QuizOptions.ScoreValue scoreValue, String dimension) {
        if (dimension != null && dimension.startsWith("DISC")) {
            return getDISCScoreValue(scoreValue);
        } else {
            return getMBTIScoreValue(scoreValue);
        }
    }

    /**
     * Get personality type distribution for analytics
     */
    public Map<String, Long> getPersonalityTypeDistribution() {
        List<PersonalityStandard> allStandards = personalityStandardRepository.findAll();
        return allStandards.stream()
                .collect(Collectors.groupingBy(
                        PersonalityStandard::getPersonalityCode,
                        Collectors.counting()
                ));
    }
}

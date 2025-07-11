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
        Map<String, Integer> dimensionScores = calculateDimensionScores(questions, submission.getAnswers());

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

        // MBTI dimensions
        int extraversion = scores.getOrDefault("E", 0);
        int introversion = scores.getOrDefault("I", 0);
        int sensing = scores.getOrDefault("S", 0);
        int intuition = scores.getOrDefault("N", 0);
        int thinking = scores.getOrDefault("T", 0);
        int feeling = scores.getOrDefault("F", 0);
        int judging = scores.getOrDefault("J", 0);
        int perceiving = scores.getOrDefault("P", 0);

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
     */
    private Map<String, Integer> calculateDimensionScores(List<QuizQuestion> questions, Map<Long, Long> answers) {
        Map<String, Integer> dimensionScores = new HashMap<>();

        for (QuizQuestion question : questions) {
            Long selectedOptionId = answers.get(question.getId());
            if (selectedOptionId == null) {
                continue; // Skip unanswered questions
            }

            QuizOptions selectedOption = quizOptionsRepository.findById(selectedOptionId)
                    .orElse(null);

            if (selectedOption != null && selectedOption.getQuestionId().equals(question.getId())) {
                String targetTrait = selectedOption.getTargetTrait();
                int scoreValue = getScoreValue(selectedOption.getScoreValue(), question.getDimension());

                // Add score to the dimension
                dimensionScores.merge(targetTrait, scoreValue, Integer::sum);
            }
        }

        log.debug("Calculated dimension scores: {}", dimensionScores);
        return dimensionScores;
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
     */
    private String determineMBTIType(int e, int i, int s, int n, int t, int f, int j, int p) {
        StringBuilder personalityCode = new StringBuilder();

        // Extraversion vs Introversion
        personalityCode.append(e >= i ? "E" : "I");

        // Sensing vs Intuition
        personalityCode.append(s >= n ? "S" : "N");

        // Thinking vs Feeling
        personalityCode.append(t >= f ? "T" : "F");

        // Judging vs Perceiving
        personalityCode.append(j >= p ? "J" : "P");

        return personalityCode.toString();
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
     */
    private int getScoreValue(QuizOptions.ScoreValue scoreValue, String dimension) {
        if (dimension != null && dimension.startsWith("DISC")) {
            // For DISC questions, use a different scoring scale
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
        } else {
            // Default MBTI scoring
            switch (scoreValue) {
                case NEGATIVE_ONE:
                    return -1;
                case ZERO:
                    return 0;
                case POSITIVE_ONE:
                    return 1;
                default:
                    return 0;
            }
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

    /**
     * Get recommended careers for a personality type
     */
    public List<String> getCareerRecommendations(String personalityCode) {
        return personalityStandardRepository.findByPersonalityCode(personalityCode)
                .map(standard -> Arrays.asList(standard.getCareerMappingPersonality().split(",")))
                .orElse(new ArrayList<>());
    }

    /**
     * Calculate personality compatibility (bonus feature)
     */
    public double calculateCompatibility(String personalityCode1, String personalityCode2) {
        // This is a simplified compatibility calculation
        // You can implement more sophisticated logic based on personality theory

        if (personalityCode1.equals(personalityCode2)) {
            return 1.0; // Perfect match
        }

        // For MBTI, calculate based on similar traits
        if (personalityCode1.length() == 4 && personalityCode2.length() == 4) {
            int commonTraits = 0;
            for (int i = 0; i < 4; i++) {
                if (personalityCode1.charAt(i) == personalityCode2.charAt(i)) {
                    commonTraits++;
                }
            }
            return commonTraits / 4.0;
        }

        // For DISC, implement specific compatibility rules
        if (personalityCode1.length() == 1 && personalityCode2.length() == 1) {
            return calculateDISCCompatibility(personalityCode1, personalityCode2);
        }

        return 0.5; // Default compatibility
    }

    /**
     * Calculate DISC compatibility
     */
    private double calculateDISCCompatibility(String type1, String type2) {
        // DISC compatibility matrix (simplified)
        Map<String, Map<String, Double>> compatibilityMatrix = Map.of(
                "D", Map.of("D", 0.7, "I", 0.8, "S", 0.6, "C", 0.5),
                "I", Map.of("D", 0.8, "I", 0.9, "S", 0.7, "C", 0.6),
                "S", Map.of("D", 0.6, "I", 0.7, "S", 0.8, "C", 0.9),
                "C", Map.of("D", 0.5, "I", 0.6, "S", 0.9, "C", 0.8)
        );

        return compatibilityMatrix.getOrDefault(type1, Map.of())
                .getOrDefault(type2, 0.5);
    }
}

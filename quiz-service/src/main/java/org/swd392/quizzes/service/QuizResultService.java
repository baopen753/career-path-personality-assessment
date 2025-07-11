package org.swd392.quizzes.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.swd392.quizzes.dto.PersonalityResultDTO;
import org.swd392.quizzes.dto.QuizResultDTO;
import org.swd392.quizzes.dto.QuizSubmissionDTO;
import org.swd392.quizzes.dto.UserQuizResultsDTO;
import org.swd392.quizzes.dto.external.ApiResponse;
import org.swd392.quizzes.dto.external.UserResponse;
import org.swd392.quizzes.dto.external.UserRole;
import org.swd392.quizzes.entity.PersonalityStandard;
import org.swd392.quizzes.entity.QuizResult;
import org.swd392.quizzes.exception.InvalidQuizSubmissionException;
import org.swd392.quizzes.exception.QuizNotFoundException;
import org.swd392.quizzes.repository.PersonalityStandardRepository;
import org.swd392.quizzes.repository.QuizResultRepository;
import org.swd392.quizzes.repository.httpclient.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;
    private final PersonalityStandardRepository personalityStandardRepository;
    private final PersonalityCalculationService personalityCalculationService;
    private final MicroserviceIntegrationService microserviceIntegrationService;
    private final AuthServiceClient authServiceClient;
    private final ObjectMapper objectMapper;

    /**
     * Submit quiz result with complete microservices integration
     * This method integrates with auth-service, career-service, and university-service
     */
    public PersonalityResultDTO submitQuizResultWithMicroservices(QuizSubmissionDTO submission, String userId) {
        log.info("Processing quiz submission with microservices integration for user: {} and quiz: {}",
                userId, submission.getQuizId());

        try {
            // Convert String userId to Long for database storage
            Long userIdLong = Long.valueOf(userId);
            submission.setUserId(userIdLong);

            // 1. Calculate personality using existing logic
            PersonalityResultDTO personalityResult = personalityCalculationService.calculatePersonality(submission);
            log.info("Calculated personality type: {} for user: {}", personalityResult.getPersonalityCode(), userIdLong);

            // 2. Enrich with career and university recommendations from microservices
            PersonalityResultDTO enrichedResult = microserviceIntegrationService.enrichPersonalityResult(personalityResult);
            log.info("Enriched personality result with microservices data");

            // 3. Save quiz result to database
            QuizResult quizResult = saveQuizResult(submission, enrichedResult);
            log.info("Saved quiz result with ID: {}", quizResult.getId());

            log.info("Quiz submission completed successfully for user: {}", userIdLong);

            return enrichedResult;

        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: {}", userId, e);
            throw new RuntimeException("Invalid user ID format: " + userId);
        } catch (Exception e) {
            log.error("Failed to process quiz submission with microservices for user: {}", userId, e);
            throw new RuntimeException("Failed to submit quiz: " + e.getMessage());
        }
    }

    /**
     * Original submit method (kept for backward compatibility)
     */
    public PersonalityResultDTO submitQuizResult(QuizSubmissionDTO submission) {
        log.info("Processing quiz submission for user: {} and quiz: {}",
                submission.getUserId(), submission.getQuizId());

        try {
            // Validate submission
            validateSubmission(submission);

            // Calculate personality
            PersonalityResultDTO personalityResult = personalityCalculationService.calculatePersonality(submission);

            // Save result
            QuizResult quizResult = saveQuizResult(submission, personalityResult);

            log.info("Quiz submission processed successfully. Result ID: {}, Personality: {}",
                    quizResult.getId(), personalityResult.getPersonalityCode());

            return personalityResult;

        } catch (Exception e) {
            log.error("Failed to process quiz submission", e);
            throw new RuntimeException("Failed to submit quiz: " + e.getMessage());
        }
    }

    /**
     * Save quiz result to database
     */
    private QuizResult saveQuizResult(QuizSubmissionDTO submission, PersonalityResultDTO personalityResult) {
        try {
            // Convert PersonalityResultDTO to JSON for storage
            String personalityJson = objectMapper.writeValueAsString(personalityResult);

            // Create QuizResult entity manually (no builder pattern)
            QuizResult quizResult = new QuizResult();
            quizResult.setUserId(submission.getUserId());
            quizResult.setQuizId(submission.getQuizId());
            quizResult.setResultType(personalityResult.getPersonalityCode());
            quizResult.setResultJson(personalityJson);
            quizResult.setTimeSubmit(LocalDateTime.now());
            quizResult.setAttemptOrder(getNextAttemptOrder(submission.getUserId(), submission.getQuizId()));

            // Find and set personality standard
            Optional<PersonalityStandard> personalityStandard = personalityStandardRepository
                    .findByPersonalityCode(personalityResult.getPersonalityCode());
            personalityStandard.ifPresent(standard -> quizResult.setPersonalityId(standard.getId()));

            return quizResultRepository.save(quizResult);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize personality result to JSON", e);
            throw new RuntimeException("Failed to save quiz result");
        }
    }

    /**
     * Get next attempt order for user and quiz
     */
    private Integer getNextAttemptOrder(Long userId, Long quizId) {
        return getQuizAttemptCount(userId, quizId) + 1;
    }

    /**
     * Get all quiz results for a specific user
     */
    @Transactional(readOnly = true)
    public List<QuizResultDTO> getResultsByUserId(Long userId) {
        log.debug("Fetching quiz results for user: {}", userId);

        List<QuizResult> results = quizResultRepository.findByUserIdOrderByTimeSubmitDesc(userId);
        return results.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get comprehensive quiz results for the authenticated user only
     */
    public UserQuizResultsDTO getMyQuizResults(String authorizationHeader) {
        try {
            log.info("Fetching quiz results for authenticated user");

            // Get current authenticated user information
            ApiResponse<UserResponse> currentUserResponse = authServiceClient.getCurrentUser(authorizationHeader);
            UserResponse currentUser = currentUserResponse.getResult();

            if (currentUser == null) {
                throw new RuntimeException("Unable to get user information from auth service");
            }

            // Use the authenticated user's ID (now Long)
            Long authenticatedUserId = currentUser.getId();
            log.info("User {} requesting their own quiz results", authenticatedUserId);

            // Get quiz results for the authenticated user only
            List<QuizResult> results = quizResultRepository.findByUserIdWithPersonalityDetails(authenticatedUserId);

            // Build response using the authenticated user's information
            return buildUserQuizResultsDTO(currentUser, results);

        } catch (Exception e) {
            log.error("Failed to get user results for authenticated user", e);
            throw new RuntimeException("Failed to fetch user results", e);
        }
    }

    /**
     * Helper method to check if user has admin role
     */
    private boolean isAdmin(UserRole role) {
        return role == UserRole.ADMIN;
    }

    /**
     * Get specific quiz result by ID
     */
    @Transactional(readOnly = true)
    public QuizResultDTO getResultById(Long id) {
        log.debug("Fetching quiz result with ID: {}", id);

        QuizResult result = quizResultRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz result not found with id: " + id));
        return convertToDTO(result);
    }

    /**
     * Get quiz results for specific quiz and user
     */
    @Transactional(readOnly = true)
    public List<QuizResultDTO> getResultsByQuizAndUser(Long quizId, Long userId) {
        log.debug("Fetching quiz results for quiz: {} and user: {}", quizId, userId);

        List<QuizResult> results = quizResultRepository.findByQuizIdAndUserId(quizId, userId);
        return results.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get quiz attempt count for a user and quiz
     */
    @Transactional(readOnly = true)
    public Integer getQuizAttemptCount(Long userId, Long quizId) {
        return quizResultRepository.countByQuizIdAndUserId(quizId, userId);
    }

    /**
     * Check if user can attempt quiz again
     */
    @Transactional(readOnly = true)
    public boolean canUserAttemptQuiz(Long userId, Long quizId) {
        Integer attemptCount = getQuizAttemptCount(userId, quizId);

        // TODO: Integrate with user premium service
        // boolean isPremium = userPremiumService.isUserPremium(userId);
        // For now, allow multiple attempts for all users
        return attemptCount < 3; // Allow up to 3 attempts for now
    }

    /**
     * Get latest quiz result for user and quiz
     */
    @Transactional(readOnly = true)
    public Optional<QuizResultDTO> getLatestResultByQuizAndUser(Long quizId, Long userId) {
        List<QuizResult> results = quizResultRepository.findByQuizIdAndUserId(quizId, userId);

        return results.stream()
                .max(Comparator.comparing(QuizResult::getTimeSubmit))
                .map(this::convertToDTO);
    }

    /**
     * Delete quiz result (admin functionality)
     */
    public void deleteQuizResult(Long id) {
        log.info("Deleting quiz result with ID: {}", id);

        if (!quizResultRepository.existsById(id)) {
            throw new QuizNotFoundException("Quiz result not found with id: " + id);
        }

        quizResultRepository.deleteById(id);
        log.info("Quiz result deleted successfully with ID: {}", id);
    }

    /**
     * Get personality result from saved JSON
     */
    @Transactional(readOnly = true)
    public PersonalityResultDTO getPersonalityResult(Long resultId) {
        QuizResult result = quizResultRepository.findById(resultId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz result not found with id: " + resultId));

        try {
            return objectMapper.readValue(result.getResultJson(), PersonalityResultDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing personality result JSON for result ID: {}", resultId, e);
            throw new InvalidQuizSubmissionException("Invalid result JSON format");
        }
    }

    /**
     * Validate quiz submission
     */
    private void validateSubmission(QuizSubmissionDTO submission) {
        if (submission == null) {
            throw new InvalidQuizSubmissionException("Quiz submission cannot be null");
        }

        if (submission.getUserId() == null) {
            throw new InvalidQuizSubmissionException("User ID cannot be null");
        }

        if (submission.getQuizId() == null) {
            throw new InvalidQuizSubmissionException("Quiz ID cannot be null");
        }

        if (submission.getAnswers() == null || submission.getAnswers().isEmpty()) {
            throw new InvalidQuizSubmissionException("Quiz answers cannot be null or empty");
        }
    }

    /**
     * Convert PersonalityResultDTO to JSON string
     */
    private String convertToJson(PersonalityResultDTO result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            log.error("Error converting PersonalityResultDTO to JSON", e);
            throw new InvalidQuizSubmissionException("Failed to process quiz result");
        }
    }

    /**
     * Convert QuizResult entity to DTO
     */
    private QuizResultDTO convertToDTO(QuizResult result) {
        QuizResultDTO dto = new QuizResultDTO();
        dto.setId(result.getId());
        dto.setResultType(result.getResultType());
        dto.setTimeSubmit(result.getTimeSubmit());
        dto.setAttemptOrder(result.getAttemptOrder());
        dto.setResultJson(result.getResultJson());
        dto.setQuizId(result.getQuizId());
        dto.setUserId(result.getUserId());
        dto.setPersonalityId(result.getPersonalityId());
        return dto;
    }

    /**
     * Get quiz statistics (for admin dashboard)
     */
    @Transactional(readOnly = true)
    public QuizStatisticsDTO getQuizStatistics(Long quizId) {
        List<QuizResult> results = quizResultRepository.findAll().stream()
                .filter(r -> r.getQuizId().equals(quizId))
                .toList();

        QuizStatisticsDTO stats = new QuizStatisticsDTO();
        stats.setQuizId(quizId);
        stats.setTotalAttempts(results.size());
        stats.setUniqueUsers(results.stream()
                .map(QuizResult::getUserId)
                .collect(Collectors.toSet()).size());

        // Most common personality types
        stats.setPersonalityTypeDistribution(results.stream()
                .collect(Collectors.groupingBy(
                        QuizResult::getResultType,
                        Collectors.counting())));

        return stats;
    }


    /**
     * Get all quiz results for a user by their email address
     * This method supports both student self-access and parent access to student results
     */
    public UserQuizResultsDTO getUserResultByEmail(String email, String authorizationHeader) {
        log.info("Fetching quiz results for user with email: {}", email);

        try {
            // 1. Get current authenticated user information from auth-service
            ApiResponse<UserResponse> currentUserResponse = authServiceClient.getCurrentUser(authorizationHeader);
            UserResponse currentUser = currentUserResponse.getResult();

            if (currentUser == null) {
                throw new RuntimeException("Unable to get user information from auth service");
            }

            log.info("Request made by user: {} with role: {}", currentUser.getEmail(), currentUser.getRole());

            // 2. Check access permissions
            if (email.equals(currentUser.getEmail())) {
                // Case 1: User is requesting their own quiz results
                log.info("User requesting their own quiz results");
                Long userId = currentUser.getId();
                List<QuizResult> quizResults = quizResultRepository.findByUserIdWithPersonalityDetails(userId);
                return buildUserQuizResultsDTO(currentUser, quizResults);

            } else if ("PARENT".equals(currentUser.getRole().toString())) {
                // Case 2: Parent is requesting student's quiz results
                log.info("Parent user requesting student quiz results for email: {}", email);

                // Get the target student's quiz results by searching through database
                Long targetUserId = findUserIdByEmail(email, authorizationHeader);

                // Return empty results if no user found
                if (targetUserId == null) {
                    log.info("No quiz results found for student with email: {}", email);
                    return UserQuizResultsDTO.builder()
                            .email(email)
                            .totalQuizzesTaken(0)
                            .quizResults(new ArrayList<>())
                            .build();
                }

                List<QuizResult> quizResults = quizResultRepository.findByUserIdWithPersonalityDetails(targetUserId);

                // Create user response for the target student
                UserResponse targetStudent = UserResponse.builder()
                        .id(targetUserId)
                        .email(email)
                        .role(UserRole.STUDENT)
                        .build();

                UserQuizResultsDTO response = buildUserQuizResultsDTO(targetStudent, quizResults);
                log.info("Parent successfully retrieved {} quiz results for student: {}",
                        quizResults.size(), email);
                return response;

            } else {
                // Case 3: Unauthorized access
                throw new RuntimeException("Access denied. Only students can view their own results or parents can view student results. Current role: " + currentUser.getRole());
            }

        } catch (Exception e) {
            log.error("Failed to get quiz results for user with email: {}", email, e);
            throw new RuntimeException("Failed to retrieve user quiz results: " + e.getMessage());
        }
    }

    /**
     * Helper method to find userId by email from existing quiz results
     * This is a workaround since we can't directly query users by email without their token
     */
    private Long findUserIdByEmail(String email, String authorizationHeader) {
        log.debug("Searching for user ID by email: {} with auth header", email);

        try {
            // Get user directly from auth service using parent's token
            ApiResponse<UserResponse> userResponse = authServiceClient.getUserByEmail(email, authorizationHeader);

            if (userResponse != null && userResponse.getResult() != null) {
                Long userId = userResponse.getResult().getId();
                log.debug("Found user ID {} from auth service", userId);
                return userId;
            }

            log.warn("User not found in auth service for email: {}", email);
            return null;

        } catch (Exception e) {
            log.error("Error while searching for user ID by email: {}", email, e);
            throw new RuntimeException("Failed to search for user results: " + e.getMessage());
        }
    }

    /**
     * Helper method to build UserQuizResultsDTO from user and quiz results
     */
    private UserQuizResultsDTO buildUserQuizResultsDTO(UserResponse user, List<QuizResult> quizResults) {
        // Convert quiz results to summary DTOs
        List<UserQuizResultsDTO.QuizResultSummaryDTO> resultSummaries = quizResults.stream()
                .map(this::convertToResultSummary)
                .collect(Collectors.toList());

        // Calculate statistics
        LocalDateTime firstQuizDate = quizResults.stream()
                .map(QuizResult::getTimeSubmit)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime lastQuizDate = quizResults.stream()
                .map(QuizResult::getTimeSubmit)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return UserQuizResultsDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .totalQuizzesTaken(quizResults.size())
                .firstQuizDate(firstQuizDate)
                .lastQuizDate(lastQuizDate)
                .quizResults(resultSummaries)
                .build();
    }

    /**
     * Helper method to convert QuizResult entity to QuizResultSummaryDTO
     */
    private UserQuizResultsDTO.QuizResultSummaryDTO convertToResultSummary(QuizResult quizResult) {
        return UserQuizResultsDTO.QuizResultSummaryDTO.builder()
                .resultId(quizResult.getId())
                .quizId(quizResult.getQuizId())
                .quizTitle(quizResult.getQuiz() != null ? quizResult.getQuiz().getTitle() : "Unknown Quiz")
                .resultType(quizResult.getResultType())
                .personalityCode(quizResult.getPersonalityStandard() != null ?
                        quizResult.getPersonalityStandard().getPersonalityCode() : null)
                .personalityName(quizResult.getPersonalityStandard() != null ?
                        quizResult.getPersonalityStandard().getNickname() : null)
                .personalityDescription(quizResult.getPersonalityStandard() != null ?
                        quizResult.getPersonalityStandard().getDescription() : null)
                .attemptOrder(quizResult.getAttemptOrder())
                .timeSubmit(quizResult.getTimeSubmit())
                .resultJson(quizResult.getResultJson())
                .build();
    }

    // Inner class for statistics with Lombok annotations
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class QuizStatisticsDTO {
        private Long quizId;
        private Integer totalAttempts;
        private Integer uniqueUsers;
        private java.util.Map<String, Long> personalityTypeDistribution;
    }
}

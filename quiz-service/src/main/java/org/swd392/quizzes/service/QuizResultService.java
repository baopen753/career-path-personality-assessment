package org.swd392.quizzes.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.swd392.quizzes.dto.PersonalityResultDTO;
import org.swd392.quizzes.dto.QuizResultDTO;
import org.swd392.quizzes.dto.QuizSubmissionDTO;
import org.swd392.quizzes.dto.UserQuizResultsDTO;
import org.swd392.quizzes.dto.external.ApiResponse;
import org.swd392.quizzes.dto.external.UserResponseDto;
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
    public PersonalityResultDTO submitQuizResultWithMicroservices(QuizSubmissionDTO submission, Long userId) {
        log.info("Processing quiz submission with microservices integration for user: {} and quiz: {}",
                userId, submission.getQuizId());

        try {
            submission.setUserId(userId);

            // 1. Calculate personality using existing logic
            PersonalityResultDTO personalityResult = personalityCalculationService.calculatePersonality(submission);
            log.info("Calculated personality type: {} for user: {}", personalityResult.getPersonalityCode(), userId);

            // 2. Enrich with career and university recommendations from microservices
            PersonalityResultDTO enrichedResult = microserviceIntegrationService.enrichPersonalityResult(personalityResult);
            log.info("Enriched personality result with microservices data");

            // 3. Save quiz result to database
            QuizResult quizResult = saveQuizResult(submission, enrichedResult);
            log.info("Saved quiz result with ID: {}", quizResult.getId());

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
     * Get comprehensive quiz results for the authenticated user only
     */
    public UserQuizResultsDTO getMyQuizResults(Long id) {
        try {
            log.info("Fetching user details for authenticated user: {}", id);
            // gọi user-service để lấy thông tin chi tiết của người dùng (tên, email,...)
            ApiResponse<UserResponseDto> userResponse = authServiceClient.getUser(id);
            UserResponseDto currentUser = userResponse.getResult();

            if (currentUser == null) {
                log.error("Could not fetch user details for user ID: {}", id);
                throw new RuntimeException("User not found with ID: " + id);
            }

            log.info("Fetching quiz results for authenticated user: {}", id);
            // lấy danh sách kết quả bài trắc nghiệm từ DB như cũ
            List<QuizResult> results = quizResultRepository.findByUserIdWithPersonalityDetails(id);

            // xây dựng DTO trả về với thông tin người dùng đã được điền đầy đủ
            return buildUserQuizResultsDTO(currentUser, results); // truyền đối tượng currentUser từ header vào

        } catch (Exception e) {
            log.error("Failed to get user results for authenticated user", e);
            throw new RuntimeException("Failed to fetch user results", e);
        }
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
     * Get all quiz results for a user by their email address
     * This method supports both student self-access and parent access to student results
     */
    public UserQuizResultsDTO getUserResultByEmail(String email, String parentId) {
        log.info("Executing request for parent {} to get results of student {}", parentId, email);

        try {
            // 1. Get student's information
            ApiResponse<UserResponseDto> studentResponse = authServiceClient.getUserByEmail(email);
            UserResponseDto student = studentResponse.getResult();

            if (student == null) {
                log.warn("No student found with email: {}", email);
                return UserQuizResultsDTO.builder()
                        .email(email)
                        .totalQuizzesTaken(0)
                        .quizResults(new ArrayList<>())
                        .build();
            }
            // 3. Get student's quiz results
            List<QuizResult> quizResults = quizResultRepository.findByUserIdWithPersonalityDetails(student.getId());
            log.info("Successfully retrieved {} quiz results for student: {}", quizResults.size(), email);

            return buildUserQuizResultsDTO(student, quizResults);

        } catch (Exception e) {
            log.error("Failed to get quiz results for student with email: {}", email, e);
            throw new RuntimeException("Failed to retrieve student quiz results: " + e.getMessage());
        }
    }
    /**
     * Helper method to build UserQuizResultsDTO from user and quiz results
     */
    private UserQuizResultsDTO buildUserQuizResultsDTO(UserResponseDto user, List<QuizResult> quizResults) {
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
}

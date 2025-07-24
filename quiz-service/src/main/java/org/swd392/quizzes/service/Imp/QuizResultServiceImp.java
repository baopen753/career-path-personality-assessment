package org.swd392.quizzes.service.Imp;

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
import org.swd392.quizzes.exception.QuizNotFoundException;
import org.swd392.quizzes.repository.PersonalityStandardRepository;
import org.swd392.quizzes.repository.QuizResultRepository;
import org.swd392.quizzes.repository.httpclient.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swd392.quizzes.service.QuizResultService;

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
public class QuizResultServiceImp implements QuizResultService {

    private final QuizResultRepository quizResultRepository;
    private final PersonalityStandardRepository personalityStandardRepository;
    private final PersonalityCalculationServiceImp personalityCalculationService;
    private final MicroserviceIntegrationServiceImp microserviceIntegrationService;
    private final AuthServiceClient authServiceClient;
    private final ObjectMapper objectMapper;

    public PersonalityResultDTO submitQuizResultWithMicroservices(QuizSubmissionDTO submission, Long userId) {
        log.info("Processing quiz submission with microservices integration for user: {} and quiz: {}",
                userId, submission.getQuizId());

        try {
            submission.setUserId(userId);

            PersonalityResultDTO personalityResult = personalityCalculationService.calculatePersonality(submission);
            log.info("Calculated personality type: {} for user: {}", personalityResult.getPersonalityCode(), userId);

            PersonalityResultDTO enrichedResult = microserviceIntegrationService.enrichPersonalityResult(personalityResult);
            log.info("Enriched personality result with microservices data");

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

    private QuizResult saveQuizResult(QuizSubmissionDTO submission, PersonalityResultDTO personalityResult) {
        try {

            String personalityJson = objectMapper.writeValueAsString(personalityResult);

            QuizResult quizResult = new QuizResult();
            quizResult.setUserId(submission.getUserId());
            quizResult.setQuizId(submission.getQuizId());
            quizResult.setResultType(personalityResult.getPersonalityCode());
            quizResult.setResultJson(personalityJson);
            quizResult.setTimeSubmit(LocalDateTime.now());
            quizResult.setAttemptOrder(getNextAttemptOrder(submission.getUserId(), submission.getQuizId()));

            Optional<PersonalityStandard> personalityStandard = personalityStandardRepository
                    .findByPersonalityCode(personalityResult.getPersonalityCode());
            personalityStandard.ifPresent(standard -> quizResult.setPersonalityId(standard.getId()));

            return quizResultRepository.save(quizResult);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize personality result to JSON", e);
            throw new RuntimeException("Failed to save quiz result");
        }
    }

    private Integer getNextAttemptOrder(Long userId, Long quizId) {
        return getQuizAttemptCount(userId, quizId) + 1;
    }

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

            List<QuizResult> results = quizResultRepository.findByUserIdWithPersonalityDetails(id);

            return buildUserQuizResultsDTO(currentUser, results); // truyền đối tượng currentUser từ header vào

        } catch (Exception e) {
            log.error("Failed to get user results for authenticated user", e);
            throw new RuntimeException("Failed to fetch user results", e);
        }
    }

    @Transactional(readOnly = true)
    public QuizResultDTO getResultById(Long id) {
        log.debug("Fetching quiz result with ID: {}", id);

        QuizResult result = quizResultRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz result not found with id: " + id));
        return convertToDTO(result);
    }
    @Transactional(readOnly = true)
    public Integer getQuizAttemptCount(Long userId, Long quizId) {
        return quizResultRepository.countByQuizIdAndUserId(quizId, userId);
    }

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

    public UserQuizResultsDTO getUserResultByEmail(String email, Long parentId) {
        log.info("Executing request for parent {} to get results of student {}", parentId, email);

        try {
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
            List<QuizResult> quizResults = quizResultRepository.findByUserIdWithPersonalityDetails(student.getId());
            log.info("Successfully retrieved {} quiz results for student: {}", quizResults.size(), email);

            return buildUserQuizResultsDTO(student, quizResults);

        } catch (Exception e) {
            log.error("Failed to get quiz results for student with email: {}", email, e);
            throw new RuntimeException("Failed to retrieve student quiz results: " + e.getMessage());
        }
    }

    private UserQuizResultsDTO buildUserQuizResultsDTO(UserResponseDto user, List<QuizResult> quizResults) {

        List<UserQuizResultsDTO.QuizResultSummaryDTO> resultSummaries = quizResults.stream()
                .map(this::convertToResultSummary)
                .collect(Collectors.toList());

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

package org.swd392.quizzes.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizResultService {

//    private final QuizResultRepository quizResultRepository;
//    private final PersonalityStandardRepository personalityStandardRepository;
//    private final PersonalityCalculationService personalityCalculationService;
//
//    public PersonalityResultDTO submitQuizResult(QuizSubmissionDTO submission) {
//        // Check attempt limit for non-premium users
//        checkAttemptLimit(submission.getUserId(), submission.getQuizId());
//
//        // Calculate personality result
//        PersonalityResultDTO personalityResult = personalityCalculationService.calculatePersonality(submission);
//
//        // Save result to database
//        QuizResult quizResult = new QuizResult();
//        quizResult.setQuizId(submission.getQuizId());
//        quizResult.setUserId(submission.getUserId());
//        quizResult.setResultType(personalityResult.getPersonalityCode());
//        quizResult.setTimeSubmit(LocalDateTime.now());
//        quizResult.setAttemptOrder(getNextAttemptOrder(submission.getUserId(), submission.getQuizId()));
//        quizResult.setResultJson(convertToJson(personalityResult));
//
//        // Find personality standard
//        PersonalityStandard personalityStandard = personalityStandardRepository
//                .findByPersonalityCode(personalityResult.getPersonalityCode())
//                .orElse(null);
//
//        if (personalityStandard != null) {
//            quizResult.setPersonalityId(personalityStandard.getId());
//        }
//
//        quizResultRepository.save(quizResult);
//
//        return personalityResult;
//    }
//
//    public List<QuizResultDTO> getResultsByUserId(Long userId) {
//        return quizResultRepository.findByUserIdOrderByTimeSubmitDesc(userId).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public QuizResultDTO getResultById(Long id) {
//        QuizResult result = quizResultRepository.findById(id)
//                .orElseThrow(() -> new QuizNotFoundException("Quiz result not found with id: " + id));
//        return convertToDTO(result);
//    }
//
//    public Integer getQuizAttemptCount(Long userId, Long quizId) {
//        return quizResultRepository.countByQuizIdAndUserId(quizId, userId);
//    }
//
//    private void checkAttemptLimit(Long userId, Long quizId) {
//        Integer attemptCount = getQuizAttemptCount(userId, quizId);
//        // Assuming non-premium users can only attempt once
//        // This should be checked against user's premium status
//        if (attemptCount >= 1) {
//            throw new QuizAttemptLimitExceededException("Quiz attempt limit exceeded for user: " + userId);
//        }
//    }
//
//    private Integer getNextAttemptOrder(Long userId, Long quizId) {
//        return getQuizAttemptCount(userId, quizId) + 1;
//    }
//
//    private String convertToJson(PersonalityResultDTO result) {
//        // Convert PersonalityResultDTO to JSON string
//        // This would typically use ObjectMapper
//        return "{}"; // Placeholder
//    }
//
//    private QuizResultDTO convertToDTO(QuizResult result) {
//        QuizResultDTO dto = new QuizResultDTO();
//        dto.setId(result.getId());
//        dto.setResultType(result.getResultType());
//        dto.setTimeSubmit(result.getTimeSubmit());
//        dto.setAttemptOrder(result.getAttemptOrder());
//        dto.setResultJson(result.getResultJson());
//        dto.setQuizId(result.getQuizId());
//        dto.setUserId(result.getUserId());
//        dto.setPersonalityId(result.getPersonalityId());
//        return dto;
//    }
}

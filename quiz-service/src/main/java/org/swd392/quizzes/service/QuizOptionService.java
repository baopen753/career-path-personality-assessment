package org.swd392.quizzes.service;

import org.swd392.quizzes.dto.QuizOptionsDTO;
import org.swd392.quizzes.entity.QuizOptions;
import org.swd392.quizzes.entity.QuizQuestion;
import org.swd392.quizzes.exception.QuizNotFoundException;
import org.swd392.quizzes.exception.InvalidQuizSubmissionException;
import org.swd392.quizzes.repository.QuizOptionsRepository;
import org.swd392.quizzes.repository.QuizQuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuizOptionService {

    private final QuizOptionsRepository quizOptionsRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    /**
     * Get all options for a specific quiz question (CACHED)
     */
    @Cacheable(value = "quiz-options", key = "#questionId")
    @Transactional(readOnly = true)
    public List<QuizOptionsDTO> getOptionsByQuestionId(Long questionId) {
        log.debug("Fetching options for question ID: {} (checking cache first)", questionId);

        long startTime = System.currentTimeMillis();

        // Verify question exists
        if (!quizQuestionRepository.existsById(questionId)) {
            throw new QuizNotFoundException("Quiz question not found with id: " + questionId);
        }

        List<QuizOptions> options = quizOptionsRepository.findByQuestionId(questionId);
        List<QuizOptionsDTO> result = options.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("Fetched {} options for question {} in {} ms", result.size(), questionId, (endTime - startTime));

        return result;
    }

    /**
     * Get all options for multiple quiz questions
     */
    @Transactional(readOnly = true)
    public List<QuizOptionsDTO> getOptionsByQuestionIds(List<Long> questionIds) {
        log.debug("Fetching options for {} questions", questionIds.size());

        if (questionIds == null || questionIds.isEmpty()) {
            return List.of();
        }

        // Verify all questions exist
        List<QuizQuestion> questions = quizQuestionRepository.findAllById(questionIds);
        if (questions.size() != questionIds.size()) {
            throw new QuizNotFoundException("One or more quiz questions not found");
        }

        List<QuizOptions> options = quizOptionsRepository.findByQuestionIdIn(questionIds);
        return options.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get options by target trait
     */
    @Transactional(readOnly = true)
    public List<QuizOptionsDTO> getOptionsByTargetTrait(String targetTrait) {
        log.debug("Fetching options for target trait: {}", targetTrait);

        if (targetTrait == null || targetTrait.trim().isEmpty()) {
            throw new InvalidQuizSubmissionException("Target trait cannot be empty");
        }

        List<QuizOptions> options = quizOptionsRepository.findByTargetTrait(targetTrait);
        return options.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get options by score value
     */
    @Transactional(readOnly = true)
    public List<QuizOptionsDTO> getOptionsByScoreValue(QuizOptions.ScoreValue scoreValue) {
        log.debug("Fetching options for score value: {}", scoreValue);

        if (scoreValue == null) {
            throw new InvalidQuizSubmissionException("Score value cannot be null");
        }

        List<QuizOptions> options = quizOptionsRepository.findByScoreValue(scoreValue);
        return options.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific option by ID
     */
    @Transactional(readOnly = true)
    public QuizOptionsDTO getOptionById(Long id) {
        log.debug("Fetching option with ID: {}", id);

        QuizOptions option = quizOptionsRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz option not found with id: " + id));

        return convertToDTO(option);
    }

    /**
     * Create a new quiz option
     */
    @CacheEvict(value = "quiz-options", key = "#optionDTO.questionId")
    public QuizOptionsDTO createOption(QuizOptionsDTO optionDTO) {
        log.info("Creating new option for question ID: {}", optionDTO.getQuestionId());

        validateOptionDTO(optionDTO);

        // Verify question exists
        if (!quizQuestionRepository.existsById(optionDTO.getQuestionId())) {
            throw new QuizNotFoundException("Quiz question not found with id: " + optionDTO.getQuestionId());
        }

        QuizOptions option = convertToEntity(optionDTO);
        QuizOptions savedOption = quizOptionsRepository.save(option);

        log.info("Option created successfully with ID: {}", savedOption.getId());
        return convertToDTO(savedOption);
    }

    /**
     * Create multiple quiz options
     */
    @CacheEvict(value = "quiz-options", allEntries = true)
    public List<QuizOptionsDTO> createOptions(List<QuizOptionsDTO> optionDTOs) {
        log.info("Creating {} new options", optionDTOs.size());

        if (optionDTOs == null || optionDTOs.isEmpty()) {
            throw new InvalidQuizSubmissionException("Options list cannot be empty");
        }

        // Validate all options
        for (QuizOptionsDTO optionDTO : optionDTOs) {
            validateOptionDTO(optionDTO);
        }

        // Verify all questions exist
        List<Long> questionIds = optionDTOs.stream()
                .map(QuizOptionsDTO::getQuestionId)
                .distinct()
                .collect(Collectors.toList());

        List<QuizQuestion> questions = quizQuestionRepository.findAllById(questionIds);
        if (questions.size() != questionIds.size()) {
            throw new QuizNotFoundException("One or more quiz questions not found");
        }

        // **Delete existing options for these questions**
        for (Long questionId : questionIds) {
            quizOptionsRepository.deleteByQuestionId(questionId);
        }

        // Create and save options
        List<QuizOptions> options = optionDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        List<QuizOptions> savedOptions = quizOptionsRepository.saveAll(options);

        log.info("Successfully created {} options", savedOptions.size());
        return savedOptions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing quiz option
     */
    @CacheEvict(value = "quiz-options", key = "#optionDTO.questionId")
    public QuizOptionsDTO updateOption(Long id, QuizOptionsDTO optionDTO) {
        log.info("Updating option with ID: {}", id);

        QuizOptions existingOption = quizOptionsRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz option not found with id: " + id));

        validateOptionDTO(optionDTO);

        // Verify question exists if question ID is being changed
        if (!existingOption.getQuestionId().equals(optionDTO.getQuestionId())) {
            if (!quizQuestionRepository.existsById(optionDTO.getQuestionId())) {
                throw new QuizNotFoundException("Quiz question not found with id: " + optionDTO.getQuestionId());
            }
        }

        // Update option fields
        existingOption.setOptionText(optionDTO.getOptionText());
        existingOption.setTargetTrait(optionDTO.getTargetTrait());
        existingOption.setScoreValue(optionDTO.getScoreValue());
        existingOption.setQuestionId(optionDTO.getQuestionId());

        QuizOptions savedOption = quizOptionsRepository.save(existingOption);

        log.info("Option updated successfully with ID: {}", savedOption.getId());
        return convertToDTO(savedOption);
    }

    /**
     * Delete a quiz option
     */
    @CacheEvict(value = "quiz-options", allEntries = true)
    public void deleteOption(Long id) {
        log.info("Deleting option with ID: {}", id);

        QuizOptions option = quizOptionsRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz option not found with id: " + id));

        quizOptionsRepository.delete(option);

        log.info("Option deleted successfully with ID: {}", id);
    }

    /**
     * Delete all options for a specific question
     */
    @CacheEvict(value = "quiz-options", key = "#questionId")
    public void deleteOptionsByQuestionId(Long questionId) {
        log.info("Deleting all options for question ID: {}", questionId);

        // Verify question exists
        if (!quizQuestionRepository.existsById(questionId)) {
            throw new QuizNotFoundException("Quiz question not found with id: " + questionId);
        }

        List<QuizOptions> options = quizOptionsRepository.findByQuestionId(questionId);
        if (!options.isEmpty()) {
            quizOptionsRepository.deleteAll(options);
            log.info("Deleted {} options for question ID: {}", options.size(), questionId);
        } else {
            log.info("No options found for question ID: {}", questionId);
        }
    }

    /**
     * Count options for a specific question
     */
    @Transactional(readOnly = true)
    public long countOptionsByQuestionId(Long questionId) {
        log.debug("Counting options for question ID: {}", questionId);

        return quizOptionsRepository.countByQuestionId(questionId);
    }

    /**
     * Check if option exists
     */
    @Transactional(readOnly = true)
    public boolean optionExists(Long id) {
        return quizOptionsRepository.existsById(id);
    }

    /**
     * Get options grouped by target trait for a question
     */
    @Transactional(readOnly = true)
    public List<QuizOptionsDTO> getOptionsGroupedByTargetTrait(Long questionId, String targetTrait) {
        log.debug("Fetching options for question ID: {} and target trait: {}", questionId, targetTrait);

        if (!quizQuestionRepository.existsById(questionId)) {
            throw new QuizNotFoundException("Quiz question not found with id: " + questionId);
        }

        if (targetTrait == null || targetTrait.trim().isEmpty()) {
            throw new InvalidQuizSubmissionException("Target trait cannot be empty");
        }

        List<QuizOptions> options = quizOptionsRepository.findByQuestionIdAndTargetTrait(questionId, targetTrait);
        return options.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Validate quiz option DTO
     */
    private void validateOptionDTO(QuizOptionsDTO optionDTO) {
        if (optionDTO == null) {
            throw new InvalidQuizSubmissionException("Option data cannot be null");
        }

        if (optionDTO.getOptionText() == null || optionDTO.getOptionText().trim().isEmpty()) {
            throw new InvalidQuizSubmissionException("Option text cannot be empty");
        }

        if (optionDTO.getOptionText().length() > 1000) {
            throw new InvalidQuizSubmissionException("Option text cannot exceed 1000 characters");
        }

        if (optionDTO.getTargetTrait() == null || optionDTO.getTargetTrait().trim().isEmpty()) {
            throw new InvalidQuizSubmissionException("Target trait cannot be empty");
        }

        if (optionDTO.getTargetTrait().length() > 50) {
            throw new InvalidQuizSubmissionException("Target trait cannot exceed 50 characters");
        }

        if (optionDTO.getScoreValue() == null) {
            throw new InvalidQuizSubmissionException("Score value cannot be null");
        }

        if (optionDTO.getQuestionId() == null) {
            throw new InvalidQuizSubmissionException("Question ID cannot be null");
        }
    }

    /**
     * Convert QuizOptions entity to DTO
     */
    private QuizOptionsDTO convertToDTO(QuizOptions option) {
        QuizOptionsDTO dto = new QuizOptionsDTO();
        dto.setId(option.getId());
        dto.setOptionText(option.getOptionText());
        dto.setTargetTrait(option.getTargetTrait());
        dto.setScoreValue(option.getScoreValue());
        dto.setQuestionId(option.getQuestionId());
        return dto;
    }

    /**
     * Convert QuizOptionsDTO to entity
     */
    private QuizOptions convertToEntity(QuizOptionsDTO dto) {
        QuizOptions option = new QuizOptions();
        option.setId(dto.getId());
        option.setOptionText(dto.getOptionText().trim());
        option.setTargetTrait(dto.getTargetTrait().trim());
        option.setScoreValue(dto.getScoreValue());
        option.setQuestionId(dto.getQuestionId());
        return option;
    }
}

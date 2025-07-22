package org.swd392.quizzes.service.Imp;

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
import org.swd392.quizzes.service.QuizOptionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuizOptionServiceImp implements QuizOptionService {

    private final QuizOptionsRepository quizOptionsRepository;
    private final QuizQuestionRepository quizQuestionRepository;

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

    @Transactional(readOnly = true)
    public QuizOptionsDTO getOptionById(Long id) {
        log.debug("Fetching option with ID: {}", id);

        QuizOptions option = quizOptionsRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz option not found with id: " + id));

        return convertToDTO(option);
    }

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

    @CacheEvict(value = "quiz-options", allEntries = true)
    public List<QuizOptionsDTO> createOptions(List<QuizOptionsDTO> optionDTOs) {
        log.info("Creating {} new options", optionDTOs.size());

        if (optionDTOs == null || optionDTOs.isEmpty()) {
            throw new InvalidQuizSubmissionException("Options list cannot be empty");
        }

        for (QuizOptionsDTO optionDTO : optionDTOs) {
            validateOptionDTO(optionDTO);
        }

        List<Long> questionIds = optionDTOs.stream()
                .map(QuizOptionsDTO::getQuestionId)
                .distinct()
                .collect(Collectors.toList());

        List<QuizQuestion> questions = quizQuestionRepository.findAllById(questionIds);
        if (questions.size() != questionIds.size()) {
            throw new QuizNotFoundException("One or more quiz questions not found");
        }

        for (Long questionId : questionIds) {
            quizOptionsRepository.deleteByQuestionId(questionId);
        }

        List<QuizOptions> options = optionDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        List<QuizOptions> savedOptions = quizOptionsRepository.saveAll(options);

        log.info("Successfully created {} options", savedOptions.size());
        return savedOptions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "quiz-options", key = "#optionDTO.questionId")
    public QuizOptionsDTO updateOption(Long id, QuizOptionsDTO optionDTO) {
        log.info("Updating option with ID: {}", id);

        QuizOptions existingOption = quizOptionsRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz option not found with id: " + id));

        validateOptionDTO(optionDTO);

        if (!existingOption.getQuestionId().equals(optionDTO.getQuestionId())) {
            if (!quizQuestionRepository.existsById(optionDTO.getQuestionId())) {
                throw new QuizNotFoundException("Quiz question not found with id: " + optionDTO.getQuestionId());
            }
        }

        existingOption.setOptionText(optionDTO.getOptionText());
        existingOption.setTargetTrait(optionDTO.getTargetTrait());
        existingOption.setScoreValue(optionDTO.getScoreValue());
        existingOption.setQuestionId(optionDTO.getQuestionId());

        QuizOptions savedOption = quizOptionsRepository.save(existingOption);

        log.info("Option updated successfully with ID: {}", savedOption.getId());
        return convertToDTO(savedOption);
    }

    @CacheEvict(value = "quiz-options", key = "#questionId")
    public void deleteOptionsByQuestionId(Long questionId) {
        log.info("Deleting all options for question ID: {}", questionId);

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

    @Transactional(readOnly = true)
    public long countOptionsByQuestionId(Long questionId) {
        log.debug("Counting options for question ID: {}", questionId);

        return quizOptionsRepository.countByQuestionId(questionId);
    }

    @Transactional(readOnly = true)
    public boolean optionExists(Long id) {
        return quizOptionsRepository.existsById(id);
    }

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

    private QuizOptionsDTO convertToDTO(QuizOptions option) {
        QuizOptionsDTO dto = new QuizOptionsDTO();
        dto.setId(option.getId());
        dto.setOptionText(option.getOptionText());
        dto.setTargetTrait(option.getTargetTrait());
        dto.setScoreValue(option.getScoreValue());
        dto.setQuestionId(option.getQuestionId());
        return dto;
    }

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

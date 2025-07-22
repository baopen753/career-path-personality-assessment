package org.swd392.quizzes.service.Imp;

import org.swd392.quizzes.dto.QuizOptionsDTO;
import org.swd392.quizzes.dto.QuizQuestionDTO;
import org.swd392.quizzes.entity.Quiz;
import org.swd392.quizzes.entity.QuizOptions;
import org.swd392.quizzes.entity.QuizQuestion;
import org.swd392.quizzes.exception.InvalidQuizSubmissionException;
import org.swd392.quizzes.exception.QuizNotFoundException;
import org.swd392.quizzes.repository.QuizOptionsRepository;
import org.swd392.quizzes.repository.QuizQuestionRepository;
import org.swd392.quizzes.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swd392.quizzes.service.QuizQuestionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuizQuestionServiceImp implements QuizQuestionService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizOptionsRepository quizOptionsRepository;
    private final QuizRepository quizRepository;

    @Cacheable(value = "quiz-questions", key = "#quizId")
    @Transactional(readOnly = true)
    public List<QuizQuestionDTO> getQuestionsByQuizId(Long quizId) {
        log.debug("Fetching questions for quiz ID: {} (checking cache first)", quizId);

        long startTime = System.currentTimeMillis();

        if (!quizRepository.existsById(quizId)) {
            throw new QuizNotFoundException("Quiz not found with id: " + quizId);
        }

        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdWithOptions(quizId);

        List<QuizQuestionDTO> result = questions.stream()
                .map(this::convertToDTOWithLoadedOptions)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("Fetched {} questions for quiz {} in {} ms", result.size(), quizId, (endTime - startTime));

        return result;
    }

    @Transactional(readOnly = true)
    public QuizQuestionDTO getQuestionById(Long id) {
        log.debug("Fetching question with ID: {}", id);

        QuizQuestion question = quizQuestionRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz question not found with id: " + id));

        return convertToDTO(question);
    }

    @Transactional(readOnly = true)
    public List<QuizQuestionDTO> getQuestionsByDimension(String dimension) {
        log.debug("Fetching questions for dimension: {}", dimension);

        List<QuizQuestion> questions = quizQuestionRepository.findByDimension(dimension);
        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public QuizQuestionDTO createQuestion(QuizQuestionDTO questionDTO) {
        log.info("Creating new question for quiz ID: {}", questionDTO.getQuizId());

        validateQuestionDTO(questionDTO);

        Quiz quiz = quizRepository.findById(questionDTO.getQuizId())
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + questionDTO.getQuizId()));

        if (questionDTO.getOrderNumber() == null) {
            Integer maxOrder = getMaxOrderNumberForQuiz(questionDTO.getQuizId());
            questionDTO.setOrderNumber(maxOrder + 1);
        }

        QuizQuestion question = convertToEntity(questionDTO);
        QuizQuestion savedQuestion = quizQuestionRepository.save(question);

        if (questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
            List<QuizOptions> options = questionDTO.getOptions().stream()
                    .map(optionDTO -> convertOptionToEntity(optionDTO, savedQuestion.getId()))
                    .collect(Collectors.toList());

            quizOptionsRepository.saveAll(options);
        }

        log.info("Question created successfully with ID: {}", savedQuestion.getId());
        return convertToDTO(savedQuestion);
    }

    public QuizQuestionDTO updateQuestion(Long id, QuizQuestionDTO questionDTO) {
        log.info("Updating question with ID: {}", id);

        QuizQuestion existingQuestion = quizQuestionRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz question not found with id: " + id));

        validateQuestionDTO(questionDTO);

        existingQuestion.setContent(questionDTO.getContent());
        existingQuestion.setDimension(questionDTO.getDimension());

        if (questionDTO.getOrderNumber() != null) {
            existingQuestion.setOrderNumber(questionDTO.getOrderNumber());
        }

        QuizQuestion savedQuestion = quizQuestionRepository.save(existingQuestion);

        if (questionDTO.getOptions() != null) {
            updateQuestionOptions(savedQuestion.getId(), questionDTO.getOptions());
        }

        log.info("Question updated successfully with ID: {}", savedQuestion.getId());
        return convertToDTO(savedQuestion);
    }

    public void deleteQuestion(Long id) {
        log.info("Deleting question with ID: {}", id);

        QuizQuestion question = quizQuestionRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz question not found with id: " + id));

        // Delete associated options first
        quizOptionsRepository.deleteAll(quizOptionsRepository.findByQuestionId(id));

        // Delete the question
        quizQuestionRepository.delete(question);

        log.info("Question deleted successfully with ID: {}", id);
    }

    private void validateQuestionDTO(QuizQuestionDTO questionDTO) {
        if (questionDTO == null) {
            throw new InvalidQuizSubmissionException("Question data cannot be null");
        }

        if (questionDTO.getContent() == null || questionDTO.getContent().trim().isEmpty()) {
            throw new InvalidQuizSubmissionException("Question content cannot be empty");
        }

        if (questionDTO.getDimension() == null || questionDTO.getDimension().trim().isEmpty()) {
            throw new InvalidQuizSubmissionException("Question dimension cannot be empty");
        }

        if (questionDTO.getQuizId() == null) {
            throw new InvalidQuizSubmissionException("Quiz ID cannot be null");
        }

        if (questionDTO.getOptions() != null) {
            for (QuizOptionsDTO option : questionDTO.getOptions()) {
                validateOptionDTO(option);
            }
        }
    }

    private void validateOptionDTO(QuizOptionsDTO optionDTO) {
        if (optionDTO.getOptionText() == null || optionDTO.getOptionText().trim().isEmpty()) {
            throw new InvalidQuizSubmissionException("Option text cannot be empty");
        }

        if (optionDTO.getTargetTrait() == null || optionDTO.getTargetTrait().trim().isEmpty()) {
            throw new InvalidQuizSubmissionException("Target trait cannot be empty");
        }

        if (optionDTO.getScoreValue() == null) {
            throw new InvalidQuizSubmissionException("Score value cannot be null");
        }
    }

    private Integer getMaxOrderNumberForQuiz(Long quizId) {
        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByOrderNumber(quizId);
        return questions.stream()
                .mapToInt(QuizQuestion::getOrderNumber)
                .max()
                .orElse(0);
    }

    private void updateQuestionOptions(Long questionId, List<QuizOptionsDTO> optionDTOs) {
        // Delete existing options
        quizOptionsRepository.deleteAll(quizOptionsRepository.findByQuestionId(questionId));

        // Create new options
        List<QuizOptions> newOptions = optionDTOs.stream()
                .map(optionDTO -> convertOptionToEntity(optionDTO, questionId))
                .collect(Collectors.toList());

        quizOptionsRepository.saveAll(newOptions);
    }

    private QuizQuestionDTO convertToDTOWithLoadedOptions(QuizQuestion question) {
        QuizQuestionDTO dto = new QuizQuestionDTO();
        dto.setId(question.getId());
        dto.setContent(question.getContent());
        dto.setOrderNumber(question.getOrderNumber());
        dto.setDimension(question.getDimension());
        dto.setQuizId(question.getQuizId());

        if (question.getOptions() != null) {
            List<QuizOptionsDTO> optionDTOs = question.getOptions().stream()
                    .map(this::convertOptionToDTO)
                    .collect(Collectors.toList());
            dto.setOptions(optionDTOs);
        }

        return dto;
    }

    private QuizQuestionDTO convertToDTO(QuizQuestion question) {
        QuizQuestionDTO dto = new QuizQuestionDTO();
        dto.setId(question.getId());
        dto.setContent(question.getContent());
        dto.setOrderNumber(question.getOrderNumber());
        dto.setDimension(question.getDimension());
        dto.setQuizId(question.getQuizId());

        List<QuizOptions> options = quizOptionsRepository.findByQuestionId(question.getId());
        List<QuizOptionsDTO> optionDTOs = options.stream()
                .map(this::convertOptionToDTO)
                .collect(Collectors.toList());
        dto.setOptions(optionDTOs);

        return dto;
    }

    private QuizQuestion convertToEntity(QuizQuestionDTO dto) {
        QuizQuestion question = new QuizQuestion();
        question.setId(dto.getId());
        question.setContent(dto.getContent());
        question.setOrderNumber(dto.getOrderNumber());
        question.setDimension(dto.getDimension());
        question.setQuizId(dto.getQuizId());
        return question;
    }

    private QuizOptionsDTO convertOptionToDTO(QuizOptions option) {
        QuizOptionsDTO dto = new QuizOptionsDTO();
        dto.setId(option.getId());
        dto.setOptionText(option.getOptionText());
        dto.setTargetTrait(option.getTargetTrait());
        dto.setScoreValue(option.getScoreValue());
        dto.setQuestionId(option.getQuestionId());
        return dto;
    }

    private QuizOptions convertOptionToEntity(QuizOptionsDTO dto, Long questionId) {
        QuizOptions option = new QuizOptions();
        option.setId(dto.getId());
        option.setOptionText(dto.getOptionText());
        option.setTargetTrait(dto.getTargetTrait());
        option.setScoreValue(dto.getScoreValue());
        option.setQuestionId(questionId);
        return option;
    }
}

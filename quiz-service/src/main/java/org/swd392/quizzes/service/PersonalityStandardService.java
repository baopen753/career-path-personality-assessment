package org.swd392.quizzes.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swd392.quizzes.repository.PersonalityStandardRepository;
import org.swd392.quizzes.dto.PersonalityStandardDTO;
import org.swd392.quizzes.entity.PersonalityStandard;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalityStandardService {

    private final PersonalityStandardRepository repository;

    public List<PersonalityStandardDTO> getAllPersonalityStandards() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PersonalityStandardDTO getPersonalityStandardById(Long id) {
        PersonalityStandard standard = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personality standard not found with ID: " + id));
        return convertToDTO(standard);
    }

    public List<PersonalityStandardDTO> getByStandard(PersonalityStandard.StandardType standard) {
        return repository.findByStandardOrderByPersonalityCode(standard).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PersonalityStandardDTO getByPersonalityCode(String personalityCode) {
        PersonalityStandard standard = repository.findByPersonalityCode(personalityCode)
                .orElseThrow(() -> new IllegalArgumentException("Personality standard not found with code: " + personalityCode));
        return convertToDTO(standard);
    }

    public PersonalityStandardDTO createPersonalityStandard(PersonalityStandardDTO dto) {
        if (repository.existsByPersonalityCode(dto.getPersonalityCode())) {
            throw new IllegalArgumentException("Personality code already exists: " + dto.getPersonalityCode());
        }
        PersonalityStandard entity = convertToEntity(dto);
        PersonalityStandard saved = repository.save(entity);
        return convertToDTO(saved);
    }

    public PersonalityStandardDTO updatePersonalityStandard(Long id, PersonalityStandardDTO dto) {
        PersonalityStandard existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personality standard not found with ID: " + id));

        existing.setStandard(dto.getStandard());
        existing.setPersonalityCode(dto.getPersonalityCode());
        existing.setNickname(dto.getNickname());
        existing.setKeyTraits(dto.getKeyTraits());
        existing.setDescription(dto.getDescription());
        existing.setCareerMappingPersonality(dto.getCareerMappingPersonality());

        PersonalityStandard updated = repository.save(existing);
        return convertToDTO(updated);
    }

    public void deletePersonalityStandard(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Personality standard not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    private PersonalityStandardDTO convertToDTO(PersonalityStandard entity) {
        return new PersonalityStandardDTO(
                entity.getId(),
                entity.getStandard(),
                entity.getPersonalityCode(),
                entity.getNickname(),
                entity.getKeyTraits(),
                entity.getDescription(),
                entity.getCareerMappingPersonality()
        );
    }

    private PersonalityStandard convertToEntity(PersonalityStandardDTO dto) {
        return PersonalityStandard.builder()
                .id(dto.getId())
                .standard(dto.getStandard())
                .personalityCode(dto.getPersonalityCode())
                .nickname(dto.getNickname())
                .keyTraits(dto.getKeyTraits())
                .description(dto.getDescription())
                .careerMappingPersonality(dto.getCareerMappingPersonality())
                .build();
    }
}

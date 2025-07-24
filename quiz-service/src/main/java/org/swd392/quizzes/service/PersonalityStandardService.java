package org.swd392.quizzes.service;

import org.swd392.quizzes.dto.PersonalityStandardDTO;
import org.swd392.quizzes.entity.PersonalityStandard;

import java.util.List;

public interface PersonalityStandardService {
    List<PersonalityStandardDTO> getAllPersonalityStandards();

    PersonalityStandardDTO getPersonalityStandardById(Long id);

    List<PersonalityStandardDTO> getByStandard(PersonalityStandard.StandardType standard);

    PersonalityStandardDTO getByPersonalityCode(String personalityCode);

    PersonalityStandardDTO updatePersonalityStandard(Long id, PersonalityStandardDTO dto);
}

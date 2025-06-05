package org.swd392.quizzes.repository;

import org.swd392.quizzes.entity.PersonalityStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalityStandardRepository extends JpaRepository<PersonalityStandard, Long> {

    List<PersonalityStandard> findByStandard(PersonalityStandard.StandardType standard);

    Optional<PersonalityStandard> findByPersonalityCode(String personalityCode);

    boolean existsByPersonalityCode(String personalityCode);

    List<PersonalityStandard> findByStandardOrderByPersonalityCode(PersonalityStandard.StandardType standard);
}

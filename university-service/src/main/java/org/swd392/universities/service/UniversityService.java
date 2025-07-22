package org.swd392.universities.service;

import org.swd392.universities.dto.UniversityDTO;
import org.swd392.universities.entity.University;

import java.util.List;
import java.util.Optional;

public interface UniversityService {
    University createUniversity(UniversityDTO dto);
    List<University> getAllUniversities();
    Optional<University> getUniversityById(String id);
    University updateUniversity(String id, UniversityDTO dto);
    void deleteUniversity(String id);
    List<University> findByMajorIn(List<String> majors);
    List<University> findByMajor(String major);
}

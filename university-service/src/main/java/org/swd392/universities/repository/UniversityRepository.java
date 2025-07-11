package org.swd392.universities.repository;

import org.swd392.universities.entity.University;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniversityRepository extends MongoRepository<University, String> {
    List<University> findByMajorContainingIgnoreCase(String major);
    List<University> findByMajorIn(List<String> majors);
}
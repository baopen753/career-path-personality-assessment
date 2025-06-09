package com.sba301.university_service.repository;

import com.sba301.university_service.entity.University;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UniversityRepository extends MongoRepository<University, String> {
}
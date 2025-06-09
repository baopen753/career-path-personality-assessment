package org.swd392.careers.repository;

import org.swd392.careers.entity.Career;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CareerRepository extends MongoRepository<Career, String> {
}
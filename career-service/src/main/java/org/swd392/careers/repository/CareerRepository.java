package org.swd392.careers.repository;

import org.swd392.careers.entity.Career;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerRepository extends MongoRepository<Career, String> {
    List<Career> findByNameContainingIgnoreCase(String name);
    List<Career> findByNameInIgnoreCase(List<String> names);
}
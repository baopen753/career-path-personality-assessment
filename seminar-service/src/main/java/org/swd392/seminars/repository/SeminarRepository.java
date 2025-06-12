package org.swd392.seminars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swd392.seminars.entity.Seminar;

import java.util.List;

public interface SeminarRepository extends JpaRepository<Seminar, Integer> {
    List<Seminar> findByCreatedBy(Integer eventManagerId);
    List<Seminar> findByStatus(String status);
} 
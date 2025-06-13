package org.swd392.seminars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swd392.seminars.entity.Seminar;

import java.util.List;

@Repository
public interface SeminarRepository extends JpaRepository<Seminar, Integer> {
    List<Seminar> findByCreateBy(Integer createBy);
    List<Seminar> findByStatus(Seminar.Status status);
    List<Seminar> findByStatusApprove(Seminar.StatusApprove statusApprove);
} 
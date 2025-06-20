package org.swd392.seminars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swd392.seminars.entity.SeminarTicket;

import java.util.List;

public interface SeminarTicketRepository extends JpaRepository<SeminarTicket, Integer> {
    List<SeminarTicket> findBySeminarId(Integer seminarId);
    List<SeminarTicket> findByUserId(Integer userId);
    boolean existsBySeminarIdAndUserId(Integer seminarId, Integer userId);
    long countBySeminarId(Integer seminarId);
} 
package org.swd392.seminars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swd392.seminars.entity.SeminarTicket;

import java.util.List;

public interface SeminarTicketRepository extends JpaRepository<SeminarTicket, Integer> {
    List<SeminarTicket> findBySeminarId(Integer seminarId);
    List<SeminarTicket> findByUserProfileId(Integer userProfileId);
    boolean existsBySeminarIdAndUserProfileId(Integer seminarId, Integer userProfileId);
    long countBySeminarId(Integer seminarId);
} 
package org.swd392.seminars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.swd392.seminars.entity.SeminarTicket;

import java.util.List;
import java.util.Optional;

public interface SeminarTicketRepository extends JpaRepository<SeminarTicket, Integer> {
    List<SeminarTicket> findBySeminarId(Integer seminarId);
    List<SeminarTicket> findByUserProfileId(Integer userProfileId);
    boolean existsBySeminarIdAndUserProfileId(Integer seminarId, Integer userProfileId);
    long countBySeminarIdAndStatusTrue(Integer seminarId);

    @Query("SELECT st FROM SeminarTicket st WHERE st.seminar.id = :seminarId AND st.userProfileId = :userProfileId")
    Optional<SeminarTicket> findBySeminarIdAndUserProfileId(Integer seminarId, Integer userProfileId);

}
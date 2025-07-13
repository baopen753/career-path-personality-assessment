package org.swd392.seminars.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.swd392.seminars.entity.Seminar;
import org.swd392.seminars.repository.SeminarRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeminarStatusUpdateTask {

    private final SeminarRepository seminarRepository;

    @Scheduled(fixedDelay = 60000) // Check every minute
    @Transactional
    public void updateExpiredSeminars() {
        log.debug("Checking for seminars that have ended...");
        
        LocalDateTime now = LocalDateTime.now();
        
        // Find all seminars that are ONGOING and have passed their ending time
        List<Seminar> expiredSeminars = seminarRepository.findByStatusAndEndingTimeBefore(
            Seminar.Status.ONGOING, now);
        
        for (Seminar seminar : expiredSeminars) {
            log.info("Updating seminar ID: {} status from ONGOING to COMPLETED (ended at: {})", 
                seminar.getId(), seminar.getEndingTime());
            
            seminar.setStatus(Seminar.Status.COMPLETED);
            seminarRepository.save(seminar);
        }
        
        if (!expiredSeminars.isEmpty()) {
            log.info("Updated {} seminars to COMPLETED status", expiredSeminars.size());
        }
    }
} 
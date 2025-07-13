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

    @Scheduled(fixedDelay = 60000) // Chạy mỗi phút
    @Transactional
    public void updateExpiredSeminars() {
        log.debug("[SeminarStatusUpdateTask] Checking for seminars that have ended...");
        LocalDateTime now = LocalDateTime.now();
        List<Seminar> expiredSeminars = seminarRepository.findByStatusAndEndingTimeBefore(
            Seminar.Status.ONGOING, now);
        log.info("[SeminarStatusUpdateTask] Found {} seminars to update (status=ONGOING, ending_time < now)", expiredSeminars.size());
        for (Seminar seminar : expiredSeminars) {
            log.info("[SeminarStatusUpdateTask] Seminar ID: {}, Title: '{}', Status before: {}, Ending time: {}", 
                seminar.getId(), seminar.getTitle(), seminar.getStatus(), seminar.getEndingTime());
            seminar.setStatus(Seminar.Status.COMPLETED);
            seminarRepository.save(seminar);
            log.info("[SeminarStatusUpdateTask] Seminar ID: {} updated to status: COMPLETED", seminar.getId());
        }
        if (expiredSeminars.isEmpty()) {
            log.info("[SeminarStatusUpdateTask] No seminars need to be updated at this time.");
        } else {
            log.info("[SeminarStatusUpdateTask] Updated {} seminars to COMPLETED status", expiredSeminars.size());
        }
    }
} 
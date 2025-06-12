package org.swd392.seminars.payload.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SeminarTicketResponse {
    private Integer id;
    private Integer seminarId; // Only return seminar ID instead of full seminar object
    private Integer userProfileId;
    private LocalDateTime bookingTime;
}

package org.swd392.seminars.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SeminarTicketResponse {
    private Integer id;
    private Integer seminarId;
    private Integer userProfileId;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startingTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime bookingTime;
    
    private boolean status;
}

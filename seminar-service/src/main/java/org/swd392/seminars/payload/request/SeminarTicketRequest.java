package org.swd392.seminars.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SeminarTicketRequest {
    private Integer seminarId;
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startingTime;
    
    @JsonIgnore
    private Integer userProfileId;
} 
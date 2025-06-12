package org.swd392.seminars.payload.request;

import lombok.Data;

@Data
public class SeminarTicketRequest {
    private Integer seminarId;
    private Integer userProfileId;
} 
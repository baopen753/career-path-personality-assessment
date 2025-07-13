package org.swd392.seminars.payload.request;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class SeminarTicketRequest {
    private Integer seminarId;
    private String description;
    @Nullable
    private Long price;
    private Integer userId;
}
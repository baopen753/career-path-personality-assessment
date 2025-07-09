package org.swd392.seminars.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SeminarTicketRequest {
    private Integer seminarId;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Nullable
    private LocalDateTime startingTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Nullable
    private LocalDateTime endingTime;

    @Nullable
    private Long price;

    private Integer userId;
    }
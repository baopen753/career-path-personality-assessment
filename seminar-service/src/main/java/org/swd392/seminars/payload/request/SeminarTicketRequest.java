package org.swd392.seminars.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SeminarTicketRequest {
    private Integer seminarId;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Nullable
    private LocalDate startingTime;

    @Nullable
    private Integer price;

    private Integer userProfileId;
}
package org.swd392.seminars.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.swd392.seminars.entity.Seminar;
import java.time.LocalDateTime;

@Data
public class SeminarResponse {
    private Integer id;
    private String title;
    private String description;
    private Seminar.Status status;
    private Seminar.StatusApprove statusApprove;
    private Integer duration;
    private String meetingUrl;
    private String formUrl;
    private Integer slot;
    private Integer createBy;
    private Double price;
    private String imageUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startingTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endingTime;
}

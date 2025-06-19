package org.swd392.seminars.payload.response;

import lombok.Data;
import org.swd392.seminars.entity.Seminar;

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
}

package org.swd392.seminars.payload.response;

import lombok.Data;

import java.util.UUID;

@Data
public class SeminarResponse {
    private Integer id;
    private String title;
    private String description;
    private String status;
    private Integer duration;
    private String meetingUrl;
    private String formUrl;
    private String videoUrl;
    private Integer slot;
    private Integer createdBy;
    private Double price;
    private String imageUrl;
}

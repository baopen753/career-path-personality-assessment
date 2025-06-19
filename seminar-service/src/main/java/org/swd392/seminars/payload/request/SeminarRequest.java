package org.swd392.seminars.payload.request;

import lombok.Data;

@Data
public class SeminarRequest {
    private String title;
    private String description;
    private Integer duration;
    private Double price;
    private String meetingUrl;
    private String formUrl;
    private Integer slot;
    private String imageUrl;
} 
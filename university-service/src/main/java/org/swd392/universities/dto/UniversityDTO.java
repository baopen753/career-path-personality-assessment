package com.sba301.university_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniversityDTO {
    private String name;
    private String major;
    private String hotline;
    private String location;
    private String description;
    private String picture;
}
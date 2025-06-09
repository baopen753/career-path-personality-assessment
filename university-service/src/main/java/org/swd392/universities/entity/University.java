package com.sba301.university_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "universities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class University {
    @Id
    private String id;  // MongoDB uses String IDs

    private String name;

    private String major;

    private String hotline;

    private String location;

    private String description;

    private String picture;
}
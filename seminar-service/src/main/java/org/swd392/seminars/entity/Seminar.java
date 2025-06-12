package org.swd392.seminars.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "seminars")
public class Seminar {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seminar_seq")
    @SequenceGenerator(name = "seminar_seq", sequenceName = "seminar_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;
    private String title;
    private String description;
    private String status; // PENDING, APPROVED, REJECTED, COMPLETED
    private Integer duration;
    private String meetingUrl;
    private String formUrl;
    private String videoUrl;
    private Integer slot;
    private Integer createdBy;
    private Double price;
    private String imageUrl;
} 
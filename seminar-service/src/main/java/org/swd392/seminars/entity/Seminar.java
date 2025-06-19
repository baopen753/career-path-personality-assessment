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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Double price;

    @Column(name = "meeting_url")
    private String meetingUrl;

    @Column(name = "form_url")
    private String formUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; 

    @Enumerated(EnumType.STRING)
    @Column(name = "status_approve", nullable = false)
    private StatusApprove statusApprove; 

    @Column(nullable = false)
    private Integer slot;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "create_by", nullable = false)
    private Integer createBy;

    public enum Status {
       PENDING,
       ONGOING,
       COMPLETED,
       CANCELLED
    }
    public enum StatusApprove {
        PENDING,
        APPROVED,
        REJECTED
    }
} 

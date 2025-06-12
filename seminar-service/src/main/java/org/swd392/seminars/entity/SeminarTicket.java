package org.swd392.seminars.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "seminar_tickets")
public class SeminarTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seminar_ticket_seq")
    @SequenceGenerator(name = "seminar_ticket_seq", sequenceName = "seminar_ticket_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "seminar_id")
    private Seminar seminar;

    private Integer userProfileId;
    private LocalDateTime bookingTime;
} 
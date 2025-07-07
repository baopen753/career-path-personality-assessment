package org.swd392.seminars.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "seminar_tickets",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seminar_id", "user_id"}, 
            name = "uk_seminar_user")
    }
)
public class SeminarTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seminar_id", nullable = false)
    private Seminar seminar;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "starting_time", nullable = false)
    private LocalDateTime startingTime;

    @Column(name = "ending_time", nullable = false)
    private LocalDateTime endingTime;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime = LocalDateTime.now();

    @Column(nullable = false)
    private boolean status = true;

    @PrePersist
    protected void onCreate() {
        if (bookingTime == null) {
            bookingTime = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeminarTicket)) return false;
        return id != null && id.equals(((SeminarTicket) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 
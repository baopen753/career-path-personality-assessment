package org.swd392.seminars.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "seminars")
public class Seminar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_approve", nullable = false)
    private StatusApprove statusApprove = StatusApprove.PENDING;

    @Column(nullable = false)
    private Integer slot;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "create_by", nullable = false)
    private Integer createBy;

    @OneToMany(mappedBy = "seminar", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SeminarTicket> tickets = new ArrayList<>();

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

    // Helper method to add ticket
    public void addTicket(SeminarTicket ticket) {
        tickets.add(ticket);
        ticket.setSeminar(this);
    }

    // Helper method to remove ticket
    public void removeTicket(SeminarTicket ticket) {
        tickets.remove(ticket);
        ticket.setSeminar(null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Seminar seminar = (Seminar) o;
        return getId() != null && Objects.equals(getId(), seminar.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

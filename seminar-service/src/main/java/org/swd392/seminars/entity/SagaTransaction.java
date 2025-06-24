package org.swd392.seminars.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "saga_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer userId;
    
    @Column(nullable = false)
    private Integer seminarId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;
    
    @Column
    private String paymentOrderCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStep currentStep;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Getter
    public enum SagaStatus {
        PENDING("Transaction is pending"),
        BOOKING_COMPLETED("Ticket booking completed"),
        PAYMENT_PENDING("Payment is pending"),
        COMPLETED("Transaction completed successfully"),
        FAILED("Transaction failed");
        
        private final String description;
        
        SagaStatus(String description) {
            this.description = description;
        }

        public boolean isCompleted() {
            return this == COMPLETED;
        }
        
        public boolean isFailed() {
            return this == FAILED;
        }
        
        public boolean isPending() {
            return this == PENDING || this == PAYMENT_PENDING;
        }
    }
    
    public enum SagaStep {
        BOOKING_INITIATED,
        BOOKING_COMPLETED,
        PAYMENT_INITIATED,
        PAYMENT_PENDING_EXTERNAL,
        PAYMENT_COMPLETED,
        COMPENSATION_REQUIRED
    }
    
    // Helper methods for the entity
    public boolean isCompleted() {
        return status.isCompleted();
    }
    
    public boolean isFailed() {
        return status.isFailed();
    }
    
    public boolean isPending() {
        return status.isPending();
    }
} 
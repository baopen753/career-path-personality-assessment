package org.swd392.seminars.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketBookedEvent {
    private Integer userId;
    private String email;
    private String fullName;
    private String paymentOrderCode;
    private String status;
    private LocalDateTime createdAt;
}

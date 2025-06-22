package org.swd392.seminars.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPendingEvent {
    private Long sagaId;
    private String paymentReference;
} 
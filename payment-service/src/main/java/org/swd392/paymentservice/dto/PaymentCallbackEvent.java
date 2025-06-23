package org.swd392.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCallbackEvent {
    private Long paymentOrderCode;
    private boolean success;
    private String message;
} 
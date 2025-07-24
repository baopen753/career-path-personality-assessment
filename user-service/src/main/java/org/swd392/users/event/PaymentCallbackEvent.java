package org.swd392.users.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackEvent {
    private String paymentOrderCode;
    private boolean success;
    private String message;
} 
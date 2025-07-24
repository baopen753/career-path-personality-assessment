package org.swd392.seminars.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackEvent {
    private Long paymentOrderCode;  // Changed to Long to match payment-service
    private boolean success;
    private String message;
    
    // Helper method to get String version
    public String getPaymentOrderCodeAsString() {
        return paymentOrderCode != null ? paymentOrderCode.toString() : null;
    }
} 
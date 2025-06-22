package org.swd392.seminars.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiationResponse {
    private Long sagaId;
    private String orderCode;
    private String checkoutUrl;
    private String status;
    private String message;
} 
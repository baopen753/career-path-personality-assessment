package org.swd392.paymentservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponseDto {
    private long orderCode;
    private String checkoutUrl;
}

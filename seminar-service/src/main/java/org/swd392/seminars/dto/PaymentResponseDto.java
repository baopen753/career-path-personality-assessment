package org.swd392.seminars.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponseDto {
    private Long orderCode;
    private String checkoutUrl;
}

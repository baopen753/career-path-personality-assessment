package org.swd392.users.dto;

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

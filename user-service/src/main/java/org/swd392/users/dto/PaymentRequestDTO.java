package org.swd392.users.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PaymentRequestDTO {
    private Double amount;
    private String description;
}
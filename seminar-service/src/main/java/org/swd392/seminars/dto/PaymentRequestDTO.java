package org.swd392.seminars.dto;

import lombok.Builder;

@Builder
public class PaymentRequestDTO {
    private Integer amount;
    private String description;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(Integer amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

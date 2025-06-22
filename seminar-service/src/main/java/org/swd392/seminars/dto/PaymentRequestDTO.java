package org.swd392.seminars.dto;

import lombok.Builder;

@Builder
public class PaymentRequestDTO {
    private Integer amount;
    private String description;
    private Long sagaId;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(Integer amount, String description, Long sagaId) {
        this.amount = amount;
        this.description = description;
        this.sagaId = sagaId;
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

    public Long getSagaId() {
        return sagaId;
    }

    public void setSagaId(Long sagaId) {
        this.sagaId = sagaId;
    }
}

package org.swd392.paymentservice.dto;


public class PaymentRequestDTO {
    private Integer amount;
    private String description;
    private String paymentFlow;
    private Integer referenceId;


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
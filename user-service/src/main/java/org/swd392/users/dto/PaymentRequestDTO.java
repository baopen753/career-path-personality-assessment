package org.swd392.users.dto;

public class PaymentRequestDTO {
    private long amount;
    private String description;

    public PaymentRequestDTO() {
    }
    public PaymentRequestDTO(long amount, String description) {
        this.amount = amount;
        this.description = description;
    }
    public long getAmount() {
        return amount;
    }
    public void setAmount(long amount) {
        this.amount = amount;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

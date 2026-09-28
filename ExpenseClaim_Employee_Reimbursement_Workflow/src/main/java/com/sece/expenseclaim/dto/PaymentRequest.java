package com.sece.expenseclaim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotNull
    private Long financeId;
    @NotBlank
    private String paymentReference;

    public Long getFinanceId() { return financeId; }
    public String getPaymentReference() { return paymentReference; }
    public void setFinanceId(Long financeId) { this.financeId = financeId; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
}

package com.sece.expenseclaim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
    @NotNull Long financeId,
    @NotBlank String paymentReference
) {}

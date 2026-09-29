package com.sece.expenseclaim.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record ExpenseItemRequest(
    @NotBlank String category,
    @NotBlank String description,
    @DecimalMin(value="0.01") BigDecimal amount
) {}

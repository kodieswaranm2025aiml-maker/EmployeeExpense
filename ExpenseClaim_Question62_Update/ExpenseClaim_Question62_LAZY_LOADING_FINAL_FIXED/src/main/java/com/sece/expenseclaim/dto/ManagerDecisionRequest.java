package com.sece.expenseclaim.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record ManagerDecisionRequest(
    @NotNull Long managerId,
    @NotNull Boolean overridePolicy,
    @NotBlank String remarks
) {}

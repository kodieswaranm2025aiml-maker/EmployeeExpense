package com.sece.expenseclaim.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ClaimCreateRequest(
    @NotNull Long employeeId,
    String title,
    String description,
    @NotEmpty List<@Valid ExpenseItemRequest> items
) {}

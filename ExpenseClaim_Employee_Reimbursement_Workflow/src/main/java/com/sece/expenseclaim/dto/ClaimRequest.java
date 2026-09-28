package com.sece.expenseclaim.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ClaimRequest {
    @NotNull
    private Long employeeId;
    private Long managerId;
    @NotEmpty
    @Valid
    private List<ExpenseItemRequest> items;

    public Long getEmployeeId() { return employeeId; }
    public Long getManagerId() { return managerId; }
    public List<ExpenseItemRequest> getItems() { return items; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public void setItems(List<ExpenseItemRequest> items) { this.items = items; }
}

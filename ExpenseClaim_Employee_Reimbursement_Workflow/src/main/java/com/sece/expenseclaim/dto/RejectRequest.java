package com.sece.expenseclaim.dto;

import jakarta.validation.constraints.NotNull;

public class RejectRequest {
    @NotNull
    private Long managerId;
    private String remarks;

    public Long getManagerId() { return managerId; }
    public String getRemarks() { return remarks; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

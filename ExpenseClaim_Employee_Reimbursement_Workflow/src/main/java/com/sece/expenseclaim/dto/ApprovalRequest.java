package com.sece.expenseclaim.dto;

import jakarta.validation.constraints.NotNull;

public class ApprovalRequest {
    @NotNull
    private Long managerId;
    private boolean overridePolicyLimit;
    private String remarks;

    public Long getManagerId() { return managerId; }
    public boolean isOverridePolicyLimit() { return overridePolicyLimit; }
    public String getRemarks() { return remarks; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public void setOverridePolicyLimit(boolean overridePolicyLimit) { this.overridePolicyLimit = overridePolicyLimit; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

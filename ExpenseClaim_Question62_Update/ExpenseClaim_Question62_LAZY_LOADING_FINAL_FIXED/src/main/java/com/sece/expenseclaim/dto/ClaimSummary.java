package com.sece.expenseclaim.dto;

import com.sece.expenseclaim.entity.ClaimStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClaimSummary(Long id,String claimNumber,Long employeeId,String employeeName,String title,BigDecimal totalAmount,boolean policyViolation,boolean managerOverride,ClaimStatus status,LocalDateTime submittedAt,String paymentReference) {}

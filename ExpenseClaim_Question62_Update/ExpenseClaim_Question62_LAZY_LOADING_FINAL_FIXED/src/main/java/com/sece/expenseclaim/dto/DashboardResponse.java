package com.sece.expenseclaim.dto;
public record DashboardResponse(long pending,long approved,long paid,long rejected,long violations) {}

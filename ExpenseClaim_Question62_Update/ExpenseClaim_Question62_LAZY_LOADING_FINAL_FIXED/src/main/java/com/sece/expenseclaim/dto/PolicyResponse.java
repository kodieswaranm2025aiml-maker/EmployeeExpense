package com.sece.expenseclaim.dto;
import java.math.BigDecimal;
public record PolicyResponse(Long id,String category,BigDecimal maxAmount,boolean active) {}

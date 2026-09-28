package com.sece.expenseclaim.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class PolicyService {
    private final Map<String, BigDecimal> limits = new LinkedHashMap<>();

    public PolicyService() {
        limits.put("TRAVEL", new BigDecimal("5000.00"));
        limits.put("FOOD", new BigDecimal("2000.00"));
        limits.put("HOTEL", new BigDecimal("8000.00"));
        limits.put("LOCAL_TRAVEL", new BigDecimal("1500.00"));
        limits.put("OTHER", new BigDecimal("3000.00"));
    }

    public BigDecimal getLimit(String category) {
        return limits.getOrDefault(category.trim().toUpperCase(Locale.ROOT), limits.get("OTHER"));
    }

    public Map<String, BigDecimal> getAllLimits() {
        return limits;
    }
}

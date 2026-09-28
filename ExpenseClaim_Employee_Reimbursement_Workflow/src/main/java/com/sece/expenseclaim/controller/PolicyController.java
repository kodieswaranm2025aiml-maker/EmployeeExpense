package com.sece.expenseclaim.controller;

import com.sece.expenseclaim.service.PolicyService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/policies")
@CrossOrigin(origins = "*")
public class PolicyController {
    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public Map<String, BigDecimal> policies() {
        return policyService.getAllLimits();
    }
}

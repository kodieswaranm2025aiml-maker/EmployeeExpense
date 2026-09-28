package com.sece.expenseclaim.controller;

import com.sece.expenseclaim.dto.*;
import com.sece.expenseclaim.entity.Claim;
import com.sece.expenseclaim.entity.ClaimStatus;
import com.sece.expenseclaim.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "*")
public class ClaimController {
    private final ClaimService service;

    public ClaimController(ClaimService service) {
        this.service = service;
    }

    @PostMapping
    public Claim create(@Valid @RequestBody ClaimRequest request) {
        return service.create(request);
    }

    @PostMapping("/{id}/submit")
    public Claim submit(@PathVariable Long id) {
        return service.submit(id);
    }

    @PostMapping("/{id}/approve")
    public Claim approve(@PathVariable Long id, @Valid @RequestBody ApprovalRequest request) {
        return service.approve(id, request);
    }

    @PostMapping("/{id}/reject")
    public Claim reject(@PathVariable Long id, @Valid @RequestBody RejectRequest request) {
        return service.reject(id, request);
    }

    @PostMapping("/{id}/pay")
    public Claim pay(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        return service.pay(id, request);
    }

    @GetMapping
    public List<Claim> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public Claim get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/page")
    public Page<Claim> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.page(page, size);
    }

    @GetMapping("/pending/manager")
    public List<Claim> pendingManager() {
        return service.pendingManager();
    }

    @GetMapping("/pending/employee/{employeeId}")
    public List<Claim> pendingEmployee(@PathVariable Long employeeId) {
        return service.pendingForEmployee(employeeId);
    }

    @GetMapping("/pending/manager/{managerId}")
    public List<Claim> pendingManagerForUser(@PathVariable Long managerId) {
        return service.pendingForManager(managerId);
    }

    @GetMapping("/employee/{employeeId}")
    public List<Claim> byEmployee(@PathVariable Long employeeId) {
        return service.byEmployee(employeeId);
    }

    @GetMapping("/status/{status}")
    public List<Claim> byStatus(@PathVariable ClaimStatus status) {
        return service.all().stream().filter(c -> c.getStatus() == status).toList();
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return service.summary();
    }
}

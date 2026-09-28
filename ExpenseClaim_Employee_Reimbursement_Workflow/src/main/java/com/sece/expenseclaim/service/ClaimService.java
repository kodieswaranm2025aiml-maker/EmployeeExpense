package com.sece.expenseclaim.service;

import com.sece.expenseclaim.dto.*;
import com.sece.expenseclaim.entity.*;
import com.sece.expenseclaim.exception.BusinessException;
import com.sece.expenseclaim.exception.ResourceNotFoundException;
import com.sece.expenseclaim.repository.ApprovalStepRepository;
import com.sece.expenseclaim.repository.ClaimRepository;
import com.sece.expenseclaim.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final EmployeeRepository employeeRepository;
    private final ApprovalStepRepository approvalStepRepository;
    private final PolicyService policyService;

    public ClaimService(ClaimRepository claimRepository,
                        EmployeeRepository employeeRepository,
                        ApprovalStepRepository approvalStepRepository,
                        PolicyService policyService) {
        this.claimRepository = claimRepository;
        this.employeeRepository = employeeRepository;
        this.approvalStepRepository = approvalStepRepository;
        this.policyService = policyService;
    }

    @Transactional
    public Claim create(ClaimRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + request.getEmployeeId()));

        if (employee.getRole() != Role.EMPLOYEE) {
            throw new BusinessException("Only an EMPLOYEE can submit an expense claim.");
        }

        Claim claim = new Claim();
        claim.setEmployee(employee);

        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + request.getManagerId()));
            if (manager.getRole() != Role.MANAGER) {
                throw new BusinessException("Selected manager does not have MANAGER role.");
            }
            claim.setManager(manager);
        }

        BigDecimal total = BigDecimal.ZERO;
        boolean violation = false;

        for (ExpenseItemRequest itemRequest : request.getItems()) {
            ExpenseItem item = new ExpenseItem();
            item.setClaim(claim);
            item.setCategory(itemRequest.getCategory().trim().toUpperCase(Locale.ROOT));
            item.setDescription(itemRequest.getDescription());
            item.setAmount(itemRequest.getAmount());

            BigDecimal limit = policyService.getLimit(item.getCategory());
            boolean exceeds = item.getAmount().compareTo(limit) > 0;
            item.setPolicyLimit(limit);
            item.setExceedsPolicyLimit(exceeds);

            claim.getExpenseItems().add(item);
            total = total.add(item.getAmount());
            violation = violation || exceeds;
        }

        claim.setTotalAmount(total);
        claim.setPolicyViolation(violation);
        claim.setStatus(ClaimStatus.DRAFT);
        return claimRepository.save(claim);
    }

    @Transactional
    public Claim submit(Long id) {
        Claim claim = get(id);
        if (claim.getStatus() != ClaimStatus.DRAFT) {
            throw new BusinessException("Only DRAFT claims can be submitted.");
        }
        if (claim.getManager() == null) {
            throw new BusinessException("A manager must be assigned before submission.");
        }

        claim.setStatus(ClaimStatus.PENDING_MANAGER);
        claim.setSubmittedAt(LocalDateTime.now());

        ApprovalStep step = new ApprovalStep();
        step.setClaim(claim);
        step.setStage(ApprovalStage.MANAGER);
        step.setStatus(ApprovalStatus.PENDING);
        approvalStepRepository.save(step);

        return claimRepository.save(claim);
    }

    @Transactional
    public Claim approve(Long id, ApprovalRequest request) {
        Claim claim = get(id);
        if (claim.getStatus() != ClaimStatus.PENDING_MANAGER) {
            throw new BusinessException("Claim is not waiting for manager approval.");
        }

        Employee manager = employeeRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + request.getManagerId()));

        if (manager.getRole() != Role.MANAGER) {
            throw new BusinessException("Only a MANAGER can approve a claim.");
        }
        if (claim.getManager() == null || !claim.getManager().getId().equals(manager.getId())) {
            throw new BusinessException("This manager is not assigned to the claim.");
        }

        if (claim.isPolicyViolation() && !request.isOverridePolicyLimit()) {
            throw new BusinessException(
                    "Policy limit exceeded. Manager override is required before approval."
            );
        }

        ApprovalStep step = approvalStepRepository
                .findTopByClaimIdAndStageOrderByIdDesc(id, ApprovalStage.MANAGER)
                .orElseThrow(() -> new BusinessException("Manager approval step not found."));

        step.setStatus(ApprovalStatus.APPROVED);
        step.setActedBy(manager);
        step.setRemarks(request.getRemarks());
        step.setActedAt(LocalDateTime.now());

        claim.setManagerRemarks(request.getRemarks());
        claim.setStatus(ClaimStatus.APPROVED);
        approvalStepRepository.save(step);

        return claimRepository.save(claim);
    }

    @Transactional
    public Claim reject(Long id, RejectRequest request) {
        Claim claim = get(id);
        if (claim.getStatus() != ClaimStatus.PENDING_MANAGER) {
            throw new BusinessException("Claim is not waiting for manager approval.");
        }

        Employee manager = employeeRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + request.getManagerId()));

        if (manager.getRole() != Role.MANAGER) {
            throw new BusinessException("Only a MANAGER can reject a claim.");
        }
        if (claim.getManager() == null || !claim.getManager().getId().equals(manager.getId())) {
            throw new BusinessException("This manager is not assigned to the claim.");
        }

        ApprovalStep step = approvalStepRepository
                .findTopByClaimIdAndStageOrderByIdDesc(id, ApprovalStage.MANAGER)
                .orElseThrow(() -> new BusinessException("Manager approval step not found."));

        step.setStatus(ApprovalStatus.REJECTED);
        step.setActedBy(manager);
        step.setRemarks(request.getRemarks());
        step.setActedAt(LocalDateTime.now());

        claim.setManagerRemarks(request.getRemarks());
        claim.setStatus(ClaimStatus.REJECTED);
        approvalStepRepository.save(step);

        return claimRepository.save(claim);
    }

    @Transactional
    public Claim pay(Long id, PaymentRequest request) {
        Claim claim = get(id);

        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new BusinessException(
                    "Finance can mark a claim as PAID only after manager approval."
            );
        }

        Employee finance = employeeRepository.findById(request.getFinanceId())
                .orElseThrow(() -> new ResourceNotFoundException("Finance employee not found: " + request.getFinanceId()));

        if (finance.getRole() != Role.FINANCE) {
            throw new BusinessException("Only a FINANCE user can mark a claim as paid.");
        }

        claim.setPaymentReference(request.getPaymentReference());
        claim.setPaidAt(LocalDateTime.now());
        claim.setStatus(ClaimStatus.PAID);

        ApprovalStep step = new ApprovalStep();
        step.setClaim(claim);
        step.setStage(ApprovalStage.FINANCE);
        step.setStatus(ApprovalStatus.COMPLETED);
        step.setActedBy(finance);
        step.setRemarks("Payment completed. Reference: " + request.getPaymentReference());
        step.setActedAt(LocalDateTime.now());
        approvalStepRepository.save(step);

        return claimRepository.save(claim);
    }

    public Claim get(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + id));
    }

    public List<Claim> all() {
        return claimRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Page<Claim> page(int page, int size) {
        return claimRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );
    }

    public List<Claim> pendingManager() {
        return claimRepository.findByStatusOrderBySubmittedAtAsc(ClaimStatus.PENDING_MANAGER);
    }

    public List<Claim> pendingForEmployee(Long employeeId) {
        return claimRepository.findByEmployeeIdAndStatusOrderBySubmittedAtDesc(
                employeeId, ClaimStatus.PENDING_MANAGER
        );
    }

    public List<Claim> pendingForManager(Long managerId) {
        return claimRepository.findByManagerIdAndStatusOrderBySubmittedAtAsc(
                managerId, ClaimStatus.PENDING_MANAGER
        );
    }

    public List<Claim> byEmployee(Long employeeId) {
        return claimRepository.findByEmployeeIdOrderBySubmittedAtDesc(employeeId);
    }

    public java.util.Map<String, Object> summary() {
        long total = claimRepository.count();
        long pending = claimRepository.findByStatusOrderBySubmittedAtAsc(ClaimStatus.PENDING_MANAGER).size();
        long approved = claimRepository.findByStatusOrderBySubmittedAtAsc(ClaimStatus.APPROVED).size();
        long paid = claimRepository.findByStatusOrderBySubmittedAtAsc(ClaimStatus.PAID).size();
        long rejected = claimRepository.findByStatusOrderBySubmittedAtAsc(ClaimStatus.REJECTED).size();

        return java.util.Map.of(
                "totalClaims", total,
                "pendingManager", pending,
                "approved", approved,
                "paid", paid,
                "rejected", rejected
        );
    }
}

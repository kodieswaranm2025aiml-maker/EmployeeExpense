package com.sece.expenseclaim.service;

import com.sece.expenseclaim.dto.*;
import com.sece.expenseclaim.entity.*;
import com.sece.expenseclaim.exception.BusinessRuleException;
import com.sece.expenseclaim.exception.ResourceNotFoundException;
import com.sece.expenseclaim.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ClaimService {
    private final ClaimRepository claimRepo;
    private final EmployeeRepository employeeRepo;
    private final CategoryPolicyRepository policyRepo;
    private final ApprovalStepRepository approvalRepo;
    private final AuditService audit;

    public ClaimService(ClaimRepository claimRepo, EmployeeRepository employeeRepo, CategoryPolicyRepository policyRepo, ApprovalStepRepository approvalRepo, AuditService audit){
        this.claimRepo=claimRepo; this.employeeRepo=employeeRepo; this.policyRepo=policyRepo; this.approvalRepo=approvalRepo; this.audit=audit;
    }

    @Transactional
    public Claim create(ClaimCreateRequest req){
        Employee employee=employeeRepo.findById(req.employeeId()).orElseThrow(()->new ResourceNotFoundException("Employee not found: "+req.employeeId()));
        if(employee.getRole()!=Role.EMPLOYEE) throw new BusinessRuleException("Only an employee can submit a reimbursement claim.");
        if(req.items()==null || req.items().isEmpty()) throw new BusinessRuleException("A claim must contain at least one expense item.");

        Claim claim=new Claim();
        claim.setClaimNumber("CLM-"+System.currentTimeMillis());
        claim.setEmployee(employee); claim.setTitle(req.title()==null || req.title().isBlank() ? "Expense Reimbursement Claim" : req.title().trim()); claim.setDescription(req.description());
        claim.setSubmittedAt(LocalDateTime.now()); claim.setStatus(ClaimStatus.PENDING_MANAGER); claim.setManagerOverride(false);
        BigDecimal total=BigDecimal.ZERO; boolean violation=false;
        for(ExpenseItemRequest itemReq:req.items()){
            CategoryPolicy policy=policyRepo.findByCategoryIgnoreCaseAndActiveTrue(itemReq.category().trim())
                .orElseThrow(()->new ResourceNotFoundException("No active policy found for category: "+itemReq.category()));
            BigDecimal amount=itemReq.amount();
            boolean over=amount.compareTo(policy.getMaxAmount())>0;
            ExpenseItem item=new ExpenseItem(); item.setClaim(claim); item.setCategory(policy.getCategory()); item.setDescription(itemReq.description().trim()); item.setAmount(amount); item.setPolicyLimit(policy.getMaxAmount()); item.setOverLimit(over);
            claim.getExpenseItems().add(item); total=total.add(amount); violation=violation||over;
        }
        claim.setTotalAmount(total); claim.setPolicyViolation(violation);
        ApprovalStep pending=new ApprovalStep(); pending.setClaim(claim); pending.setStepType(ApprovalStepType.MANAGER_APPROVAL); pending.setAction(ApprovalAction.PENDING); pending.setOverrideGranted(false); claim.getApprovalSteps().add(pending);
        Claim saved=claimRepo.save(claim);
        audit.log("CLAIM",saved.getId(),"SUBMITTED",employee.getName(),"Claim "+saved.getClaimNumber()+" submitted; total="+total+"; policyViolation="+violation);
        return saved;
    }

    @Transactional(readOnly=true)
    public Claim get(Long id){return claimRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Claim not found: "+id));}

    /**
     * Safe DTO lookup. The controller must never serialize a detached JPA Claim/Employee
     * graph. This method resolves the employee name while the repository call is active
     * and returns only primitive/DTO data to the web layer.
     */
    @Transactional(readOnly=true)
    public ClaimSummary getSummary(Long id){
        Claim claim = claimRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Claim not found: "+id));
        return summary(claim);
    }

    @Transactional(readOnly=true)
    public Page<ClaimSummary> page(ClaimStatus status, Long employeeId, Pageable pageable){
        Page<Claim> page;
        if(status!=null && employeeId!=null) page=claimRepo.findByEmployeeIdAndStatus(employeeId,status,pageable);
        else if(status!=null) page=claimRepo.findByStatus(status,pageable);
        else if(employeeId!=null) page=claimRepo.findByEmployeeId(employeeId,pageable);
        else page=claimRepo.findAll(pageable);
        return page.map(this::summary);
    }

    @Transactional(readOnly=true)
    public ClaimSummary summary(Claim c){
        // Employee is LAZY on Claim. This mapping is deliberately done inside a
        // transactional service method, so no Hibernate proxy escapes to Jackson.
        Employee employee = c.getEmployee();
        return new ClaimSummary(c.getId(),c.getClaimNumber(),employee.getId(),employee.getName(),c.getTitle(),c.getTotalAmount(),c.isPolicyViolation(),c.isManagerOverride(),c.getStatus(),c.getSubmittedAt(),c.getPaymentReference());
    }

    @Transactional
    public ClaimSummary createSummary(ClaimCreateRequest req){ return summary(create(req)); }

    @Transactional
    public ClaimSummary decideSummary(Long id, ManagerDecisionRequest req){ return summary(decide(id, req)); }

    @Transactional
    public ClaimSummary rejectSummary(Long id, ManagerDecisionRequest req){ return summary(reject(id, req)); }

    @Transactional
    public ClaimSummary paySummary(Long id, PaymentRequest req){ return summary(pay(id, req)); }

    @Transactional
    public Claim decide(Long id, ManagerDecisionRequest req){
        Claim claim=get(id); Employee manager=employeeRepo.findById(req.managerId()).orElseThrow(()->new ResourceNotFoundException("Manager not found: "+req.managerId()));
        if(manager.getRole()!=Role.MANAGER) throw new BusinessRuleException("Only a MANAGER can approve or reject a claim.");
        if(req.remarks()==null || req.remarks().isBlank()) throw new BusinessRuleException("Manager remarks are required.");
        // Approval is intentionally idempotent. If the browser has a stale queue row and
        // the claim was already approved, simply return the approved claim instead of
        // showing the confusing "not pending" error.
        if(claim.getStatus()==ClaimStatus.APPROVED){
            return claim;
        }
        if(claim.getStatus()!=ClaimStatus.PENDING_MANAGER) throw new BusinessRuleException("Claim is not pending manager approval. Current status: "+claim.getStatus());
        boolean override=Boolean.TRUE.equals(req.overridePolicy());
        String normalized=req.remarks().trim();
        if(claim.isPolicyViolation() && !override) throw new BusinessRuleException("Policy violation detected. Manager override is required before approval.");
        if(override) claim.setManagerOverride(true);
        claim.setManagerRemarks(normalized);
        ApprovalStep step=new ApprovalStep(); step.setClaim(claim); step.setStepType(ApprovalStepType.MANAGER_APPROVAL); step.setActor(manager); step.setActedAt(LocalDateTime.now()); step.setRemarks(normalized); step.setOverrideGranted(override);
        if(override || !claim.isPolicyViolation()){
            claim.setStatus(ClaimStatus.APPROVED); claim.setApprovedAt(LocalDateTime.now()); step.setAction(ApprovalAction.APPROVED);
            audit.log("CLAIM",id,"MANAGER_APPROVED",manager.getName(),"Approved with override="+override+". Remarks: "+normalized);
        } else { throw new BusinessRuleException("Invalid manager decision."); }
        approvalRepo.save(step); return claimRepo.save(claim);
    }

    @Transactional
    public Claim reject(Long id, ManagerDecisionRequest req){
        Claim claim=get(id); Employee manager=employeeRepo.findById(req.managerId()).orElseThrow(()->new ResourceNotFoundException("Manager not found: "+req.managerId()));
        if(manager.getRole()!=Role.MANAGER) throw new BusinessRuleException("Only a MANAGER can reject a claim.");
        if(claim.getStatus()!=ClaimStatus.PENDING_MANAGER) throw new BusinessRuleException("Claim is not pending manager approval.");
        if(req.remarks()==null || req.remarks().isBlank()) throw new BusinessRuleException("Manager remarks are required for rejection.");
        claim.setStatus(ClaimStatus.REJECTED); claim.setRejectedAt(LocalDateTime.now()); claim.setManagerRemarks(req.remarks().trim());
        ApprovalStep step=new ApprovalStep(); step.setClaim(claim); step.setActor(manager); step.setStepType(ApprovalStepType.MANAGER_APPROVAL); step.setAction(ApprovalAction.REJECTED); step.setRemarks(req.remarks().trim()); step.setOverrideGranted(false); step.setActedAt(LocalDateTime.now()); approvalRepo.save(step);
        audit.log("CLAIM",id,"MANAGER_REJECTED",manager.getName(),"Rejected. Remarks: "+req.remarks().trim()); return claimRepo.save(claim);
    }

    @Transactional
    public Claim pay(Long id, PaymentRequest req){
        Claim claim=get(id); Employee finance=employeeRepo.findById(req.financeId()).orElseThrow(()->new ResourceNotFoundException("Finance user not found: "+req.financeId()));
        if(finance.getRole()!=Role.FINANCE) throw new BusinessRuleException("Only a FINANCE user can mark a claim as paid.");
        if(claim.getStatus()!=ClaimStatus.APPROVED) throw new BusinessRuleException("Finance can only pay a claim after manager approval. Current status: "+claim.getStatus());
        if(req.paymentReference()==null || req.paymentReference().isBlank()) throw new BusinessRuleException("Payment reference is required.");
        claim.setPaymentReference(req.paymentReference().trim()); claim.setPaidAt(LocalDateTime.now()); claim.setStatus(ClaimStatus.PAID);
        audit.log("CLAIM",id,"PAYMENT_COMPLETED",finance.getName(),"Payment reference: "+req.paymentReference().trim()); return claimRepo.save(claim);
    }

    @Transactional(readOnly=true)
    public DashboardResponse dashboard(){
        return new DashboardResponse(claimRepo.countByStatus(ClaimStatus.PENDING_MANAGER),claimRepo.countByStatus(ClaimStatus.APPROVED),claimRepo.countByStatus(ClaimStatus.PAID),claimRepo.countByStatus(ClaimStatus.REJECTED),claimRepo.countByPolicyViolationTrue());
    }
}

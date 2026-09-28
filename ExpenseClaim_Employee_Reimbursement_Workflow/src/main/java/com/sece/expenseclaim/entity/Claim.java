package com.sece.expenseclaim.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Employee employee;

    @ManyToOne
    private Employee manager;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.DRAFT;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean policyViolation;

    private String managerRemarks;
    private String paymentReference;
    private LocalDateTime submittedAt;
    private LocalDateTime paidAt;

    @JsonManagedReference("claim-items")
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseItem> expenseItems = new ArrayList<>();

    @JsonManagedReference("claim-approvals")
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprovalStep> approvalSteps = new ArrayList<>();

    public Long getId() { return id; }
    public Employee getEmployee() { return employee; }
    public Employee getManager() { return manager; }
    public ClaimStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public boolean isPolicyViolation() { return policyViolation; }
    public String getManagerRemarks() { return managerRemarks; }
    public String getPaymentReference() { return paymentReference; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public List<ExpenseItem> getExpenseItems() { return expenseItems; }
    public List<ApprovalStep> getApprovalSteps() { return approvalSteps; }

    public void setId(Long id) { this.id = id; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public void setManager(Employee manager) { this.manager = manager; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setPolicyViolation(boolean policyViolation) { this.policyViolation = policyViolation; }
    public void setManagerRemarks(String managerRemarks) { this.managerRemarks = managerRemarks; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}

package com.sece.expenseclaim.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="claims")
public class Claim {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=40) private String claimNumber;
    @JsonIgnore @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="employee_id", nullable=false) private Employee employee;
    @Column(nullable=false, length=150) private String title;
    @Column(length=1000) private String description;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal totalAmount=BigDecimal.ZERO;
    @Column(nullable=false) private boolean policyViolation;
    @Column(nullable=false) private boolean managerOverride;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private ClaimStatus status;
    @Column(length=500) private String managerRemarks;
    @Column(length=80) private String paymentReference;
    private LocalDateTime submittedAt, approvedAt, rejectedAt, paidAt;
    @OneToMany(mappedBy="claim", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ExpenseItem> expenseItems=new ArrayList<>();
    @JsonIgnore @OneToMany(mappedBy="claim", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ApprovalStep> approvalSteps=new ArrayList<>();

    public Claim() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getClaimNumber(){return claimNumber;} public void setClaimNumber(String v){claimNumber=v;}
    public Employee getEmployee(){return employee;} public void setEmployee(Employee v){employee=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){totalAmount=v;}
    public boolean isPolicyViolation(){return policyViolation;} public void setPolicyViolation(boolean v){policyViolation=v;}
    public boolean isManagerOverride(){return managerOverride;} public void setManagerOverride(boolean v){managerOverride=v;}
    public ClaimStatus getStatus(){return status;} public void setStatus(ClaimStatus v){status=v;}
    public String getManagerRemarks(){return managerRemarks;} public void setManagerRemarks(String v){managerRemarks=v;}
    public String getPaymentReference(){return paymentReference;} public void setPaymentReference(String v){paymentReference=v;}
    public LocalDateTime getSubmittedAt(){return submittedAt;} public void setSubmittedAt(LocalDateTime v){submittedAt=v;}
    public LocalDateTime getApprovedAt(){return approvedAt;} public void setApprovedAt(LocalDateTime v){approvedAt=v;}
    public LocalDateTime getRejectedAt(){return rejectedAt;} public void setRejectedAt(LocalDateTime v){rejectedAt=v;}
    public LocalDateTime getPaidAt(){return paidAt;} public void setPaidAt(LocalDateTime v){paidAt=v;}
    public List<ExpenseItem> getExpenseItems(){return expenseItems;} public void setExpenseItems(List<ExpenseItem> v){expenseItems=v;}
    public List<ApprovalStep> getApprovalSteps(){return approvalSteps;} public void setApprovalSteps(List<ApprovalStep> v){approvalSteps=v;}
}

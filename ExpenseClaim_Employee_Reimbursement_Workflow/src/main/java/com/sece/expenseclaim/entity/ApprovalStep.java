package com.sece.expenseclaim.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_steps")
public class ApprovalStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("claim-approvals")
    @ManyToOne(optional = false)
    private Claim claim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStage stage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @ManyToOne
    private Employee actedBy;

    private String remarks;
    private LocalDateTime actedAt;

    public Long getId() { return id; }
    @JsonIgnore
    public Claim getClaim() { return claim; }
    public ApprovalStage getStage() { return stage; }
    public ApprovalStatus getStatus() { return status; }
    public Employee getActedBy() { return actedBy; }
    public String getRemarks() { return remarks; }
    public LocalDateTime getActedAt() { return actedAt; }

    public void setId(Long id) { this.id = id; }
    public void setClaim(Claim claim) { this.claim = claim; }
    public void setStage(ApprovalStage stage) { this.stage = stage; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
    public void setActedBy(Employee actedBy) { this.actedBy = actedBy; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public void setActedAt(LocalDateTime actedAt) { this.actedAt = actedAt; }
}

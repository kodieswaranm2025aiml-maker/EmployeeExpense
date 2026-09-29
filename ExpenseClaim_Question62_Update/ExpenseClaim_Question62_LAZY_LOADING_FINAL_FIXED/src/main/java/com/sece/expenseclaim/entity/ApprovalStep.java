package com.sece.expenseclaim.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="approval_steps")
public class ApprovalStep {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @JsonIgnore @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="claim_id",nullable=false) private Claim claim;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private ApprovalStepType stepType;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private ApprovalAction action;
    @JsonIgnore @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="actor_id") private Employee actor;
    @Column(length=500) private String remarks;
    @Column(nullable=false) private boolean overrideGranted;
    private LocalDateTime actedAt;
    public ApprovalStep(){}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Claim getClaim(){return claim;} public void setClaim(Claim v){claim=v;}
    public ApprovalStepType getStepType(){return stepType;} public void setStepType(ApprovalStepType v){stepType=v;}
    public ApprovalAction getAction(){return action;} public void setAction(ApprovalAction v){action=v;}
    public Employee getActor(){return actor;} public void setActor(Employee v){actor=v;}
    public String getRemarks(){return remarks;} public void setRemarks(String v){remarks=v;}
    public boolean isOverrideGranted(){return overrideGranted;} public void setOverrideGranted(boolean v){overrideGranted=v;}
    public LocalDateTime getActedAt(){return actedAt;} public void setActedAt(LocalDateTime v){actedAt=v;}
}

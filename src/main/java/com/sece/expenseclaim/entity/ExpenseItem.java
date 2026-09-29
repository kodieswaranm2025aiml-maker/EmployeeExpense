package com.sece.expenseclaim.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Table(name="expense_items")
public class ExpenseItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @JsonIgnore @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="claim_id", nullable=false) private Claim claim;
    @Column(nullable=false,length=50) @NotBlank private String category;
    @Column(nullable=false,length=300) @NotBlank private String description;
    @Column(nullable=false,precision=12,scale=2) @DecimalMin("0.01") private BigDecimal amount;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal policyLimit;
    @Column(nullable=false) private boolean overLimit;
    public ExpenseItem(){}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Claim getClaim(){return claim;} public void setClaim(Claim v){claim=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
    public BigDecimal getPolicyLimit(){return policyLimit;} public void setPolicyLimit(BigDecimal v){policyLimit=v;}
    public boolean isOverLimit(){return overLimit;} public void setOverLimit(boolean v){overLimit=v;}
}

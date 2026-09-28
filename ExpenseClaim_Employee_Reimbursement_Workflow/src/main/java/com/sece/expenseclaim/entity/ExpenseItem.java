package com.sece.expenseclaim.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "expense_items")
public class ExpenseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("claim-items")
    @ManyToOne(optional = false)
    private Claim claim;

    @NotBlank
    private String category;

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal policyLimit;

    @Column(nullable = false)
    private boolean exceedsPolicyLimit;

    public Long getId() { return id; }
    @JsonIgnore
    public Claim getClaim() { return claim; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getPolicyLimit() { return policyLimit; }
    public boolean isExceedsPolicyLimit() { return exceedsPolicyLimit; }

    public void setId(Long id) { this.id = id; }
    public void setClaim(Claim claim) { this.claim = claim; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPolicyLimit(BigDecimal policyLimit) { this.policyLimit = policyLimit; }
    public void setExceedsPolicyLimit(boolean exceedsPolicyLimit) { this.exceedsPolicyLimit = exceedsPolicyLimit; }
}

package com.sece.expenseclaim.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Table(name="category_policies", uniqueConstraints=@UniqueConstraint(columnNames="category"))
public class CategoryPolicy {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=50) @NotBlank private String category;
    @Column(nullable=false, precision=12, scale=2) @DecimalMin("0.01") private BigDecimal maxAmount;
    @Column(nullable=false) private boolean active=true;
    public CategoryPolicy() {}
    public CategoryPolicy(String category, BigDecimal maxAmount){this.category=category;this.maxAmount=maxAmount;}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public BigDecimal getMaxAmount(){return maxAmount;} public void setMaxAmount(BigDecimal v){maxAmount=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}

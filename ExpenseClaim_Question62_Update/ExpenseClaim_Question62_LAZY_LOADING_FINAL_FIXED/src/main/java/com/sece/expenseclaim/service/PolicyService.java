package com.sece.expenseclaim.service;
import com.sece.expenseclaim.dto.PolicyResponse;
import com.sece.expenseclaim.entity.CategoryPolicy;
import com.sece.expenseclaim.exception.ResourceNotFoundException;
import com.sece.expenseclaim.repository.CategoryPolicyRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
@Service
public class PolicyService {
    private final CategoryPolicyRepository repo;
    public PolicyService(CategoryPolicyRepository repo){this.repo=repo;}
    public CategoryPolicy find(String category){return repo.findByCategoryIgnoreCaseAndActiveTrue(category).orElseThrow(()->new ResourceNotFoundException("No active policy found for category: "+category));}
    public List<PolicyResponse> all(){return repo.findAll().stream().map(p->new PolicyResponse(p.getId(),p.getCategory(),p.getMaxAmount(),p.isActive())).toList();}
    public CategoryPolicy save(String category, BigDecimal max){return repo.save(new CategoryPolicy(category,max));}
}

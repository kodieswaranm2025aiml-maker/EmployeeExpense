package com.sece.expenseclaim.repository;
import com.sece.expenseclaim.entity.CategoryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CategoryPolicyRepository extends JpaRepository<CategoryPolicy,Long>{ Optional<CategoryPolicy> findByCategoryIgnoreCaseAndActiveTrue(String category); }

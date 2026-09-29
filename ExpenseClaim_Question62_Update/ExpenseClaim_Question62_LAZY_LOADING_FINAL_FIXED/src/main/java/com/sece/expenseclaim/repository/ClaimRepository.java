package com.sece.expenseclaim.repository;
import com.sece.expenseclaim.entity.Claim;
import com.sece.expenseclaim.entity.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClaimRepository extends JpaRepository<Claim,Long>{
 Page<Claim> findByStatus(ClaimStatus status, Pageable pageable);
 Page<Claim> findByEmployeeId(Long employeeId, Pageable pageable);
 Page<Claim> findByEmployeeIdAndStatus(Long employeeId, ClaimStatus status, Pageable pageable);
 long countByStatus(ClaimStatus status);
 long countByPolicyViolationTrue();
}

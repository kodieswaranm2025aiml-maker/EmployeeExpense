package com.sece.expenseclaim.repository;

import com.sece.expenseclaim.entity.Claim;
import com.sece.expenseclaim.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByStatusOrderBySubmittedAtAsc(ClaimStatus status);
    List<Claim> findByEmployeeIdOrderBySubmittedAtDesc(Long employeeId);
    List<Claim> findByEmployeeIdAndStatusOrderBySubmittedAtDesc(Long employeeId, ClaimStatus status);
    List<Claim> findByManagerIdAndStatusOrderBySubmittedAtAsc(Long managerId, ClaimStatus status);
}

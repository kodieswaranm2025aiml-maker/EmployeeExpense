package com.sece.expenseclaim.repository;

import com.sece.expenseclaim.entity.ApprovalStep;
import com.sece.expenseclaim.entity.ApprovalStage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {
    List<ApprovalStep> findByStageAndStatus(ApprovalStage stage, com.sece.expenseclaim.entity.ApprovalStatus status);
    Optional<ApprovalStep> findTopByClaimIdAndStageOrderByIdDesc(Long claimId, ApprovalStage stage);
}

package com.sece.expenseclaim.service;
import com.sece.expenseclaim.entity.AuditLog;
import com.sece.expenseclaim.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
public class AuditService {
    private final AuditLogRepository repo;
    public AuditService(AuditLogRepository repo){this.repo=repo;}
    public void log(String type,Long id,String action,String actor,String details){repo.save(new AuditLog(type,id,action,actor,details));}
    public Page<AuditLog> find(Pageable pageable){return repo.findAllByOrderByCreatedAtDesc(pageable);}
}

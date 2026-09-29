package com.sece.expenseclaim.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=50) private String entityType;
    @Column(nullable=false) private Long entityId;
    @Column(nullable=false,length=60) private String action;
    @Column(nullable=false,length=120) private String actor;
    @Column(length=1000) private String details;
    @Column(nullable=false) private LocalDateTime createdAt;
    public AuditLog(){}
    public AuditLog(String entityType,Long entityId,String action,String actor,String details){this.entityType=entityType;this.entityId=entityId;this.action=action;this.actor=actor;this.details=details;this.createdAt=LocalDateTime.now();}
    public Long getId(){return id;} public String getEntityType(){return entityType;} public Long getEntityId(){return entityId;} public String getAction(){return action;} public String getActor(){return actor;} public String getDetails(){return details;} public LocalDateTime getCreatedAt(){return createdAt;}
}

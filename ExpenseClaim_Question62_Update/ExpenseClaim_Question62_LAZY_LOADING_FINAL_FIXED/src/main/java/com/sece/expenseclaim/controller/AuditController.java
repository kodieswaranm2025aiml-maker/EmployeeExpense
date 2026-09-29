package com.sece.expenseclaim.controller;
import com.sece.expenseclaim.entity.AuditLog;
import com.sece.expenseclaim.service.AuditService;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/audit") @CrossOrigin(origins="*")
public class AuditController { private final AuditService service; public AuditController(AuditService service){this.service=service;} @GetMapping public Page<AuditLog> all(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.find(PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),100)));} }

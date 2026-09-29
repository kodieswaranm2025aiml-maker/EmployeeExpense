package com.sece.expenseclaim.controller;
import com.sece.expenseclaim.dto.PolicyResponse;
import com.sece.expenseclaim.service.PolicyService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/policies") @CrossOrigin(origins="*")
public class PolicyController { private final PolicyService service; public PolicyController(PolicyService service){this.service=service;} @GetMapping public List<PolicyResponse> all(){return service.all();} }

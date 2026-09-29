package com.sece.expenseclaim.controller;

import com.sece.expenseclaim.dto.EmployeeSummary;
import com.sece.expenseclaim.entity.Role;
import com.sece.expenseclaim.service.EmployeeService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins="*")
public class EmployeeController {
    private final EmployeeService service;
    public EmployeeController(EmployeeService service){this.service=service;}
    @GetMapping public List<EmployeeSummary> all(){return service.all();}
    @GetMapping("/{id}") public EmployeeSummary get(@PathVariable Long id){return service.get(id);}
    @GetMapping("/role/{role}") public List<EmployeeSummary> role(@PathVariable Role role){return service.byRole(role);}
}

package com.sece.expenseclaim.controller;

import com.sece.expenseclaim.entity.Employee;
import com.sece.expenseclaim.entity.Role;
import com.sece.expenseclaim.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {
    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping
    public Employee create(@Valid @RequestBody Employee employee) {
        return service.create(employee);
    }

    @GetMapping
    public List<Employee> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public Employee get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/role/{role}")
    public List<Employee> byRole(@PathVariable Role role) {
        return service.all().stream()
                .filter(e -> e.getRole() == role)
                .toList();
    }
}

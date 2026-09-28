package com.sece.expenseclaim.service;

import com.sece.expenseclaim.entity.Employee;
import com.sece.expenseclaim.entity.Role;
import com.sece.expenseclaim.exception.BusinessException;
import com.sece.expenseclaim.exception.ResourceNotFoundException;
import com.sece.expenseclaim.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee create(Employee employee) {
        return repository.save(employee);
    }

    public List<Employee> all() {
        return repository.findAll();
    }

    public Employee get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    public Employee requireRole(Long id, Role role) {
        Employee employee = get(id);
        if (employee.getRole() != role) {
            throw new BusinessException("Employee " + id + " must have role " + role);
        }
        return employee;
    }
}

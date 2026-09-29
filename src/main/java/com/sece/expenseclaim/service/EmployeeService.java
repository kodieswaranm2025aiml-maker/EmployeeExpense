package com.sece.expenseclaim.service;
import com.sece.expenseclaim.dto.EmployeeSummary;
import com.sece.expenseclaim.entity.Employee;
import com.sece.expenseclaim.entity.Role;
import com.sece.expenseclaim.exception.ResourceNotFoundException;
import com.sece.expenseclaim.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class EmployeeService {
    private final EmployeeRepository repo;
    public EmployeeService(EmployeeRepository repo){this.repo=repo;}
    public EmployeeSummary get(Long id){Employee e=repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Employee not found: "+id)); return summary(e);}
    public List<EmployeeSummary> all(){return repo.findAll().stream().map(this::summary).toList();}
    public List<EmployeeSummary> byRole(Role role){return repo.findByRole(role).stream().map(this::summary).toList();}
    private EmployeeSummary summary(Employee e){return new EmployeeSummary(e.getId(),e.getEmployeeCode(),e.getName(),e.getEmail(),e.getRole());}
}

package com.sece.expenseclaim.repository;

import com.sece.expenseclaim.entity.Employee;
import com.sece.expenseclaim.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByRole(Role role);
}

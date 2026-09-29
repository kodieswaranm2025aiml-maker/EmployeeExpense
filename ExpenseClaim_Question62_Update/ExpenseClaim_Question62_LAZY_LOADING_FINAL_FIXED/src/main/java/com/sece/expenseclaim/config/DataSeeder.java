package com.sece.expenseclaim.config;

import com.sece.expenseclaim.entity.*;
import com.sece.expenseclaim.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(EmployeeRepository employees, CategoryPolicyRepository policies){
        return args -> {
            if(employees.count()==0){
                employees.save(new Employee("EMP001","Arun Kumar","arun@sece.edu",Role.EMPLOYEE));
                employees.save(new Employee("EMP002","Divya Priya","divya@sece.edu",Role.EMPLOYEE));
                employees.save(new Employee("MGR001","Priya Manager","priya.manager@sece.edu",Role.MANAGER));
                employees.save(new Employee("FIN001","Meena Finance","meena.finance@sece.edu",Role.FINANCE));
            }
            if(policies.count()==0){
                policies.save(new CategoryPolicy("Travel",new BigDecimal("5000.00")));
                policies.save(new CategoryPolicy("Food",new BigDecimal("1500.00")));
                policies.save(new CategoryPolicy("Accommodation",new BigDecimal("10000.00")));
                policies.save(new CategoryPolicy("Communication",new BigDecimal("2000.00")));
                policies.save(new CategoryPolicy("Office Supplies",new BigDecimal("3000.00")));
                policies.save(new CategoryPolicy("Other",new BigDecimal("2500.00")));
            }
        };
    }
}

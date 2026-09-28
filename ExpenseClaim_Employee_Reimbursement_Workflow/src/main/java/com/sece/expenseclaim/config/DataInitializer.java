package com.sece.expenseclaim.config;

import com.sece.expenseclaim.entity.Employee;
import com.sece.expenseclaim.entity.Role;
import com.sece.expenseclaim.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(EmployeeRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Employee("Arun Employee", "arun.employee@example.com", Role.EMPLOYEE));
                repository.save(new Employee("Priya Manager", "priya.manager@example.com", Role.MANAGER));
                repository.save(new Employee("Finance User", "finance@example.com", Role.FINANCE));
            }
        };
    }
}

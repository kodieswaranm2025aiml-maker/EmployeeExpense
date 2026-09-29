package com.sece.expenseclaim.dto;

import com.sece.expenseclaim.entity.Role;

public record EmployeeSummary(Long id, String employeeCode, String name, String email, Role role) {}

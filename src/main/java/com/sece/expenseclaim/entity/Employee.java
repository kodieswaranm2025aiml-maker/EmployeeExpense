package com.sece.expenseclaim.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 30)
    @NotBlank private String employeeCode;
    @Column(nullable = false, length = 100)
    @NotBlank private String name;
    @Column(nullable = false, unique = true, length = 150)
    @Email @NotBlank private String email;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Role role;
    @JsonIgnore @OneToMany(mappedBy = "employee")
    private List<Claim> claims = new ArrayList<>();

    public Employee() {}
    public Employee(String employeeCode, String name, String email, Role role) { this.employeeCode=employeeCode; this.name=name; this.email=email; this.role=role; }
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getEmployeeCode(){return employeeCode;} public void setEmployeeCode(String v){employeeCode=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public Role getRole(){return role;} public void setRole(Role v){role=v;}
    public List<Claim> getClaims(){return claims;} public void setClaims(List<Claim> v){claims=v;}
}

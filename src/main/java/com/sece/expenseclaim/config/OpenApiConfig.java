package com.sece.expenseclaim.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {
 @Bean public OpenAPI expenseClaimOpenAPI(){return new OpenAPI().info(new Info().title("ExpenseClaim API").version("1.0.0").description("Employee Expense Reimbursement Approval Workflow - Sri Eshwar College Project Leap Question 62"));}
}

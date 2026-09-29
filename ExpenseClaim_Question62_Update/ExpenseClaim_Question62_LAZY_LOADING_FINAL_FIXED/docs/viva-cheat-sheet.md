# Review / Viva Cheat Sheet

**Q1. Why is the service layer important?**
A. Business rules must be enforced before persistence. The service checks policy limits, manager role, override requirement and payment eligibility before saving changes.

**Q2. Why use @OneToMany and @ManyToOne?**
A. One Employee can submit many Claims; one Claim owns many ExpenseItems and ApprovalSteps. Each child points back to its Claim using @ManyToOne.

**Q3. How is an over-limit item detected?**
A. The service loads the active CategoryPolicy and compares `amount.compareTo(maxAmount) > 0`. It stores both `policyLimit` and `overLimit` on the item and sets `policyViolation` on the claim.

**Q4. Why can’t a manager approve a violation without override?**
A. The approval service explicitly rejects that request with HTTP 409. This prevents invalid workflow state even if the database itself would accept the row.

**Q5. Why can’t finance pay a pending claim?**
A. The payment service requires `ClaimStatus.APPROVED`; otherwise it throws a business-rule exception before saving.

**Q6. Why use DTOs?**
A. DTOs keep API input separate from JPA entities, provide validation at the boundary, and prevent clients from directly changing workflow state.

**Q7. What does @ControllerAdvice do?**
A. It converts validation failures, missing resources and business-rule violations into clear JSON error responses instead of exposing stack traces.

**Q8. What is the status flow?**
A. PENDING_MANAGER -> APPROVED -> PAID, or PENDING_MANAGER -> REJECTED. Policy violation is a flag on the claim and item, not a bypass of the approval stage.

**Q9. What bonus features did you implement?**
A. Pagination, sorting, audit logging, Swagger/OpenAPI, and an H2 fallback profile.

**Q10. How do you test the business rules?**
A. Submit a normal claim, submit an over-limit claim, try manager approval without override, approve with override, then try finance payment before approval and after approval. The Postman collection contains these scenarios.

**Q11. Why use MySQL and H2?**
A. MySQL satisfies the DBMS requirement. H2 gives a self-contained fallback profile for demos when MySQL is unavailable.

**Q12. What are the main tables?**
A. employees, category_policies, claims, expense_items, approval_steps and audit_logs.

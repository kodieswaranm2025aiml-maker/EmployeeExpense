# ExpenseClaim — Employee Expense Reimbursement Approval Workflow

This project implements the requirements from the supplied assessment question:

- Employee submits a claim with multiple expense line items.
- Category-wise policy limits are checked automatically.
- Any line item above its category limit flags the claim.
- Manager must use an explicit override before approving a policy-violating claim.
- Manager can approve or reject with remarks.
- Finance can mark a claim as paid only after manager approval.
- Payment reference is stored.
- Pending claims can be viewed by approval stage / employee / manager.
- Service layer enforces business rules.
- Bean validation and global exception handling return clear errors.
- Pagination and sorting are included.
- Postman collection and Swagger UI are included.
- A simple HTML dashboard is included.

## Technology

Java 17, Spring Boot 3.5.6, Spring Web, Spring Data JPA, Validation, MySQL, HTML/CSS/JavaScript.

## Database

Create the database:

```sql
CREATE DATABASE expense_claim_db;
```

Then edit:

`src/main/resources/application.properties`

Replace:

```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

with your local MySQL password.

## Run

From the folder containing `pom.xml`:

```bash
mvn clean
mvn spring-boot:run
```

Open:

http://localhost:8081/

Swagger:

http://localhost:8081/swagger-ui.html

## Seeded users

On an empty database, the application creates:

| ID | Name | Role |
|---|---|---|
| 1 | Arun Employee | EMPLOYEE |
| 2 | Priya Manager | MANAGER |
| 3 | Finance User | FINANCE |

## Policy limits

The question sheet requires category-wise limits but does not specify numeric values. This implementation therefore uses clearly configurable sample limits:

- TRAVEL = 5000
- FOOD = 2000
- HOTEL = 8000
- LOCAL_TRAVEL = 1500
- OTHER = 3000

These are stored in the service layer so they can be changed easily.

## Main workflow

1. POST `/api/claims` to create a claim with multiple items.
2. POST `/api/claims/{id}/submit`.
3. Manager uses `/approve` or `/reject`.
4. If any item exceeds policy, `/approve` requires `overridePolicyLimit=true`.
5. Finance uses `/pay` only after status becomes APPROVED.
6. GET `/api/claims/pending/manager` shows pending manager approvals.

## Important business rules

- Only EMPLOYEE role can create claims.
- Claim must have at least one expense item.
- Amount must be positive.
- Every item is checked against its category limit.
- A policy-violating claim cannot be approved without manager override.
- Only the assigned manager can approve/reject.
- Only FINANCE role can mark a claim paid.
- A claim can be marked paid only after manager approval.
- Payment reference is mandatory.
- Invalid requests are rejected by validation / global exception handling.

## Assessment mapping

### Technical Implementation — 40 marks
REST APIs, CRUD employee management, claim creation, multiple expense items, policy validation, manager approval/rejection, finance payment, repository/service/controller layers and MySQL integration.

### System Design & Architecture — 25 marks
Four required core entities are present: Employee, Claim, ExpenseItem, ApprovalStep. Relationships use JPA `@ManyToOne` and `@OneToMany`. Layered Controller → Service → Repository architecture is used.

### Code Quality & Efficiency — 20 marks
Validation, service-layer business rules, reusable PolicyService, global exception handler, pagination/sorting, meaningful HTTP errors and clean separation of responsibilities.

### Presentation & Communication — 15 marks
Dashboard, Postman collection, Swagger documentation and an end-to-end demo flow are included.

## Demo sequence for viva

Use these seeded IDs:

Employee = 1
Manager = 2
Finance = 3

Normal claim:
- TRAVEL 3500
- FOOD 1200

Submit → Manager approve with override=false → Finance pay.

Policy violation demo:
- TRAVEL 7000

Submit → Manager approval with override=false must fail → repeat with override=true → approval succeeds.

Finance rule demo:
- Try `/pay` before manager approval → request must fail.
- Approve first → `/pay` succeeds with payment reference.

## Improved evaluation dashboard

The dashboard was redesigned for the strict evaluation demo. It explains the business scenario, shows the five-step workflow, displays seeded users and policy limits, provides normal and policy-violation scenarios, shows pending/approved/history tables, and gives readable success/error messages.

## JSON output fix

Child-to-parent JPA references use Jackson `@JsonIgnore` so REST responses do not recursively serialize the complete claim graph.

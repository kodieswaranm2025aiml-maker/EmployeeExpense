# ExpenseClaim — Employee Expense Reimbursement Approval Workflow

Sri Eshwar College of Engineering — Project Leap Java & DBMS — Question 62.

## What this project demonstrates
Employee submits an itemized reimbursement claim -> service layer checks category policy limits -> manager reviews/approves/rejects -> finance pays only an approved claim -> every state change is recorded in an audit log.

## Entities and relationships
- Employee: one employee can submit many claims; role is EMPLOYEE, MANAGER or FINANCE.
- Claim: belongs to one employee and contains many expense items and approval steps.
- ExpenseItem: belongs to one claim and stores the category limit and over-limit flag used by the policy check.
- ApprovalStep: records manager workflow actions and override decisions.
- CategoryPolicy: category-wise reimbursement limit used by the service layer.
- AuditLog: records who changed what and when.

## Claim status flow
`PENDING_MANAGER -> APPROVED -> PAID`

or

`PENDING_MANAGER -> REJECTED`

Policy violations are represented by `policyViolation=true` on the claim and `overLimit=true` on the relevant line items. They do not bypass the workflow: manager approval requires `overridePolicy=true` when a violation exists.

## Required business rules
1. Every expense item is checked against its active category policy before the claim is saved.
2. Any over-limit item flags the claim.
3. A flagged claim cannot be approved without explicit manager override.
4. Finance cannot pay unless status is APPROVED.
5. Manager remarks are required for approval and rejection.
6. Payment reference is required for payment.
7. Role checks are enforced in the service layer.

## Technology
- Java 17
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA
- Bean Validation
- MySQL 8.x
- H2 fallback profile
- Springdoc OpenAPI / Swagger UI
- HTML, CSS, JavaScript frontend

## Run with MySQL
1. In MySQL Workbench:
```sql
CREATE DATABASE IF NOT EXISTS expense_claim_db;
```
2. Open `src/main/resources/application-mysql.properties` and set the environment variable `MYSQL_PASSWORD` to your local MySQL password, or edit `spring.datasource.password` directly.
3. From the project root:
```powershell
mvn clean
mvn spring-boot:run
```
4. Open `http://localhost:8081/`.

JPA uses `ddl-auto=update`, so tables are created automatically. Seed users and policies are inserted on first startup.

## Run without MySQL using H2
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```
Then open `http://localhost:8081/`.
H2 console: `http://localhost:8081/h2-console` with JDBC URL `jdbc:h2:file:./data/expense_claim_db`, user `sa`, blank password.

## Seed users
- Arun Kumar — EMP001 — EMPLOYEE
- Divya Priya — EMP002 — EMPLOYEE
- Priya Manager — MGR001 — MANAGER
- Meena Finance — FIN001 — FINANCE

## Seed category limits
- Travel: ₹5,000
- Food: ₹1,500
- Accommodation: ₹10,000
- Communication: ₹2,000
- Office Supplies: ₹3,000
- Other: ₹2,500

## REST API list
### Employees
- `GET /api/employees`
- `GET /api/employees/{id}`
- `GET /api/employees/role/{role}`

### Policies
- `GET /api/policies`

### Claims
- `POST /api/claims` — submit itemized claim
- `GET /api/claims/{id}` — claim details
- `GET /api/claims` — paginated, filtered and sorted claim list
- `GET /api/claims/stage/{status}` — pending/approved/rejected/paid by stage
- `GET /api/claims/employee/{employeeId}` — employee claim history
- `GET /api/claims/dashboard` — workflow counts
- `PUT /api/claims/{id}/approve` — manager approval + override
- `PUT /api/claims/{id}/reject` — manager rejection + remarks
- `PUT /api/claims/{id}/pay` — finance payment + reference

### Audit
- `GET /api/audit` — paginated audit trail

### Swagger
`http://localhost:8081/swagger-ui.html`

## Example claim request
```json
{
  "employeeId": 1,
  "title": "Client visit expenses",
  "description": "Travel and lunch for client meeting",
  "items": [
    {"category":"Travel","description":"Train travel","amount":3500},
    {"category":"Food","description":"Client lunch","amount":1200}
  ]
}
```

## Policy violation test
Travel limit is ₹5,000. Submit Travel for ₹7,000. The claim is saved as `PENDING_MANAGER` with `policyViolation=true` and the item is flagged. A manager approval request with `overridePolicy=false` returns HTTP 409 with a clear rule message. A request with `overridePolicy=true` and remarks can approve it.

## Finance guard test
Calling `PUT /api/claims/{id}/pay` while the claim is `PENDING_MANAGER` or `REJECTED` returns HTTP 409. Only `APPROVED` claims can become `PAID`.

## Bonus features
- Pagination and sorting on claim read endpoints.
- Audit log for submission, approval, rejection and payment.
- H2 fallback profile.
- Swagger/OpenAPI.
- Responsive role-based demo UI.

## Project structure
```text
ExpenseClaim_Complete_Project/
├── pom.xml
├── database_setup.sql
├── README.md
├── docs/
│   ├── architecture.md
│   ├── viva-cheat-sheet.md
│   └── demo-script.md
├── postman/
│   └── ExpenseClaim-Question62.postman_collection.json
└── src/main/
    ├── java/com/sece/expenseclaim/
    │   ├── config/
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── exception/
    │   ├── repository/
    │   └── service/
    └── resources/
        ├── application.properties
        ├── application-mysql.properties
        ├── application-h2.properties
        └── static/
            ├── index.html
            ├── app.css
            └── app.js
```


Manager Review fix: the Review button now uses delegated click handling and POST workflow endpoints, with stale-status protection and explicit modal buttons.

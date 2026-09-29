# 2-Minute Review Demo Script

## 0:00–0:20 — Architecture
Say: “This is a Spring Boot layered application. The controller handles HTTP, the service layer enforces business rules, repositories persist through JPA, and MySQL/H2 stores the data. The frontend is plain HTML, CSS and JavaScript consuming the REST APIs.”

## 0:20–0:55 — Normal claim
1. Keep role = Employee.
2. Click **Normal demo**.
3. Submit the claim.
4. Point to the claim appearing in Manager Queue.
5. Explain that multiple expense items are stored under one Claim.

## 0:55–1:25 — Manager
1. Switch View as = Manager.
2. Click Review on the pending claim.
3. Enter remarks.
4. Approve.
5. Explain that the service checks role and current status before changing the claim to APPROVED.

## 1:25–1:45 — Finance
1. Switch View as = Finance.
2. Click Pay on the approved claim.
3. Enter `PAY-2026-001`.
4. Mark as paid.
5. Point to PAID status and audit trail.

## 1:45–2:00 — Rule violation
1. Click **Violation demo**.
2. Show Travel ₹7,000 against the ₹5,000 policy.
3. Explain: “The service flags the item. Approval without override is rejected with HTTP 409. The manager must explicitly override and provide remarks.”
4. Mention: “Finance has a separate service-layer guard and cannot pay an unapproved claim.”

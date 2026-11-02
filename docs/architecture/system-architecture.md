# SkillBridge System Architecture Design

## 1. High-Level Architecture

SkillBridge follows a modern decoupled architecture separating presentation clients from business services and data storage:

```
+-------------------------------------------------------------+
|                         Clients                             |
|  +---------------------------+  +------------------------+  |
|  | Web (React 18 + TS + Vite)|  | Mobile (Expo + RN + TS)|  |
|  +---------------------------+  +------------------------+  |
|                |                             |              |
+-------------------------------------------------------------+
                                 | REST / WebSocket
                                 v
+-------------------------------------------------------------+
|                  SkillBridge Backend API                    |
|                      (Spring Boot)                          |
|                                                             |
|  +-------------------------------------------------------+  |
|  | Security & Auth Layer (JWT, BCrypt, RBAC)             |  |
|  +-------------------------------------------------------+  |
|  | Controllers (REST Endpoints) & WebSocket Broker       |  |
|  +-------------------------------------------------------+  |
|  | Service Layer (Business Logic & Transactions)         |  |
|  +-------------------------------------------------------+  |
|  | Integration Layer (Spring AI, Gemini/Groq, Cashfree)  |  |
|  +-------------------------------------------------------+  |
|  | Repositories (Spring Data JPA / Hibernate)            |  |
|  +-------------------------------------------------------+  |
+-------------------------------------------------------------+
                                 |
                                 v
+-------------------------------------------------------------+
|                       Data Tier                             |
|  +-------------------------------------------------------+  |
|  | MySQL Database (Aiven Cloud / Local MySQL)            |  |
|  | Schema Migrations managed by Flyway                   |  |
|  +-------------------------------------------------------+  |
+-------------------------------------------------------------+
```

## 2. Shared Domain Models & Contracts

The `packages/shared` workspace provides isomorphic TypeScript definitions:
- User roles: `CLIENT`, `FREELANCER`, `ADMIN`
- Project statuses: `DRAFT`, `OPEN`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`
- Proposal statuses: `SUBMITTED`, `SHORTLISTED`, `ACCEPTED`, `REJECTED`, `WITHDRAWN`
- Milestone & Task models
- Real-time chat & Notification payloads

## 3. Security Architecture
- Stateless JWT authentication: short-lived access tokens (15-60m) paired with rotating refresh tokens (7 days).
- Role-based authorization enforced via `@PreAuthorize` on backend endpoints.
- Ownership checks performed in service layer before mutating or returning private records.


# SkillBridge REST API Specification

**Base URL:** `/api/v1`

All responses follow a standard envelope:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {},
  "timestamp": "2026-11-03T00:55:00Z"
}
```

Error responses:
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Invalid email address format"
    }
  ],
  "timestamp": "2026-11-03T00:55:00Z"
}
```

## Endpoints Summary

### Authentication (`/api/v1/auth`)
- `POST /auth/register` - Create new user account
- `POST /auth/login` - Authenticate & obtain JWT tokens
- `POST /auth/refresh` - Refresh access token using refresh token
- `POST /auth/logout` - Invalidate current session/refresh token
- `POST /auth/verify-email` - Complete email verification

### Users & Profiles (`/api/v1/users`, `/api/v1/freelancers`, `/api/v1/clients`)
- `GET /users/me` - Current authenticated user details
- `GET /freelancers` - Search & filter freelancers
- `GET /freelancers/{id}` - Public freelancer profile & portfolio
- `PUT /freelancers/me` - Update current freelancer profile
- `GET /clients/{id}` - Public client company profile
- `PUT /clients/me` - Update current client profile

### Projects & Proposals (`/api/v1/projects`, `/api/v1/proposals`)
- `GET /projects` - Search marketplace projects with filters & sorting
- `POST /projects` - Client posts new project
- `GET /projects/{id}` - Project details & required skills
- `PUT /projects/{id}` - Edit project details
- `DELETE /projects/{id}` - Cancel/archive project
- `POST /projects/{id}/proposals` - Freelancer submits proposal
- `GET /projects/{id}/proposals` - Client views proposals for project
- `GET /proposals/my` - Freelancer views submitted proposals
- `PUT /proposals/{id}/accept` - Client accepts proposal to create contract

### Contracts & Milestones (`/api/v1/contracts`, `/api/v1/milestones`, `/api/v1/tasks`)
- `GET /contracts/my` - User active/completed contracts
- `GET /contracts/{id}` - Contract workspace overview
- `POST /contracts/{id}/milestones` - Add milestone
- `PUT /milestones/{id}/submit` - Freelancer submits milestone deliverable
- `PUT /milestones/{id}/approve` - Client approves and releases payment
- `GET /contracts/{id}/tasks` - Project task board
- `POST /contracts/{id}/tasks` - Create task
- `PUT /tasks/{id}` - Update task status (Kanban column)

### Real-Time Messaging (`/api/v1/conversations`, `/ws`)
- `GET /conversations` - List user conversations
- `GET /conversations/{id}/messages` - Message history with pagination
- `POST /conversations/{id}/messages` - Send message (HTTP fallback)
- WebSocket endpoint: `/ws` with STOMP protocol
  - Destination: `/app/chat.sendMessage`
  - User queue: `/user/queue/messages`

### AI Features (`/api/v1/ai`)
- `POST /ai/talent-matching` - Match freelancers to project with compatibility breakdown
- `POST /ai/resume-analysis` - Parse & score resume against target skills
- `POST /ai/project-description` - Generate structured project brief & scope
- `POST /ai/proposal-assistance` - Enhance proposal draft & tone
- `POST /ai/budget-estimation` - Estimate duration, complexity & milestone breakdown
- `POST /ai/career-coaching` - Identify skill gaps and learning roadmaps
- `POST /ai/scam-detection` - Scan project/profile for fraudulent patterns


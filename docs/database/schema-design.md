# SkillBridge Database Schema Specification

## Target Relational Models (MySQL + Hibernate JPA)

### 1. Core Identity & RBAC
- **`users`**
  - `id`: BIGINT AUTO_INCREMENT PRIMARY KEY
  - `full_name`: VARCHAR(150) NOT NULL
  - `email`: VARCHAR(180) UNIQUE NOT NULL
  - `password_hash`: VARCHAR(255) NOT NULL
  - `phone`: VARCHAR(30)
  - `role`: ENUM('CLIENT', 'FREELANCER', 'ADMIN') NOT NULL
  - `account_status`: ENUM('ACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION') DEFAULT 'ACTIVE'
  - `email_verified`: BOOLEAN DEFAULT FALSE
  - `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  - `updated_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

- **`refresh_tokens`**
  - `id`: BIGINT AUTO_INCREMENT PRIMARY KEY
  - `user_id`: BIGINT NOT NULL (FK -> users.id)
  - `token`: VARCHAR(512) UNIQUE NOT NULL
  - `expiry_date`: TIMESTAMP NOT NULL
  - `revoked`: BOOLEAN DEFAULT FALSE

### 2. Profiles
- **`freelancer_profiles`**
  - `id`: BIGINT AUTO_INCREMENT PRIMARY KEY
  - `user_id`: BIGINT UNIQUE NOT NULL (FK -> users.id)
  - `professional_title`: VARCHAR(150)
  - `description`: TEXT
  - `hourly_rate`: DECIMAL(10,2)
  - `experience_years`: INT
  - `availability`: ENUM('AVAILABLE', 'BUSY', 'NOT_LOOKING') DEFAULT 'AVAILABLE'
  - `location`: VARCHAR(100)
  - `resume_url`: VARCHAR(255)
  - `github_url`: VARCHAR(255)
  - `linkedin_url`: VARCHAR(255)
  - `completed_projects_count`: INT DEFAULT 0
  - `rating_average`: DECIMAL(3,2) DEFAULT 0.00

- **`client_profiles`**
  - `id`: BIGINT AUTO_INCREMENT PRIMARY KEY
  - `user_id`: BIGINT UNIQUE NOT NULL (FK -> users.id)
  - `company_name`: VARCHAR(150)
  - `company_description`: TEXT
  - `industry`: VARCHAR(100)
  - `website`: VARCHAR(255)
  - `company_size`: VARCHAR(50)
  - `logo_url`: VARCHAR(255)

### 3. Skills & Portfolios
- **`skills`** (`id`, `name`, `category`)
- **`freelancer_skills`** (`freelancer_id`, `skill_id`, `experience_level`)
- **`portfolio_items`** (`id`, `freelancer_id`, `title`, `description`, `project_url`, `github_url`, `image_url`)

### 4. Projects, Proposals & Contracts
- **`projects`** (`id`, `client_id`, `title`, `description`, `category`, `budget_min`, `budget_max`, `duration`, `status`, `deadline`)
- **`project_skills`** (`project_id`, `skill_id`)
- **`proposals`** (`id`, `project_id`, `freelancer_id`, `cover_letter`, `proposed_budget`, `estimated_duration`, `status`)
- **`contracts`** (`id`, `project_id`, `client_id`, `freelancer_id`, `status`, `total_amount`, `start_date`, `end_date`)
- **`milestones`** (`id`, `contract_id`, `title`, `description`, `amount`, `deadline`, `status`)
- **`tasks`** (`id`, `milestone_id`, `title`, `description`, `assigned_to`, `priority`, `status`, `deadline`)

### 5. Communication & Payments
- **`conversations`** (`id`, `created_at`, `updated_at`)
- **`conversation_participants`** (`conversation_id`, `user_id`)
- **`messages`** (`id`, `conversation_id`, `sender_id`, `content`, `attachment_url`, `read_status`, `created_at`)
- **`transactions`** (`id`, `contract_id`, `milestone_id`, `payer_id`, `payee_id`, `amount`, `type`, `status`, `gateway_ref`)
- **`reviews`** (`id`, `reviewer_id`, `reviewed_user_id`, `project_id`, `rating`, `comment`)
- **`notifications`** (`id`, `user_id`, `title`, `message`, `type`, `read_status`, `created_at`)


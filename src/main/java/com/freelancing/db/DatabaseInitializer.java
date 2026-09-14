package com.freelancing.db;


import com.freelancing.config.SecurityConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes the SkillBridge SQLite database schema and seeds initial master records.
 * Creates all 27 tables with foreign key constraints, indexes, and initial records.
 */
public class DatabaseInitializer {

    private DatabaseInitializer() {}

    /**
     * Initializes schema and seed data.
     */
    public static synchronized void initialize() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            createTables(conn);
            createIndexes(conn);
            seedInitialData(conn);
            System.out.println("SkillBridge SQLite schema and seed verification complete.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // 1. Users
            stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "username VARCHAR(50) UNIQUE NOT NULL, "
                    + "email VARCHAR(100) UNIQUE NOT NULL, "
                    + "phone VARCHAR(25), "
                    + "password_hash VARCHAR(255) NOT NULL, "
                    + "role VARCHAR(20) NOT NULL CHECK(role IN ('FREELANCER', 'CLIENT', 'ADMIN')), "
                    + "status VARCHAR(20) DEFAULT 'ACTIVE', "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 2. Freelancer Profiles
            stmt.execute("CREATE TABLE IF NOT EXISTS freelancer_profiles ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "user_id VARCHAR(50) UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "title VARCHAR(100), "
                    + "bio TEXT, "
                    + "hourly_rate REAL DEFAULT 0.0, "
                    + "experience_years INTEGER DEFAULT 0, "
                    + "rating REAL DEFAULT 5.0, "
                    + "total_reviews INTEGER DEFAULT 0, "
                    + "completed_projects INTEGER DEFAULT 0, "
                    + "avatar_path VARCHAR(255), "
                    + "resume_path VARCHAR(255), "
                    + "github_url VARCHAR(255), "
                    + "linkedin_url VARCHAR(255), "
                    + "availability VARCHAR(30) DEFAULT 'AVAILABLE', "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 3. Client Profiles
            stmt.execute("CREATE TABLE IF NOT EXISTS client_profiles ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "user_id VARCHAR(50) UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "company_name VARCHAR(100), "
                    + "industry VARCHAR(100), "
                    + "company_website VARCHAR(255), "
                    + "about TEXT, "
                    + "avatar_path VARCHAR(255), "
                    + "total_spent REAL DEFAULT 0.0, "
                    + "posted_projects INTEGER DEFAULT 0, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 4. Skills Catalog
            stmt.execute("CREATE TABLE IF NOT EXISTS skills ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "name VARCHAR(50) UNIQUE NOT NULL, "
                    + "category VARCHAR(50), "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 5. Freelancer Skills Join
            stmt.execute("CREATE TABLE IF NOT EXISTS freelancer_skills ("
                    + "freelancer_id VARCHAR(50) NOT NULL REFERENCES freelancer_profiles(id) ON DELETE CASCADE, "
                    + "skill_id VARCHAR(50) NOT NULL REFERENCES skills(id) ON DELETE CASCADE, "
                    + "proficiency VARCHAR(20) DEFAULT 'INTERMEDIATE', "
                    + "PRIMARY KEY(freelancer_id, skill_id)"
                    + ");");

            // 6. Portfolio Items
            stmt.execute("CREATE TABLE IF NOT EXISTS portfolio_items ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "freelancer_id VARCHAR(50) NOT NULL REFERENCES freelancer_profiles(id) ON DELETE CASCADE, "
                    + "title VARCHAR(100) NOT NULL, "
                    + "description TEXT, "
                    + "project_url VARCHAR(255), "
                    + "image_path VARCHAR(255), "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 7. Certifications
            stmt.execute("CREATE TABLE IF NOT EXISTS certifications ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "freelancer_id VARCHAR(50) NOT NULL REFERENCES freelancer_profiles(id) ON DELETE CASCADE, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "issuer VARCHAR(100), "
                    + "issue_date VARCHAR(30), "
                    + "credential_url VARCHAR(255), "
                    + "certificate_path VARCHAR(255), "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 8. Projects
            stmt.execute("CREATE TABLE IF NOT EXISTS projects ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "client_id VARCHAR(50) NOT NULL REFERENCES client_profiles(id) ON DELETE CASCADE, "
                    + "title VARCHAR(150) NOT NULL, "
                    + "description TEXT NOT NULL, "
                    + "category VARCHAR(50) NOT NULL, "
                    + "budget_type VARCHAR(20) DEFAULT 'FIXED', "
                    + "budget_min REAL DEFAULT 0.0, "
                    + "budget_max REAL DEFAULT 0.0, "
                    + "deadline VARCHAR(30), "
                    + "experience_level VARCHAR(30) DEFAULT 'INTERMEDIATE', "
                    + "status VARCHAR(30) DEFAULT 'OPEN', "
                    + "awarded_freelancer_id VARCHAR(50) REFERENCES freelancer_profiles(id), "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 9. Project Skills Join
            stmt.execute("CREATE TABLE IF NOT EXISTS project_skills ("
                    + "project_id VARCHAR(50) NOT NULL REFERENCES projects(id) ON DELETE CASCADE, "
                    + "skill_id VARCHAR(50) NOT NULL REFERENCES skills(id) ON DELETE CASCADE, "
                    + "PRIMARY KEY(project_id, skill_id)"
                    + ");");

            // 10. Proposals
            stmt.execute("CREATE TABLE IF NOT EXISTS proposals ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "project_id VARCHAR(50) NOT NULL REFERENCES projects(id) ON DELETE CASCADE, "
                    + "freelancer_id VARCHAR(50) NOT NULL REFERENCES freelancer_profiles(id) ON DELETE CASCADE, "
                    + "bid_amount REAL NOT NULL, "
                    + "delivery_days INTEGER NOT NULL, "
                    + "cover_letter TEXT NOT NULL, "
                    + "status VARCHAR(30) DEFAULT 'SUBMITTED', "
                    + "ai_match_score REAL DEFAULT 0.0, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + "UNIQUE(project_id, freelancer_id)"
                    + ");");

            // 11. Contracts
            stmt.execute("CREATE TABLE IF NOT EXISTS contracts ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "project_id VARCHAR(50) NOT NULL REFERENCES projects(id) ON DELETE CASCADE, "
                    + "proposal_id VARCHAR(50) REFERENCES proposals(id), "
                    + "client_id VARCHAR(50) NOT NULL REFERENCES client_profiles(id), "
                    + "freelancer_id VARCHAR(50) NOT NULL REFERENCES freelancer_profiles(id), "
                    + "total_amount REAL NOT NULL, "
                    + "escrow_balance REAL DEFAULT 0.0, "
                    + "status VARCHAR(30) DEFAULT 'ACTIVE', "
                    + "start_date VARCHAR(30), "
                    + "end_date VARCHAR(30), "
                    + "client_signed BOOLEAN DEFAULT 0, "
                    + "freelancer_signed BOOLEAN DEFAULT 0, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 12. Milestones
            stmt.execute("CREATE TABLE IF NOT EXISTS milestones ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "contract_id VARCHAR(50) NOT NULL REFERENCES contracts(id) ON DELETE CASCADE, "
                    + "title VARCHAR(100) NOT NULL, "
                    + "description TEXT, "
                    + "amount REAL NOT NULL, "
                    + "deadline VARCHAR(30), "
                    + "status VARCHAR(30) DEFAULT 'PENDING', "
                    + "sequence_order INTEGER DEFAULT 1, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 13. Calendar Events
            stmt.execute("CREATE TABLE IF NOT EXISTS calendar_events ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "user_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "title VARCHAR(150) NOT NULL, "
                    + "description TEXT, "
                    + "event_date VARCHAR(30) NOT NULL, "
                    + "event_time VARCHAR(20), "
                    + "event_type VARCHAR(30) DEFAULT 'REMINDER', "
                    + "project_id VARCHAR(50) REFERENCES projects(id) ON DELETE SET NULL, "
                    + "contract_id VARCHAR(50) REFERENCES contracts(id) ON DELETE SET NULL, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 14. Reports
            stmt.execute("CREATE TABLE IF NOT EXISTS reports ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "title VARCHAR(150) NOT NULL, "
                    + "report_type VARCHAR(50) NOT NULL, "
                    + "generated_by VARCHAR(50) REFERENCES users(id), "
                    + "content TEXT, "
                    + "file_path VARCHAR(255), "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 15. Deliverables
            stmt.execute("CREATE TABLE IF NOT EXISTS deliverables ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "milestone_id VARCHAR(50) NOT NULL REFERENCES milestones(id) ON DELETE CASCADE, "
                    + "freelancer_id VARCHAR(50) NOT NULL REFERENCES freelancer_profiles(id), "
                    + "title VARCHAR(150) NOT NULL, "
                    + "description TEXT, "
                    + "file_path VARCHAR(255), "
                    + "status VARCHAR(30) DEFAULT 'SUBMITTED', "
                    + "submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 16. Conversations
            stmt.execute("CREATE TABLE IF NOT EXISTS conversations ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "user1_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "user2_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "project_id VARCHAR(50) REFERENCES projects(id) ON DELETE SET NULL, "
                    + "last_message TEXT, "
                    + "last_message_time DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 17. Messages
            stmt.execute("CREATE TABLE IF NOT EXISTS messages ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "conversation_id VARCHAR(50) NOT NULL REFERENCES conversations(id) ON DELETE CASCADE, "
                    + "sender_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "content TEXT NOT NULL, "
                    + "attachment_path VARCHAR(255), "
                    + "is_read BOOLEAN DEFAULT 0, "
                    + "sent_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 18. Notifications
            stmt.execute("CREATE TABLE IF NOT EXISTS notifications ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "user_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "title VARCHAR(150) NOT NULL, "
                    + "message TEXT NOT NULL, "
                    + "type VARCHAR(50) DEFAULT 'INFO', "
                    + "reference_id VARCHAR(50), "
                    + "is_read BOOLEAN DEFAULT 0, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 19. Reviews
            stmt.execute("CREATE TABLE IF NOT EXISTS reviews ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "project_id VARCHAR(50) REFERENCES projects(id) ON DELETE SET NULL, "
                    + "reviewer_id VARCHAR(50) NOT NULL REFERENCES users(id), "
                    + "reviewee_id VARCHAR(50) NOT NULL REFERENCES users(id), "
                    + "rating REAL NOT NULL CHECK(rating >= 1.0 AND rating <= 5.0), "
                    + "feedback TEXT, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 20. Favorites
            stmt.execute("CREATE TABLE IF NOT EXISTS favorites ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "user_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "item_type VARCHAR(30) NOT NULL, "
                    + "item_id VARCHAR(50) NOT NULL, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + "UNIQUE(user_id, item_type, item_id)"
                    + ");");

            // 21. Skill Exchange
            stmt.execute("CREATE TABLE IF NOT EXISTS skill_exchange ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "offerer_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "offered_skill VARCHAR(100) NOT NULL, "
                    + "requested_skill VARCHAR(100) NOT NULL, "
                    + "description TEXT, "
                    + "status VARCHAR(30) DEFAULT 'OPEN', "
                    + "requester_id VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL, "
                    + "requester_note TEXT, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 22. Community Posts
            stmt.execute("CREATE TABLE IF NOT EXISTS community_posts ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "author_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "title VARCHAR(150) NOT NULL, "
                    + "content TEXT NOT NULL, "
                    + "category VARCHAR(50) DEFAULT 'GENERAL', "
                    + "likes_count INTEGER DEFAULT 0, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 23. Community Comments
            stmt.execute("CREATE TABLE IF NOT EXISTS community_comments ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "post_id VARCHAR(50) NOT NULL REFERENCES community_posts(id) ON DELETE CASCADE, "
                    + "author_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "content TEXT NOT NULL, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 24. Support Tickets
            stmt.execute("CREATE TABLE IF NOT EXISTS support_tickets ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "user_id VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                    + "subject VARCHAR(150) NOT NULL, "
                    + "description TEXT NOT NULL, "
                    + "priority VARCHAR(20) DEFAULT 'MEDIUM', "
                    + "status VARCHAR(30) DEFAULT 'OPEN', "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 25. Disputes
            stmt.execute("CREATE TABLE IF NOT EXISTS disputes ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "contract_id VARCHAR(50) NOT NULL REFERENCES contracts(id) ON DELETE CASCADE, "
                    + "raised_by VARCHAR(50) NOT NULL REFERENCES users(id), "
                    + "reason VARCHAR(150) NOT NULL, "
                    + "details TEXT, "
                    + "description TEXT, "
                    + "status VARCHAR(30) DEFAULT 'OPEN', "
                    + "resolution TEXT, "
                    + "resolved_by VARCHAR(50) REFERENCES users(id), "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 26. Transactions
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "contract_id VARCHAR(50) REFERENCES contracts(id) ON DELETE SET NULL, "
                    + "milestone_id VARCHAR(50) REFERENCES milestones(id) ON DELETE SET NULL, "
                    + "sender_id VARCHAR(50) REFERENCES users(id), "
                    + "receiver_id VARCHAR(50) REFERENCES users(id), "
                    + "from_user_id VARCHAR(50) REFERENCES users(id), "
                    + "to_user_id VARCHAR(50) REFERENCES users(id), "
                    + "amount REAL NOT NULL CHECK(amount >= 0), "
                    + "type VARCHAR(30) NOT NULL, "
                    + "status VARCHAR(30) DEFAULT 'COMPLETED', "
                    + "reference VARCHAR(100), "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // 27. Audit Logs
            stmt.execute("CREATE TABLE IF NOT EXISTS audit_logs ("
                    + "id VARCHAR(50) PRIMARY KEY, "
                    + "user_id VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL, "
                    + "action VARCHAR(100) NOT NULL, "
                    + "details TEXT, "
                    + "ip_address VARCHAR(50) DEFAULT '127.0.0.1', "
                    + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");

            // Schema migrations for existing databases
            try { stmt.execute("ALTER TABLE skill_exchange ADD COLUMN requester_id VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL;"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE skill_exchange ADD COLUMN requester_note TEXT;"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE disputes ADD COLUMN details TEXT;"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE disputes ADD COLUMN description TEXT;"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE disputes ADD COLUMN resolution TEXT;"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE disputes ADD COLUMN resolved_by VARCHAR(50) REFERENCES users(id);"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE transactions ADD COLUMN sender_id VARCHAR(50) REFERENCES users(id);"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE transactions ADD COLUMN receiver_id VARCHAR(50) REFERENCES users(id);"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE transactions ADD COLUMN from_user_id VARCHAR(50) REFERENCES users(id);"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE transactions ADD COLUMN to_user_id VARCHAR(50) REFERENCES users(id);"); } catch (SQLException ignored) {}
        }
    }

    private static void createIndexes(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_fp_user_id ON freelancer_profiles(user_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_cp_user_id ON client_profiles(user_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_projects_client_id ON projects(client_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_projects_category ON projects(category);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_proposals_project ON proposals(project_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_proposals_freelancer ON proposals(freelancer_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_contracts_project ON contracts(project_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_contracts_client ON contracts(client_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_contracts_freelancer ON contracts(freelancer_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_milestones_contract ON milestones(contract_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_messages_conv ON messages(conversation_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id, is_read);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_transactions_contract ON transactions(contract_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_community_posts_author ON community_posts(author_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_community_posts_cat ON community_posts(category);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_community_comments_post ON community_comments(post_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_skill_exchange_offerer ON skill_exchange(offerer_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_skill_exchange_status ON skill_exchange(status);");
        }
    }

    private static void seedInitialData(Connection conn) throws SQLException {
        // Check if users already seeded
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users;")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Already seeded
            }
        }

        System.out.println("Seeding SkillBridge initial records...");

        // 1. Seed Skills Catalog
        String[][] standardSkills = {
            {"sk_1", "Java 17", "Software Development"},
            {"sk_2", "JavaFX", "Desktop Development"},
            {"sk_3", "SQLite / JDBC", "Database"},
            {"sk_4", "Spring Boot", "Backend Development"},
            {"sk_5", "Python", "Data & AI"},
            {"sk_6", "Machine Learning", "Data & AI"},
            {"sk_7", "UI/UX Design", "Design & Creative"},
            {"sk_8", "Figma", "Design & Creative"},
            {"sk_9", "Flutter", "Mobile Development"},
            {"sk_10", "REST APIs", "Software Development"},
            {"sk_11", "Docker & DevOps", "DevOps & Cloud"},
            {"sk_12", "Cybersecurity", "Security & Auditing"}
        };
        try (PreparedStatement ps = conn.prepareStatement("INSERT OR IGNORE INTO skills (id, name, category) VALUES (?, ?, ?);")) {
            for (String[] sk : standardSkills) {
                ps.setString(1, sk[0]);
                ps.setString(2, sk[1]);
                ps.setString(3, sk[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }

        // 2. Seed Admin User
        String adminPass = SecurityConfig.hashPassword("admin123");
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (id, username, email, phone, password_hash, role, status) VALUES (?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "usr_admin");
            ps.setString(2, "admin");
            ps.setString(3, "admin@skillbridge.com");
            ps.setString(4, "+18005550100");
            ps.setString(5, adminPass);
            ps.setString(6, "ADMIN");
            ps.setString(7, "ACTIVE");
            ps.executeUpdate();
        }

        // 3. Seed Demo Freelancer User
        String freePass = SecurityConfig.hashPassword("free123");
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (id, username, email, phone, password_hash, role, status) VALUES (?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "usr_free1");
            ps.setString(2, "alex_dev");
            ps.setString(3, "alex.dev@skillbridge.com");
            ps.setString(4, "+18005550101");
            ps.setString(5, freePass);
            ps.setString(6, "FREELANCER");
            ps.setString(7, "ACTIVE");
            ps.executeUpdate();
        }

        // Freelancer Profile
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO freelancer_profiles (id, user_id, title, bio, hourly_rate, experience_years, rating, total_reviews, completed_projects, github_url, linkedin_url, availability) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "fp_alex");
            ps.setString(2, "usr_free1");
            ps.setString(3, "Senior Full-Stack Java & Desktop Architect");
            ps.setString(4, "Specialist in high-performance JavaFX desktop suites, JDBC optimization, and secure SQLite persistence.");
            ps.setDouble(5, 65.0);
            ps.setInt(6, 6);
            ps.setDouble(7, 4.95);
            ps.setInt(8, 18);
            ps.setInt(9, 24);
            ps.setString(10, "https://github.com/alexdev-skillbridge");
            ps.setString(11, "https://linkedin.com/in/alexdev");
            ps.setString(12, "AVAILABLE");
            ps.executeUpdate();
        }

        // Attach Freelancer Skills
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO freelancer_skills (freelancer_id, skill_id, proficiency) VALUES (?, ?, ?);")) {
            String[] fSkills = {"sk_1", "sk_2", "sk_3", "sk_4", "sk_7"};
            for (String sId : fSkills) {
                ps.setString(1, "fp_alex");
                ps.setString(2, sId);
                ps.setString(3, "EXPERT");
                ps.addBatch();
            }
            ps.executeBatch();
        }

        // Portfolio Item
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO portfolio_items (id, freelancer_id, title, description, project_url) VALUES (?, ?, ?, ?, ?);")) {
            ps.setString(1, "port_1");
            ps.setString(2, "fp_alex");
            ps.setString(3, "Enterprise Algorithmic Trading Desk");
            ps.setString(4, "Sub-millisecond JavaFX trading terminal with offline caching and SQLite telemetry.");
            ps.setString(5, "https://github.com/alexdev/trading-desk");
            ps.executeUpdate();
        }

        // 4. Seed Demo Client Users
        String clientPass = SecurityConfig.hashPassword("client123");
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (id, username, email, phone, password_hash, role, status) VALUES (?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "usr_client1");
            ps.setString(2, "sarah_client");
            ps.setString(3, "sarah.client@skillbridge.com");
            ps.setString(4, "+18005550102");
            ps.setString(5, clientPass);
            ps.setString(6, "CLIENT");
            ps.setString(7, "ACTIVE");
            ps.addBatch();

            ps.setString(1, "usr_client2");
            ps.setString(2, "techcorp");
            ps.setString(3, "enterprise@techcorp.example.com");
            ps.setString(4, "+18005550103");
            ps.setString(5, SecurityConfig.hashPassword("techcorp123"));
            ps.setString(6, "CLIENT");
            ps.setString(7, "ACTIVE");
            ps.addBatch();

            ps.setString(1, "usr_client3");
            ps.setString(2, "designstudio");
            ps.setString(3, "contact@aetheric.example.com");
            ps.setString(4, "+18005550105");
            ps.setString(5, SecurityConfig.hashPassword("studio123"));
            ps.setString(6, "CLIENT");
            ps.setString(7, "ACTIVE");
            ps.addBatch();

            ps.executeBatch();
        }

        // Client Profiles
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO client_profiles (id, user_id, company_name, industry, company_website, about, total_spent, posted_projects) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "cp_sarah");
            ps.setString(2, "usr_client1");
            ps.setString(3, "NovaTech Financial Innovations");
            ps.setString(4, "FinTech & Enterprise Software");
            ps.setString(5, "https://novatech.example.com");
            ps.setString(6, "Building next-generation desktop analytics and financial transaction management tools.");
            ps.setDouble(7, 24500.0);
            ps.setInt(8, 5);
            ps.addBatch();

            ps.setString(1, "cp_techcorp");
            ps.setString(2, "usr_client2");
            ps.setString(3, "TechCorp Cloud Infrastructure");
            ps.setString(4, "Cloud & Enterprise Architecture");
            ps.setString(5, "https://techcorp.example.com");
            ps.setString(6, "Global provider of scalable cloud computing and distributed systems.");
            ps.setDouble(7, 52000.0);
            ps.setInt(8, 8);
            ps.addBatch();

            ps.setString(1, "cp_studio");
            ps.setString(2, "usr_client3");
            ps.setString(3, "Aetheric Design Studios & Labs");
            ps.setString(4, "Creative Digital Product Agency");
            ps.setString(5, "https://aetheric.example.com");
            ps.setString(6, "High-end product design agency partnering with tier-1 technology innovators globally.");
            ps.setDouble(7, 38000.0);
            ps.setInt(8, 6);
            ps.addBatch();

            ps.executeBatch();
        }

        // 4b. Seed Additional Freelancer User
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (id, username, email, phone, password_hash, role, status) VALUES (?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "usr_free2");
            ps.setString(2, "sarah_ui");
            ps.setString(3, "sarah.ui@skillbridge.com");
            ps.setString(4, "+18005550104");
            ps.setString(5, freePass);
            ps.setString(6, "FREELANCER");
            ps.setString(7, "ACTIVE");
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO freelancer_profiles (id, user_id, title, bio, hourly_rate, experience_years, rating, total_reviews, completed_projects, github_url, linkedin_url, availability) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "fp_sarah");
            ps.setString(2, "usr_free2");
            ps.setString(3, "Lead UI/UX & Design Systems Architect");
            ps.setString(4, "Crafting pixel-perfect, accessible desktop and web interfaces with Figma design tokens, smooth animations, and user-centric flows.");
            ps.setDouble(5, 75.0);
            ps.setInt(6, 5);
            ps.setDouble(7, 4.90);
            ps.setInt(8, 14);
            ps.setInt(9, 19);
            ps.setString(10, "https://github.com/sarahui");
            ps.setString(11, "https://linkedin.com/in/sarahui");
            ps.setString(12, "AVAILABLE");
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO freelancer_skills (freelancer_id, skill_id, proficiency) VALUES (?, ?, ?);")) {
            String[] sSkills = {"sk_7", "sk_8", "sk_2", "sk_9"};
            for (String sId : sSkills) {
                ps.setString(1, "fp_sarah");
                ps.setString(2, sId);
                ps.setString(3, "EXPERT");
                ps.addBatch();
            }
            ps.executeBatch();
        }

        // 4c. Seed Third Freelancer User (Marcus Backend)
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (id, username, email, phone, password_hash, role, status) VALUES (?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "usr_free3");
            ps.setString(2, "marcus_backend");
            ps.setString(3, "marcus.backend@skillbridge.com");
            ps.setString(4, "+18005550106");
            ps.setString(5, freePass);
            ps.setString(6, "FREELANCER");
            ps.setString(7, "ACTIVE");
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO freelancer_profiles (id, user_id, title, bio, hourly_rate, experience_years, rating, total_reviews, completed_projects, github_url, linkedin_url, availability) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "fp_marcus");
            ps.setString(2, "usr_free3");
            ps.setString(3, "Distributed Systems & Cloud Security Engineer");
            ps.setString(4, "Specialist in high-throughput backend pipelines, microservices, Spring Boot, and robust data protection.");
            ps.setDouble(5, 80.0);
            ps.setInt(6, 7);
            ps.setDouble(7, 4.92);
            ps.setInt(8, 16);
            ps.setInt(9, 21);
            ps.setString(10, "https://github.com/marcusbackend");
            ps.setString(11, "https://linkedin.com/in/marcusbackend");
            ps.setString(12, "AVAILABLE");
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO freelancer_skills (freelancer_id, skill_id, proficiency) VALUES (?, ?, ?);")) {
            String[] mSkills = {"sk_1", "sk_4", "sk_10", "sk_11", "sk_12"};
            for (String sId : mSkills) {
                ps.setString(1, "fp_marcus");
                ps.setString(2, sId);
                ps.setString(3, "EXPERT");
                ps.addBatch();
            }
            ps.executeBatch();
        }

        // Portfolio Item for Marcus
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO portfolio_items (id, freelancer_id, title, description, project_url) VALUES (?, ?, ?, ?, ?);")) {
            ps.setString(1, "port_2");
            ps.setString(2, "fp_marcus");
            ps.setString(3, "Cloud Microservices Mesh & Distributed Caching");
            ps.setString(4, "Zero-trust microservice architecture processing 50k req/sec with SQLite persistent buffers.");
            ps.setString(5, "https://github.com/marcusbackend/cloud-mesh");
            ps.executeUpdate();
        }

        // Certification for Marcus
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO certifications (id, freelancer_id, name, issuer, issue_date, credential_url) VALUES (?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "cert_1");
            ps.setString(2, "fp_marcus");
            ps.setString(3, "AWS Certified Solutions Architect - Professional");
            ps.setString(4, "Amazon Web Services");
            ps.setString(5, "2024-03-15");
            ps.setString(6, "https://aws.amazon.com/verify/cert-architect");
            ps.executeUpdate();
        }

        // 5. Seed Projects
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO projects (id, client_id, title, description, category, budget_type, budget_min, budget_max, deadline, experience_level, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
            
            // Project 1
            ps.setString(1, "proj_1");
            ps.setString(2, "cp_sarah");
            ps.setString(3, "Enterprise Desktop Financial Terminal in JavaFX");
            ps.setString(4, "Need an experienced JavaFX developer to build a multi-threaded financial analytics dashboard with local SQLite persistence and live charts.");
            ps.setString(5, "Desktop Development");
            ps.setString(6, "FIXED");
            ps.setDouble(7, 3000.0);
            ps.setDouble(8, 4500.0);
            ps.setString(9, "2026-10-31");
            ps.setString(10, "EXPERT");
            ps.setString(11, "IN_PROGRESS");
            ps.addBatch();

            // Project 2
            ps.setString(1, "proj_2");
            ps.setString(2, "cp_sarah");
            ps.setString(3, "AI Matching Engine & Recommendation Pipeline");
            ps.setString(4, "Implement a deterministic weighted matching algorithm in Core Java that scores freelancer skills and portfolio matches against project requirements.");
            ps.setString(5, "Data & AI");
            ps.setString(6, "FIXED");
            ps.setDouble(7, 2500.0);
            ps.setDouble(8, 4000.0);
            ps.setString(9, "2026-11-15");
            ps.setString(10, "INTERMEDIATE");
            ps.setString(11, "OPEN");
            ps.addBatch();

            // Project 3
            ps.setString(1, "proj_3");
            ps.setString(2, "cp_techcorp");
            ps.setString(3, "Enterprise Cloud Microservices & Distributed Telemetry");
            ps.setString(4, "Architecting resilient RESTful backend microservices, real-time audit logging, and SQLite local telemetry caches.");
            ps.setString(5, "Backend Development");
            ps.setString(6, "HOURLY");
            ps.setDouble(7, 50.0);
            ps.setDouble(8, 85.0);
            ps.setString(9, "2026-12-15");
            ps.setString(10, "EXPERT");
            ps.setString(11, "OPEN");
            ps.addBatch();

            // Project 4
            ps.setString(1, "proj_4");
            ps.setString(2, "cp_studio");
            ps.setString(3, "Next-Gen Design System & Component Library for JavaFX");
            ps.setString(4, "Develop a rich desktop theme library with dynamic CSS variables, glassmorphic card containers, and fluent micro-animations.");
            ps.setString(5, "Design & Creative");
            ps.setString(6, "FIXED");
            ps.setDouble(7, 3500.0);
            ps.setDouble(8, 5000.0);
            ps.setString(9, "2026-11-30");
            ps.setString(10, "EXPERT");
            ps.setString(11, "OPEN");
            ps.addBatch();

            // Project 5
            ps.setString(1, "proj_5");
            ps.setString(2, "cp_techcorp");
            ps.setString(3, "Cloud Database Migration & WAL Telemetry Daemon");
            ps.setString(4, "Construct a daemon service synchronizing offline SQLite transaction logs to cloud data repositories with zero loss.");
            ps.setString(5, "DevOps & Cloud");
            ps.setString(6, "FIXED");
            ps.setDouble(7, 4000.0);
            ps.setDouble(8, 6500.0);
            ps.setString(9, "2026-12-01");
            ps.setString(10, "EXPERT");
            ps.setString(11, "OPEN");
            ps.addBatch();

            ps.executeBatch();
        }

        // Project Skills
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO project_skills (project_id, skill_id) VALUES (?, ?);")) {
            ps.setString(1, "proj_1");
            ps.setString(2, "sk_1"); // Java
            ps.addBatch();
            ps.setString(1, "proj_1");
            ps.setString(2, "sk_2"); // JavaFX
            ps.addBatch();
            ps.setString(1, "proj_1");
            ps.setString(2, "sk_3"); // SQLite
            ps.addBatch();
            ps.setString(1, "proj_2");
            ps.setString(2, "sk_1"); // Java
            ps.addBatch();
            ps.setString(1, "proj_2");
            ps.setString(2, "sk_6"); // ML
            ps.addBatch();
            ps.setString(1, "proj_3");
            ps.setString(2, "sk_4"); // Spring Boot
            ps.addBatch();
            ps.setString(1, "proj_3");
            ps.setString(2, "sk_10"); // REST APIs
            ps.addBatch();
            ps.setString(1, "proj_4");
            ps.setString(2, "sk_7"); // UI/UX Design
            ps.addBatch();
            ps.setString(1, "proj_4");
            ps.setString(2, "sk_8"); // Figma
            ps.addBatch();
            ps.setString(1, "proj_4");
            ps.setString(2, "sk_2"); // JavaFX
            ps.addBatch();
            ps.setString(1, "proj_5");
            ps.setString(2, "sk_3"); // SQLite
            ps.addBatch();
            ps.setString(1, "proj_5");
            ps.setString(2, "sk_11"); // Docker & DevOps
            ps.addBatch();
            ps.setString(1, "proj_5");
            ps.setString(2, "sk_10"); // REST APIs
            ps.addBatch();
            ps.executeBatch();
        }

        // 6. Seed Proposals
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO proposals (id, project_id, freelancer_id, bid_amount, delivery_days, cover_letter, status, ai_match_score) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "prop_1");
            ps.setString(2, "proj_1");
            ps.setString(3, "fp_alex");
            ps.setDouble(4, 4000.0);
            ps.setInt(5, 21);
            ps.setString(6, "I have over 6 years of expertise architecting JavaFX desktop systems with custom themes, SQLite WAL persistence, and hardware-accelerated rendering.");
            ps.setString(7, "ACCEPTED");
            ps.setDouble(8, 98.0);
            ps.addBatch();

            ps.setString(1, "prop_2");
            ps.setString(2, "proj_4");
            ps.setString(3, "fp_sarah");
            ps.setDouble(4, 4500.0);
            ps.setInt(5, 18);
            ps.setString(6, "Extensive experience designing desktop design tokens and responsive JavaFX layout hierarchies matching modern Figma specs.");
            ps.setString(7, "PENDING");
            ps.setDouble(8, 96.5);
            ps.addBatch();

            ps.setString(1, "prop_3");
            ps.setString(2, "proj_5");
            ps.setString(3, "fp_marcus");
            ps.setDouble(4, 5200.0);
            ps.setInt(5, 24);
            ps.setString(6, "Deep background in distributed systems, SQLite replication, and high-availability telemetry pipelines.");
            ps.setString(7, "PENDING");
            ps.setDouble(8, 94.0);
            ps.addBatch();

            ps.executeBatch();
        }

        // 6b. Seed Contract & Milestones & Escrow
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO contracts (id, project_id, proposal_id, client_id, freelancer_id, total_amount, escrow_balance, status, start_date, end_date, client_signed, freelancer_signed) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "ctr_seed_1");
            ps.setString(2, "proj_1");
            ps.setString(3, "prop_1");
            ps.setString(4, "cp_sarah");
            ps.setString(5, "fp_alex");
            ps.setDouble(6, 4000.0);
            ps.setDouble(7, 2000.0); // 2000 remaining in escrow, 2000 already released
            ps.setString(8, "ACTIVE");
            ps.setString(9, "2026-09-01");
            ps.setString(10, "2026-10-31");
            ps.setInt(11, 1);
            ps.setInt(12, 1);
            ps.executeUpdate();
        }

        // Milestones
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO milestones (id, contract_id, project_id, title, description, amount, deadline, status, sequence_order) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "ms_seed_1");
            ps.setString(2, "ctr_seed_1");
            ps.setString(3, "proj_1");
            ps.setString(4, "Sprint 1: Architecture & SQLite Models");
            ps.setString(5, "Design database schema, connection pool, and DAO layer.");
            ps.setDouble(6, 2000.0);
            ps.setString(7, "2026-09-15");
            ps.setString(8, "PAID");
            ps.setInt(9, 1);
            ps.addBatch();

            ps.setString(1, "ms_seed_2");
            ps.setString(2, "ctr_seed_1");
            ps.setString(3, "proj_1");
            ps.setString(4, "Sprint 2: JavaFX UI & Live Telemetry");
            ps.setString(5, "Build charting dashboards and real-time execution engine.");
            ps.setDouble(6, 2000.0);
            ps.setString(7, "2026-10-15");
            ps.setString(8, "IN_PROGRESS");
            ps.setInt(9, 2);
            ps.addBatch();

            ps.executeBatch();
        }

        // Deliverable for Milestone 1
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO deliverables (id, milestone_id, freelancer_id, title, description, status) "
                + "VALUES (?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "del_seed_1");
            ps.setString(2, "ms_seed_1");
            ps.setString(3, "fp_alex");
            ps.setString(4, "Architecture Specification & SQLite Schema");
            ps.setString(5, "Committed 27 tables with WAL mode and unit test suite.");
            ps.setString(6, "APPROVED");
            ps.executeUpdate();
        }

        // Ledger Transactions
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO transactions (id, contract_id, milestone_id, sender_id, receiver_id, amount, type, status, reference) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "tx_seed_1");
            ps.setString(2, "ctr_seed_1");
            ps.setString(3, null);
            ps.setString(4, "usr_client1");
            ps.setString(5, "usr_free1");
            ps.setDouble(6, 4000.0);
            ps.setString(7, "ESCROW_DEPOSIT");
            ps.setString(8, "COMPLETED");
            ps.setString(9, "DEMO-ESCROW-DEP-SEED1");
            ps.addBatch();

            ps.setString(1, "tx_seed_2");
            ps.setString(2, "ctr_seed_1");
            ps.setString(3, "ms_seed_1");
            ps.setString(4, "usr_client1");
            ps.setString(5, "usr_free1");
            ps.setDouble(6, 2000.0);
            ps.setString(7, "PAYMENT");
            ps.setString(8, "COMPLETED");
            ps.setString(9, "DEMO-ESCROW-REL-SEED2");
            ps.addBatch();

            ps.executeBatch();
        }

        // 6c. Seed Conversation & Messages
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO conversations (id, user1_id, user2_id, project_id, last_message, last_message_time) "
                + "VALUES (?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "conv_seed_1");
            ps.setString(2, "usr_free1");
            ps.setString(3, "usr_client1");
            ps.setString(4, "proj_1");
            ps.setString(5, "The architecture specification and SQLite database models have been committed and approved.");
            ps.setString(6, "2026-09-10 14:30:00");
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO messages (id, conversation_id, sender_id, content, is_read, sent_at) "
                + "VALUES (?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "msg_seed_1");
            ps.setString(2, "conv_seed_1");
            ps.setString(3, "usr_client1");
            ps.setString(4, "Hi Alex, welcome to the project! Looking forward to reviewing the Sprint 1 milestone architecture.");
            ps.setInt(5, 1);
            ps.setString(6, "2026-09-02 10:00:00");
            ps.addBatch();

            ps.setString(1, "msg_seed_2");
            ps.setString(2, "conv_seed_1");
            ps.setString(3, "usr_free1");
            ps.setString(4, "The architecture specification and SQLite database models have been committed and approved.");
            ps.setInt(5, 1);
            ps.setString(6, "2026-09-10 14:30:00");
            ps.addBatch();

            ps.executeBatch();
        }

        // 6d. Seed Calendar Events
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO calendar_events (id, user_id, title, description, event_date, event_time, event_type, project_id, contract_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "cal_seed_1");
            ps.setString(2, "usr_free1");
            ps.setString(3, "Sprint 2 Deliverable Deadline");
            ps.setString(4, "Complete live JavaFX telemetry charts and UI styling.");
            ps.setString(5, "2026-10-15");
            ps.setString(6, "17:00");
            ps.setString(7, "DEADLINE");
            ps.setString(8, "proj_1");
            ps.setString(9, "ctr_seed_1");
            ps.addBatch();

            ps.setString(1, "cal_seed_2");
            ps.setString(2, "usr_client1");
            ps.setString(3, "Sprint Review & Architecture Sync");
            ps.setString(4, "Virtual meeting to review sprint milestone demo.");
            ps.setString(5, "2026-10-05");
            ps.setString(6, "14:00");
            ps.setString(7, "MEETING");
            ps.setString(8, "proj_1");
            ps.setString(9, "ctr_seed_1");
            ps.addBatch();

            ps.executeBatch();
        }

        // 6e. Seed Reviews
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO reviews (id, contract_id, project_id, reviewer_id, reviewee_id, rating, feedback) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "rev_seed_1");
            ps.setString(2, "ctr_seed_1");
            ps.setString(3, "proj_1");
            ps.setString(4, "usr_client1");
            ps.setString(5, "usr_free1");
            ps.setDouble(6, 5.0);
            ps.setString(7, "Outstanding technical depth, clean code structure, and reliable sprint deliveries!");
            ps.executeUpdate();
        }

        // 7. Seed Notifications
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO notifications (id, user_id, title, message, type, reference_id, is_read) VALUES (?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "notif_1");
            ps.setString(2, "usr_client1");
            ps.setString(3, "New Proposal Received");
            ps.setString(4, "Alex Dev submitted a proposal for Enterprise Desktop Financial Terminal.");
            ps.setString(5, "PROPOSAL");
            ps.setString(6, "prop_1");
            ps.setInt(7, 0);
            ps.addBatch();

            ps.setString(1, "notif_2");
            ps.setString(2, "usr_free1");
            ps.setString(3, "Welcome to SkillBridge");
            ps.setString(4, "Your profile is verified. You have high compatibility matches in Desktop Development!");
            ps.setString(5, "SYSTEM");
            ps.setString(6, null);
            ps.setInt(7, 1);
            ps.addBatch();

            ps.executeBatch();
        }

        // 8. Seed Audit Log
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO audit_logs (id, user_id, action, details) VALUES (?, ?, ?, ?);")) {
            ps.setString(1, "log_1");
            ps.setString(2, "usr_admin");
            ps.setString(3, "SYSTEM_INITIALIZATION");
            ps.setString(4, "SkillBridge platform initialized with 27 SQLite tables and master seed data.");
            ps.executeUpdate();
        }

        // 9. Seed Community Posts
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO community_posts (id, author_id, title, content, category, likes_count) VALUES (?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "post_1");
            ps.setString(2, "usr_free1");
            ps.setString(3, "Best Practices for Offline SQLite WAL Performance in Desktop Java");
            ps.setString(4, "When building high-concurrency desktop tools with SQLite, setting PRAGMA journal_mode = WAL and synchronous = NORMAL yields immense write throughput while avoiding database locks.");
            ps.setString(5, "DISCUSSIONS");
            ps.setInt(6, 5);
            ps.addBatch();

            ps.setString(1, "post_2");
            ps.setString(2, "usr_client1");
            ps.setString(3, "Looking for Experienced Spring Boot & JavaFX Specialists");
            ps.setString(4, "We are scaling our enterprise analytics platform and looking to hire contract developers for 3-month milestone sprints.");
            ps.setString(5, "HIRING");
            ps.setInt(6, 3);
            ps.addBatch();

            ps.setString(1, "post_3");
            ps.setString(2, "usr_free1");
            ps.setString(3, "Showcase: High-Density Canvas Charting Component");
            ps.setString(4, "Just open-sourced a dynamic rendering canvas with responsive zoom and glassmorphism styling in pure JavaFX!");
            ps.setString(5, "SHOWCASE");
            ps.setInt(6, 8);
            ps.addBatch();

            ps.executeBatch();
        }

        // 10. Seed Community Comments
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO community_comments (id, post_id, author_id, content) VALUES (?, ?, ?, ?);")) {
            ps.setString(1, "comm_1");
            ps.setString(2, "post_1");
            ps.setString(3, "usr_client1");
            ps.setString(4, "Great tips! The WAL mode especially helped reduce contention between our background telemetry and the UI thread.");
            ps.executeUpdate();
        }

        // 11. Seed Skill Exchange
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO skill_exchange (id, offerer_id, offered_skill, requested_skill, description, status) VALUES (?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "swap_1");
            ps.setString(2, "usr_free1");
            ps.setString(3, "Java / JavaFX Desktop Architecture");
            ps.setString(4, "React / Modern Web Frontend");
            ps.setString(5, "Willing to provide 2 hours of 1-on-1 code reviews and architecture guidance in exchange for Next.js mentoring.");
            ps.setString(6, "OPEN");
            ps.addBatch();

            ps.setString(1, "swap_2");
            ps.setString(2, "usr_free1");
            ps.setString(3, "SQLite & Database Optimization");
            ps.setString(4, "Docker & CI/CD Pipelines");
            ps.setString(5, "Experienced in database indexing and schema design. Looking for DevOps guidance.");
            ps.setString(6, "OPEN");
            ps.addBatch();

            ps.setString(1, "swap_3");
            ps.setString(2, "usr_free2");
            ps.setString(3, "Figma & UI/UX Design Systems");
            ps.setString(4, "Spring Boot & Backend Architecture");
            ps.setString(5, "Offering interactive design token consultations in exchange for Spring Security & REST API architectural guidance.");
            ps.setString(6, "OPEN");
            ps.addBatch();

            ps.executeBatch();
        }

        // 12. Seed Community Post 4 & Comment 2
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO community_posts (id, author_id, title, content, category, likes_count) VALUES (?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "post_4");
            ps.setString(2, "usr_free2");
            ps.setString(3, "Building Design Tokens and Responsive Micro-Animations in JavaFX");
            ps.setString(4, "Using CSS variables and custom skin classes allows JavaFX desktop apps to match modern Figma design systems effortlessly.");
            ps.setString(5, "SHOWCASE");
            ps.setInt(6, 12);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO community_comments (id, post_id, author_id, content) VALUES (?, ?, ?, ?);")) {
            ps.setString(1, "comm_2");
            ps.setString(2, "post_4");
            ps.setString(3, "usr_free1");
            ps.setString(4, "Completely agree! The dynamic pseudo-classes and transition timelines keep the 60fps smoothness intact.");
            ps.executeUpdate();
        }

        // 13. Seed Support Ticket
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO support_tickets (id, user_id, subject, description, priority, status) VALUES (?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "tkt_1");
            ps.setString(2, "usr_free1");
            ps.setString(3, "Escrow milestone timeline query");
            ps.setString(4, "Inquiry regarding standard automated release window once deliverable is approved.");
            ps.setString(5, "MEDIUM");
            ps.setString(6, "OPEN");
            ps.executeUpdate();
        }

        // 14. Seed Dispute
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO disputes (id, contract_id, raised_by, reason, details, description, status, resolution, resolved_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, "disp_1");
            ps.setString(2, "ctr_seed_1");
            ps.setString(3, "usr_client1");
            ps.setString(4, "Sprint 2 Scope Clarification");
            ps.setString(5, "Clarification needed on live telemetry dashboard metrics vs historical export.");
            ps.setString(6, "Clarification needed on live telemetry dashboard metrics vs historical export.");
            ps.setString(7, "RESOLVED");
            ps.setString(8, "Specifications finalized: live metrics in dashboard, CSV historical export included.");
            ps.setString(9, "usr_admin");
            ps.executeUpdate();
        }

        System.out.println("SkillBridge seed data successfully committed.");
    }
}

package com.freelancing.db;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseInitializerTest {

    private static final String[] EXPECTED_TABLES = {
        "users", "freelancer_profiles", "client_profiles", "skills", "freelancer_skills",
        "portfolio_items", "certifications", "projects", "project_skills", "proposals",
        "contracts", "milestones", "calendar_events", "reports", "deliverables",
        "conversations", "messages", "notifications", "reviews", "favorites",
        "skill_exchange", "community_posts", "community_comments", "support_tickets",
        "disputes", "transactions", "audit_logs"
    };

    @BeforeAll
    public static void setUp() {
        DatabaseInitializer.initialize();
    }

    @Test
    @DisplayName("Verify SQLite DB file exists in data directory")
    public void testDatabaseFileExists() {
        File dbFile = new File(DatabaseConnection.DB_DIR, DatabaseConnection.DB_NAME);
        assertTrue(dbFile.exists(), "Database file data/skillbridge.db should exist");
        assertTrue(dbFile.length() > 0, "Database file should not be empty");
    }

    @Test
    @DisplayName("Verify all 27 SkillBridge tables are created")
    public void testAll27TablesExist() throws SQLException {
        Set<String> actualTables = new HashSet<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table';")) {
            while (rs.next()) {
                actualTables.add(rs.getString("name").toLowerCase());
            }
        }

        assertEquals(27, EXPECTED_TABLES.length, "Expected tables count should be 27");
        for (String table : EXPECTED_TABLES) {
            assertTrue(actualTables.contains(table.toLowerCase()), "Table '" + table + "' must exist in SQLite database");
        }
    }

    @Test
    @DisplayName("Verify PRAGMA foreign_keys is enabled")
    public void testForeignKeysEnabled() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA foreign_keys;")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1), "PRAGMA foreign_keys must be 1 (enabled)");
        }
    }

    @Test
    @DisplayName("Verify foreign key constraints are strictly enforced")
    public void testForeignKeyConstraintEnforcement() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO freelancer_profiles (id, user_id, title) VALUES (?, ?, ?);")) {
            ps.setString(1, "fp_invalid");
            ps.setString(2, "usr_non_existent_id");
            ps.setString(3, "Ghost Engineer");
            assertThrows(SQLException.class, () -> ps.executeUpdate(),
                    "Inserting with non-existent foreign key must throw SQLException");
        }
    }

    @Test
    @DisplayName("Verify initial seed data is populated")
    public void testSeedDataPopulated() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Verify Users
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users;")) {
                assertTrue(rs.next());
                assertTrue(rs.getInt(1) >= 3, "At least 3 users (Admin, Freelancer, Client) should be seeded");
            }

            // Verify Skills
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM skills;")) {
                assertTrue(rs.next());
                assertTrue(rs.getInt(1) >= 10, "Standard skills catalog should have at least 10 skills");
            }

            // Verify Projects
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM projects;")) {
                assertTrue(rs.next());
                assertTrue(rs.getInt(1) >= 2, "Seed projects should be created");
            }

            // Verify Proposals
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM proposals;")) {
                assertTrue(rs.next());
                assertTrue(rs.getInt(1) >= 1, "Seed proposal should be created");
            }

            // Verify Admin User Password is not plaintext
            try (ResultSet rs = stmt.executeQuery("SELECT password_hash FROM users WHERE username='admin';")) {
                assertTrue(rs.next());
                String pass = rs.getString("password_hash");
                assertNotNull(pass);
                assertNotEquals("admin123", pass, "Password must be hashed, never plaintext");
                assertEquals(64, pass.length(), "SHA-256 hex string should be 64 characters");
            }
        }
    }
}

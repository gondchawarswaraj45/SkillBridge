package com.freelancing.service;

import com.freelancing.dao.common.UserDAO;
import com.freelancing.model.common.User;
import com.freelancing.service.common.AuthService;

import com.freelancing.app.SessionManager;
import com.freelancing.db.DatabaseConnection;
import com.freelancing.db.DatabaseInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private static AuthService authService;
    private static UserDAO userDAO;

    @BeforeAll
    public static void setUp() {
        DatabaseInitializer.initialize();
        userDAO = new UserDAO();
        authService = new AuthService(userDAO);
    }

    @Test
    @DisplayName("Verify seed Admin authentication with salted hash")
    public void testAdminLoginSuccess() {
        User admin = authService.login("admin", "admin123");
        assertNotNull(admin, "Admin should log in successfully");
        assertEquals("admin", admin.getUsername());
        assertEquals(User.Role.ADMIN, admin.getRole());
        assertTrue(admin.isAdmin());
    }

    @Test
    @DisplayName("Verify seed Freelancer authentication with username or email")
    public void testFreelancerLoginSuccess() {
        // Via username
        User freelancer = authService.login("alex_dev", "free123");
        assertNotNull(freelancer, "Freelancer should log in via username");
        assertEquals(User.Role.FREELANCER, freelancer.getRole());
        assertTrue(freelancer.isFreelancer());

        // Via email
        User freelancerByEmail = authService.login("alex.dev@skillbridge.com", "free123");
        assertNotNull(freelancerByEmail, "Freelancer should log in via email");
        assertEquals(freelancer.getId(), freelancerByEmail.getId());
    }

    @Test
    @DisplayName("Verify seed Client authentication")
    public void testClientLoginSuccess() {
        User client = authService.login("sarah_client", "client123");
        assertNotNull(client, "Client should log in successfully");
        assertEquals(User.Role.CLIENT, client.getRole());
        assertTrue(client.isClient());
    }

    @Test
    @DisplayName("Verify login rejection with incorrect credentials")
    public void testLoginFailure() {
        User wrongPassword = authService.login("alex_dev", "incorrect_pwd");
        assertNull(wrongPassword, "Login should fail with wrong password");

        User nonExistent = authService.login("non_existent_ghost_user", "password");
        assertNull(nonExistent, "Login should fail for non-existent user");
    }

    @Test
    @DisplayName("Verify username and email availability checks")
    public void testAvailabilityChecks() {
        assertFalse(authService.isUsernameAvailable("admin"), "admin username should not be available");
        assertFalse(authService.isEmailAvailable("admin@skillbridge.com"), "admin email should not be available");

        assertTrue(authService.isUsernameAvailable("totally_unique_user_2026"), "New username should be available");
        assertTrue(authService.isEmailAvailable("new_dev_user_2026@domain.test"), "New email should be available");
    }

    @Test
    @DisplayName("Verify new Freelancer registration and automatic SQLite profile initialization")
    public void testRegisterNewFreelancer() throws SQLException {
        String testUser = "dev_marcus_" + System.currentTimeMillis();
        String testEmail = testUser + "@skillbridge.test";

        User registered = authService.registerUser(testUser, testEmail, "+19998887777", "secureDevPass2026", User.Role.FREELANCER);
        assertNotNull(registered);
        assertNotNull(registered.getId());
        assertEquals(testUser, registered.getUsername());
        assertEquals(User.Role.FREELANCER, registered.getRole());

        // Verify password is encrypted in SQLite
        User fetchedFromDb = userDAO.findById(registered.getId());
        assertNotNull(fetchedFromDb);
        assertNotEquals("secureDevPass2026", fetchedFromDb.getPassword(), "Stored password must be salted hash");

        // Verify freelancer_profiles row was created
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT title, hourly_rate FROM freelancer_profiles WHERE user_id = ?;")) {
            ps.setString(1, registered.getId());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "freelancer_profiles record must exist in SQLite");
                assertNotNull(rs.getString("title"));
            }
        }

        // Test login with the newly created account
        User authenticated = authService.login(testUser, "secureDevPass2026");
        assertNotNull(authenticated, "Newly registered user should be able to log in");
        assertEquals(registered.getId(), authenticated.getId());
    }

    @Test
    @DisplayName("Verify SessionManager desktop state and reactive listeners")
    public void testSessionManager() {
        SessionManager session = SessionManager.getInstance();
        session.logout();
        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());

        User testUser = new User("usr_session_test", "session_user", "session@test.com", "+1234", "hash", User.Role.CLIENT, User.Status.ACTIVE, "2026-01-01");

        AtomicBoolean listenerCalled = new AtomicBoolean(false);
        session.addSessionListener(u -> {
            if (u != null && u.getUsername().equals("session_user")) {
                listenerCalled.set(true);
            }
        });

        session.login(testUser);
        assertTrue(session.isLoggedIn());
        assertTrue(session.isClient());
        assertEquals("usr_session_test", session.getCurrentUserId());
        assertEquals("session_user", session.getCurrentUsername());
        assertTrue(listenerCalled.get(), "Session listener must be notified on login");

        session.logout();
        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
    }
}

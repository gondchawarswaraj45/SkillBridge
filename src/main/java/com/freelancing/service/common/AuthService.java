package com.freelancing.service.common;

import com.freelancing.dao.common.UserDAO;
import com.freelancing.model.common.User;

import com.freelancing.config.SecurityConfig;
import com.freelancing.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Service managing user authentication, registration, password hashing, and credentials verification.
 * Backed by UserDAO and SQLite persistence.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticate a user by username, email, or phone and raw password.
     */
    public User login(String identifier, String password) {
        if (identifier == null || password == null || identifier.trim().isEmpty()) {
            System.out.println("[SkillBridge-Auth] Login attempted with empty credentials.");
            return null;
        }

        System.out.println("[SkillBridge-Auth] Authenticating user: " + identifier.trim());
        User user = userDAO.findByIdentifier(identifier.trim());
        if (user == null) {
            System.out.println("[SkillBridge-Auth] User not found: " + identifier.trim());
            return null;
        }

        // Verify password hash
        if (SecurityConfig.verifyPassword(password, user.getPassword())) {
            System.out.println("[SkillBridge-Auth] Password verified successfully for " + user.getUsername() + " (" + user.getRole() + ")");
            logAudit(user.getId(), "USER_LOGIN", "Successful login for " + user.getUsername() + " (" + user.getRole() + ")");
            return user;
        }

        System.out.println("[SkillBridge-Auth] Invalid password attempt for " + user.getUsername());
        logAudit(user.getId(), "LOGIN_FAILED", "Invalid password attempt for " + user.getUsername());
        return null;
    }

    public boolean isUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        return !userDAO.existsByUsername(username.trim());
    }

    public boolean isEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return !userDAO.existsByEmail(email.trim());
    }

    /**
     * Register a new user with salted password hash and create corresponding role profile in SQLite.
     */
    public User registerUser(String username, String email, String phone, String password, User.Role role) {
        System.out.println("[SkillBridge-Auth] Processing registration request for new " + role + ": " + username);
        if (username == null || email == null || password == null || role == null) {
            throw new IllegalArgumentException("Username, email, password, and role are required.");
        }

        String trimmedUser = username.trim();
        String trimmedEmail = email.trim();
        String trimmedPhone = phone != null ? phone.trim() : "";

        if (userDAO.existsByUsername(trimmedUser)) {
            throw new IllegalStateException("Username is already taken.");
        }
        if (userDAO.existsByEmail(trimmedEmail)) {
            throw new IllegalStateException("Email address is already registered.");
        }

        String userId = "usr_" + UUID.randomUUID().toString().substring(0, 8);
        String passwordHash = SecurityConfig.hashPassword(password);

        User newUser = new User(
                userId,
                trimmedUser,
                trimmedEmail,
                trimmedPhone,
                passwordHash,
                role,
                User.Status.ACTIVE,
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        );

        boolean created = userDAO.create(newUser);
        if (!created) {
            throw new RuntimeException("Failed to persist user in database.");
        }

        // Initialize role profile
        createRoleProfile(newUser);

        // Send welcome notification
        createWelcomeNotification(newUser);

        // Audit log
        logAudit(userId, "USER_REGISTERED", "New account registered as " + role + " (" + trimmedUser + ")");

        return newUser;
    }

    /**
     * Change user password after verifying current password.
     */
    public boolean changePassword(String userId, String currentPassword, String newPassword) {
        User user = userDAO.findById(userId);
        if (user == null) return false;

        if (!SecurityConfig.verifyPassword(currentPassword, user.getPassword())) {
            return false;
        }

        String newHash = SecurityConfig.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(userId, newHash);
        if (updated) {
            logAudit(userId, "PASSWORD_CHANGED", "Password successfully updated.");
        }
        return updated;
    }

    public String generateSimulatedOtp() {
        int otp = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    private void createRoleProfile(User user) {
        if (user.getRole() == User.Role.FREELANCER) {
            String sql = "INSERT OR IGNORE INTO freelancer_profiles (id, user_id, title, bio, hourly_rate, experience_years, rating, availability) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "fp_" + user.getId().substring(4));
                ps.setString(2, user.getId());
                ps.setString(3, "Freelance Specialist");
                ps.setString(4, "Welcome to SkillBridge! Professional specialist looking for creative freelance opportunities.");
                ps.setDouble(5, 45.0);
                ps.setInt(6, 1);
                ps.setDouble(7, 5.0);
                ps.setString(8, "AVAILABLE");
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Failed to create freelancer profile: " + e.getMessage());
            }
        } else if (user.getRole() == User.Role.CLIENT) {
            String sql = "INSERT OR IGNORE INTO client_profiles (id, user_id, company_name, industry, about) "
                       + "VALUES (?, ?, ?, ?, ?);";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "cp_" + user.getId().substring(4));
                ps.setString(2, user.getId());
                ps.setString(3, user.getUsername() + " Ventures");
                ps.setString(4, "Technology & Professional Services");
                ps.setString(5, "Hiring vetted talent across software, design, and AI development.");
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Failed to create client profile: " + e.getMessage());
            }
        }
    }

    private void createWelcomeNotification(User user) {
        String sql = "INSERT INTO notifications (id, user_id, title, message, type, is_read) VALUES (?, ?, ?, ?, ?, 0);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "notif_" + UUID.randomUUID().toString().substring(0, 8));
            ps.setString(2, user.getId());
            ps.setString(3, "Welcome to SkillBridge!");
            ps.setString(4, "Your account has been created. Explore the marketplace or complete your profile to unlock high-match recommendations.");
            ps.setString(5, "SYSTEM");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to send welcome notification: " + e.getMessage());
        }
    }

    private void logAudit(String userId, String action, String details) {
        String sql = "INSERT INTO audit_logs (id, user_id, action, details, ip_address) VALUES (?, ?, ?, ?, '127.0.0.1');";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "log_" + UUID.randomUUID().toString().substring(0, 8));
            ps.setString(2, userId);
            ps.setString(3, action);
            ps.setString(4, details);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to log audit event: " + e.getMessage());
        }
    }
}

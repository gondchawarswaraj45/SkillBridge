package com.freelancing.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Security standards, input sanitization, and password hashing for SkillBridge.
 */
public class SecurityConfig {

    private static final String SALT = "SkillBridge_Secure_Salt_2026";

    private SecurityConfig() {}

    /**
     * Compute SHA-256 hash with salt for credential verification.
     */
    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = password + SALT;
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // Fallback
        }
    }

    /**
     * Verify a raw password against a stored salted SHA-256 hash.
     */
    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) return false;
        return hashPassword(rawPassword).equals(hashedPassword);
    }

    /**
     * Sanitize user text inputs to prevent script / markup injection.
     */
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.replace("<", "&lt;").replace(">", "&gt;").trim();
    }
}

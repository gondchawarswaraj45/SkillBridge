package com.freelancing.util;

/**
 * Input validation utilities for the SkillBridge platform.
 * Provides validation for emails, phones, UPI IDs, bank details, and prices.
 */
public class ValidationUtil {

    private ValidationUtil() {} // Utility class

    /** Validate email format */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /** Validate phone number (10+ digits, optional country code) */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String cleaned = phone.replaceAll("[\\s\\-()]+", "");
        return cleaned.matches("^\\+?[0-9]{10,15}$");
    }

    /** Validate UPI ID format (e.g., username@bank) */
    public static boolean isValidUpiId(String upiId) {
        if (upiId == null || upiId.trim().isEmpty()) return false;
        return upiId.matches("^[a-zA-Z0-9.\\-_]+@[a-zA-Z0-9]+$");
    }

    /** Validate IFSC code format (e.g., ABCD0123456) */
    public static boolean isValidIfsc(String ifsc) {
        if (ifsc == null || ifsc.trim().isEmpty()) return false;
        return ifsc.matches("^[A-Z]{4}0[A-Z0-9]{6}$");
    }

    /** Validate bank account number (8-18 digits) */
    public static boolean isValidAccountNumber(String accNum) {
        if (accNum == null || accNum.trim().isEmpty()) return false;
        return accNum.matches("^[0-9]{8,18}$");
    }

    /** Validate positive price/amount */
    public static boolean isValidPrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) return false;
        try {
            double val = Double.parseDouble(priceStr.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Validate non-empty string */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /** Validate string length */
    public static boolean isValidLength(String value, int minLen, int maxLen) {
        if (value == null) return minLen == 0;
        int len = value.trim().length();
        return len >= minLen && len <= maxLen;
    }

    /** Validate date format (yyyy-MM-dd) */
    public static boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) return false;
        return date.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    /** Validate password strength (min 8 chars, at least 1 upper, 1 lower, 1 digit) */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasUpper && hasLower && hasDigit;
    }

    /** Sanitize input — strip dangerous characters */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.replaceAll("[<>\"';&|]", "").trim();
    }

    /** Validate guest count (positive integer) */
    public static boolean isValidGuestCount(String countStr) {
        if (countStr == null || countStr.trim().isEmpty()) return false;
        try {
            int val = Integer.parseInt(countStr.trim());
            return val > 0 && val <= 50000;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

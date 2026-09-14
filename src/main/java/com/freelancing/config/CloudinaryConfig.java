package com.freelancing.config;

/**
 * Cloudinary configuration for profile and portfolio media storage.
 */
public class CloudinaryConfig {
    private static String cloudName = "";
    private static String apiKey = "";
    private static String apiSecret = "";

    public static boolean isConfigured() {
        return cloudName != null && !cloudName.isEmpty() && !apiKey.isEmpty();
    }

    public static String getCloudName() {
        return cloudName;
    }

    public static void setCloudName(String name) {
        cloudName = name;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String key) {
        apiKey = key;
    }

    public static String getApiSecret() {
        return apiSecret;
    }

    public static void setApiSecret(String secret) {
        apiSecret = secret;
    }
}

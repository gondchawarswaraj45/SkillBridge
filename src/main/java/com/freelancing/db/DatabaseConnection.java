package com.freelancing.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton database connection manager for SkillBridge SQLite persistence.
 * Enforces PRAGMA foreign_keys = ON, WAL mode, and directory creation.
 */
public class DatabaseConnection {

    public static final String DB_DIR = "data";
    public static final String DB_NAME = "skillbridge.db";
    public static final String DB_URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_NAME;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            File dir = new File(DB_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: SQLite JDBC Driver missing: " + e.getMessage());
        }
    }

    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private DatabaseConnection() {}

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    /**
     * Get a fresh Connection with PRAGMAs configured.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute("PRAGMA journal_mode = WAL;");
            stmt.execute("PRAGMA synchronous = NORMAL;");
            stmt.execute("PRAGMA busy_timeout = 5000;");
        }
        return conn;
    }

    /**
     * Get the absolute path of the SQLite database file.
     */
    public static String getDatabaseAbsolutePath() {
        return new File(DB_DIR, DB_NAME).getAbsolutePath();
    }
}

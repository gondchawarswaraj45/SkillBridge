package com.freelancing.util;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Structured logging utility with log levels and audit trail support.
 * Logs to both console and a rolling log file.
 */
public class LoggingUtil {

    public enum Level {
        DEBUG, INFO, WARN, ERROR, AUDIT
    }

    private static final String LOG_FILE = "skillbridge.log";
    private static final String AUDIT_FILE = "skillbridge_audit.log";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final List<String> recentLogs = Collections.synchronizedList(new ArrayList<>());
    private static final int MAX_RECENT = 500;
    private static Level minLevel = Level.INFO;

    private LoggingUtil() {}

    public static void setMinLevel(Level level) {
        minLevel = level;
    }

    public static void debug(String tag, String message) {
        log(Level.DEBUG, tag, message);
    }

    public static void info(String tag, String message) {
        log(Level.INFO, tag, message);
    }

    public static void warn(String tag, String message) {
        log(Level.WARN, tag, message);
    }

    public static void error(String tag, String message) {
        log(Level.ERROR, tag, message);
    }

    public static void error(String tag, String message, Throwable throwable) {
        log(Level.ERROR, tag, message + " | Exception: " + throwable.getMessage());
    }

    /** Audit log for admin actions — always written regardless of minLevel */
    public static void audit(String userId, String action, String details) {
        String timestamp = SDF.format(new Date());
        String entry = String.format("[%s] AUDIT | User: %s | Action: %s | %s", timestamp, userId, action, details);
        System.out.println(entry);
        writeToFile(AUDIT_FILE, entry);
        addRecent(entry);
    }

    private static void log(Level level, String tag, String message) {
        if (level.ordinal() < minLevel.ordinal()) return;

        String timestamp = SDF.format(new Date());
        String entry = String.format("[%s] %s [%s] %s", timestamp, level.name(), tag, message);

        // Console output with color-coded level
        if (level == Level.ERROR) {
            System.err.println(entry);
        } else if (level == Level.WARN) {
            System.out.println("⚠ " + entry);
        } else {
            System.out.println(entry);
        }

        writeToFile(LOG_FILE, entry);
        addRecent(entry);
    }

    private static void addRecent(String entry) {
        recentLogs.add(entry);
        while (recentLogs.size() > MAX_RECENT) {
            recentLogs.remove(0);
        }
    }

    private static void writeToFile(String filename, String entry) {
        try (FileWriter fw = new FileWriter(filename, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(entry);
        } catch (IOException e) {
            // Silent fail — don't log about logging failures
        }
    }

    /** Get recent log entries for admin dashboard */
    public static List<String> getRecentLogs() {
        return new ArrayList<>(recentLogs);
    }

    /** Get recent logs filtered by level */
    public static List<String> getRecentLogs(Level level) {
        List<String> filtered = new ArrayList<>();
        String levelStr = level.name();
        for (String log : recentLogs) {
            if (log.contains(levelStr)) filtered.add(log);
        }
        return filtered;
    }

    /** Clear the log files */
    public static void clearLogs() {
        recentLogs.clear();
        new File(LOG_FILE).delete();
    }
}

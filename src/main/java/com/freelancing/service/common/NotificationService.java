package com.freelancing.service.common;

import com.freelancing.dao.common.NotificationDAO;
import com.freelancing.model.common.Notification;

import com.freelancing.db.DatabaseManager;
import com.freelancing.util.LoggingUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service managing user notifications backed by SQLite `notifications` table.
 */
public class NotificationService {
    private static final String TAG = "NotificationService";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final NotificationDAO notificationDAO;
    private final DatabaseManager db = DatabaseManager.getInstance();

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public NotificationService(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    public void sendNotification(String userId, String title, String message) {
        sendNotification(userId, title, message, "INFO", null);
    }

    public void sendNotification(String userId, String title, String message, String type, String referenceId) {
        if (userId == null || userId.trim().isEmpty()) return;

        String notifId = "notif_" + UUID.randomUUID().toString().substring(0, 8);
        String ts = LocalDateTime.now().format(FORMATTER);

        Notification notif = new Notification();
        notif.setId(notifId);
        notif.setUserId(userId);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setType(type != null ? type : "INFO");
        notif.setReferenceId(referenceId);
        notif.setRead(false);
        notif.setCreatedAt(ts);
        notif.setTimestamp(ts);

        // 1. Persist to SQLite
        notificationDAO.create(notif);

        // 2. Synchronize to in-memory map for backwards compatibility
        try {
            db.getNotifications().put(notifId, notif);
            db.invalidateCaches();
        } catch (Exception ignored) {}

        LoggingUtil.info(TAG, "Notification sent to " + userId + ": " + title);
    }

    public List<Notification> getUserNotifications(String userId) {
        if (userId == null) return List.of();
        return notificationDAO.findByUserId(userId);
    }

    public int getUnreadCount(String userId) {
        if (userId == null) return 0;
        return notificationDAO.getUnreadCount(userId);
    }

    public boolean markAsRead(String notificationId) {
        if (notificationId == null) return false;
        return notificationDAO.markAsRead(notificationId);
    }

    public boolean markAllAsRead(String userId) {
        if (userId == null) return false;
        return notificationDAO.markAllAsRead(userId);
    }

    public boolean deleteNotification(String notificationId) {
        if (notificationId == null) return false;
        return notificationDAO.delete(notificationId);
    }
}

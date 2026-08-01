package com.freelancing.service;

import com.freelancing.db.DatabaseManager;
import com.freelancing.model.Notification;
import java.util.List;
import java.util.UUID;

public class NotificationService {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public void sendNotification(String userId, String title, String message) {
        String notifId = "notif_" + UUID.randomUUID().toString().substring(0, 8);
        String ts = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
        Notification notif = new Notification(notifId, userId, title, message, false, ts);
        db.getNotifications().put(notifId, notif);
        db.invalidateCaches(); // Invalidate notification cache
        db.saveData();
    }

    /** Uses indexed lookup O(1) from DatabaseManager instead of O(n) full scan */
    public List<Notification> getUserNotifications(String userId) {
        return db.getNotificationsByUser(userId);
    }
}

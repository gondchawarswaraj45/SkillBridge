package com.freelancing.service;

import com.freelancing.db.DatabaseManager;
import com.freelancing.model.Notification;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationService {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public void sendNotification(String userId, String title, String message) {
        String notifId = "notif_" + UUID.randomUUID().toString().substring(0, 8);
        String ts = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
        Notification notif = new Notification(notifId, userId, title, message, false, ts);
        db.getNotifications().put(notifId, notif);
        db.saveData();
    }

    public List<Notification> getUserNotifications(String userId) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : db.getNotifications().values()) {
            if (n.getUserId().equals(userId)) {
                result.add(n);
            }
        }
        result.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return result;
    }
}

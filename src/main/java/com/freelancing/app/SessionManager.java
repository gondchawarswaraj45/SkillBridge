package com.freelancing.app;

import com.freelancing.model.common.User;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Thread-safe desktop session manager for SkillBridge.
 * Maintains current user state, role authorization checks, and session lifecycle listeners.
 */
public class SessionManager {

    private static final SessionManager instance = new SessionManager();

    private volatile User currentUser;
    private final List<Consumer<User>> sessionListeners = new CopyOnWriteArrayList<>();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return instance;
    }

    public synchronized void login(User user) {
        this.currentUser = user;
        notifyListeners(user);
    }

    public synchronized void logout() {
        this.currentUser = null;
        notifyListeners(null);
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "Guest";
    }

    public User.Role getCurrentRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public boolean isFreelancer() {
        return currentUser != null && currentUser.isFreelancer();
    }

    public boolean isClient() {
        return currentUser != null && currentUser.isClient();
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public void addSessionListener(Consumer<User> listener) {
        if (listener != null) {
            sessionListeners.add(listener);
        }
    }

    public void removeSessionListener(Consumer<User> listener) {
        sessionListeners.remove(listener);
    }

    private void notifyListeners(User user) {
        for (Consumer<User> listener : sessionListeners) {
            try {
                listener.accept(user);
            } catch (Exception e) {
                System.err.println("SessionListener notification error: " + e.getMessage());
            }
        }
    }
}

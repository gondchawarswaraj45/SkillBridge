package com.freelancing.service;

import com.freelancing.db.DatabaseManager;
import com.freelancing.model.ClientProfile;
import com.freelancing.model.FreelancerProfile;
import com.freelancing.model.User;

import java.util.UUID;

public class AuthService {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public User login(String identifier, String password) {
        for (User user : db.getUsers().values()) {
            if ((user.getUsername().equalsIgnoreCase(identifier) ||
                 user.getEmail().equalsIgnoreCase(identifier) ||
                 user.getPhone().equals(identifier)) &&
                user.getPassword().equals(password)) {
                
                db.logActivity("User logged in: " + user.getUsername() + " (" + user.getRole() + ")");
                return user;
            }
        }
        return null;
    }

    public boolean isUsernameAvailable(String username) {
        for (User user : db.getUsers().values()) {
            if (user.getUsername().equalsIgnoreCase(username)) return false;
        }
        return true;
    }

    public boolean isEmailAvailable(String email) {
        for (User user : db.getUsers().values()) {
            if (user.getEmail().equalsIgnoreCase(email)) return false;
        }
        return true;
    }

    public User registerUser(String username, String email, String phone, String password, User.Role role) {
        String userId = "usr_" + UUID.randomUUID().toString().substring(0, 8);
        User user = new User(userId, username, email, phone, password, role, User.Status.VERIFIED, new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        
        db.getUsers().put(userId, user);

        if (role == User.Role.FREELANCER) {
            FreelancerProfile fp = new FreelancerProfile();
            fp.setUserId(userId);
            fp.setTitle("Freelance Specialist");
            fp.setBio("Professional freelancer looking for innovative opportunities.");
            db.getFreelancerProfiles().put(userId, fp);
        } else if (role == User.Role.CLIENT) {
            ClientProfile cp = new ClientProfile();
            cp.setUserId(userId);
            cp.setCompanyName(username + " Organization");
            cp.setIndustry("General Enterprise");
            db.getClientProfiles().put(userId, cp);
        }

        db.logActivity("New user registered: " + username + " as " + role);
        db.saveData();
        return user;
    }

    public String generateSimulatedOtp() {
        int otp = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }
}

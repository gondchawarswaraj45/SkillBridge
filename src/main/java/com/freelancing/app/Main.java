package com.freelancing.app;

import com.freelancing.ui.common.HomePage;

import com.freelancing.db.DatabaseInitializer;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("🚀 [SkillBridge] Starting Next-Gen Freelancing & Skill Exchange Platform");
        System.out.println("==================================================================");
        try {
            System.out.println("[SkillBridge] Initializing SQLite database schema and seed data...");
            DatabaseInitializer.initialize();
            System.out.println("[SkillBridge] Database initialized successfully.");
            System.out.println("[SkillBridge] Launching JavaFX application UI thread...");
            Application.launch(HomePage.class, args);
        } catch (Exception e) {
            System.err.println("[SkillBridge] Application startup failure: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


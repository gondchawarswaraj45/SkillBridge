package com.freelancing.service.freelancer;

import com.freelancing.model.company.Project;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.model.freelancer.MatchingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Local Deterministic AI Proposal Drafter.
 * Analyzes project requirements, tech stack skills, budget constraints,
 * and freelancer profile achievements to generate high-conversion proposals and milestone plans.
 */
public class AIProposalAssistant {

    public static class MilestoneDraft {
        private final String title;
        private final String description;
        private final double amount;
        private final int days;

        public MilestoneDraft(String title, String description, double amount, int days) {
            this.title = title;
            this.description = description;
            this.amount = amount;
            this.days = days;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public double getAmount() { return amount; }
        public int getDays() { return days; }
    }

    /**
     * Generates a structured, client-tailored proposal cover letter.
     */
    public String generateProposalCoverLetter(Project project, FreelancerProfile freelancer, MatchingResult match) {
        if (project == null) return "";
        String clientGreeting = project.getClientName() != null && !project.getClientName().isBlank()
                ? "Dear " + project.getClientName()
                : "Hello Hiring Manager";

        String devTitle = freelancer != null && freelancer.getTitle() != null && !freelancer.getTitle().isBlank()
                ? freelancer.getTitle()
                : "Senior Software Engineer";

        List<String> matched = match != null ? match.getMatchedSkills() : project.getRequiredSkills();
        String skillsStr = matched != null && !matched.isEmpty()
                ? String.join(", ", matched)
                : "Java, Object-Oriented Architecture, and SQLite Persistence";

        int expYears = freelancer != null ? freelancer.getExperienceYears() : 5;
        double rating = freelancer != null ? freelancer.getRating() : 5.0;
        int jobsCompleted = freelancer != null ? freelancer.getCompletedProjects() : 12;

        double suggestedBid = suggestBidAmount(project, freelancer);
        int deliveryDays = suggestDeliveryDays(project);

        StringBuilder sb = new StringBuilder();
        sb.append(clientGreeting).append(",\n\n");
        sb.append("I am submitting my proposal for '").append(project.getTitle()).append("'. ")
          .append("With ").append(expYears).append("+ years of engineering experience as a ").append(devTitle)
          .append(" and a proven track record (").append(jobsCompleted).append(" completed deliveries, ")
          .append(String.format("%.2f", rating)).append("/5.0 client satisfaction), ")
          .append("I have the exact hands-on skillset in ").append(skillsStr).append(" needed to execute this project flawlessly.\n\n");

        sb.append("📌 ARCHITECTURAL STRATEGY & DELIVERABLES:\n");
        sb.append("1. Discovery & Design Verification: Aligning on technical scope, database schema constraints, and modular separation of concerns.\n");
        sb.append("2. High-Performance Implementation: Writing clean, robust, and asynchronous code with zero memory leaks and hardware acceleration.\n");
        sb.append("3. Quality Assurance: Complete automated JUnit test suites, rigorous input sanitization, and responsive UI layout validation.\n");
        sb.append("4. Deployment & Handover: Milestone packaging, documented developer runbooks, and smooth onboarding.\n\n");

        sb.append("🎯 PROPOSED SPRINT MILESTONES (Total: $").append(String.format("%.0f", suggestedBid)).append("):\n");
        List<MilestoneDraft> milestones = generateMilestoneDrafts(project, suggestedBid);
        for (int i = 0; i < milestones.size(); i++) {
            MilestoneDraft m = milestones.get(i);
            sb.append(String.format("   • Milestone %d: %s ($%.0f — %d days)\n", i + 1, m.getTitle(), m.getAmount(), m.getDays()));
        }
        sb.append("⏱️ ESTIMATED TIMELINE: Approximately ").append(deliveryDays).append(" business days from contract kickoff.\n\n");
        sb.append("I am available immediately to discuss technical requirements via desktop chat. ")
          .append("I look forward to partnering with you on '").append(project.getTitle()).append("'!\n\n");

        sb.append("Best regards,\n");
        sb.append(freelancer != null && freelancer.getTitle() != null ? freelancer.getTitle() : "Verified SkillBridge Developer");

        return sb.toString();
    }

    /**
     * Recommends a data-driven, competitive bid amount based on project budget and freelancer profile.
     */
    public double suggestBidAmount(Project project, FreelancerProfile freelancer) {
        if (project == null) return 1000.0;
        if ("HOURLY".equalsIgnoreCase(project.getBudgetType())) {
            double rate = freelancer != null && freelancer.getHourlyRate() > 0 ? freelancer.getHourlyRate() : 65.0;
            double pMax = project.getBudgetMax() > 0 ? project.getBudgetMax() : project.getBudget();
            if (pMax > 0 && rate > pMax) {
                return pMax;
            }
            return rate;
        } else {
            // FIXED PRICE
            double bMin = project.getBudgetMin();
            double bMax = project.getBudgetMax() > 0 ? project.getBudgetMax() : project.getBudget();
            if (bMin > 0 && bMax > 0 && bMin < bMax) {
                // Sweet spot: 85% towards max budget
                return Math.round(bMin + (bMax - bMin) * 0.85);
            } else if (bMax > 0) {
                return Math.round(bMax * 0.95);
            } else {
                return 2500.0;
            }
        }
    }

    /**
     * Estimates realistic delivery days based on project requirements.
     */
    public int suggestDeliveryDays(Project project) {
        if (project == null) return 14;
        String exp = project.getExperienceLevel() != null ? project.getExperienceLevel().toUpperCase() : "INTERMEDIATE";
        if ("ENTRY".equals(exp)) {
            return 10;
        } else if ("EXPERT".equals(exp)) {
            return 28;
        } else {
            return 18;
        }
    }

    /**
     * Breaks down the suggested bid into 3 industry-standard milestone deliverables.
     */
    public List<MilestoneDraft> generateMilestoneDrafts(Project project, double totalBid) {
        List<MilestoneDraft> list = new ArrayList<>();
        double m1Amount = Math.round(totalBid * 0.30);
        double m2Amount = Math.round(totalBid * 0.40);
        double m3Amount = totalBid - (m1Amount + m2Amount);

        int totalDays = suggestDeliveryDays(project);
        int d1 = Math.max(3, (int) Math.round(totalDays * 0.25));
        int d2 = Math.max(5, (int) Math.round(totalDays * 0.50));
        int d3 = Math.max(3, totalDays - (d1 + d2));

        list.add(new MilestoneDraft("Architecture, UI Mockups & Prototype",
                "Project scaffold, schema initialization, and interactive UI prototype.", m1Amount, d1));
        list.add(new MilestoneDraft("Core Module Implementation & Integration",
                "Full business logic, database transactions, and reactive UI view wiring.", m2Amount, d2));
        list.add(new MilestoneDraft("Automated Testing, Verification & Final Delivery",
                "JUnit test verification, styling polish, and production handover.", m3Amount, d3));

        return list;
    }
}

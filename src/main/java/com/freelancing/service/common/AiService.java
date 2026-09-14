package com.freelancing.service.common;

import com.freelancing.model.common.User;
import com.freelancing.model.company.Project;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.model.freelancer.MatchingResult;
import com.freelancing.service.freelancer.AIProposalAssistant;
import com.freelancing.service.freelancer.MatchingService;

public class AiService {

    private final MatchingService matchingService = new MatchingService();
    private final AIProposalAssistant proposalAssistant = new AIProposalAssistant();

    public MatchingService getMatchingService() {
        return matchingService;
    }

    public AIProposalAssistant getProposalAssistant() {
        return proposalAssistant;
    }

    /**
     * AI Skill & Talent Recommendation Engine
     * Calculates a percentage match score between a freelancer's skills/experience and a project
     * using the 6-factor deterministic algorithm (50% Skills, 15% Experience, 10% Rating, 10% Budget, 10% Availability, 5% Completeness).
     */
    public int calculateMatchScore(FreelancerProfile freelancer, Project project) {
        return matchingService.calculateMatchScore(freelancer, project);
    }

    /**
     * Returns the complete 6-factor breakdown and explanation.
     */
    public MatchingResult calculateDetailedMatch(FreelancerProfile freelancer, Project project) {
        return matchingService.calculateDetailedMatch(freelancer, project);
    }

    /**
     * AI Proposal Generator - Auto-generates a winning proposal cover letter for freelancers
     * tailored with matched skills, delivery timeline, and milestone sprint breakdown.
     */
    public String generateProposalCoverLetter(Project project, FreelancerProfile freelancer) {
        MatchingResult match = matchingService.calculateDetailedMatch(freelancer, project);
        return proposalAssistant.generateProposalCoverLetter(project, freelancer, match);
    }

    /**
     * AI Fraud & Fake Profile Risk Scanner for Admin Panel
     */
    public int detectFraudRisk(User user, FreelancerProfile fp) {
        if (user == null) return 0;
        int riskScore = 5; // Default low risk

        // Check suspicious patterns
        if (user.getUsername().length() < 3) riskScore += 30;
        if (user.getEmail().endsWith("@temp.com") || user.getEmail().endsWith("@disposable.com")) riskScore += 45;
        if (fp != null) {
            if (fp.getBio() == null || fp.getBio().trim().length() < 10) riskScore += 20;
            if (fp.getSkills() == null || fp.getSkills().isEmpty()) riskScore += 15;
        }

        return Math.min(95, riskScore);
    }

    /**
     * AI Project Description Generator for Clients
     */
    public String generateProjectDescription(String title, String category, String rawPrompt) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 PROJECT OVERVIEW:\n");
        sb.append(rawPrompt != null && !rawPrompt.trim().isEmpty() ? rawPrompt.trim() : "We are seeking a talented specialist to deliver a high-quality " + title + " solution.").append("\n\n");

        sb.append("🎯 KEY DELIVERABLES & REQUIREMENTS:\n");
        sb.append("• High-performance, scalable code implementation complying with modern industry standards.\n");
        sb.append("• Comprehensive unit testing and clean documentation.\n");
        sb.append("• Milestone-based delivery with regular progress updates via desktop chat.\n");
        sb.append("• Final deployment handover and 30-day post-launch support.\n\n");

        sb.append("🛠 PREFERRED SKILLSET:\n");
        if (category.toLowerCase().contains("mobile") || category.toLowerCase().contains("app")) {
            sb.append("• Core Java, Flutter / Android SDK, REST API integration, Firebase / SQLite.\n");
        } else if (category.toLowerCase().contains("ui") || category.toLowerCase().contains("design")) {
            sb.append("• Figma, Wireframing, UX Research, Design System Components, CSS / FX Styling.\n");
        } else if (category.toLowerCase().contains("ai") || category.toLowerCase().contains("data")) {
            sb.append("• Python, Java, NLP Models, Machine Learning Algorithms, Data Analytics.\n");
        } else {
            sb.append("• Java 17, Object-Oriented Architecture, Database Management, Git Version Control.\n");
        }

        sb.append("\n🤝 COLLABORATION:\n");
        sb.append("Weekly milestone check-ins, encrypted chat communication, and virtual meeting discussions.");

        return sb.toString();
    }

    /**
     * AI Career Coach (ChatBot) for Freelancers
     */
    public String getCareerCoachResponse(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "Hello! I am your AI Career Coach. Ask me how to improve your profile, write winning proposals, or upgrade your technical skills!";
        }

        String lower = userInput.toLowerCase();
        if (lower.contains("proposal") || lower.contains("bid") || lower.contains("apply")) {
            return "🤖 AI Advice for Winning Proposals:\n" +
                   "1. Address the client's specific problem in the first 2 sentences.\n" +
                   "2. Highlight relevant past Java / JavaFX projects or portfolio links.\n" +
                   "3. Break down your delivery timeline into clear milestones.\n" +
                   "4. Use the new 'AI Auto-Generate Winning Proposal' feature when submitting bids!";
        } else if (lower.contains("skill") || lower.contains("learn") || lower.contains("java") || lower.contains("course")) {
            return "💡 AI Skill Growth Recommendations:\n" +
                   "• Top trending skills on SkillBridge: Java 17, JavaFX Desktop UI, Spring Boot, SQLite/PostgreSQL, and AI/ML Integration.\n" +
                   "• Recommended Action: Add relevant certifications to your profile to boost your AI Match score by +15%!";
        } else if (lower.contains("portfolio") || lower.contains("resume") || lower.contains("github")) {
            return "📌 AI Portfolio Optimization Tips:\n" +
                   "• Ensure your GitHub, LinkedIn, and GitLab links are updated in 'Profile Management'.\n" +
                   "• Upload project sample screenshots or demo videos to build immediate client trust.\n" +
                   "• Freelancers with verified portfolio links receive 3.5x more project invitations!";
        } else if (lower.contains("rate") || lower.contains("earning") || lower.contains("price") || lower.contains("budget")) {
            return "💰 AI Pricing Intelligence:\n" +
                   "Based on active market data for Java / Desktop application developers, competitive hourly rates range from $35/hr to $85/hr depending on senior experience.";
        } else {
            return "🤖 AI Career Coach: That's a great goal! To stand out on SkillBridge, focus on maintaining a 4.8+ rating, delivering milestones on time, and leveraging AI recommendations to apply for high-match projects.";
        }
    }
}

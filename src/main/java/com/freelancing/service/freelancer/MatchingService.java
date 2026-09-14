package com.freelancing.service.freelancer;

import com.freelancing.model.company.Project;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.model.freelancer.MatchingResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Deterministic Core Java AI Matching Engine for SkillBridge.
 * Implements the exact 6-factor weighted algorithm:
 * - Skill Match          : 50%
 * - Experience Match     : 15%
 * - Rating Score         : 10%
 * - Budget Compatibility : 10%
 * - Availability Match   : 10%
 * - Profile Completeness :  5%
 * Total                  : 100%
 */
public class MatchingService {

    public MatchingResult calculateDetailedMatch(FreelancerProfile freelancer, Project project) {
        if (freelancer == null || project == null) {
            return new MatchingResult(50, 25.0, 7.5, 5.0, 5.0, 5.0, 2.5,
                    new ArrayList<>(), new ArrayList<>(), "Neutral baseline match (insufficient profile/project data).");
        }

        // 1. Skill Match (50%)
        List<String> reqSkills = project.getRequiredSkills() != null ? project.getRequiredSkills() : new ArrayList<>();
        List<String> freeSkills = freelancer.getSkills() != null ? freelancer.getSkills() : new ArrayList<>();

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        Set<String> freeSkillsLower = new HashSet<>();
        for (String s : freeSkills) {
            if (s != null) freeSkillsLower.add(s.toLowerCase().trim());
        }

        for (String req : reqSkills) {
            if (req == null || req.isBlank()) continue;
            boolean found = isSkillMatched(req, freeSkillsLower);

            if (found) {
                matchedSkills.add(req.trim());
            } else {
                missingSkills.add(req.trim());
            }
        }

        double skillScore;
        if (reqSkills.isEmpty()) {
            skillScore = 50.0;
        } else {
            skillScore = ((double) matchedSkills.size() / reqSkills.size()) * 50.0;
        }

        // 2. Experience Match (15%)
        String expLevel = project.getExperienceLevel() != null ? project.getExperienceLevel().toUpperCase() : "INTERMEDIATE";
        int reqYearsMin;
        if ("ENTRY".equals(expLevel)) {
            reqYearsMin = 1;
        } else if ("EXPERT".equals(expLevel)) {
            reqYearsMin = 6;
        } else {
            reqYearsMin = 3; // INTERMEDIATE
        }

        int actualYears = freelancer.getExperienceYears();
        double experienceScore;
        if (actualYears >= reqYearsMin) {
            experienceScore = 15.0;
        } else if (actualYears == reqYearsMin - 1) {
            experienceScore = 10.0;
        } else if (actualYears == reqYearsMin - 2) {
            experienceScore = 6.0;
        } else {
            experienceScore = 3.0;
        }

        // 3. Rating Score (10%)
        double ratingScore;
        if (freelancer.getTotalReviews() == 0) {
            ratingScore = 8.0; // Neutral baseline (equivalent to 4.0 / 5.0)
        } else {
            double r = Math.min(5.0, Math.max(0.0, freelancer.getRating()));
            ratingScore = (r / 5.0) * 10.0;
        }

        // 4. Budget Compatibility (10%)
        double budgetScore = 8.0; // Baseline
        double fRate = freelancer.getHourlyRate();
        if ("HOURLY".equalsIgnoreCase(project.getBudgetType())) {
            double bMin = project.getBudgetMin();
            double bMax = project.getBudgetMax() > 0 ? project.getBudgetMax() : project.getBudget();
            if (fRate > 0 && bMax > 0) {
                if (fRate >= bMin * 0.85 && fRate <= bMax * 1.15) {
                    budgetScore = 10.0;
                } else if (fRate <= bMax * 1.3) {
                    budgetScore = 7.0;
                } else {
                    budgetScore = 4.0;
                }
            } else {
                budgetScore = 8.0;
            }
        } else {
            // FIXED Price
            double fixedBudget = project.getBudgetMax() > 0 ? project.getBudgetMax() : project.getBudget();
            if (fixedBudget > 0 && fRate > 0) {
                double impliedHours = fixedBudget / fRate;
                if (impliedHours >= 20) {
                    budgetScore = 10.0;
                } else if (impliedHours >= 10) {
                    budgetScore = 8.0;
                } else {
                    budgetScore = 5.0;
                }
            } else {
                budgetScore = 8.0;
            }
        }

        // 5. Availability Match (10%)
        String avail = freelancer.getAvailability() != null ? freelancer.getAvailability().toUpperCase() : "AVAILABLE";
        double availabilityScore;
        if (avail.contains("AVAILABLE") || avail.contains("FULL_TIME") || avail.contains("OPEN")) {
            availabilityScore = 10.0;
        } else if (avail.contains("PART_TIME")) {
            availabilityScore = 7.5;
        } else if (avail.contains("BUSY")) {
            availabilityScore = 4.0;
        } else {
            availabilityScore = 0.0; // UNAVAILABLE
        }

        // 6. Profile Completeness (5%)
        double completenessScore = 0.0;
        if (freelancer.getBio() != null && freelancer.getBio().trim().length() >= 20) completenessScore += 1.0;
        if (freelancer.getAvatarPath() != null && !freelancer.getAvatarPath().isBlank()) completenessScore += 1.0;
        if (freelancer.getGithubUrl() != null && !freelancer.getGithubUrl().isBlank()) completenessScore += 1.0;
        if (freelancer.getLinkedinUrl() != null && !freelancer.getLinkedinUrl().isBlank()) completenessScore += 1.0;
        if (freelancer.getCompletedProjects() > 0) completenessScore += 1.0;

        // Overall Score Calculation (Clamped 0 - 100)
        double totalWeighted = skillScore + experienceScore + ratingScore + budgetScore + availabilityScore + completenessScore;
        int overallScore = (int) Math.round(Math.min(100.0, Math.max(0.0, totalWeighted)));

        // Human-Readable Detailed Explanation
        StringBuilder sb = new StringBuilder();
        sb.append(overallScore).append("% Overall Match: ");
        if (reqSkills.isEmpty()) {
            sb.append("No specific skills required (+").append(String.format("%.1f", skillScore)).append("/50 pts). ");
        } else {
            sb.append("Matched ").append(matchedSkills.size()).append("/").append(reqSkills.size())
              .append(" required skills (+").append(String.format("%.1f", skillScore)).append("/50 pts). ");
        }
        sb.append("Experience (").append(actualYears).append(" yrs vs ").append(expLevel).append(" tier: +")
          .append(String.format("%.1f", experienceScore)).append("/15 pts). ");
        sb.append("Client Rating (").append(String.format("%.2f", freelancer.getRating())).append("/5.0: +")
          .append(String.format("%.1f", ratingScore)).append("/10 pts). ");
        sb.append("Availability is ").append(avail).append(" (+").append(String.format("%.1f", availabilityScore)).append("/10 pts). ");
        sb.append("Profile Completeness (+").append(String.format("%.1f", completenessScore)).append("/5 pts).");

        return new MatchingResult(overallScore, skillScore, experienceScore, ratingScore, budgetScore,
                availabilityScore, completenessScore, matchedSkills, missingSkills, sb.toString());
    }

    private boolean isSkillMatched(String req, Set<String> freeSkillsLower) {
        String reqNorm = req.toLowerCase().trim();
        if (freeSkillsLower.contains(reqNorm)) return true;

        for (String free : freeSkillsLower) {
            if (free.equals(reqNorm)) return true;
            String[] parts = free.split("[/,;()\\[\\]]+");
            for (String part : parts) {
                if (part.trim().equals(reqNorm)) {
                    return true;
                }
            }
            String[] reqParts = reqNorm.split("[/,;()\\[\\]]+");
            for (String rPart : reqParts) {
                if (rPart.trim().equals(free)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int calculateMatchScore(FreelancerProfile freelancer, Project project) {
        return calculateDetailedMatch(freelancer, project).getOverallScore();
    }
}

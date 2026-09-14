package com.freelancing.model.freelancer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the results of the 6-Factor Deterministic Core Java AI Matching Engine.
 * Weights:
 * - Skill Match: 50%
 * - Experience Match: 15%
 * - Rating Score: 10%
 * - Budget Compatibility: 10%
 * - Availability Match: 10%
 * - Profile Completeness: 5%
 */
public class MatchingResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private int overallScore; // 0 - 100%
    private double skillScore; // 0 - 50.0
    private double experienceScore; // 0 - 15.0
    private double ratingScore; // 0 - 10.0
    private double budgetScore; // 0 - 10.0
    private double availabilityScore; // 0 - 10.0
    private double completenessScore; // 0 - 5.0

    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private String explanation;
    private String compatibilityLevel; // EXCELLENT, HIGH, MODERATE, LOW

    public MatchingResult() {}

    public MatchingResult(int overallScore, double skillScore, double experienceScore,
                          double ratingScore, double budgetScore, double availabilityScore,
                          double completenessScore, List<String> matchedSkills,
                          List<String> missingSkills, String explanation) {
        this.overallScore = Math.min(100, Math.max(0, overallScore));
        this.skillScore = skillScore;
        this.experienceScore = experienceScore;
        this.ratingScore = ratingScore;
        this.budgetScore = budgetScore;
        this.availabilityScore = availabilityScore;
        this.completenessScore = completenessScore;
        this.matchedSkills = matchedSkills != null ? matchedSkills : new ArrayList<>();
        this.missingSkills = missingSkills != null ? missingSkills : new ArrayList<>();
        this.explanation = explanation;

        if (this.overallScore >= 85) {
            this.compatibilityLevel = "EXCELLENT";
        } else if (this.overallScore >= 70) {
            this.compatibilityLevel = "HIGH";
        } else if (this.overallScore >= 50) {
            this.compatibilityLevel = "MODERATE";
        } else {
            this.compatibilityLevel = "LOW";
        }
    }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public double getSkillScore() { return skillScore; }
    public void setSkillScore(double skillScore) { this.skillScore = skillScore; }

    public double getExperienceScore() { return experienceScore; }
    public void setExperienceScore(double experienceScore) { this.experienceScore = experienceScore; }

    public double getRatingScore() { return ratingScore; }
    public void setRatingScore(double ratingScore) { this.ratingScore = ratingScore; }

    public double getBudgetScore() { return budgetScore; }
    public void setBudgetScore(double budgetScore) { this.budgetScore = budgetScore; }

    public double getAvailabilityScore() { return availabilityScore; }
    public void setAvailabilityScore(double availabilityScore) { this.availabilityScore = availabilityScore; }

    public double getCompletenessScore() { return completenessScore; }
    public void setCompletenessScore(double completenessScore) { this.completenessScore = completenessScore; }

    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getCompatibilityLevel() { return compatibilityLevel; }
    public void setCompatibilityLevel(String compatibilityLevel) { this.compatibilityLevel = compatibilityLevel; }

    public String getScoreBadgeText() {
        return overallScore + "% (" + compatibilityLevel + " MATCH)";
    }
}

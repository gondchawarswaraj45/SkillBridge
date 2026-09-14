package com.freelancing.model.freelancer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FreelancerProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String title;
    private String bio;
    private double hourlyRate;
    private int experienceYears;
    private double rating = 5.0;
    private int totalReviews;
    private int completedProjects;
    private String avatarPath;
    private String resumePath;
    private String githubUrl;
    private String linkedinUrl;
    private String gitlabUrl;
    private String portfolioUrl;
    private String availability = "AVAILABLE";
    private String createdAt;

    // Associated skills list
    private List<String> skills = new ArrayList<>();

    // Additional metadata
    private String experienceLevel;
    private String qualification;
    private String certifications;

    public FreelancerProfile() {}

    public FreelancerProfile(String id, String userId, String title, String bio, double hourlyRate,
                             int experienceYears, double rating, int totalReviews, int completedProjects,
                             String avatarPath, String resumePath, String githubUrl, String linkedinUrl,
                             String availability) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.bio = bio;
        this.hourlyRate = hourlyRate;
        this.experienceYears = experienceYears;
        this.rating = rating;
        this.totalReviews = totalReviews;
        this.completedProjects = completedProjects;
        this.avatarPath = avatarPath;
        this.resumePath = resumePath;
        this.githubUrl = githubUrl;
        this.linkedinUrl = linkedinUrl;
        this.availability = availability;
    }

    public FreelancerProfile(String userId, String title, String bio, List<String> skills,
                             String experienceLevel, String qualification, String certifications,
                             String resumeFileName, String linkedinUrl, String githubUrl,
                             String gitlabUrl, String portfolioUrl, double rating, int completedProjects) {
        this("fp_" + (userId != null ? userId.replace("usr_", "") : "free"), userId, title, bio, 50.0, 5, rating, 10, completedProjects,
             null, resumeFileName, githubUrl, linkedinUrl, "AVAILABLE");
        this.skills = skills != null ? skills : new ArrayList<>();
        this.experienceLevel = experienceLevel;
        this.qualification = qualification;
        this.certifications = certifications;
        this.gitlabUrl = gitlabUrl;
        this.portfolioUrl = portfolioUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

    public int getCompletedProjects() { return completedProjects; }
    public void setCompletedProjects(int completedProjects) { this.completedProjects = completedProjects; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }

    public String getResumeFileName() { return resumePath; }
    public void setResumeFileName(String resumeFileName) { this.resumePath = resumeFileName; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getGitlabUrl() { return gitlabUrl; }
    public void setGitlabUrl(String gitlabUrl) { this.gitlabUrl = gitlabUrl; }

    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills != null ? skills : new ArrayList<>(); }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
}

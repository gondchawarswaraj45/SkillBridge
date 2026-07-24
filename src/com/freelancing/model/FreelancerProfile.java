package com.freelancing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FreelancerProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String title;
    private String bio;
    private List<String> skills = new ArrayList<>();
    private String experienceLevel;
    private String qualification;
    private String certifications;
    private String resumeFileName;
    private String linkedinUrl;
    private String githubUrl;
    private String gitlabUrl;
    private String portfolioUrl;
    private double rating;
    private int completedProjects;

    public FreelancerProfile() {}

    public FreelancerProfile(String userId, String title, String bio, List<String> skills,
                             String experienceLevel, String qualification, String certifications,
                             String resumeFileName, String linkedinUrl, String githubUrl,
                             String gitlabUrl, String portfolioUrl, double rating, int completedProjects) {
        this.userId = userId;
        this.title = title;
        this.bio = bio;
        this.skills = skills != null ? skills : new ArrayList<>();
        this.experienceLevel = experienceLevel;
        this.qualification = qualification;
        this.certifications = certifications;
        this.resumeFileName = resumeFileName;
        this.linkedinUrl = linkedinUrl;
        this.githubUrl = githubUrl;
        this.gitlabUrl = gitlabUrl;
        this.portfolioUrl = portfolioUrl;
        this.rating = rating;
        this.completedProjects = completedProjects;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }

    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getGitlabUrl() { return gitlabUrl; }
    public void setGitlabUrl(String gitlabUrl) { this.gitlabUrl = gitlabUrl; }

    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getCompletedProjects() { return completedProjects; }
    public void setCompletedProjects(int completedProjects) { this.completedProjects = completedProjects; }
}

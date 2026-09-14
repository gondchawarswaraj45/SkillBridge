package com.freelancing.model.company;

import java.io.Serializable;

public class ClientProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String companyName;
    private String industry;
    private String companyWebsite;
    private String about;
    private String avatarPath;
    private double totalSpent;
    private int postedProjects;
    private double rating = 5.0;
    private String createdAt;

    public ClientProfile() {}

    public ClientProfile(String id, String userId, String companyName, String industry, String companyWebsite,
                         String about, String avatarPath, double totalSpent, int postedProjects) {
        this.id = id;
        this.userId = userId;
        this.companyName = companyName;
        this.industry = industry;
        this.companyWebsite = companyWebsite;
        this.about = about;
        this.avatarPath = avatarPath;
        this.totalSpent = totalSpent;
        this.postedProjects = postedProjects;
    }

    public ClientProfile(String userId, String companyName, String industry, String description, String website, int totalProjectsPosted, double totalSpent, double rating) {
        this("cp_" + (userId != null ? userId.replace("usr_", "") : "client"), userId, companyName, industry, website, description, null, totalSpent, totalProjectsPosted);
        this.rating = rating;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getCompanyWebsite() { return companyWebsite; }
    public void setCompanyWebsite(String companyWebsite) { this.companyWebsite = companyWebsite; }

    public String getWebsite() { return companyWebsite; }
    public void setWebsite(String website) { this.companyWebsite = website; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public String getDescription() { return about; }
    public void setDescription(String description) { this.about = description; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }

    public int getPostedProjects() { return postedProjects; }
    public void setPostedProjects(int postedProjects) { this.postedProjects = postedProjects; }

    public int getTotalProjectsPosted() { return postedProjects; }
    public void setTotalProjectsPosted(int totalProjectsPosted) { this.postedProjects = totalProjectsPosted; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

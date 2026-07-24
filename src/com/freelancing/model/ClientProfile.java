package com.freelancing.model;

import java.io.Serializable;

public class ClientProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String companyName;
    private String industry;
    private String description;
    private String website;
    private int totalProjectsPosted;
    private double totalSpent;
    private double rating;

    public ClientProfile() {}

    public ClientProfile(String userId, String companyName, String industry, String description, String website, int totalProjectsPosted, double totalSpent, double rating) {
        this.userId = userId;
        this.companyName = companyName;
        this.industry = industry;
        this.description = description;
        this.website = website;
        this.totalProjectsPosted = totalProjectsPosted;
        this.totalSpent = totalSpent;
        this.rating = rating;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public int getTotalProjectsPosted() { return totalProjectsPosted; }
    public void setTotalProjectsPosted(int totalProjectsPosted) { this.totalProjectsPosted = totalProjectsPosted; }

    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}

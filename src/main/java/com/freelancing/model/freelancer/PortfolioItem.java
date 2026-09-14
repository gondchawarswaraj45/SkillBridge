package com.freelancing.model.freelancer;

import java.io.Serializable;

public class PortfolioItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String freelancerId;
    private String title;
    private String description;
    private String projectUrl;
    private String imagePath;
    private String createdAt;

    public PortfolioItem() {}

    public PortfolioItem(String id, String freelancerId, String title, String description, String projectUrl, String imagePath) {
        this.id = id;
        this.freelancerId = freelancerId;
        this.title = title;
        this.description = description;
        this.projectUrl = projectUrl;
        this.imagePath = imagePath;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFreelancerId() { return freelancerId; }
    public void setFreelancerId(String freelancerId) { this.freelancerId = freelancerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProjectUrl() { return projectUrl; }
    public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

package com.freelancing.model.freelancer;

import java.io.Serializable;

public class Certification implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String freelancerId;
    private String name;
    private String issuer;
    private String issueDate;
    private String credentialUrl;
    private String certificatePath;
    private String createdAt;

    public Certification() {}

    public Certification(String id, String freelancerId, String name, String issuer, String issueDate,
                         String credentialUrl, String certificatePath) {
        this.id = id;
        this.freelancerId = freelancerId;
        this.name = name;
        this.issuer = issuer;
        this.issueDate = issueDate;
        this.credentialUrl = credentialUrl;
        this.certificatePath = certificatePath;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFreelancerId() { return freelancerId; }
    public void setFreelancerId(String freelancerId) { this.freelancerId = freelancerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public String getCredentialUrl() { return credentialUrl; }
    public void setCredentialUrl(String credentialUrl) { this.credentialUrl = credentialUrl; }

    public String getCertificatePath() { return certificatePath; }
    public void setCertificatePath(String certificatePath) { this.certificatePath = certificatePath; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

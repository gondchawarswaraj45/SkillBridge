package com.freelancing.model.admin;

import java.io.Serializable;

/**
 * Report model mapped to SQLite reports table.
 */
public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String reportType;
    private String generatedBy;
    private String generatedByName;
    private String content;
    private String filePath;
    private String createdAt;

    public Report() {}

    public Report(String id, String title, String reportType, String generatedBy, String content, String filePath, String createdAt) {
        this.id = id;
        this.title = title;
        this.reportType = reportType;
        this.generatedBy = generatedBy;
        this.content = content;
        this.filePath = filePath;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }

    public String getGeneratedByName() { return generatedByName; }
    public void setGeneratedByName(String generatedByName) { this.generatedByName = generatedByName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

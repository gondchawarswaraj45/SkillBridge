package com.freelancing.model.common;

import java.io.Serializable;

public class Skill implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String category;
    private String createdAt;

    public Skill() {}

    public Skill(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public Skill(String id, String name, String category, String createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String toString() {
        return name;
    }
}

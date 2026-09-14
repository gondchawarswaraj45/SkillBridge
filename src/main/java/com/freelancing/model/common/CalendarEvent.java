package com.freelancing.model.common;

import java.io.Serializable;

/**
 * Represents a calendar entry for project milestones, meetings, payments, deadlines, and reminders.
 * Used by both Client and Freelancer calendars.
 */
public class CalendarEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum EventType {
        MEETING, PAYMENT, DEADLINE, REMINDER, MILESTONE, BLOCKED
    }

    private String id;
    private String userId;
    private String projectId;
    private String contractId;
    private String title;
    private String description;
    private String date;
    private String time;
    private String endTime;
    private EventType type;
    private String color;
    private boolean allDay;
    private String createdAt;

    public CalendarEvent() {
        this.allDay = false;
    }

    public CalendarEvent(String id, String userId, String title, String date, String time, EventType type, String color) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.type = type;
        this.color = color;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public boolean isAllDay() { return allDay; }
    public void setAllDay(boolean allDay) { this.allDay = allDay; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Compatibility aliases
    public String getEventId() { return projectId; }
    public void setEventId(String eventId) { this.projectId = eventId; }
    public String getBookingId() { return contractId; }
    public void setBookingId(String bookingId) { this.contractId = bookingId; }
}

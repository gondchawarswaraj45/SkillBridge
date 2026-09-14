package com.freelancing.service.common;

import com.freelancing.dao.common.CalendarDAO;
import com.freelancing.model.common.CalendarEvent;

import com.freelancing.db.DatabaseManager;
import com.freelancing.util.LoggingUtil;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing calendar events and virtual meeting schedules backed by SQLite.
 */
public class CalendarService {
    private static final String TAG = "CalendarService";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CalendarDAO calendarDAO;
    private final NotificationService notificationService;
    private final DatabaseManager db = DatabaseManager.getInstance();

    public CalendarService() {
        this.calendarDAO = new CalendarDAO();
        this.notificationService = new NotificationService();
    }

    public CalendarService(CalendarDAO calendarDAO, NotificationService notificationService) {
        this.calendarDAO = calendarDAO;
        this.notificationService = notificationService;
    }

    public CalendarEvent createCalendarEvent(String userId, String title, String date, String time,
                                             String endTime, CalendarEvent.EventType type, String color,
                                             String projectId, String contractId, String description) {
        if (userId == null || title == null || date == null) {
            throw new IllegalArgumentException("User ID, title, and date are required.");
        }

        String calId = "cal_" + UUID.randomUUID().toString().substring(0, 8);
        String now = LocalDateTime.now().format(FORMATTER);

        CalendarEvent calEvent = new CalendarEvent(calId, userId, title, date, time, type, color);
        calEvent.setEndTime(endTime);
        calEvent.setProjectId(projectId);
        calEvent.setContractId(contractId);
        calEvent.setDescription(description);
        calEvent.setCreatedAt(now);

        // 1. Persist to SQLite
        calendarDAO.create(calEvent);

        // 2. Sync to in-memory map for backwards compatibility
        try {
            db.getCalendarEvents().put(calId, calEvent);
        } catch (Exception ignored) {}

        LoggingUtil.info(TAG, "Calendar event created: " + title + " for user " + userId);
        return calEvent;
    }

    public List<CalendarEvent> getCalendarEventsForUser(String userId) {
        if (userId == null) return List.of();
        return calendarDAO.findByUserId(userId);
    }

    public List<CalendarEvent> getCalendarEventsForDate(String userId, String date) {
        if (userId == null || date == null) return List.of();
        return calendarDAO.findByUserIdAndDate(userId, date);
    }

    public List<CalendarEvent> getCalendarEventsForMonth(String userId, int year, int month) {
        if (userId == null) return List.of();
        String prefix = String.format("%04d-%02d", year, month);
        return calendarDAO.findByUserIdAndMonth(userId, prefix);
    }

    /** Get dates that have events for a given month (for calendar dot indicators) */
    public Set<String> getActiveDatesForMonth(String userId, int year, int month) {
        return getCalendarEventsForMonth(userId, year, month).stream()
                .map(CalendarEvent::getDate)
                .collect(Collectors.toSet());
    }

    public boolean deleteCalendarEvent(String calEventId) {
        if (calEventId == null) return false;
        try {
            db.getCalendarEvents().remove(calEventId);
        } catch (Exception ignored) {}
        return calendarDAO.delete(calEventId);
    }

    public boolean updateCalendarEvent(CalendarEvent calEvent) {
        if (calEvent == null) return false;
        try {
            db.getCalendarEvents().put(calEvent.getId(), calEvent);
        } catch (Exception ignored) {}
        return calendarDAO.update(calEvent);
    }

    /**
     * Schedules a virtual meeting between two users, adding to both calendars and notifying the attendee.
     */
    public CalendarEvent createMeetingEvent(String hostUserId, String attendeeUserId, String title,
                                           String date, String time, String meetingLink, String projectId) {
        String desc = "Virtual Meeting: " + title + "\nLink: " + (meetingLink != null ? meetingLink : "https://meet.google.com/sb-demo");

        // Create for host
        CalendarEvent hostEvent = createCalendarEvent(hostUserId, "📹 " + title, date, time, null,
                CalendarEvent.EventType.MEETING, "#3B82F6", projectId, null, desc);

        // Create for attendee if provided
        if (attendeeUserId != null && !attendeeUserId.equals(hostUserId)) {
            createCalendarEvent(attendeeUserId, "📹 " + title, date, time, null,
                    CalendarEvent.EventType.MEETING, "#3B82F6", projectId, null, desc);

            notificationService.sendNotification(
                    attendeeUserId,
                    "📹 Virtual Meeting Scheduled: " + title,
                    "Date: " + date + " at " + (time != null ? time : "TBD") + "\nLink: " + (meetingLink != null ? meetingLink : "https://meet.google.com/sb-demo"),
                    "MEETING",
                    hostEvent.getId()
            );
        }

        return hostEvent;
    }

    /**
     * Auto-creates calendar deadline events for both client and freelancer from a project contract milestone.
     */
    public void createFromContractMilestone(String clientId, String freelancerId, String contractId,
                                           String milestoneTitle, String deadline) {
        if (clientId != null) {
            createCalendarEvent(clientId, "Milestone Deadline: " + milestoneTitle, deadline, "23:59", null,
                    CalendarEvent.EventType.DEADLINE, "#EF4444", null, contractId,
                    "Milestone deadline for " + milestoneTitle);
        }

        if (freelancerId != null) {
            createCalendarEvent(freelancerId, "Milestone Due: " + milestoneTitle, deadline, "23:59", null,
                    CalendarEvent.EventType.MILESTONE, "#F59E0B", null, contractId,
                    "Milestone deliverable due for " + milestoneTitle);
        }
    }

    /**
     * Safely opens a virtual meeting link in the default OS web browser.
     */
    public static boolean openMeetingLink(String url) {
        if (url == null || url.trim().isEmpty()) return false;
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url.trim()));
                return true;
            }
        } catch (Exception e) {
            LoggingUtil.error(TAG, "Failed to launch browser for meeting URL: " + url + " - " + e.getMessage(), e);
        }
        return false;
    }
}

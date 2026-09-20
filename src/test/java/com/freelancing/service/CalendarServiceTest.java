package com.freelancing.service;

import com.freelancing.model.common.CalendarEvent;
import com.freelancing.model.common.Notification;
import com.freelancing.model.common.User;
import com.freelancing.service.common.CalendarService;
import com.freelancing.service.common.NotificationService;

import com.freelancing.db.DatabaseConnection;
import com.freelancing.db.DatabaseInitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CalendarServiceTest {

    private static CalendarService calendarService;
    private static NotificationService notificationService;

    private User hostUser;
    private User attendeeUser;
    private String projectId;
    private String contractId;

    public static void initDatabase() {
        DatabaseInitializer.initialize();
        calendarService = new CalendarService();
        notificationService = new NotificationService();
    }

    public void setUp() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        hostUser = new User();
        hostUser.setId("usr_h_" + suffix);
        hostUser.setUsername("host_" + suffix);
        hostUser.setEmail("host_" + suffix + "@test.com");
        hostUser.setPassword("hashedpassword");
        hostUser.setRole(User.Role.CLIENT);

        attendeeUser = new User();
        attendeeUser.setId("usr_a_" + suffix);
        attendeeUser.setUsername("attendee_" + suffix);
        attendeeUser.setEmail("attendee_" + suffix + "@test.com");
        attendeeUser.setPassword("hashedpassword");
        attendeeUser.setRole(User.Role.FREELANCER);

        String clientProfileId = "cp_" + suffix;
        String freelancerProfileId = "fp_" + suffix;
        projectId = "proj_" + suffix;
        contractId = "ctr_" + suffix;

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (id, username, email, password_hash, role) VALUES (?, ?, ?, ?, ?);")) {
                ps.setString(1, hostUser.getId());
                ps.setString(2, hostUser.getUsername());
                ps.setString(3, hostUser.getEmail());
                ps.setString(4, hostUser.getPassword());
                ps.setString(5, hostUser.getRole().name());
                ps.executeUpdate();

                ps.setString(1, attendeeUser.getId());
                ps.setString(2, attendeeUser.getUsername());
                ps.setString(3, attendeeUser.getEmail());
                ps.setString(4, attendeeUser.getPassword());
                ps.setString(5, attendeeUser.getRole().name());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO client_profiles (id, user_id, company_name) VALUES (?, ?, ?);")) {
                ps.setString(1, clientProfileId);
                ps.setString(2, hostUser.getId());
                ps.setString(3, "Host Corp");
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO freelancer_profiles (id, user_id, title) VALUES (?, ?, ?);")) {
                ps.setString(1, freelancerProfileId);
                ps.setString(2, attendeeUser.getId());
                ps.setString(3, "Attendee Dev");
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO projects (id, client_id, title, description, category) VALUES (?, ?, ?, ?, ?);")) {
                ps.setString(1, projectId);
                ps.setString(2, clientProfileId);
                ps.setString(3, "Calendar Project");
                ps.setString(4, "Desc");
                ps.setString(5, "Design");
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO contracts (id, project_id, client_id, freelancer_id, total_amount) VALUES (?, ?, ?, ?, ?);")) {
                ps.setString(1, contractId);
                ps.setString(2, projectId);
                ps.setString(3, clientProfileId);
                ps.setString(4, freelancerProfileId);
                ps.setDouble(5, 2000.0);
                ps.executeUpdate();
            }
        }
    }

    public void testCreateAndQueryCalendarEvents() {
        CalendarEvent ev = calendarService.createCalendarEvent(
                hostUser.getId(),
                "Sprint Planning",
                "2026-10-15",
                "10:00",
                "11:00",
                CalendarEvent.EventType.MEETING,
                "#3B82F6",
                projectId,
                null,
                "Sprint planning session"
        );

        assertNotNull(ev);
        assertNotNull(ev.getId());

        List<CalendarEvent> userEvents = calendarService.getCalendarEventsForUser(hostUser.getId());
        assertEquals(1, userEvents.size());
        assertEquals("Sprint Planning", userEvents.get(0).getTitle());

        List<CalendarEvent> dateEvents = calendarService.getCalendarEventsForDate(hostUser.getId(), "2026-10-15");
        assertEquals(1, dateEvents.size());

        List<CalendarEvent> monthEvents = calendarService.getCalendarEventsForMonth(hostUser.getId(), 2026, 10);
        assertEquals(1, monthEvents.size());
    }

    public void testCreateMeetingEventBothCalendarsAndNotification() {
        CalendarEvent meeting = calendarService.createMeetingEvent(
                hostUser.getId(),
                attendeeUser.getId(),
                "Sprint Review & Demo",
                "2026-10-20",
                "14:00",
                "https://meet.google.com/sb-demo-123",
                projectId
        );

        assertNotNull(meeting);

        // Host calendar must contain meeting
        List<CalendarEvent> hostEvents = calendarService.getCalendarEventsForDate(hostUser.getId(), "2026-10-20");
        assertEquals(1, hostEvents.size());
        assertEquals(CalendarEvent.EventType.MEETING, hostEvents.get(0).getType());

        // Attendee calendar must contain meeting
        List<CalendarEvent> attendeeEvents = calendarService.getCalendarEventsForDate(attendeeUser.getId(), "2026-10-20");
        assertEquals(1, attendeeEvents.size());
        assertEquals(CalendarEvent.EventType.MEETING, attendeeEvents.get(0).getType());

        // Attendee must have received a MEETING notification
        List<Notification> attendeeNotifs = notificationService.getUserNotifications(attendeeUser.getId());
        assertFalse(attendeeNotifs.isEmpty());
        assertEquals("MEETING", attendeeNotifs.get(0).getType());
        assertTrue(attendeeNotifs.get(0).getMessage().contains("https://meet.google.com/sb-demo-123"));
    }

    public void testCreateFromContractMilestone() {
        calendarService.createFromContractMilestone(
                hostUser.getId(),
                attendeeUser.getId(),
                contractId,
                "Frontend Prototype",
                "2026-11-01"
        );

        List<CalendarEvent> clientEvents = calendarService.getCalendarEventsForDate(hostUser.getId(), "2026-11-01");
        assertEquals(1, clientEvents.size());
        assertEquals(CalendarEvent.EventType.DEADLINE, clientEvents.get(0).getType());

        List<CalendarEvent> freelancerEvents = calendarService.getCalendarEventsForDate(attendeeUser.getId(), "2026-11-01");
        assertEquals(1, freelancerEvents.size());
        assertEquals(CalendarEvent.EventType.MILESTONE, freelancerEvents.get(0).getType());
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        System.out.println("Running CalendarServiceTest...");
        int passed = 0;
        int total = 3;
        try {
            initDatabase();
        } catch (Throwable t) {
            System.err.println("Setup failed for CalendarServiceTest: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        try {
            CalendarServiceTest test = new CalendarServiceTest();
            test.setUp();
            test.testCreateAndQueryCalendarEvents();
            passed++;
            System.out.println("  [PASS] testCreateAndQueryCalendarEvents");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testCreateAndQueryCalendarEvents: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            CalendarServiceTest test = new CalendarServiceTest();
            test.setUp();
            test.testCreateMeetingEventBothCalendarsAndNotification();
            passed++;
            System.out.println("  [PASS] testCreateMeetingEventBothCalendarsAndNotification");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testCreateMeetingEventBothCalendarsAndNotification: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            CalendarServiceTest test = new CalendarServiceTest();
            test.setUp();
            test.testCreateFromContractMilestone();
            passed++;
            System.out.println("  [PASS] testCreateFromContractMilestone");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testCreateFromContractMilestone: " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("CalendarServiceTest: " + passed + "/" + total + " tests passed.\n");
        if (passed != total) {
            throw new RuntimeException("Tests failed in CalendarServiceTest");
        }
    }
}

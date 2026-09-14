package com.freelancing.dao.common;

import com.freelancing.model.common.CalendarEvent;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalendarDAO {

    public boolean create(CalendarEvent event) {
        String sql = "INSERT INTO calendar_events (id, user_id, title, description, event_date, event_time, event_type, project_id, contract_id, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getId());
            ps.setString(2, event.getUserId());
            ps.setString(3, event.getTitle());
            ps.setString(4, event.getDescription());
            ps.setString(5, event.getDate());
            ps.setString(6, event.getTime());
            ps.setString(7, event.getType() != null ? event.getType().name() : "REMINDER");
            String pId = (event.getProjectId() != null && !event.getProjectId().trim().isEmpty()) ? event.getProjectId() : null;
            String cId = (event.getContractId() != null && !event.getContractId().trim().isEmpty()) ? event.getContractId() : null;
            ps.setString(8, pId);
            ps.setString(9, cId);
            ps.setString(10, event.getCreatedAt() != null ? event.getCreatedAt() : new Timestamp(System.currentTimeMillis()).toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CalendarDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public CalendarEvent findById(String id) {
        String sql = "SELECT * FROM calendar_events WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("CalendarDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    public List<CalendarEvent> findByUserId(String userId) {
        List<CalendarEvent> list = new ArrayList<>();
        String sql = "SELECT * FROM calendar_events WHERE user_id = ? ORDER BY event_date ASC, event_time ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("CalendarDAO.findByUserId error: " + e.getMessage());
        }
        return list;
    }

    public List<CalendarEvent> findByUserIdAndDate(String userId, String date) {
        List<CalendarEvent> list = new ArrayList<>();
        String sql = "SELECT * FROM calendar_events WHERE user_id = ? AND event_date = ? ORDER BY event_time ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("CalendarDAO.findByUserIdAndDate error: " + e.getMessage());
        }
        return list;
    }

    public List<CalendarEvent> findByUserIdAndMonth(String userId, String yearMonthPrefix) {
        List<CalendarEvent> list = new ArrayList<>();
        String sql = "SELECT * FROM calendar_events WHERE user_id = ? AND event_date LIKE ? ORDER BY event_date ASC, event_time ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, yearMonthPrefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("CalendarDAO.findByUserIdAndMonth error: " + e.getMessage());
        }
        return list;
    }

    public boolean update(CalendarEvent event) {
        String sql = "UPDATE calendar_events SET title = ?, description = ?, event_date = ?, event_time = ?, event_type = ? "
                   + "WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getDate());
            ps.setString(4, event.getTime());
            ps.setString(5, event.getType() != null ? event.getType().name() : "REMINDER");
            ps.setString(6, event.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CalendarDAO.update error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String eventId) {
        String sql = "DELETE FROM calendar_events WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CalendarDAO.delete error: " + e.getMessage());
            return false;
        }
    }

    private CalendarEvent mapRow(ResultSet rs) throws SQLException {
        CalendarEvent e = new CalendarEvent();
        e.setId(rs.getString("id"));
        e.setUserId(rs.getString("user_id"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setDate(rs.getString("event_date"));
        e.setTime(rs.getString("event_time"));

        String typeStr = rs.getString("event_type");
        if (typeStr != null) {
            try {
                e.setType(CalendarEvent.EventType.valueOf(typeStr));
            } catch (IllegalArgumentException ex) {
                e.setType(CalendarEvent.EventType.REMINDER);
            }
        } else {
            e.setType(CalendarEvent.EventType.REMINDER);
        }

        e.setProjectId(rs.getString("project_id"));
        e.setContractId(rs.getString("contract_id"));
        e.setCreatedAt(rs.getString("created_at"));

        // Default color based on event type
        if (e.getType() == CalendarEvent.EventType.DEADLINE || e.getType() == CalendarEvent.EventType.MILESTONE) {
            e.setColor("#EF4444"); // Red / Coral
        } else if (e.getType() == CalendarEvent.EventType.MEETING) {
            e.setColor("#3B82F6"); // Blue
        } else if (e.getType() == CalendarEvent.EventType.PAYMENT) {
            e.setColor("#10B981"); // Green
        } else {
            e.setColor("#6366F1"); // Indigo
        }

        return e;
    }
}

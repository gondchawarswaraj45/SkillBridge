package com.freelancing.dao.admin;

import com.freelancing.model.admin.Report;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO for reports table in SQLite.
 */
public class ReportDAO {

    public Report create(Report report) {
        if (report.getId() == null || report.getId().isEmpty()) {
            report.setId("rep_" + UUID.randomUUID().toString().substring(0, 8));
        }

        String sql = "INSERT INTO reports (id, title, report_type, generated_by, content, file_path, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, datetime('now'));";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, report.getId());
            ps.setString(2, report.getTitle());
            ps.setString(3, report.getReportType());
            ps.setString(4, report.getGeneratedBy());
            ps.setString(5, report.getContent());
            ps.setString(6, report.getFilePath());
            ps.executeUpdate();
            return findById(report.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Error creating report: " + e.getMessage(), e);
        }
    }

    public Report findById(String id) {
        String sql = "SELECT r.*, u.username AS generated_by_name "
                + "FROM reports r "
                + "LEFT JOIN users u ON r.generated_by = u.id "
                + "WHERE r.id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReport(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding report: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Report> findAll() {
        List<Report> list = new ArrayList<>();
        String sql = "SELECT r.*, u.username AS generated_by_name "
                + "FROM reports r "
                + "LEFT JOIN users u ON r.generated_by = u.id "
                + "ORDER BY r.created_at DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToReport(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying reports: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM reports WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting report: " + e.getMessage(), e);
        }
    }

    private Report mapResultSetToReport(ResultSet rs) throws SQLException {
        Report r = new Report();
        r.setId(rs.getString("id"));
        r.setTitle(rs.getString("title"));
        r.setReportType(rs.getString("report_type"));
        r.setGeneratedBy(rs.getString("generated_by"));
        r.setGeneratedByName(rs.getString("generated_by_name"));
        r.setContent(rs.getString("content"));
        r.setFilePath(rs.getString("file_path"));
        r.setCreatedAt(rs.getString("created_at"));
        return r;
    }
}

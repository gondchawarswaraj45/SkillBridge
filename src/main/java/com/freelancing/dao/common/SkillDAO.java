package com.freelancing.dao.common;

import com.freelancing.model.common.Skill;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Skills catalog in SQLite.
 */
public class SkillDAO {

    public boolean create(Skill skill) {
        String sql = "INSERT OR IGNORE INTO skills (id, name, category, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, skill.getId());
            ps.setString(2, skill.getName());
            ps.setString(3, skill.getCategory());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SkillDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public Skill findById(String id) {
        String sql = "SELECT * FROM skills WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToSkill(rs);
            }
        } catch (SQLException e) {
            System.err.println("SkillDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    public Skill findByName(String name) {
        String sql = "SELECT * FROM skills WHERE LOWER(name) = LOWER(?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToSkill(rs);
            }
        } catch (SQLException e) {
            System.err.println("SkillDAO.findByName error: " + e.getMessage());
        }
        return null;
    }

    public List<Skill> findByCategory(String category) {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM skills WHERE LOWER(category) = LOWER(?) ORDER BY name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToSkill(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("SkillDAO.findByCategory error: " + e.getMessage());
        }
        return list;
    }

    public List<Skill> search(String query) {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM skills WHERE LOWER(name) LIKE ? OR LOWER(category) LIKE ? ORDER BY name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + query.toLowerCase() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToSkill(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("SkillDAO.search error: " + e.getMessage());
        }
        return list;
    }

    public List<Skill> findAll() {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM skills ORDER BY category ASC, name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToSkill(rs));
            }
        } catch (SQLException e) {
            System.err.println("SkillDAO.findAll error: " + e.getMessage());
        }
        return list;
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM skills WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SkillDAO.delete error: " + e.getMessage());
            return false;
        }
    }

    private Skill mapRowToSkill(ResultSet rs) throws SQLException {
        return new Skill(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getString("created_at")
        );
    }
}

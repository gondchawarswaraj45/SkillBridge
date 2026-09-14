package com.freelancing.dao.freelancer;

import com.freelancing.model.freelancer.PortfolioItem;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Freelancer Portfolio items in SQLite.
 */
public class PortfolioDAO {

    public boolean create(PortfolioItem item) {
        String sql = "INSERT INTO portfolio_items (id, freelancer_id, title, description, project_url, image_path, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getId());
            ps.setString(2, item.getFreelancerId());
            ps.setString(3, item.getTitle());
            ps.setString(4, item.getDescription());
            ps.setString(5, item.getProjectUrl());
            ps.setString(6, item.getImagePath());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("PortfolioDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public PortfolioItem findById(String id) {
        String sql = "SELECT * FROM portfolio_items WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToItem(rs);
            }
        } catch (SQLException e) {
            System.err.println("PortfolioDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    public List<PortfolioItem> findByFreelancerId(String freelancerId) {
        List<PortfolioItem> list = new ArrayList<>();
        String sql = "SELECT * FROM portfolio_items WHERE freelancer_id = ? ORDER BY created_at DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, freelancerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("PortfolioDAO.findByFreelancerId error: " + e.getMessage());
        }
        return list;
    }

    public boolean update(PortfolioItem item) {
        String sql = "UPDATE portfolio_items SET title = ?, description = ?, project_url = ?, image_path = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getTitle());
            ps.setString(2, item.getDescription());
            ps.setString(3, item.getProjectUrl());
            ps.setString(4, item.getImagePath());
            ps.setString(5, item.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("PortfolioDAO.update error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM portfolio_items WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("PortfolioDAO.delete error: " + e.getMessage());
            return false;
        }
    }

    private PortfolioItem mapRowToItem(ResultSet rs) throws SQLException {
        PortfolioItem item = new PortfolioItem();
        item.setId(rs.getString("id"));
        item.setFreelancerId(rs.getString("freelancer_id"));
        item.setTitle(rs.getString("title"));
        item.setDescription(rs.getString("description"));
        item.setProjectUrl(rs.getString("project_url"));
        item.setImagePath(rs.getString("image_path"));
        item.setCreatedAt(rs.getString("created_at"));
        return item;
    }
}

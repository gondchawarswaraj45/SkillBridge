package com.freelancing.dao.company;

import com.freelancing.model.company.ClientProfile;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ClientProfile persistence in SQLite.
 */
public class ClientProfileDAO {

    public boolean create(ClientProfile profile) {
        String sql = "INSERT INTO client_profiles (id, user_id, company_name, industry, company_website, about, "
                   + "avatar_path, total_spent, posted_projects, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile.getId());
            ps.setString(2, profile.getUserId());
            ps.setString(3, profile.getCompanyName());
            ps.setString(4, profile.getIndustry());
            ps.setString(5, profile.getCompanyWebsite());
            ps.setString(6, profile.getAbout());
            ps.setString(7, profile.getAvatarPath());
            ps.setDouble(8, profile.getTotalSpent());
            ps.setInt(9, profile.getPostedProjects());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ClientProfileDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Connection conn, ClientProfile profile) throws SQLException {
        String sql = "UPDATE client_profiles SET company_name = ?, industry = ?, company_website = ?, about = ?, avatar_path = ?, "
                   + "total_spent = ?, posted_projects = ? "
                   + "WHERE id = ? OR user_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile.getCompanyName());
            ps.setString(2, profile.getIndustry());
            ps.setString(3, profile.getCompanyWebsite());
            ps.setString(4, profile.getAbout());
            ps.setString(5, profile.getAvatarPath());
            ps.setDouble(6, profile.getTotalSpent());
            ps.setInt(7, profile.getPostedProjects());
            ps.setString(8, profile.getId());
            ps.setString(9, profile.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(ClientProfile profile) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return update(conn, profile);
        } catch (SQLException e) {
            System.err.println("ClientProfileDAO.update error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStats(String profileId, double totalSpent, int postedProjects) {
        String sql = "UPDATE client_profiles SET total_spent = ?, posted_projects = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, totalSpent);
            ps.setInt(2, postedProjects);
            ps.setString(3, profileId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ClientProfileDAO.updateStats error: " + e.getMessage());
            return false;
        }
    }

    public ClientProfile findById(String id) {
        String sql = "SELECT * FROM client_profiles WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToProfile(rs);
            }
        } catch (SQLException e) {
            System.err.println("ClientProfileDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    public ClientProfile findByUserId(Connection conn, String userId) throws SQLException {
        String sql = "SELECT * FROM client_profiles WHERE user_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToProfile(rs);
            }
        }
        return null;
    }

    public ClientProfile findByUserId(String userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findByUserId(conn, userId);
        } catch (SQLException e) {
            System.err.println("ClientProfileDAO.findByUserId error: " + e.getMessage());
            return null;
        }
    }

    public List<ClientProfile> findAll() {
        List<ClientProfile> list = new ArrayList<>();
        String sql = "SELECT * FROM client_profiles ORDER BY total_spent DESC, posted_projects DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToProfile(rs));
            }
        } catch (SQLException e) {
            System.err.println("ClientProfileDAO.findAll error: " + e.getMessage());
        }
        return list;
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM client_profiles WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ClientProfileDAO.delete error: " + e.getMessage());
            return false;
        }
    }

    private ClientProfile mapRowToProfile(ResultSet rs) throws SQLException {
        ClientProfile cp = new ClientProfile();
        cp.setId(rs.getString("id"));
        cp.setUserId(rs.getString("user_id"));
        cp.setCompanyName(rs.getString("company_name"));
        cp.setIndustry(rs.getString("industry"));
        cp.setCompanyWebsite(rs.getString("company_website"));
        cp.setAbout(rs.getString("about"));
        cp.setAvatarPath(rs.getString("avatar_path"));
        cp.setTotalSpent(rs.getDouble("total_spent"));
        cp.setPostedProjects(rs.getInt("posted_projects"));
        cp.setCreatedAt(rs.getString("created_at"));
        return cp;
    }
}

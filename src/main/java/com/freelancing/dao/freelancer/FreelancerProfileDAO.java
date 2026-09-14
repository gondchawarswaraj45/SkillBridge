package com.freelancing.dao.freelancer;

import com.freelancing.model.freelancer.FreelancerProfile;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for FreelancerProfile persistence in SQLite.
 */
public class FreelancerProfileDAO {

    public boolean create(FreelancerProfile profile) {
        String sql = "INSERT INTO freelancer_profiles (id, user_id, title, bio, hourly_rate, experience_years, rating, "
                   + "total_reviews, completed_projects, avatar_path, resume_path, github_url, linkedin_url, availability, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile.getId());
            ps.setString(2, profile.getUserId());
            ps.setString(3, profile.getTitle());
            ps.setString(4, profile.getBio());
            ps.setDouble(5, profile.getHourlyRate());
            ps.setInt(6, profile.getExperienceYears());
            ps.setDouble(7, profile.getRating());
            ps.setInt(8, profile.getTotalReviews());
            ps.setInt(9, profile.getCompletedProjects());
            ps.setString(10, profile.getAvatarPath());
            ps.setString(11, profile.getResumePath());
            ps.setString(12, profile.getGithubUrl());
            ps.setString(13, profile.getLinkedinUrl());
            ps.setString(14, profile.getAvailability() != null ? profile.getAvailability() : "AVAILABLE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("FreelancerProfileDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Connection conn, FreelancerProfile profile) throws SQLException {
        String sql = "UPDATE freelancer_profiles SET title = ?, bio = ?, hourly_rate = ?, experience_years = ?, "
                   + "rating = ?, total_reviews = ?, completed_projects = ?, "
                   + "avatar_path = ?, resume_path = ?, github_url = ?, linkedin_url = ?, availability = ? "
                   + "WHERE id = ? OR user_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile.getTitle());
            ps.setString(2, profile.getBio());
            ps.setDouble(3, profile.getHourlyRate());
            ps.setInt(4, profile.getExperienceYears());
            ps.setDouble(5, profile.getRating());
            ps.setInt(6, profile.getTotalReviews());
            ps.setInt(7, profile.getCompletedProjects());
            ps.setString(8, profile.getAvatarPath());
            ps.setString(9, profile.getResumePath());
            ps.setString(10, profile.getGithubUrl());
            ps.setString(11, profile.getLinkedinUrl());
            ps.setString(12, profile.getAvailability() != null ? profile.getAvailability() : "AVAILABLE");
            ps.setString(13, profile.getId());
            ps.setString(14, profile.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(FreelancerProfile profile) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return update(conn, profile);
        } catch (SQLException e) {
            System.err.println("FreelancerProfileDAO.update error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStats(String profileId, double rating, int totalReviews, int completedProjects) {
        String sql = "UPDATE freelancer_profiles SET rating = ?, total_reviews = ?, completed_projects = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, rating);
            ps.setInt(2, totalReviews);
            ps.setInt(3, completedProjects);
            ps.setString(4, profileId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("FreelancerProfileDAO.updateStats error: " + e.getMessage());
            return false;
        }
    }

    public FreelancerProfile findById(String id) {
        String sql = "SELECT * FROM freelancer_profiles WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToProfile(rs);
            }
        } catch (SQLException e) {
            System.err.println("FreelancerProfileDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    public FreelancerProfile findByUserId(String userId) {
        String sql = "SELECT * FROM freelancer_profiles WHERE user_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToProfile(rs);
            }
        } catch (SQLException e) {
            System.err.println("FreelancerProfileDAO.findByUserId error: " + e.getMessage());
        }
        return null;
    }

    public List<FreelancerProfile> findAll() {
        List<FreelancerProfile> list = new ArrayList<>();
        String sql = "SELECT * FROM freelancer_profiles ORDER BY rating DESC, completed_projects DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToProfile(rs));
            }
        } catch (SQLException e) {
            System.err.println("FreelancerProfileDAO.findAll error: " + e.getMessage());
        }
        return list;
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM freelancer_profiles WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("FreelancerProfileDAO.delete error: " + e.getMessage());
            return false;
        }
    }

    private FreelancerProfile mapRowToProfile(ResultSet rs) throws SQLException {
        FreelancerProfile fp = new FreelancerProfile();
        fp.setId(rs.getString("id"));
        fp.setUserId(rs.getString("user_id"));
        fp.setTitle(rs.getString("title"));
        fp.setBio(rs.getString("bio"));
        fp.setHourlyRate(rs.getDouble("hourly_rate"));
        fp.setExperienceYears(rs.getInt("experience_years"));
        fp.setRating(rs.getDouble("rating"));
        fp.setTotalReviews(rs.getInt("total_reviews"));
        fp.setCompletedProjects(rs.getInt("completed_projects"));
        fp.setAvatarPath(rs.getString("avatar_path"));
        fp.setResumePath(rs.getString("resume_path"));
        fp.setGithubUrl(rs.getString("github_url"));
        fp.setLinkedinUrl(rs.getString("linkedin_url"));
        fp.setAvailability(rs.getString("availability"));
        fp.setCreatedAt(rs.getString("created_at"));
        return fp;
    }
}

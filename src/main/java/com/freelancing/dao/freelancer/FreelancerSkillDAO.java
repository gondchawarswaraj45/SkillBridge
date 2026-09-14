package com.freelancing.dao.freelancer;

import com.freelancing.model.common.Skill;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object managing many-to-many relationship between Freelancers and Skills.
 */
public class FreelancerSkillDAO {

    public boolean addSkill(String freelancerId, String skillId, String proficiency) {
        String sql = "INSERT OR REPLACE INTO freelancer_skills (freelancer_id, skill_id, proficiency) VALUES (?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, freelancerId);
            ps.setString(2, skillId);
            ps.setString(3, proficiency != null ? proficiency : "INTERMEDIATE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("FreelancerSkillDAO.addSkill error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeSkill(String freelancerId, String skillId) {
        String sql = "DELETE FROM freelancer_skills WHERE freelancer_id = ? AND skill_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, freelancerId);
            ps.setString(2, skillId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("FreelancerSkillDAO.removeSkill error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeAllSkills(String freelancerId) {
        String sql = "DELETE FROM freelancer_skills WHERE freelancer_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, freelancerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("FreelancerSkillDAO.removeAllSkills error: " + e.getMessage());
            return false;
        }
    }

    public List<Skill> findSkillsByFreelancerId(String freelancerId) {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.category, s.created_at "
                   + "FROM skills s "
                   + "INNER JOIN freelancer_skills fs ON s.id = fs.skill_id "
                   + "WHERE fs.freelancer_id = ? "
                   + "ORDER BY s.name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, freelancerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Skill(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("FreelancerSkillDAO.findSkillsByFreelancerId error: " + e.getMessage());
        }
        return list;
    }

    public List<String> findSkillNamesByFreelancerId(String freelancerId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT s.name "
                   + "FROM skills s "
                   + "INNER JOIN freelancer_skills fs ON s.id = fs.skill_id "
                   + "WHERE fs.freelancer_id = ? "
                   + "ORDER BY s.name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, freelancerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("FreelancerSkillDAO.findSkillNamesByFreelancerId error: " + e.getMessage());
        }
        return list;
    }

    public boolean hasSkill(String freelancerId, String skillId) {
        String sql = "SELECT 1 FROM freelancer_skills WHERE freelancer_id = ? AND skill_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, freelancerId);
            ps.setString(2, skillId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}

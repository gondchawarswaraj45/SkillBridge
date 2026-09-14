package com.freelancing.dao.company;

import com.freelancing.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for project_skills join table in SQLite.
 */
public class ProjectSkillDAO {

    public boolean addSkillToProject(String projectId, String skillId) {
        String sql = "INSERT OR IGNORE INTO project_skills (project_id, skill_id) VALUES (?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ps.setString(2, skillId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProjectSkillDAO.addSkillToProject error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeSkillFromProject(String projectId, String skillId) {
        String sql = "DELETE FROM project_skills WHERE project_id = ? AND skill_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ps.setString(2, skillId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProjectSkillDAO.removeSkillFromProject error: " + e.getMessage());
            return false;
        }
    }

    public List<String> findSkillIdsByProjectId(String projectId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT skill_id FROM project_skills WHERE project_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("skill_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("ProjectSkillDAO.findSkillIdsByProjectId error: " + e.getMessage());
        }
        return list;
    }

    public List<String> findSkillNamesByProjectId(String projectId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT s.name FROM skills s "
                   + "JOIN project_skills ps ON s.id = ps.skill_id "
                   + "WHERE ps.project_id = ? ORDER BY s.name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("ProjectSkillDAO.findSkillNamesByProjectId error: " + e.getMessage());
        }
        return list;
    }

    public List<String> findProjectIdsBySkillId(String skillId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT project_id FROM project_skills WHERE skill_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, skillId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("project_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("ProjectSkillDAO.findProjectIdsBySkillId error: " + e.getMessage());
        }
        return list;
    }

    public boolean setProjectSkills(String projectId, List<String> skillIds) {
        if (projectId == null) return false;
        String deleteSql = "DELETE FROM project_skills WHERE project_id = ?;";
        String insertSql = "INSERT OR IGNORE INTO project_skills (project_id, skill_id) VALUES (?, ?);";

        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean origAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                    psDel.setString(1, projectId);
                    psDel.executeUpdate();
                }

                if (skillIds != null && !skillIds.isEmpty()) {
                    try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                        for (String sId : skillIds) {
                            if (sId != null && !sId.isBlank()) {
                                psIns.setString(1, projectId);
                                psIns.setString(2, sId.trim());
                                psIns.addBatch();
                            }
                        }
                        psIns.executeBatch();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(origAutoCommit);
            }
        } catch (SQLException e) {
            System.err.println("ProjectSkillDAO.setProjectSkills error: " + e.getMessage());
            return false;
        }
    }
}

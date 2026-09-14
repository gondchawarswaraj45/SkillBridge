package com.freelancing.dao.company;

import com.freelancing.model.company.Project;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Project persistence in SQLite.
 */
public class ProjectDAO {

    public boolean create(Project project) {
        String sql = "INSERT INTO projects ("
                   + "id, client_id, title, description, category, "
                   + "budget_type, budget_min, budget_max, deadline, "
                   + "experience_level, status, awarded_freelancer_id, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                   + (project.getCreatedAt() != null ? "?" : "CURRENT_TIMESTAMP") + ");";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getId());
            ps.setString(2, project.getClientId());
            ps.setString(3, project.getTitle());
            ps.setString(4, project.getDescription());
            ps.setString(5, project.getCategory());
            ps.setString(6, project.getBudgetType());
            ps.setDouble(7, project.getBudgetMin());
            ps.setDouble(8, project.getBudgetMax() > 0 ? project.getBudgetMax() : project.getBudget());
            ps.setString(9, project.getDeadline());
            ps.setString(10, project.getExperienceLevel());
            ps.setString(11, project.getStatus() != null ? project.getStatus().name() : "OPEN");
            ps.setString(12, project.getAwardedFreelancerId());
            if (project.getCreatedAt() != null) {
                ps.setString(13, project.getCreatedAt());
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProjectDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Connection conn, Project project) throws SQLException {
        String sql = "UPDATE projects SET "
                   + "title = ?, description = ?, category = ?, budget_type = ?, "
                   + "budget_min = ?, budget_max = ?, deadline = ?, experience_level = ?, "
                   + "status = ?, awarded_freelancer_id = ? "
                   + "WHERE id = ?;";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getTitle());
            ps.setString(2, project.getDescription());
            ps.setString(3, project.getCategory());
            ps.setString(4, project.getBudgetType());
            ps.setDouble(5, project.getBudgetMin());
            ps.setDouble(6, project.getBudgetMax() > 0 ? project.getBudgetMax() : project.getBudget());
            ps.setString(7, project.getDeadline());
            ps.setString(8, project.getExperienceLevel());
            ps.setString(9, project.getStatus() != null ? project.getStatus().name() : "OPEN");
            ps.setString(10, project.getAwardedFreelancerId());
            ps.setString(11, project.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Project project) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return update(conn, project);
        } catch (SQLException e) {
            System.err.println("ProjectDAO.update error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(Connection conn, String projectId, String status) throws SQLException {
        String sql = "UPDATE projects SET status = ? WHERE id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, projectId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(String projectId, String status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return updateStatus(conn, projectId, status);
        } catch (SQLException e) {
            System.err.println("ProjectDAO.updateStatus error: " + e.getMessage());
            return false;
        }
    }

    public boolean assignFreelancer(String projectId, String freelancerProfileId) {
        String sql = "UPDATE projects SET awarded_freelancer_id = ?, status = 'AWARDED' WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, freelancerProfileId);
            ps.setString(2, projectId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProjectDAO.assignFreelancer error: " + e.getMessage());
            return false;
        }
    }

    public Project findById(Connection conn, String id) throws SQLException {
        String sql = "SELECT p.*, cp.company_name, u.username AS client_username, "
                   + "(SELECT COUNT(*) FROM proposals WHERE project_id = p.id) AS proposals_count "
                   + "FROM projects p "
                   + "LEFT JOIN client_profiles cp ON p.client_id = cp.id "
                   + "LEFT JOIN users u ON cp.user_id = u.id "
                   + "WHERE p.id = ?;";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Project findById(String id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findById(conn, id);
        } catch (SQLException e) {
            System.err.println("ProjectDAO.findById error: " + e.getMessage());
            return null;
        }
    }

    public List<Project> findAll() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT p.*, cp.company_name, u.username AS client_username, "
                   + "(SELECT COUNT(*) FROM proposals WHERE project_id = p.id) AS proposals_count "
                   + "FROM projects p "
                   + "LEFT JOIN client_profiles cp ON p.client_id = cp.id "
                   + "LEFT JOIN users u ON cp.user_id = u.id "
                   + "ORDER BY p.created_at DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("ProjectDAO.findAll error: " + e.getMessage());
        }
        return list;
    }

    public List<Project> findByClientId(String clientIdOrUserId) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT p.*, cp.company_name, u.username AS client_username, "
                   + "(SELECT COUNT(*) FROM proposals WHERE project_id = p.id) AS proposals_count "
                   + "FROM projects p "
                   + "LEFT JOIN client_profiles cp ON p.client_id = cp.id "
                   + "LEFT JOIN users u ON cp.user_id = u.id "
                   + "WHERE p.client_id = ? OR cp.user_id = ? "
                   + "ORDER BY p.created_at DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clientIdOrUserId);
            ps.setString(2, clientIdOrUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ProjectDAO.findByClientId error: " + e.getMessage());
        }
        return list;
    }

    public List<Project> findByStatus(String status) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT p.*, cp.company_name, u.username AS client_username, "
                   + "(SELECT COUNT(*) FROM proposals WHERE project_id = p.id) AS proposals_count "
                   + "FROM projects p "
                   + "LEFT JOIN client_profiles cp ON p.client_id = cp.id "
                   + "LEFT JOIN users u ON cp.user_id = u.id "
                   + "WHERE p.status = ? "
                   + "ORDER BY p.created_at DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ProjectDAO.findByStatus error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Parameterized search across marketplace with category, skill, budget, and sort filters.
     */
    public List<Project> search(String keyword, String category, String skillName,
                               Double minBudget, Double maxBudget, String experienceLevel,
                               String statusFilter, String sortBy) {
        List<Project> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT p.*, cp.company_name, u.username AS client_username, ")
           .append("(SELECT COUNT(*) FROM proposals WHERE project_id = p.id) AS proposals_count ")
           .append("FROM projects p ")
           .append("LEFT JOIN client_profiles cp ON p.client_id = cp.id ")
           .append("LEFT JOIN users u ON cp.user_id = u.id ")
           .append("LEFT JOIN project_skills ps ON p.id = ps.project_id ")
           .append("LEFT JOIN skills s ON ps.skill_id = s.id ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (statusFilter != null && !statusFilter.isBlank() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append("AND p.status = ? ");
            params.add(statusFilter.toUpperCase());
        }

        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (LOWER(p.title) LIKE ? OR LOWER(p.description) LIKE ?) ");
            String kw = "%" + keyword.toLowerCase().trim() + "%";
            params.add(kw);
            params.add(kw);
        }

        if (category != null && !category.isBlank() && !"All".equalsIgnoreCase(category) && !"All Categories".equalsIgnoreCase(category)) {
            sql.append("AND LOWER(p.category) = ? ");
            params.add(category.toLowerCase().trim());
        }

        if (skillName != null && !skillName.isBlank() && !"All".equalsIgnoreCase(skillName) && !"All Skills".equalsIgnoreCase(skillName)) {
            sql.append("AND (LOWER(s.name) = ? OR LOWER(s.id) = ? OR LOWER(s.name) LIKE ?) ");
            String skClean = skillName.toLowerCase().trim();
            params.add(skClean);
            params.add(skClean);
            params.add("%" + skClean + "%");
        }

        if (minBudget != null && minBudget > 0) {
            sql.append("AND (p.budget_max >= ? OR p.budget_min >= ?) ");
            params.add(minBudget);
            params.add(minBudget);
        }

        if (maxBudget != null && maxBudget > 0) {
            sql.append("AND (p.budget_min <= ? OR p.budget_max <= ?) ");
            params.add(maxBudget);
            params.add(maxBudget);
        }

        if (experienceLevel != null && !experienceLevel.isBlank() && !"All".equalsIgnoreCase(experienceLevel) && !"All Levels".equalsIgnoreCase(experienceLevel)) {
            sql.append("AND LOWER(p.experience_level) = ? ");
            params.add(experienceLevel.toLowerCase().trim());
        }

        // Sorting
        if ("BUDGET_HIGH".equalsIgnoreCase(sortBy) || "Highest Budget".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.budget_max DESC, p.budget_min DESC ");
        } else if ("BUDGET_LOW".equalsIgnoreCase(sortBy) || "Lowest Budget".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.budget_min ASC, p.budget_max ASC ");
        } else if ("DEADLINE".equalsIgnoreCase(sortBy) || "Closest Deadline".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.deadline ASC ");
        } else {
            sql.append("ORDER BY p.created_at DESC ");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ProjectDAO.search error: " + e.getMessage());
        }

        return list;
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM projects WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProjectDAO.delete error: " + e.getMessage());
            return false;
        }
    }

    public int countAll() {
        return countWithCondition("SELECT COUNT(*) FROM projects;");
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM projects WHERE status = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("ProjectDAO.countByStatus error: " + e.getMessage());
        }
        return 0;
    }

    public int countByClientId(String clientId) {
        String sql = "SELECT COUNT(*) FROM projects WHERE client_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("ProjectDAO.countByClientId error: " + e.getMessage());
        }
        return 0;
    }

    private int countWithCondition(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("ProjectDAO count query error: " + e.getMessage());
        }
        return 0;
    }

    private Project mapRow(ResultSet rs) throws SQLException {
        Project p = new Project();
        p.setId(rs.getString("id"));
        p.setClientId(rs.getString("client_id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setCategory(rs.getString("category"));
        p.setBudgetType(rs.getString("budget_type"));
        p.setBudgetMin(rs.getDouble("budget_min"));
        p.setBudgetMax(rs.getDouble("budget_max"));
        p.setBudget(p.getBudgetMax() > 0 ? p.getBudgetMax() : p.getBudgetMin());
        p.setDeadline(rs.getString("deadline"));
        p.setExperienceLevel(rs.getString("experience_level"));
        p.setStatus(rs.getString("status"));
        p.setAwardedFreelancerId(rs.getString("awarded_freelancer_id"));
        p.setCreatedAt(rs.getString("created_at"));

        try {
            String comp = rs.getString("company_name");
            String uname = rs.getString("client_username");
            if (comp != null && !comp.isBlank()) {
                p.setClientName(comp);
            } else if (uname != null && !uname.isBlank()) {
                p.setClientName(uname);
            } else {
                p.setClientName(p.getClientId());
            }
        } catch (SQLException ignored) {
            p.setClientName(p.getClientId());
        }

        try {
            p.setProposalsCount(rs.getInt("proposals_count"));
        } catch (SQLException ignored) {}

        return p;
    }
}

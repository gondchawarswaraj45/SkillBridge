package com.freelancing.dao.freelancer;

import com.freelancing.model.freelancer.Certification;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Freelancer Certifications in SQLite.
 */
public class CertificationDAO {

    public boolean create(Certification cert) {
        String sql = "INSERT INTO certifications (id, freelancer_id, name, issuer, issue_date, credential_url, certificate_path, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cert.getId());
            ps.setString(2, cert.getFreelancerId());
            ps.setString(3, cert.getName());
            ps.setString(4, cert.getIssuer());
            ps.setString(5, cert.getIssueDate());
            ps.setString(6, cert.getCredentialUrl());
            ps.setString(7, cert.getCertificatePath());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CertificationDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public Certification findById(String id) {
        String sql = "SELECT * FROM certifications WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToCertification(rs);
            }
        } catch (SQLException e) {
            System.err.println("CertificationDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    public List<Certification> findByFreelancerId(String freelancerId) {
        List<Certification> list = new ArrayList<>();
        String sql = "SELECT * FROM certifications WHERE freelancer_id = ? ORDER BY issue_date DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, freelancerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToCertification(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("CertificationDAO.findByFreelancerId error: " + e.getMessage());
        }
        return list;
    }

    public boolean update(Certification cert) {
        String sql = "UPDATE certifications SET name = ?, issuer = ?, issue_date = ?, credential_url = ?, certificate_path = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cert.getName());
            ps.setString(2, cert.getIssuer());
            ps.setString(3, cert.getIssueDate());
            ps.setString(4, cert.getCredentialUrl());
            ps.setString(5, cert.getCertificatePath());
            ps.setString(6, cert.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CertificationDAO.update error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM certifications WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CertificationDAO.delete error: " + e.getMessage());
            return false;
        }
    }

    private Certification mapRowToCertification(ResultSet rs) throws SQLException {
        Certification cert = new Certification();
        cert.setId(rs.getString("id"));
        cert.setFreelancerId(rs.getString("freelancer_id"));
        cert.setName(rs.getString("name"));
        cert.setIssuer(rs.getString("issuer"));
        cert.setIssueDate(rs.getString("issue_date"));
        cert.setCredentialUrl(rs.getString("credential_url"));
        cert.setCertificatePath(rs.getString("certificate_path"));
        cert.setCreatedAt(rs.getString("created_at"));
        return cert;
    }
}

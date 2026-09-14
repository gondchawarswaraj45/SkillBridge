package com.freelancing.dao.company;

import com.freelancing.model.company.Contract;

import com.freelancing.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Contract entities.
 * Supports both standalone queries and atomic transaction participation.
 */
public class ContractDAO {

    private static final String SELECT_BASE =
            "SELECT c.*, p.title AS project_title, " +
            "       cu.username AS client_name, fu.username AS freelancer_name " +
            "FROM contracts c " +
            "JOIN projects p ON c.project_id = p.id " +
            "JOIN client_profiles cp ON c.client_id = cp.id " +
            "JOIN users cu ON cp.user_id = cu.id " +
            "JOIN freelancer_profiles fp ON c.freelancer_id = fp.id " +
            "JOIN users fu ON fp.user_id = fu.id ";

    public void create(Connection conn, Contract contract) throws SQLException {
        String sql = "INSERT INTO contracts (id, project_id, proposal_id, client_id, freelancer_id, " +
                     "total_amount, escrow_balance, status, start_date, end_date, " +
                     "client_signed, freelancer_signed, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, contract.getId());
            stmt.setString(2, contract.getProjectId());
            stmt.setString(3, contract.getProposalId());
            stmt.setString(4, contract.getClientId());
            stmt.setString(5, contract.getFreelancerId());
            stmt.setDouble(6, contract.getTotalAmount());
            stmt.setDouble(7, contract.getEscrowBalance());
            stmt.setString(8, contract.getStatus() != null ? contract.getStatus().name() : Contract.Status.ACTIVE.name());
            stmt.setString(9, contract.getStartDate());
            stmt.setString(10, contract.getEndDate());
            stmt.setBoolean(11, contract.isClientSigned());
            stmt.setBoolean(12, contract.isFreelancerSigned());
            stmt.setString(13, contract.getCreatedAt());
            stmt.executeUpdate();
        }
    }

    public void create(Contract contract) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            create(conn, contract);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create contract: " + e.getMessage(), e);
        }
    }

    public Contract findById(String id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findById(conn, id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find contract by id: " + e.getMessage(), e);
        }
    }

    public Contract findById(Connection conn, String id) throws SQLException {
        String sql = SELECT_BASE + "WHERE c.id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToContract(rs);
                }
            }
        }
        return null;
    }

    public Contract findByProjectId(String projectId) {
        String sql = SELECT_BASE + "WHERE c.project_id = ? ORDER BY c.created_at DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToContract(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find contract by project: " + e.getMessage(), e);
        }
        return null;
    }

    public Contract findByProposalId(String proposalId) {
        String sql = SELECT_BASE + "WHERE c.proposal_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, proposalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToContract(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find contract by proposal: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Contract> findByClientId(String clientId) {
        String sql = SELECT_BASE + "WHERE c.client_id = ? ORDER BY c.created_at DESC";
        return queryList(sql, clientId);
    }

    public List<Contract> findByClientUserId(String clientUserId) {
        String sql = SELECT_BASE + "WHERE cp.user_id = ? ORDER BY c.created_at DESC";
        return queryList(sql, clientUserId);
    }

    public List<Contract> findByFreelancerId(String freelancerId) {
        String sql = SELECT_BASE + "WHERE c.freelancer_id = ? ORDER BY c.created_at DESC";
        return queryList(sql, freelancerId);
    }

    public List<Contract> findByFreelancerUserId(String freelancerUserId) {
        String sql = SELECT_BASE + "WHERE fp.user_id = ? ORDER BY c.created_at DESC";
        return queryList(sql, freelancerUserId);
    }

    public List<Contract> findAll() {
        String sql = SELECT_BASE + "ORDER BY c.created_at DESC";
        List<Contract> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToContract(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list contracts: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean update(Connection conn, Contract contract) throws SQLException {
        String sql = "UPDATE contracts SET total_amount = ?, escrow_balance = ?, status = ?, " +
                     "start_date = ?, end_date = ?, client_signed = ?, freelancer_signed = ? " +
                     "WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, contract.getTotalAmount());
            stmt.setDouble(2, contract.getEscrowBalance());
            stmt.setString(3, contract.getStatus().name());
            stmt.setString(4, contract.getStartDate());
            stmt.setString(5, contract.getEndDate());
            stmt.setBoolean(6, contract.isClientSigned());
            stmt.setBoolean(7, contract.isFreelancerSigned());
            stmt.setString(8, contract.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean update(Contract contract) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return update(conn, contract);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update contract: " + e.getMessage(), e);
        }
    }

    public boolean updateStatus(Connection conn, String contractId, Contract.Status status) throws SQLException {
        String sql = "UPDATE contracts SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, contractId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(String contractId, Contract.Status status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return updateStatus(conn, contractId, status);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update contract status: " + e.getMessage(), e);
        }
    }

    public boolean updateSignatures(String contractId, boolean clientSigned, boolean freelancerSigned, Contract.Status status) {
        String sql = "UPDATE contracts SET client_signed = ?, freelancer_signed = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, clientSigned);
            stmt.setBoolean(2, freelancerSigned);
            stmt.setString(3, status.name());
            stmt.setString(4, contractId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update contract signatures: " + e.getMessage(), e);
        }
    }

    private List<Contract> queryList(String sql, String param) {
        List<Contract> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToContract(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query contracts: " + e.getMessage(), e);
        }
        return list;
    }

    private Contract mapResultSetToContract(ResultSet rs) throws SQLException {
        Contract c = new Contract();
        c.setId(rs.getString("id"));
        c.setProjectId(rs.getString("project_id"));
        c.setProposalId(rs.getString("proposal_id"));
        c.setClientId(rs.getString("client_id"));
        c.setFreelancerId(rs.getString("freelancer_id"));
        c.setTotalAmount(rs.getDouble("total_amount"));
        c.setEscrowBalance(rs.getDouble("escrow_balance"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                c.setStatus(Contract.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                c.setStatus(Contract.Status.ACTIVE);
            }
        }

        c.setStartDate(rs.getString("start_date"));
        c.setEndDate(rs.getString("end_date"));
        c.setClientSigned(rs.getBoolean("client_signed"));
        c.setFreelancerSigned(rs.getBoolean("freelancer_signed"));
        c.setCreatedAt(rs.getString("created_at"));

        c.setProjectTitle(rs.getString("project_title"));
        c.setClientName(rs.getString("client_name"));
        c.setFreelancerName(rs.getString("freelancer_name"));

        return c;
    }
}

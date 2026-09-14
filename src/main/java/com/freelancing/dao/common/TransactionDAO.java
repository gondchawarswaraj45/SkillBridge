package com.freelancing.dao.common;

import com.freelancing.model.common.Transaction;

import com.freelancing.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Transaction entities.
 * Supports financial ledger operations, audit tracking, and balance computations.
 */
public class TransactionDAO {

    private static final String SELECT_BASE =
            "SELECT t.*, " +
            "       su.username AS sender_username, " +
            "       ru.username AS receiver_username, " +
            "       p.title AS project_title, " +
            "       m.title AS milestone_title " +
            "FROM transactions t " +
            "LEFT JOIN users su ON t.sender_id = su.id " +
            "LEFT JOIN users ru ON t.receiver_id = ru.id " +
            "LEFT JOIN contracts c ON t.contract_id = c.id " +
            "LEFT JOIN projects p ON c.project_id = p.id " +
            "LEFT JOIN milestones m ON t.milestone_id = m.id ";

    public void create(Connection conn, Transaction tx) throws SQLException {
        String sql = "INSERT INTO transactions (id, contract_id, milestone_id, sender_id, " +
                     "receiver_id, amount, type, status, reference, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tx.getId());
            stmt.setString(2, tx.getContractId());
            stmt.setString(3, tx.getMilestoneId());
            stmt.setString(4, tx.getSenderId());
            stmt.setString(5, tx.getReceiverId());
            stmt.setDouble(6, tx.getAmount());
            stmt.setString(7, tx.getType() != null ? tx.getType().name() : Transaction.Type.PAYMENT.name());
            stmt.setString(8, tx.getStatus() != null ? tx.getStatus().name() : Transaction.Status.COMPLETED.name());
            stmt.setString(9, tx.getReference());
            stmt.setString(10, tx.getCreatedAt());
            stmt.executeUpdate();
        }
    }

    public void create(Transaction tx) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            create(conn, tx);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create transaction: " + e.getMessage(), e);
        }
    }

    public Transaction findById(String id) {
        String sql = SELECT_BASE + "WHERE t.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaction(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find transaction by id: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Transaction> findByUserId(String userId) {
        String sql = SELECT_BASE + "WHERE t.sender_id = ? OR t.receiver_id = ? ORDER BY t.created_at DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list transactions for user: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Transaction> findByContractId(String contractId) {
        String sql = SELECT_BASE + "WHERE t.contract_id = ? ORDER BY t.created_at DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, contractId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list transactions for contract: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Transaction> findByMilestoneId(String milestoneId) {
        String sql = SELECT_BASE + "WHERE t.milestone_id = ? ORDER BY t.created_at DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, milestoneId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list transactions for milestone: " + e.getMessage(), e);
        }
        return list;
    }

    public double getTotalPaidByUser(String userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM transactions " +
                     "WHERE sender_id = ? AND type = 'PAYMENT' AND status = 'COMPLETED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate total paid: " + e.getMessage(), e);
        }
        return 0.0;
    }

    public double getTotalEarnedByUser(String userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM transactions " +
                     "WHERE receiver_id = ? AND type = 'PAYMENT' AND status = 'COMPLETED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate total earned: " + e.getMessage(), e);
        }
        return 0.0;
    }

    public double getTotalWithdrawnByUser(String userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM transactions " +
                     "WHERE sender_id = ? AND type = 'WITHDRAWAL' AND status = 'COMPLETED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate total withdrawn: " + e.getMessage(), e);
        }
        return 0.0;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction tx = new Transaction();
        tx.setId(rs.getString("id"));
        tx.setContractId(rs.getString("contract_id"));
        tx.setMilestoneId(rs.getString("milestone_id"));
        tx.setSenderId(rs.getString("sender_id"));
        tx.setReceiverId(rs.getString("receiver_id"));
        tx.setAmount(rs.getDouble("amount"));

        String typeStr = rs.getString("type");
        if (typeStr != null) {
            try {
                tx.setType(Transaction.Type.valueOf(typeStr));
            } catch (IllegalArgumentException e) {
                tx.setType(Transaction.Type.PAYMENT);
            }
        }

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                tx.setStatus(Transaction.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                tx.setStatus(Transaction.Status.COMPLETED);
            }
        }

        tx.setReference(rs.getString("reference"));
        tx.setCreatedAt(rs.getString("created_at"));

        tx.setSenderUsername(rs.getString("sender_username"));
        tx.setReceiverUsername(rs.getString("receiver_username"));
        tx.setProjectTitle(rs.getString("project_title"));
        tx.setMilestoneTitle(rs.getString("milestone_title"));

        return tx;
    }
}

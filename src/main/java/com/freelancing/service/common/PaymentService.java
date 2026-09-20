package com.freelancing.service.common;

import com.freelancing.dao.common.TransactionDAO;
import com.freelancing.dao.common.UserDAO;
import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.dao.company.ContractDAO;
import com.freelancing.dao.company.MilestoneDAO;
import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.model.common.Transaction;
import com.freelancing.model.common.User;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.company.Contract;
import com.freelancing.model.company.Milestone;
import com.freelancing.model.company.Project;
import com.freelancing.model.freelancer.FreelancerProfile;

import com.freelancing.db.DatabaseConnection;
import com.freelancing.db.DatabaseManager;
import com.freelancing.util.LoggingUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing payments simulation, financial ledger, escrow release,
 * and freelancer withdrawals. All transactions are clearly identified as DEMO PAYMENT.
 */
public class PaymentService {

    private static final String TAG = "PaymentService";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TransactionDAO transactionDAO;
    private final MilestoneDAO milestoneDAO;
    private final ContractDAO contractDAO;
    private final ProjectDAO projectDAO;
    private final ClientProfileDAO clientProfileDAO;
    private final FreelancerProfileDAO freelancerProfileDAO;
    private final UserDAO userDAO;
    private final NotificationService notificationService;

    public static class PaymentReceipt {
        public String transactionId;
        public String reference;
        public String milestoneTitle;
        public String projectTitle;
        public double amount;
        public String currency = "USD";
        public String timestamp;
        public String status = "COMPLETED";
        public String clientUsername;
        public String freelancerUsername;
        public boolean isDemoPayment = true;

        public String toString() {
            return "====================================================\n" +
                   "      SKILLBRIDGE ESCROW PAYMENT RECEIPT (DEMO)     \n" +
                   "====================================================\n" +
                   " [NOTICE]: THIS IS A DEMO SIMULATION. NO REAL MONEY CHARGED.\n" +
                   " Transaction ID : " + transactionId + "\n" +
                   " Reference No   : " + reference + "\n" +
                   " Project        : " + (projectTitle != null ? projectTitle : "-") + "\n" +
                   " Milestone      : " + milestoneTitle + "\n" +
                   " Client (Sender): " + (clientUsername != null ? clientUsername : "-") + "\n" +
                   " Payee (Vendor) : " + (freelancerUsername != null ? freelancerUsername : "-") + "\n" +
                   " Amount Released: $" + String.format("%.2f", amount) + " " + currency + "\n" +
                   " Status         : " + status + "\n" +
                   " Timestamp      : " + timestamp + "\n" +
                   "====================================================\n" +
                   " Escrow protection guaranteed by SkillBridge Platform!";
        }
    }

    public static class FreelancerFinancialSummary {
        public double totalEarned;
        public double availableBalance;
        public double totalWithdrawn;
        public double pendingEscrow;
        public int transactionsCount;
    }

    public static class ClientFinancialSummary {
        public double totalSpent;
        public double activeEscrowCommitted;
        public int completedContractsCount;
        public int transactionsCount;
    }

    public PaymentService() {
        this.transactionDAO = new TransactionDAO();
        this.milestoneDAO = new MilestoneDAO();
        this.contractDAO = new ContractDAO();
        this.projectDAO = new ProjectDAO();
        this.clientProfileDAO = new ClientProfileDAO();
        this.freelancerProfileDAO = new FreelancerProfileDAO();
        this.userDAO = new UserDAO();
        this.notificationService = new NotificationService();
    }

    public PaymentService(TransactionDAO transactionDAO, MilestoneDAO milestoneDAO,
                          ContractDAO contractDAO, ProjectDAO projectDAO,
                          ClientProfileDAO clientProfileDAO, FreelancerProfileDAO freelancerProfileDAO,
                          UserDAO userDAO, NotificationService notificationService) {
        this.transactionDAO = transactionDAO;
        this.milestoneDAO = milestoneDAO;
        this.contractDAO = contractDAO;
        this.projectDAO = projectDAO;
        this.clientProfileDAO = clientProfileDAO;
        this.freelancerProfileDAO = freelancerProfileDAO;
        this.userDAO = userDAO;
        this.notificationService = notificationService;
    }

    /**
     * Executes demo milestone escrow payment release with atomic JDBC transaction.
     */
    public PaymentReceipt processMilestonePayment(String milestoneId, String clientUserId) {
        if (milestoneId == null || milestoneId.trim().isEmpty()) {
            throw new IllegalArgumentException("Milestone ID is required.");
        }
        if (clientUserId == null || clientUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Client User ID is required.");
        }

        Milestone milestone = milestoneDAO.findById(milestoneId);
        if (milestone == null) {
            throw new IllegalArgumentException("Milestone not found: " + milestoneId);
        }

        Contract contract = contractDAO.findById(milestone.getContractId());
        if (contract == null) {
            throw new IllegalArgumentException("Contract not found for milestone: " + milestone.getContractId());
        }

        ClientProfile clientProfile = clientProfileDAO.findByUserId(clientUserId);
        if (clientProfile == null || !clientProfile.getId().equals(contract.getClientId())) {
            throw new SecurityException("User does not have client authority over this contract.");
        }

        if (milestone.getStatus() == Milestone.Status.PAID) {
            throw new IllegalStateException("Milestone has already been paid.");
        }

        FreelancerProfile freelancerProfile = freelancerProfileDAO.findById(contract.getFreelancerId());
        if (freelancerProfile == null || freelancerProfile.getUserId() == null) {
            throw new IllegalStateException("Freelancer profile not found for contract.");
        }

        String freelancerUserId = freelancerProfile.getUserId();
        String txId = "tx_" + UUID.randomUUID().toString().substring(0, 8);
        String refNo = "DEMO-ESCROW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String now = LocalDateTime.now().format(FORMATTER);

        boolean allPaid;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Mark milestone PAID
                milestoneDAO.updateStatus(conn, milestoneId, Milestone.Status.PAID);

                // 2. Insert transaction record
                Transaction tx = new Transaction();
                tx.setId(txId);
                tx.setContractId(contract.getId());
                tx.setMilestoneId(milestone.getId());
                tx.setSenderId(clientUserId);
                tx.setReceiverId(freelancerUserId);
                tx.setAmount(milestone.getAmount());
                tx.setType(Transaction.Type.PAYMENT);
                tx.setStatus(Transaction.Status.COMPLETED);
                tx.setReference(refNo);
                tx.setCreatedAt(now);
                transactionDAO.create(conn, tx);

                // 3. Update client profile total spent
                clientProfile.setTotalSpent(clientProfile.getTotalSpent() + milestone.getAmount());
                clientProfileDAO.update(conn, clientProfile);

                // 4. Update contract escrow balance
                double newEscrow = Math.max(0.0, contract.getEscrowBalance() - milestone.getAmount());
                contract.setEscrowBalance(newEscrow);
                contractDAO.update(conn, contract);

                // 5. Check if all milestones in contract are now paid
                List<Milestone> allMilestones = milestoneDAO.findByContractId(conn, contract.getId());
                allPaid = allMilestones.stream().allMatch(m -> m.getId().equals(milestoneId) || m.getStatus() == Milestone.Status.PAID);

                if (allPaid) {
                    contractDAO.updateStatus(conn, contract.getId(), Contract.Status.COMPLETED);
                    projectDAO.updateStatus(conn, contract.getProjectId(), Project.Status.COMPLETED.name());

                    freelancerProfile.setCompletedProjects(freelancerProfile.getCompletedProjects() + 1);
                    freelancerProfileDAO.update(conn, freelancerProfile);
                }

                conn.commit();
                LoggingUtil.info(TAG, "Milestone payment transaction committed: " + txId);
            } catch (Exception e) {
                conn.rollback();
                LoggingUtil.error(TAG, "Milestone payment failed, rolled back: " + e.getMessage(), e);
                throw new RuntimeException("Payment processing failed: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during payment: " + e.getMessage(), e);
        }

        // Post-commit notifications & receipt
        User clientUser = userDAO.findById(clientUserId);
        User freeUser = userDAO.findById(freelancerUserId);

        notificationService.sendNotification(freelancerUserId, "💰 Payment Received!",
                "Demo escrow payment of $" + String.format("%.2f", milestone.getAmount()) + " released for milestone '" + milestone.getTitle() + "'.");
        notificationService.sendNotification(clientUserId, "💳 Payment Released",
                "You released $" + String.format("%.2f", milestone.getAmount()) + " to " + (freeUser != null ? freeUser.getUsername() : "freelancer") + " for milestone '" + milestone.getTitle() + "'.");

        if (allPaid) {
            notificationService.sendNotification(clientUserId, "🏆 Contract Completed!",
                    "All milestones for contract '" + contract.getProjectTitle() + "' have been delivered and paid.");
            notificationService.sendNotification(freelancerUserId, "🏆 Contract Completed!",
                    "All milestones for contract '" + contract.getProjectTitle() + "' have been fully paid. Great work!");
        }

        DatabaseManager.getInstance().logActivity("Demo payment " + txId + " ($" + milestone.getAmount() + ") released for milestone " + milestone.getTitle());

        PaymentReceipt receipt = new PaymentReceipt();
        receipt.transactionId = txId;
        receipt.reference = refNo;
        receipt.milestoneTitle = milestone.getTitle();
        receipt.projectTitle = contract.getProjectTitle();
        receipt.amount = milestone.getAmount();
        receipt.timestamp = now;
        receipt.status = "COMPLETED";
        receipt.clientUsername = clientUser != null ? clientUser.getUsername() : "Client";
        receipt.freelancerUsername = freeUser != null ? freeUser.getUsername() : "Freelancer";

        return receipt;
    }

    /**
     * Simulates withdrawing available earnings to an external account.
     */
    public Transaction withdrawEarnings(String freelancerUserId, double amount, String method, String accountRef) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }

        double totalEarned = transactionDAO.getTotalEarnedByUser(freelancerUserId);
        double totalWithdrawn = transactionDAO.getTotalWithdrawnByUser(freelancerUserId);
        double availableBalance = totalEarned - totalWithdrawn;

        if (amount > availableBalance) {
            throw new IllegalArgumentException(String.format("Insufficient funds. Available balance: $%.2f, Requested: $%.2f",
                    availableBalance, amount));
        }

        String txId = "tx_wd_" + UUID.randomUUID().toString().substring(0, 8);
        String refNo = "DEMO-WITHDRAW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String now = LocalDateTime.now().format(FORMATTER);

        Transaction tx = new Transaction();
        tx.setId(txId);
        tx.setSenderId(freelancerUserId);
        tx.setReceiverId(freelancerUserId);
        tx.setAmount(amount);
        tx.setType(Transaction.Type.WITHDRAWAL);
        tx.setStatus(Transaction.Status.COMPLETED);
        tx.setReference(refNo + " (" + (method != null ? method : "Direct Bank") + ": " + (accountRef != null ? accountRef : "Demo") + ")");
        tx.setCreatedAt(now);

        transactionDAO.create(tx);

        notificationService.sendNotification(freelancerUserId, "💸 Withdrawal Processed (Demo)",
                "Demo withdrawal of $" + String.format("%.2f", amount) + " processed to " + method + ".");
        DatabaseManager.getInstance().logActivity("Freelancer " + freelancerUserId + " withdrew $" + amount + " (Txn: " + txId + ")");

        return tx;
    }

    public FreelancerFinancialSummary getFinancialSummaryForFreelancer(String freelancerUserId) {
        FreelancerFinancialSummary summary = new FreelancerFinancialSummary();
        summary.totalEarned = transactionDAO.getTotalEarnedByUser(freelancerUserId);
        summary.totalWithdrawn = transactionDAO.getTotalWithdrawnByUser(freelancerUserId);
        summary.availableBalance = summary.totalEarned - summary.totalWithdrawn;

        List<Contract> contracts = contractDAO.findByFreelancerUserId(freelancerUserId);
        summary.pendingEscrow = contracts.stream()
                .filter(c -> c.getStatus() == Contract.Status.ACTIVE)
                .mapToDouble(Contract::getEscrowBalance)
                .sum();

        List<Transaction> txs = transactionDAO.findByUserId(freelancerUserId);
        summary.transactionsCount = txs.size();

        return summary;
    }

    public ClientFinancialSummary getFinancialSummaryForClient(String clientUserId) {
        ClientFinancialSummary summary = new ClientFinancialSummary();
        summary.totalSpent = transactionDAO.getTotalPaidByUser(clientUserId);

        List<Contract> contracts = contractDAO.findByClientUserId(clientUserId);
        summary.activeEscrowCommitted = contracts.stream()
                .filter(c -> c.getStatus() == Contract.Status.ACTIVE)
                .mapToDouble(Contract::getEscrowBalance)
                .sum();

        summary.completedContractsCount = (int) contracts.stream()
                .filter(c -> c.getStatus() == Contract.Status.COMPLETED)
                .count();

        List<Transaction> txs = transactionDAO.findByUserId(clientUserId);
        summary.transactionsCount = txs.size();

        return summary;
    }

    public List<Transaction> getTransactionHistory(String userId) {
        return transactionDAO.findByUserId(userId);
    }

    /** Compatibility for existing callers */
    public PaymentReceipt processRazorpayPayment(Milestone milestone, Project project) {
        if (milestone != null && project != null) {
            String clientUserId = project.getClientId();
            ClientProfile cp = clientProfileDAO.findById(project.getClientId());
            if (cp != null && cp.getUserId() != null) {
                clientUserId = cp.getUserId();
            }
            return processMilestonePayment(milestone.getId(), clientUserId);
        }
        return null;
    }
}

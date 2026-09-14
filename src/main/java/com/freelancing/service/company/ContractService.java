package com.freelancing.service.company;

import com.freelancing.dao.common.TransactionDAO;
import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.dao.company.ContractDAO;
import com.freelancing.dao.company.MilestoneDAO;
import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.dao.company.ProposalDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.model.common.Transaction;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.company.Contract;
import com.freelancing.model.company.Milestone;
import com.freelancing.model.company.Project;
import com.freelancing.model.company.Proposal;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.service.common.NotificationService;
import com.freelancing.service.freelancer.AIProposalAssistant;

import com.freelancing.db.DatabaseConnection;
import com.freelancing.db.DatabaseManager;
import com.freelancing.util.LoggingUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing contracts between clients and freelancers.
 * Implements atomic JDBC transaction for hiring and milestone generation.
 */
public class ContractService {

    private static final String TAG = "ContractService";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ContractDAO contractDAO;
    private final MilestoneDAO milestoneDAO;
    private final ProposalDAO proposalDAO;
    private final ProjectDAO projectDAO;
    private final ClientProfileDAO clientProfileDAO;
    private final FreelancerProfileDAO freelancerProfileDAO;
    private final NotificationService notificationService;
    private final AIProposalAssistant aiProposalAssistant;
    private final TransactionDAO transactionDAO;

    public ContractService() {
        this.contractDAO = new ContractDAO();
        this.milestoneDAO = new MilestoneDAO();
        this.proposalDAO = new ProposalDAO();
        this.projectDAO = new ProjectDAO();
        this.clientProfileDAO = new ClientProfileDAO();
        this.freelancerProfileDAO = new FreelancerProfileDAO();
        this.notificationService = new NotificationService();
        this.aiProposalAssistant = new AIProposalAssistant();
        this.transactionDAO = new TransactionDAO();
    }

    public ContractService(ContractDAO contractDAO, MilestoneDAO milestoneDAO, ProposalDAO proposalDAO,
                           ProjectDAO projectDAO, ClientProfileDAO clientProfileDAO,
                           FreelancerProfileDAO freelancerProfileDAO, NotificationService notificationService,
                           AIProposalAssistant aiProposalAssistant) {
        this.contractDAO = contractDAO;
        this.milestoneDAO = milestoneDAO;
        this.proposalDAO = proposalDAO;
        this.projectDAO = projectDAO;
        this.clientProfileDAO = clientProfileDAO;
        this.freelancerProfileDAO = freelancerProfileDAO;
        this.notificationService = notificationService;
        this.aiProposalAssistant = aiProposalAssistant;
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Executes the atomic hiring transaction:
     * BEGIN -> Accept Proposal -> Update Project to IN_PROGRESS -> Create Contract -> Create Initial Milestones -> COMMIT (or ROLLBACK).
     */
    public Contract hireFreelancer(String proposalId, String clientUserId, List<AIProposalAssistant.MilestoneDraft> customMilestones) {
        if (proposalId == null || proposalId.trim().isEmpty()) {
            throw new IllegalArgumentException("Proposal ID is required.");
        }
        if (clientUserId == null || clientUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Client User ID is required.");
        }

        Contract contract;
        Proposal proposal;
        Project project;
        List<Milestone> createdMilestones = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Fetch proposal in transaction
                proposal = proposalDAO.findById(conn, proposalId);
                if (proposal == null) {
                    throw new IllegalArgumentException("Proposal not found: " + proposalId);
                }
                if (proposal.getStatus() == Proposal.Status.ACCEPTED) {
                    throw new IllegalStateException("Proposal has already been accepted.");
                }
                if (proposal.getStatus() == Proposal.Status.WITHDRAWN) {
                    throw new IllegalStateException("Cannot hire on a withdrawn proposal.");
                }

                // 2. Fetch project and verify client ownership
                project = projectDAO.findById(conn, proposal.getProjectId());
                if (project == null) {
                    throw new IllegalArgumentException("Project not found: " + proposal.getProjectId());
                }

                ClientProfile clientProfile = clientProfileDAO.findByUserId(conn, clientUserId);
                if (clientProfile == null || !clientProfile.getId().equals(project.getClientId())) {
                    throw new SecurityException("User does not have client authority over this project.");
                }

                // 3. Update proposal status to ACCEPTED
                proposalDAO.updateStatus(conn, proposalId, Proposal.Status.ACCEPTED);
                proposal.setStatus(Proposal.Status.ACCEPTED);

                // 4. Update project status to IN_PROGRESS and assign freelancer
                project.setStatus(Project.Status.IN_PROGRESS);
                project.setAwardedFreelancerId(proposal.getFreelancerId());
                projectDAO.update(conn, project);

                // 5. Create Contract entity in SQLite
                String contractId = "ctr_" + UUID.randomUUID().toString().substring(0, 8);
                String now = LocalDateTime.now().format(FORMATTER);
                contract = new Contract();
                contract.setId(contractId);
                contract.setProjectId(project.getId());
                contract.setProposalId(proposal.getId());
                contract.setClientId(project.getClientId());
                contract.setFreelancerId(proposal.getFreelancerId());
                contract.setTotalAmount(proposal.getBidAmount());
                contract.setEscrowBalance(proposal.getBidAmount());
                contract.setStatus(Contract.Status.ACTIVE);
                contract.setStartDate(LocalDate.now().toString());
                int deliveryDays = Math.max(1, proposal.getDeliveryDays());
                contract.setEndDate(LocalDate.now().plusDays(deliveryDays).toString());
                contract.setClientSigned(true);
                contract.setFreelancerSigned(true);
                contract.setCreatedAt(now);

                contractDAO.create(conn, contract);

                // 6. Create Milestones in SQLite
                List<AIProposalAssistant.MilestoneDraft> drafts = customMilestones;
                if (drafts == null || drafts.isEmpty()) {
                    drafts = aiProposalAssistant.generateMilestoneDrafts(project, proposal.getBidAmount());
                }

                int order = 1;
                int accumulatedDays = 0;
                for (AIProposalAssistant.MilestoneDraft draft : drafts) {
                    Milestone m = new Milestone();
                    m.setId("ms_" + UUID.randomUUID().toString().substring(0, 8));
                    m.setContractId(contractId);
                    m.setProjectId(project.getId());
                    m.setTitle(draft.getTitle());
                    m.setDescription(draft.getDescription());
                    m.setAmount(draft.getAmount());
                    accumulatedDays += Math.max(1, draft.getDays());
                    m.setDeadline(LocalDate.now().plusDays(accumulatedDays).toString());
                    m.setStatus(order == 1 ? Milestone.Status.IN_PROGRESS : Milestone.Status.PENDING);
                    m.setSequenceOrder(order++);
                    m.setCreatedAt(now);

                    milestoneDAO.create(conn, m);
                    createdMilestones.add(m);
                }

                // 7. Record Escrow Deposit transaction in SQLite
                String txId = "tx_" + UUID.randomUUID().toString().substring(0, 8);
                String refNo = "DEMO-ESCROW-DEP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Transaction depositTx = new Transaction();
                depositTx.setId(txId);
                depositTx.setContractId(contractId);
                depositTx.setSenderId(clientUserId);
                String freeUserId = proposal.getFreelancerUserId();
                if (freeUserId == null) {
                    FreelancerProfile fp = freelancerProfileDAO.findById(proposal.getFreelancerId());
                    if (fp != null) {
                        freeUserId = fp.getUserId();
                    }
                }
                depositTx.setReceiverId(freeUserId);
                depositTx.setAmount(proposal.getBidAmount());
                depositTx.setType(Transaction.Type.ESCROW_DEPOSIT);
                depositTx.setStatus(Transaction.Status.COMPLETED);
                depositTx.setReference(refNo);
                depositTx.setCreatedAt(now);
                transactionDAO.create(conn, depositTx);

                // 8. Commit atomic transaction!
                conn.commit();
                LoggingUtil.info(TAG, "Atomic hiring transaction committed successfully for contract: " + contractId);
            } catch (Exception e) {
                conn.rollback();
                LoggingUtil.error(TAG, "Hiring transaction failed and was rolled back: " + e.getMessage(), e);
                throw new RuntimeException("Hiring transaction failed: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during hiring transaction: " + e.getMessage(), e);
        }

        // 8. Post-commit cache synchronization & notifications
        contract.setMilestones(createdMilestones);
        contract.setProjectTitle(project.getTitle());
        contract.setClientName(project.getClientName());
        contract.setFreelancerName(proposal.getFreelancerName());

        DatabaseManager.getInstance().getContracts().put(contract.getId(), contract);
        DatabaseManager.getInstance().getProjects().put(project.getId(), project);
        DatabaseManager.getInstance().getProposals().put(proposal.getId(), proposal);
        for (Milestone m : createdMilestones) {
            DatabaseManager.getInstance().getMilestones().put(m.getId(), m);
        }

        if (proposal.getFreelancerUserId() != null) {
            notificationService.sendNotification(proposal.getFreelancerUserId(), "🎉 You Have Been Hired!",
                    "Congratulations! Your proposal for '" + project.getTitle() + "' was accepted. Contract " + contract.getId() + " is now active.");
        }
        notificationService.sendNotification(clientUserId, "📋 Contract Active",
                "Contract for '" + project.getTitle() + "' has been initiated with " + createdMilestones.size() + " milestones.");

        DatabaseManager.getInstance().logActivity("Hired " + proposal.getFreelancerName() + " for " + project.getTitle() + " (Contract: " + contract.getId() + ")");
        return contract;
    }

    public Contract getContract(String contractId) {
        Contract contract = contractDAO.findById(contractId);
        if (contract != null) {
            contract.setMilestones(milestoneDAO.findByContractId(contractId));
        }
        return contract;
    }

    public Contract getContractById(String contractId) {
        return getContract(contractId);
    }

    public Contract getContractByProjectId(String projectId) {
        Contract contract = contractDAO.findByProjectId(projectId);
        if (contract != null) {
            contract.setMilestones(milestoneDAO.findByContractId(contract.getId()));
        }
        return contract;
    }

    public Contract getContractByProposalId(String proposalId) {
        Contract contract = contractDAO.findByProposalId(proposalId);
        if (contract != null) {
            contract.setMilestones(milestoneDAO.findByContractId(contract.getId()));
        }
        return contract;
    }

    public List<Contract> getContractsByClient(String clientId) {
        List<Contract> list = contractDAO.findByClientId(clientId);
        for (Contract c : list) {
            c.setMilestones(milestoneDAO.findByContractId(c.getId()));
        }
        return list;
    }

    public List<Contract> getContractsByClientUserId(String clientUserId) {
        List<Contract> list = contractDAO.findByClientUserId(clientUserId);
        for (Contract c : list) {
            c.setMilestones(milestoneDAO.findByContractId(c.getId()));
        }
        return list;
    }

    public List<Contract> getContractsByFreelancer(String freelancerId) {
        List<Contract> list = contractDAO.findByFreelancerId(freelancerId);
        for (Contract c : list) {
            c.setMilestones(milestoneDAO.findByContractId(c.getId()));
        }
        return list;
    }

    public List<Contract> getContractsByFreelancerUserId(String freelancerUserId) {
        List<Contract> list = contractDAO.findByFreelancerUserId(freelancerUserId);
        for (Contract c : list) {
            c.setMilestones(milestoneDAO.findByContractId(c.getId()));
        }
        return list;
    }

    public List<Contract> getActiveContracts(String userId) {
        List<Contract> clientContracts = getContractsByClientUserId(userId);
        List<Contract> freelancerContracts = getContractsByFreelancerUserId(userId);
        List<Contract> combined = new ArrayList<>(clientContracts);
        combined.addAll(freelancerContracts);

        return combined.stream()
                .filter(c -> c.getStatus() == Contract.Status.ACTIVE)
                .collect(Collectors.toList());
    }

    public void clientSign(String contractId) {
        Contract contract = getContract(contractId);
        if (contract != null) {
            contract.setClientSigned(true);
            Contract.Status nextStatus = contract.isFreelancerSigned() ? Contract.Status.ACTIVE : Contract.Status.PENDING_FREELANCER;
            contractDAO.updateSignatures(contractId, true, contract.isFreelancerSigned(), nextStatus);

            if (contract.getFreelancerId() != null) {
                notificationService.sendNotification(contract.getFreelancerId(), "✍️ Contract Signed by Client",
                        "Client signed the contract for '" + contract.getProjectTitle() + "'.");
            }
        }
    }

    public void freelancerSign(String contractId) {
        Contract contract = getContract(contractId);
        if (contract != null) {
            contract.setFreelancerSigned(true);
            Contract.Status nextStatus = contract.isClientSigned() ? Contract.Status.ACTIVE : Contract.Status.PENDING_CLIENT;
            contractDAO.updateSignatures(contractId, contract.isClientSigned(), true, nextStatus);

            if (contract.getClientId() != null) {
                notificationService.sendNotification(contract.getClientId(), "✍️ Contract Signed by Freelancer",
                        "Freelancer signed the contract for '" + contract.getProjectTitle() + "'.");
            }
        }
    }

    /** Compatibility for existing callers */
    public Contract generateContract(Project project, Proposal proposal, List<String> deliverables,
                                     String paymentTerms, String cancellationTerms) {
        if (proposal != null && project != null) {
            ClientProfile cp = clientProfileDAO.findById(project.getClientId());
            String clientUserId = cp != null ? cp.getUserId() : project.getClientId();
            return hireFreelancer(proposal.getId(), clientUserId, null);
        }
        return null;
    }

    public void customerSign(String contractId) { clientSign(contractId); }
    public void vendorSign(String contractId) { freelancerSign(contractId); }
    public List<Contract> getContractsByCustomer(String customerId) { return getContractsByClient(customerId); }
    public List<Contract> getContractsByVendor(String vendorId) { return getContractsByFreelancer(vendorId); }
}

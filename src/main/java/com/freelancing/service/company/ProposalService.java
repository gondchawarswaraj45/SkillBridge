package com.freelancing.service.company;

import com.freelancing.dao.common.UserDAO;
import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.dao.company.ProposalDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.model.common.User;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.company.Contract;
import com.freelancing.model.company.Project;
import com.freelancing.model.company.Proposal;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.service.common.NotificationService;
import com.freelancing.service.freelancer.MatchingService;

import com.freelancing.db.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service managing Proposal lifecycle: submissions, duplicate prevention,
 * shortlisting, rejection, acceptance, withdrawal, and notifications.
 */
public class ProposalService {

    private final ProposalDAO proposalDAO;
    private final ProjectDAO projectDAO;
    private final FreelancerProfileDAO freelancerProfileDAO;
    private final ClientProfileDAO clientProfileDAO;
    private final UserDAO userDAO;
    private final MatchingService matchingService;
    private final NotificationService notificationService;
    private final ContractService contractService;

    public ProposalService() {
        this.proposalDAO = new ProposalDAO();
        this.projectDAO = new ProjectDAO();
        this.freelancerProfileDAO = new FreelancerProfileDAO();
        this.clientProfileDAO = new ClientProfileDAO();
        this.userDAO = new UserDAO();
        this.matchingService = new MatchingService();
        this.notificationService = new NotificationService();
        this.contractService = new ContractService();
    }

    public ProposalService(ProposalDAO proposalDAO, ProjectDAO projectDAO,
                           FreelancerProfileDAO freelancerProfileDAO, ClientProfileDAO clientProfileDAO,
                           UserDAO userDAO, MatchingService matchingService,
                           NotificationService notificationService) {
        this.proposalDAO = proposalDAO;
        this.projectDAO = projectDAO;
        this.freelancerProfileDAO = freelancerProfileDAO;
        this.clientProfileDAO = clientProfileDAO;
        this.userDAO = userDAO;
        this.matchingService = matchingService;
        this.notificationService = notificationService;
        this.contractService = new ContractService();
    }

    /**
     * Submits a proposal for a project. Enforces positive amounts, open status,
     * calculates 6-factor deterministic AI score, and prevents duplicate submissions.
     */
    public Proposal submitProposal(String projectId, String freelancerUserId,
                                   double bidAmount, int deliveryDays, String coverLetter) {
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException("Project ID is required.");
        }
        if (freelancerUserId == null || freelancerUserId.isBlank()) {
            throw new IllegalArgumentException("Freelancer User ID is required.");
        }
        if (bidAmount <= 0) {
            throw new IllegalArgumentException("Bid amount must be greater than zero.");
        }
        if (deliveryDays <= 0) {
            throw new IllegalArgumentException("Estimated delivery days must be greater than zero.");
        }
        if (coverLetter == null || coverLetter.trim().isEmpty()) {
            throw new IllegalArgumentException("Cover letter cannot be empty.");
        }

        Project project = projectDAO.findById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }
        if (project.getStatus() != Project.Status.OPEN) {
            throw new IllegalStateException("Cannot submit proposal. Project status is " + project.getStatus());
        }

        FreelancerProfile fp = freelancerProfileDAO.findByUserId(freelancerUserId);
        if (fp == null) {
            throw new IllegalArgumentException("Freelancer profile not found for user: " + freelancerUserId);
        }

        // Duplicate proposal check
        if (hasAlreadySubmitted(projectId, freelancerUserId)) {
            throw new IllegalStateException("You have already submitted a proposal for this project.");
        }

        // Calculate deterministic AI match score
        int aiScore = matchingService.calculateMatchScore(fp, project);

        String propId = "prop_" + UUID.randomUUID().toString().substring(0, 8);
        Proposal proposal = new Proposal();
        proposal.setId(propId);
        proposal.setProjectId(projectId);
        proposal.setFreelancerId(fp.getId());
        proposal.setBidAmount(bidAmount);
        proposal.setDeliveryDays(deliveryDays);
        proposal.setCoverLetter(coverLetter.trim());
        proposal.setStatus(Proposal.Status.SUBMITTED);
        proposal.setAiMatchScore(aiScore);

        boolean created = proposalDAO.create(proposal);
        if (!created) {
            throw new RuntimeException("Failed to persist proposal to database.");
        }

        // Fetch populated proposal with joins
        Proposal populated = proposalDAO.findById(propId);
        if (populated == null) {
            populated = proposal;
        }

        // Sync into memory for legacy callers
        DatabaseManager.getInstance().getProposals().put(propId, populated);

        // Notify client
        ClientProfile clientProfile = clientProfileDAO.findById(project.getClientId());
        String clientUserId = clientProfile != null ? clientProfile.getUserId() : project.getClientId();
        User freelancerUser = userDAO.findById(freelancerUserId);
        String fName = freelancerUser != null ? freelancerUser.getUsername() : "A freelancer";

        notificationService.sendNotification(clientUserId, "🎯 New Proposal Received",
                fName + " submitted a bid of $" + String.format("%.0f", bidAmount)
                + " (" + aiScore + "% AI Match) for '" + project.getTitle() + "'");

        DatabaseManager.getInstance().logActivity("Freelancer " + fName + " submitted proposal for '" + project.getTitle() + "'");
        return populated;
    }

    public boolean hasAlreadySubmitted(String projectId, String freelancerUserId) {
        FreelancerProfile fp = freelancerProfileDAO.findByUserId(freelancerUserId);
        if (fp == null) return false;
        return proposalDAO.findByProjectAndFreelancer(projectId, fp.getId()) != null;
    }

    public Proposal getProposalById(String proposalId) {
        return proposalDAO.findById(proposalId);
    }

    public List<Proposal> getProposalsForProject(String projectId) {
        return proposalDAO.findByProjectId(projectId);
    }

    public List<Proposal> getProposalsForFreelancer(String freelancerUserId) {
        FreelancerProfile fp = freelancerProfileDAO.findByUserId(freelancerUserId);
        if (fp == null) return new ArrayList<>();
        return proposalDAO.findByFreelancerProfileId(fp.getId());
    }

    public List<Proposal> getProposalsForClient(String clientUserId) {
        ClientProfile cp = clientProfileDAO.findByUserId(clientUserId);
        if (cp == null) return new ArrayList<>();
        return proposalDAO.findByClientProfileId(cp.getId());
    }

    /**
     * Shortlists a proposal.
     */
    public boolean shortlistProposal(String proposalId, String clientUserId) {
        Proposal proposal = proposalDAO.findById(proposalId);
        if (proposal == null) {
            throw new IllegalArgumentException("Proposal not found: " + proposalId);
        }

        validateClientOwnership(proposal, clientUserId);

        boolean updated = proposalDAO.updateStatus(proposalId, Proposal.Status.SHORTLISTED);
        if (updated) {
            proposal.setStatus(Proposal.Status.SHORTLISTED);
            DatabaseManager.getInstance().getProposals().put(proposalId, proposal);

            if (proposal.getFreelancerUserId() != null) {
                notificationService.sendNotification(proposal.getFreelancerUserId(), "⭐ Proposal Shortlisted!",
                        "Your proposal for '" + proposal.getProjectTitle() + "' has been shortlisted by the client.");
            }
        }
        return updated;
    }

    /**
     * Declines/rejects a proposal.
     */
    public boolean rejectProposal(String proposalId, String clientUserId) {
        Proposal proposal = proposalDAO.findById(proposalId);
        if (proposal == null) {
            throw new IllegalArgumentException("Proposal not found: " + proposalId);
        }

        validateClientOwnership(proposal, clientUserId);

        boolean updated = proposalDAO.updateStatus(proposalId, Proposal.Status.REJECTED);
        if (updated) {
            proposal.setStatus(Proposal.Status.REJECTED);
            DatabaseManager.getInstance().getProposals().put(proposalId, proposal);

            if (proposal.getFreelancerUserId() != null) {
                notificationService.sendNotification(proposal.getFreelancerUserId(), "Proposal Status Update",
                        "The client did not select your proposal for '" + proposal.getProjectTitle() + "'. Keep applying!");
            }
        }
        return updated;
    }

    /**
     * Accepts a proposal, awarding the project and initializing the contract and milestones atomically.
     */
    public boolean acceptProposal(String proposalId, String clientUserId) {
        Contract contract = contractService.hireFreelancer(proposalId, clientUserId, null);
        return contract != null;
    }

    /**
     * Withdraws a proposal by the submitting freelancer.
     */
    public boolean withdrawProposal(String proposalId, String freelancerUserId) {
        Proposal proposal = proposalDAO.findById(proposalId);
        if (proposal == null) {
            throw new IllegalArgumentException("Proposal not found: " + proposalId);
        }

        FreelancerProfile fp = freelancerProfileDAO.findByUserId(freelancerUserId);
        if (fp == null || !fp.getId().equals(proposal.getFreelancerId())) {
            throw new SecurityException("Unauthorized: You can only withdraw your own proposals.");
        }

        if (proposal.getStatus() == Proposal.Status.ACCEPTED) {
            throw new IllegalStateException("Cannot withdraw an accepted proposal.");
        }

        boolean updated = proposalDAO.updateStatus(proposalId, Proposal.Status.WITHDRAWN);
        if (updated) {
            proposal.setStatus(Proposal.Status.WITHDRAWN);
            DatabaseManager.getInstance().getProposals().put(proposalId, proposal);
        }
        return updated;
    }

    private void validateClientOwnership(Proposal proposal, String clientUserId) {
        ClientProfile cp = clientProfileDAO.findByUserId(clientUserId);
        if (cp == null || !cp.getId().equals(proposal.getClientId())) {
            throw new SecurityException("Unauthorized: You do not own the project for this proposal.");
        }
    }
}

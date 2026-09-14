package com.freelancing.service.company;

import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.dao.company.ContractDAO;
import com.freelancing.dao.company.DeliverableDAO;
import com.freelancing.dao.company.MilestoneDAO;
import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.company.Contract;
import com.freelancing.model.company.Deliverable;
import com.freelancing.model.company.Milestone;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.service.common.NotificationService;
import com.freelancing.service.common.PaymentService;

import com.freelancing.db.DatabaseManager;
import com.freelancing.util.LoggingUtil;
import com.freelancing.util.StorageManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing Milestone lifecycles and Deliverables submissions.
 * Status flow: PENDING -> IN_PROGRESS -> SUBMITTED -> REVISION_REQUESTED -> APPROVED -> PAID
 */
public class MilestoneService {

    private static final String TAG = "MilestoneService";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MilestoneDAO milestoneDAO;
    private final DeliverableDAO deliverableDAO;
    private final ContractDAO contractDAO;
    private final ProjectDAO projectDAO;
    private final FreelancerProfileDAO freelancerProfileDAO;
    private final ClientProfileDAO clientProfileDAO;
    private final NotificationService notificationService;
    private final PaymentService paymentService;

    public MilestoneService() {
        this.milestoneDAO = new MilestoneDAO();
        this.deliverableDAO = new DeliverableDAO();
        this.contractDAO = new ContractDAO();
        this.projectDAO = new ProjectDAO();
        this.freelancerProfileDAO = new FreelancerProfileDAO();
        this.clientProfileDAO = new ClientProfileDAO();
        this.notificationService = new NotificationService();
        this.paymentService = new PaymentService();
    }

    public MilestoneService(MilestoneDAO milestoneDAO, DeliverableDAO deliverableDAO,
                            ContractDAO contractDAO, ProjectDAO projectDAO,
                            FreelancerProfileDAO freelancerProfileDAO, ClientProfileDAO clientProfileDAO,
                            NotificationService notificationService) {
        this.milestoneDAO = milestoneDAO;
        this.deliverableDAO = deliverableDAO;
        this.contractDAO = contractDAO;
        this.projectDAO = projectDAO;
        this.freelancerProfileDAO = freelancerProfileDAO;
        this.clientProfileDAO = clientProfileDAO;
        this.notificationService = notificationService;
        this.paymentService = new PaymentService();
    }

    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    public List<Milestone> getMilestonesByContract(String contractId) {
        List<Milestone> milestones = milestoneDAO.findByContractId(contractId);
        for (Milestone m : milestones) {
            Deliverable latest = deliverableDAO.findLatestByMilestoneId(m.getId());
            m.setLatestDeliverable(latest);
            if (latest != null) {
                m.setDeliverableFile(latest.getFilePath());
                m.setDeliverableNotes(latest.getDescription());
            }
        }
        return milestones;
    }

    public List<Milestone> getMilestonesByProject(String projectId) {
        List<Milestone> milestones = milestoneDAO.findByProjectId(projectId);
        for (Milestone m : milestones) {
            Deliverable latest = deliverableDAO.findLatestByMilestoneId(m.getId());
            m.setLatestDeliverable(latest);
            if (latest != null) {
                m.setDeliverableFile(latest.getFilePath());
                m.setDeliverableNotes(latest.getDescription());
            }
        }
        return milestones;
    }

    public Milestone getMilestoneById(String milestoneId) {
        Milestone m = milestoneDAO.findById(milestoneId);
        if (m != null) {
            Deliverable latest = deliverableDAO.findLatestByMilestoneId(m.getId());
            m.setLatestDeliverable(latest);
            if (latest != null) {
                m.setDeliverableFile(latest.getFilePath());
                m.setDeliverableNotes(latest.getDescription());
            }
        }
        return m;
    }

    public boolean startMilestone(String milestoneId, String freelancerUserId) {
        Milestone m = milestoneDAO.findById(milestoneId);
        if (m == null) {
            throw new IllegalArgumentException("Milestone not found: " + milestoneId);
        }

        Contract c = contractDAO.findById(m.getContractId());
        if (c == null) {
            throw new IllegalArgumentException("Contract not found for milestone: " + m.getContractId());
        }

        FreelancerProfile fp = freelancerProfileDAO.findByUserId(freelancerUserId);
        if (fp == null || !fp.getId().equals(c.getFreelancerId())) {
            throw new SecurityException("User is not authorized to start this milestone.");
        }

        if (m.getStatus() != Milestone.Status.PENDING) {
            throw new IllegalStateException("Only PENDING milestones can be started. Current status: " + m.getStatus());
        }

        boolean updated = milestoneDAO.updateStatus(milestoneId, Milestone.Status.IN_PROGRESS);
        if (updated) {
            LoggingUtil.info(TAG, "Milestone " + milestoneId + " started by freelancer " + freelancerUserId);
        }
        return updated;
    }

    public Deliverable submitDeliverable(String milestoneId, String freelancerUserId,
                                         String title, String description, File file) throws IOException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Deliverable title is required.");
        }

        Milestone m = milestoneDAO.findById(milestoneId);
        if (m == null) {
            throw new IllegalArgumentException("Milestone not found: " + milestoneId);
        }

        Contract c = contractDAO.findById(m.getContractId());
        if (c == null) {
            throw new IllegalArgumentException("Contract not found for milestone: " + m.getContractId());
        }

        FreelancerProfile fp = freelancerProfileDAO.findByUserId(freelancerUserId);
        if (fp == null || !fp.getId().equals(c.getFreelancerId())) {
            throw new SecurityException("Only the assigned freelancer can submit deliverables for this milestone.");
        }

        if (m.getStatus() != Milestone.Status.IN_PROGRESS && m.getStatus() != Milestone.Status.REVISION_REQUESTED) {
            throw new IllegalStateException("Deliverables can only be submitted for IN_PROGRESS or REVISION_REQUESTED milestones.");
        }

        String deliverableId = "del_" + UUID.randomUUID().toString().substring(0, 8);
        String savedFilePath = null;
        if (file != null && file.exists()) {
            savedFilePath = StorageManager.saveDeliverableFile(deliverableId, file);
        }

        String now = LocalDateTime.now().format(FORMATTER);
        Deliverable deliverable = new Deliverable();
        deliverable.setId(deliverableId);
        deliverable.setMilestoneId(milestoneId);
        deliverable.setFreelancerId(fp.getId());
        deliverable.setTitle(title.trim());
        deliverable.setDescription(description != null ? description.trim() : "");
        deliverable.setFilePath(savedFilePath);
        deliverable.setStatus(Deliverable.Status.SUBMITTED);
        deliverable.setSubmittedAt(now);

        deliverableDAO.create(deliverable);
        milestoneDAO.updateStatus(milestoneId, Milestone.Status.SUBMITTED);

        // Keep legacy in-memory cache in sync
        m.setStatus(Milestone.Status.SUBMITTED);
        m.setDeliverableFile(savedFilePath);
        m.setDeliverableNotes(description);
        DatabaseManager.getInstance().getMilestones().put(m.getId(), m);

        // Notify client
        ClientProfile cp = clientProfileDAO.findById(c.getClientId());
        if (cp != null && cp.getUserId() != null) {
            notificationService.sendNotification(cp.getUserId(), "📦 Deliverable Submitted",
                    "Freelancer submitted deliverable '" + title.trim() + "' for milestone '" + m.getTitle() + "'. Please review.");
        }

        DatabaseManager.getInstance().logActivity("Deliverable " + deliverableId + " submitted for milestone " + m.getTitle());
        LoggingUtil.info(TAG, "Deliverable " + deliverableId + " submitted for milestone " + milestoneId);
        return deliverable;
    }

    public boolean requestRevision(String milestoneId, String clientUserId, String feedback) {
        Milestone m = milestoneDAO.findById(milestoneId);
        if (m == null) {
            throw new IllegalArgumentException("Milestone not found: " + milestoneId);
        }

        Contract c = contractDAO.findById(m.getContractId());
        if (c == null) {
            throw new IllegalArgumentException("Contract not found for milestone: " + m.getContractId());
        }

        ClientProfile cp = clientProfileDAO.findByUserId(clientUserId);
        if (cp == null || !cp.getId().equals(c.getClientId())) {
            throw new SecurityException("Only the project client can request revisions.");
        }

        if (m.getStatus() != Milestone.Status.SUBMITTED) {
            throw new IllegalStateException("Revisions can only be requested for SUBMITTED milestones.");
        }

        milestoneDAO.updateStatus(milestoneId, Milestone.Status.REVISION_REQUESTED);

        Deliverable latest = deliverableDAO.findLatestByMilestoneId(milestoneId);
        if (latest != null) {
            deliverableDAO.updateStatus(latest.getId(), Deliverable.Status.REVISION_REQUESTED);
        }

        // Notify freelancer
        FreelancerProfile fp = freelancerProfileDAO.findById(c.getFreelancerId());
        if (fp != null && fp.getUserId() != null) {
            String note = (feedback != null && !feedback.trim().isEmpty()) ? ": " + feedback.trim() : ".";
            notificationService.sendNotification(fp.getUserId(), "🔄 Revision Requested",
                    "Client requested revisions for milestone '" + m.getTitle() + "'" + note);
        }

        DatabaseManager.getInstance().logActivity("Revision requested for milestone " + m.getTitle());
        return true;
    }

    public boolean approveMilestone(String milestoneId, String clientUserId) {
        Milestone m = milestoneDAO.findById(milestoneId);
        if (m == null) {
            throw new IllegalArgumentException("Milestone not found: " + milestoneId);
        }

        Contract c = contractDAO.findById(m.getContractId());
        if (c == null) {
            throw new IllegalArgumentException("Contract not found for milestone: " + m.getContractId());
        }

        ClientProfile cp = clientProfileDAO.findByUserId(clientUserId);
        if (cp == null || !cp.getId().equals(c.getClientId())) {
            throw new SecurityException("Only the project client can approve milestones.");
        }

        if (m.getStatus() != Milestone.Status.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED milestones can be approved.");
        }

        boolean updated = milestoneDAO.updateStatus(milestoneId, Milestone.Status.APPROVED);
        if (updated) {
            Deliverable latest = deliverableDAO.findLatestByMilestoneId(milestoneId);
            if (latest != null) {
                deliverableDAO.updateStatus(latest.getId(), Deliverable.Status.APPROVED);
            }

            // Automatically advance next sequential milestone from PENDING to IN_PROGRESS
            List<Milestone> allMilestones = milestoneDAO.findByContractId(c.getId());
            for (Milestone nextM : allMilestones) {
                if (nextM.getSequenceOrder() == m.getSequenceOrder() + 1 && nextM.getStatus() == Milestone.Status.PENDING) {
                    milestoneDAO.updateStatus(nextM.getId(), Milestone.Status.IN_PROGRESS);
                    LoggingUtil.info(TAG, "Advanced next milestone " + nextM.getId() + " to IN_PROGRESS");
                    break;
                }
            }

            // Notify freelancer
            FreelancerProfile fp = freelancerProfileDAO.findById(c.getFreelancerId());
            if (fp != null && fp.getUserId() != null) {
                notificationService.sendNotification(fp.getUserId(), "✅ Milestone Approved!",
                        "Congratulations! Your work for milestone '" + m.getTitle() + "' ($" + m.getAmount() + ") was approved by the client.");
            }

            DatabaseManager.getInstance().logActivity("Milestone " + m.getTitle() + " approved for contract " + c.getId());
        }
        return updated;
    }

    public boolean payMilestone(String milestoneId, String clientUserId) {
        PaymentService.PaymentReceipt receipt = paymentService.processMilestonePayment(milestoneId, clientUserId);
        return receipt != null;
    }
}

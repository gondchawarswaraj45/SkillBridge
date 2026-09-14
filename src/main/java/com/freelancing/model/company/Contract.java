package com.freelancing.model.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a contract between client and freelancer for an awarded project.
 * Mapped to SQLite table 'contracts'.
 */
public class Contract implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        DRAFT, PENDING_CLIENT, PENDING_FREELANCER, ACTIVE, COMPLETED, CANCELLED, DISPUTED
    }

    private String id;
    private String projectId;
    private String proposalId;
    private String clientId;
    private String freelancerId;
    private double totalAmount;
    private double escrowBalance;
    private Status status = Status.ACTIVE;
    private String startDate;
    private String endDate;
    private boolean clientSigned;
    private boolean freelancerSigned;
    private String createdAt;

    // Rich display & joined metadata
    private String clientName;
    private String freelancerName;
    private String projectTitle;
    private String clientSignedAt;
    private String freelancerSignedAt;
    private String updatedAt;
    private String paymentTerms = "Milestone-based escrow release upon client approval.";
    private String cancellationTerms = "SkillBridge Escrow Protection and Mediation policy applies.";
    private List<Milestone> milestones = new ArrayList<>();
    private List<String> deliverables = new ArrayList<>();

    public Contract() {
        this.status = Status.ACTIVE;
        this.clientSigned = false;
        this.freelancerSigned = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getProposalId() { return proposalId; }
    public void setProposalId(String proposalId) { this.proposalId = proposalId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getFreelancerId() { return freelancerId; }
    public void setFreelancerId(String freelancerId) { this.freelancerId = freelancerId; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getPrice() { return totalAmount; }
    public void setPrice(double price) { this.totalAmount = price; }

    public double getEscrowBalance() { return escrowBalance; }
    public void setEscrowBalance(double escrowBalance) { this.escrowBalance = escrowBalance; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public boolean isClientSigned() { return clientSigned; }
    public void setClientSigned(boolean clientSigned) { this.clientSigned = clientSigned; }

    public boolean isFreelancerSigned() { return freelancerSigned; }
    public void setFreelancerSigned(boolean freelancerSigned) { this.freelancerSigned = freelancerSigned; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getFreelancerName() { return freelancerName; }
    public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public String getClientSignedAt() { return clientSignedAt; }
    public void setClientSignedAt(String clientSignedAt) { this.clientSignedAt = clientSignedAt; }

    public String getFreelancerSignedAt() { return freelancerSignedAt; }
    public void setFreelancerSignedAt(String freelancerSignedAt) { this.freelancerSignedAt = freelancerSignedAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getCancellationTerms() { return cancellationTerms; }
    public void setCancellationTerms(String cancellationTerms) { this.cancellationTerms = cancellationTerms; }

    public List<Milestone> getMilestones() { return milestones; }
    public void setMilestones(List<Milestone> milestones) { this.milestones = milestones != null ? milestones : new ArrayList<>(); }

    public List<String> getDeliverables() { return deliverables; }
    public void setDeliverables(List<String> deliverables) { this.deliverables = deliverables != null ? deliverables : new ArrayList<>(); }

    // Compatibility aliases for existing callers
    public String getCustomerId() { return clientId; }
    public void setCustomerId(String customerId) { this.clientId = customerId; }
    public String getCustomerName() { return clientName; }
    public void setCustomerName(String customerName) { this.clientName = customerName; }
    public String getVendorId() { return freelancerId; }
    public void setVendorId(String vendorId) { this.freelancerId = vendorId; }
    public String getVendorName() { return freelancerName; }
    public void setVendorName(String vendorName) { this.freelancerName = vendorName; }
    public String getServiceName() { return projectTitle; }
    public void setServiceName(String serviceName) { this.projectTitle = serviceName; }
    public boolean isCustomerSigned() { return clientSigned; }
    public void setCustomerSigned(boolean customerSigned) { this.clientSigned = customerSigned; }
    public boolean isVendorSigned() { return freelancerSigned; }
    public void setVendorSigned(boolean vendorSigned) { this.freelancerSigned = vendorSigned; }
    public String getCustomerSignedAt() { return clientSignedAt; }
    public void setCustomerSignedAt(String customerSignedAt) { this.clientSignedAt = customerSignedAt; }
    public String getVendorSignedAt() { return freelancerSignedAt; }
    public void setVendorSignedAt(String vendorSignedAt) { this.freelancerSignedAt = vendorSignedAt; }
    public String getClientUserId() { return clientId; }
    public void setClientUserId(String clientUserId) { this.clientId = clientUserId; }
    public String getFreelancerUserId() { return freelancerId; }
    public void setFreelancerUserId(String freelancerUserId) { this.freelancerId = freelancerUserId; }
}

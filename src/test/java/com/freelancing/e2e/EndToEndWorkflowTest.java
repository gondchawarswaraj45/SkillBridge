package com.freelancing.e2e;

import com.freelancing.config.SecurityConfig;
import com.freelancing.dao.common.TransactionDAO;
import com.freelancing.dao.common.UserDAO;
import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.dao.company.ContractDAO;
import com.freelancing.dao.company.MilestoneDAO;
import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.dao.company.ProjectSkillDAO;
import com.freelancing.dao.company.ProposalDAO;
import com.freelancing.dao.freelancer.CertificationDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.dao.freelancer.FreelancerSkillDAO;
import com.freelancing.dao.freelancer.PortfolioDAO;
import com.freelancing.model.common.ChatMessage;
import com.freelancing.model.common.Conversation;
import com.freelancing.model.common.Notification;
import com.freelancing.model.common.Skill;
import com.freelancing.model.common.Transaction;
import com.freelancing.model.common.User;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.company.Contract;
import com.freelancing.model.company.Deliverable;
import com.freelancing.model.company.Milestone;
import com.freelancing.model.company.Project;
import com.freelancing.model.company.Proposal;
import com.freelancing.model.company.Review;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.model.freelancer.MatchingResult;
import com.freelancing.service.common.AuthService;
import com.freelancing.service.common.ChatService;
import com.freelancing.service.common.NotificationService;
import com.freelancing.service.common.PaymentService;
import com.freelancing.service.common.SkillService;
import com.freelancing.service.company.ClientService;
import com.freelancing.service.company.ContractService;
import com.freelancing.service.company.MilestoneService;
import com.freelancing.service.company.ProjectService;
import com.freelancing.service.company.ProposalService;
import com.freelancing.service.company.ReviewService;
import com.freelancing.service.freelancer.FreelancerService;
import com.freelancing.service.freelancer.MatchingService;

import com.freelancing.db.DatabaseConnection;
import com.freelancing.db.DatabaseInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive End-to-End Workflow Test for SkillBridge.
 * Validates complete hiring lifecycle from client registration to review and rating submission.
 */
public class EndToEndWorkflowTest {

    private static AuthService authService;
    private static ClientService clientService;
    private static FreelancerService freelancerService;
    private static SkillService skillService;
    private static ProjectService projectService;
    private static MatchingService matchingService;
    private static ProposalService proposalService;
    private static ContractService contractService;
    private static MilestoneService milestoneService;
    private static PaymentService paymentService;
    private static ReviewService reviewService;
    private static NotificationService notificationService;

    private static UserDAO userDAO;
    private static ClientProfileDAO clientProfileDAO;
    private static FreelancerProfileDAO freelancerProfileDAO;
    private static ProjectDAO projectDAO;
    private static ProjectSkillDAO projectSkillDAO;
    private static ProposalDAO proposalDAO;
    private static ContractDAO contractDAO;
    private static MilestoneDAO milestoneDAO;
    private static TransactionDAO transactionDAO;

    @BeforeAll
    public static void setUp() {
        DatabaseInitializer.initialize();

        userDAO = new UserDAO();
        clientProfileDAO = new ClientProfileDAO();
        freelancerProfileDAO = new FreelancerProfileDAO();
        projectDAO = new ProjectDAO();
        projectSkillDAO = new ProjectSkillDAO();
        proposalDAO = new ProposalDAO();
        contractDAO = new ContractDAO();
        milestoneDAO = new MilestoneDAO();
        transactionDAO = new TransactionDAO();

        authService = new AuthService(userDAO);
        clientService = new ClientService(clientProfileDAO);
        freelancerService = new FreelancerService(freelancerProfileDAO, new FreelancerSkillDAO(), new PortfolioDAO(), new CertificationDAO());
        skillService = new SkillService();
        projectService = new ProjectService();
        matchingService = new MatchingService();
        proposalService = new ProposalService();
        contractService = new ContractService();
        milestoneService = new MilestoneService();
        paymentService = new PaymentService();
        reviewService = new ReviewService();
        notificationService = new NotificationService();
    }

    @Test
    @DisplayName("Complete End-to-End SkillBridge Lifecycle: Register -> Post -> Bid -> Hire -> Deliver -> Approve -> Pay -> Review")
    public void testCompleteSkillBridgeLifecycle() throws Exception {
        String uid = UUID.randomUUID().toString().substring(0, 6);

        // =====================================================================
        // STEP 1: Client Registration & Profile Setup
        // =====================================================================
        String clientUsername = "e2e_client_" + uid;
        String clientEmail = "client_" + uid + "@skillbridge.test";
        User clientUser = authService.registerUser(clientUsername, clientEmail, "555-0101", "ClientPass123!", User.Role.CLIENT);
        assertNotNull(clientUser, "Client should be registered successfully");
        assertEquals(User.Role.CLIENT, clientUser.getRole());

        // Verify password is hashed with salt (not stored plaintext)
        User clientInDb = userDAO.findById(clientUser.getId());
        assertNotNull(clientInDb);
        assertNotEquals("ClientPass123!", clientInDb.getPassword(), "Password must be salted/hashed");
        assertTrue(SecurityConfig.verifyPassword("ClientPass123!", clientInDb.getPassword()), "Hash must verify with SecurityConfig");
        assertEquals(64, clientInDb.getPassword().length(), "Salted SHA-256 hash must be 64 characters");

        // Verify Client profile auto-creation & update
        ClientProfile clientProfile = clientProfileDAO.findByUserId(clientUser.getId());
        assertNotNull(clientProfile, "Client profile record should be auto-created");
        clientProfile.setCompanyName("Apex Solutions " + uid);
        clientProfile.setIndustry("FinTech");
        clientProfile.setAbout("Pioneering financial technology solutions.");
        boolean clientProfileUpdated = clientService.updateProfile(clientProfile);
        assertTrue(clientProfileUpdated, "Client profile update should succeed");

        // =====================================================================
        // STEP 2: Freelancer Registration & Skill Catalog Setup
        // =====================================================================
        String freelancerUsername = "e2e_free_" + uid;
        String freelancerEmail = "free_" + uid + "@skillbridge.test";
        User freelancerUser = authService.registerUser(freelancerUsername, freelancerEmail, "555-0202", "FreePass123!", User.Role.FREELANCER);
        assertNotNull(freelancerUser, "Freelancer should be registered successfully");
        assertEquals(User.Role.FREELANCER, freelancerUser.getRole());

        // Freelancer Profile & Skills
        FreelancerProfile freelancerProfile = freelancerProfileDAO.findByUserId(freelancerUser.getId());
        assertNotNull(freelancerProfile, "Freelancer profile record should be auto-created");
        freelancerProfile.setTitle("Senior Full-Stack Architect");
        freelancerProfile.setBio("10+ years in Java, Spring Boot, React, and cloud native architectures.");
        freelancerProfile.setHourlyRate(85.0);
        freelancerProfile.setExperienceYears(8);
        freelancerProfile.setAvailability("AVAILABLE");
        boolean fpUpdated = freelancerService.updateProfile(freelancerProfile);
        assertTrue(fpUpdated, "Freelancer profile update should succeed");

        // Tag skills
        Skill skillJava = skillService.getOrCreateSkill("Java", "Backend");
        Skill skillReact = skillService.getOrCreateSkill("React", "Frontend");
        Skill skillSQLite = skillService.getOrCreateSkill("SQLite", "Database");
        assertNotNull(skillJava);
        assertNotNull(skillReact);
        assertNotNull(skillSQLite);

        freelancerService.addSkill(freelancerProfile.getId(), skillJava.getId(), "EXPERT");
        freelancerService.addSkill(freelancerProfile.getId(), skillReact.getId(), "EXPERT");
        freelancerService.addSkill(freelancerProfile.getId(), skillSQLite.getId(), "INTERMEDIATE");

        // =====================================================================
        // STEP 3: Client Posts New Project with Required Skills
        // =====================================================================
        String projectTitle = "Enterprise Cloud Portal " + uid;
        String projectDesc = "Build a high-throughput enterprise portal using Java backend and React UI.";
        Project projectToCreate = new Project();
        projectToCreate.setClientId(clientUser.getId());
        projectToCreate.setTitle(projectTitle);
        projectToCreate.setDescription(projectDesc);
        projectToCreate.setCategory("Development");
        projectToCreate.setBudgetType("FIXED");
        projectToCreate.setBudgetMin(4000.0);
        projectToCreate.setBudgetMax(5000.0);
        projectToCreate.setDeadline("2026-11-30");
        projectToCreate.setExperienceLevel("INTERMEDIATE");

        Project project = projectService.createProject(projectToCreate, List.of("Java", "React"));
        assertNotNull(project, "Project should be created successfully in SQLite");
        assertNotNull(project.getId());
        assertEquals(Project.Status.OPEN, project.getStatus());

        // Verify project in DB
        Project projectInDb = projectDAO.findById(project.getId());
        assertNotNull(projectInDb);
        assertEquals(projectTitle, projectInDb.getTitle());
        assertEquals("FIXED", projectInDb.getBudgetType());

        // Verify project skills join table
        List<String> projectSkills = projectSkillDAO.findSkillNamesByProjectId(project.getId());
        assertTrue(projectSkills.contains("Java"));
        assertTrue(projectSkills.contains("React"));

        // Verify client posted projects count in SQLite
        ClientProfile cpAfterPost = clientProfileDAO.findByUserId(clientUser.getId());
        assertTrue(cpAfterPost.getPostedProjects() >= 1, "Posted projects count should be incremented");

        // =====================================================================
        // STEP 4: Freelancer Searches Projects & Computes AI Match
        // =====================================================================
        List<Project> searchResults = projectService.searchProjects(
                "Enterprise",
                "Development",
                "Java",
                3000.0,
                6000.0,
                "INTERMEDIATE",
                "OPEN",
                "NEWEST"
        );
        assertFalse(searchResults.isEmpty(), "Search should find the matching project");
        boolean foundCreated = searchResults.stream().anyMatch(p -> p.getId().equals(project.getId()));
        assertTrue(foundCreated, "Newly created project must appear in search results");

        // Compute AI Match Score with hydrated skills
        freelancerProfile = freelancerService.getProfile(freelancerUser.getId());
        assertNotNull(freelancerProfile);
        assertFalse(freelancerProfile.getSkills().isEmpty(), "Freelancer skills must be hydrated from SQLite");

        MatchingResult matchResult = matchingService.calculateDetailedMatch(freelancerProfile, project);
        assertNotNull(matchResult);
        assertTrue(matchResult.getOverallScore() >= 60, "Match score should be >= 60% due to matching skills (was: " + matchResult.getOverallScore() + "%)");
        assertTrue(matchResult.getMatchedSkills().contains("Java"));
        assertTrue(matchResult.getMatchedSkills().contains("React"));

        // =====================================================================
        // STEP 5: Freelancer Submits Proposal
        // =====================================================================
        double bidAmount = 4500.0;
        int deliveryDays = 30;
        String coverLetter = "I have extensive experience building scalable Java and React portals.";
        Proposal proposal = proposalService.submitProposal(project.getId(), freelancerUser.getId(), bidAmount, deliveryDays, coverLetter);
        assertNotNull(proposal, "Proposal submission should succeed");
        assertEquals(Proposal.Status.SUBMITTED, proposal.getStatus());
        assertEquals(bidAmount, proposal.getBidAmount());

        // Duplicate submission prevention
        assertThrows(IllegalStateException.class, () -> {
            proposalService.submitProposal(project.getId(), freelancerUser.getId(), bidAmount, deliveryDays, coverLetter);
        }, "Duplicate proposal submission must throw IllegalStateException");

        // Verify Client received in-app notification
        List<Notification> clientNotifs = notificationService.getUserNotifications(clientUser.getId());
        assertFalse(clientNotifs.isEmpty(), "Client should receive proposal notification");
        assertTrue(clientNotifs.stream().anyMatch(n -> n.getMessage().contains(freelancerUsername) || n.getMessage().contains("Enterprise Cloud Portal")));

        // Client shortlists proposal
        boolean shortlisted = proposalService.shortlistProposal(proposal.getId(), clientUser.getId());
        assertTrue(shortlisted, "Client should be able to shortlist proposal");
        Proposal shortlistedProp = proposalDAO.findById(proposal.getId());
        assertEquals(Proposal.Status.SHORTLISTED, shortlistedProp.getStatus());

        // =====================================================================
        // STEP 6: Client Accepts Proposal & Atomic Hiring Transaction Executes
        // =====================================================================
        Contract contract = contractService.hireFreelancer(proposal.getId(), clientUser.getId(), null);
        assertNotNull(contract, "Hiring transaction must succeed and return active contract");
        assertEquals(Contract.Status.ACTIVE, contract.getStatus());
        assertEquals(bidAmount, contract.getTotalAmount(), 0.01);
        assertEquals(bidAmount, contract.getEscrowBalance(), 0.01, "Initial escrow balance must equal contract amount");

        // Verify SQLite Project status transitioned to IN_PROGRESS
        Project activeProj = projectDAO.findById(project.getId());
        assertEquals(Project.Status.IN_PROGRESS, activeProj.getStatus());
        assertEquals(freelancerProfile.getId(), activeProj.getAwardedFreelancerId());

        // Verify Proposal status transitioned to ACCEPTED
        Proposal acceptedProp = proposalDAO.findById(proposal.getId());
        assertEquals(Proposal.Status.ACCEPTED, acceptedProp.getStatus());

        // Verify Milestones created atomically in SQLite
        List<Milestone> milestones = milestoneDAO.findByContractId(contract.getId());
        assertFalse(milestones.isEmpty(), "Sprint milestones should be created");
        double totalMilestoneSum = milestones.stream().mapToDouble(Milestone::getAmount).sum();
        assertEquals(bidAmount, totalMilestoneSum, 0.01, "Milestones sum must match contract total");

        // First milestone should be IN_PROGRESS, others PENDING
        Milestone m1 = milestones.get(0);
        assertEquals(Milestone.Status.IN_PROGRESS, m1.getStatus());

        // Verify escrow deposit transaction in SQLite transactions table
        List<Transaction> txList = transactionDAO.findByContractId(contract.getId());
        assertFalse(txList.isEmpty(), "Escrow deposit transaction must be recorded");
        assertTrue(txList.stream().anyMatch(t -> t.getType() == Transaction.Type.ESCROW_DEPOSIT));

        // =====================================================================
        // STEP 7: Freelancer Submits Deliverable for Milestone 1
        // =====================================================================
        Deliverable del1 = milestoneService.submitDeliverable(
                m1.getId(),
                freelancerUser.getId(),
                "Sprint 1 Core Architecture",
                "Delivered database models, initial endpoints, and React shells.",
                null
        );
        assertNotNull(del1, "Deliverable submission should succeed");
        assertEquals(Deliverable.Status.SUBMITTED, del1.getStatus());

        Milestone m1Submitted = milestoneDAO.findById(m1.getId());
        assertEquals(Milestone.Status.SUBMITTED, m1Submitted.getStatus());

        // =====================================================================
        // STEP 8: Client Approves Milestone & Automatic Escrow Release Triggers
        // =====================================================================
        boolean m1Approved = milestoneService.approveMilestone(m1.getId(), clientUser.getId());
        assertTrue(m1Approved, "Milestone approval should succeed");

        boolean m1PaidSuccess = milestoneService.payMilestone(m1.getId(), clientUser.getId());
        assertTrue(m1PaidSuccess, "Milestone payment release should succeed");

        Milestone m1Paid = milestoneDAO.findById(m1.getId());
        assertEquals(Milestone.Status.PAID, m1Paid.getStatus());

        // Verify escrow payment release in SQLite transactions
        List<Transaction> txAfterM1 = transactionDAO.findByContractId(contract.getId());
        assertTrue(txAfterM1.stream().anyMatch(t -> t.getType() == Transaction.Type.PAYMENT || t.getType() == Transaction.Type.EARNING),
                "Escrow release transaction must be committed");

        // Contract escrow balance must be deducted
        Contract contractAfterM1 = contractDAO.findById(contract.getId());
        assertEquals(contract.getTotalAmount() - m1.getAmount(), contractAfterM1.getEscrowBalance(), 0.01);

        // =====================================================================
        // STEP 9: Complete Remaining Milestones to Finalize Contract
        // =====================================================================
        List<Milestone> remainingMilestones = milestoneDAO.findByContractId(contract.getId());
        for (int i = 1; i < remainingMilestones.size(); i++) {
            Milestone m = remainingMilestones.get(i);
            milestoneService.submitDeliverable(
                    m.getId(),
                    freelancerUser.getId(),
                    "Sprint " + (i + 1) + " Deliverable",
                    "Completed specification and full test coverage.",
                    null
            );
            milestoneService.approveMilestone(m.getId(), clientUser.getId());
            milestoneService.payMilestone(m.getId(), clientUser.getId());
        }

        // Verify contract is now COMPLETED
        Contract finalContract = contractDAO.findById(contract.getId());
        assertEquals(Contract.Status.COMPLETED, finalContract.getStatus());
        assertEquals(0.0, finalContract.getEscrowBalance(), 0.01, "All escrow funds must be disbursed");

        // Verify project is COMPLETED
        Project finalProject = projectDAO.findById(project.getId());
        assertEquals(Project.Status.COMPLETED, finalProject.getStatus());

        // Verify freelancer completed projects metric incremented
        FreelancerProfile fpAfterComplete = freelancerProfileDAO.findByUserId(freelancerUser.getId());
        assertTrue(fpAfterComplete.getCompletedProjects() >= 1, "Completed projects count should be incremented");

        // =====================================================================
        // STEP 10: Client Submits Review & Rating for Freelancer
        // =====================================================================
        Review review = reviewService.submitReview(
                clientUser.getId(),
                freelancerUser.getId(),
                project.getId(),
                5.0,
                "Exemplary architecture, rapid sprint deliverables, and clean Java code!"
        );
        assertNotNull(review, "Review submission should succeed");
        assertEquals(5.0, review.getRating());

        // Verify review in SQLite reviews table
        List<Review> freelancerReviews = reviewService.getReviewsForUser(freelancerUser.getId());
        assertFalse(freelancerReviews.isEmpty(), "Freelancer must have reviews in database");
        assertEquals(5.0, freelancerReviews.get(0).getRating());

        // Verify freelancer profile rating and total review count updated in SQLite
        FreelancerProfile fpAfterReview = freelancerProfileDAO.findByUserId(freelancerUser.getId());
        assertEquals(5.0, fpAfterReview.getRating(), 0.01);
        assertEquals(1, fpAfterReview.getTotalReviews());

        // =====================================================================
        // STEP 11: Freelancer Financial Ledger & Withdrawal Simulation
        // =====================================================================
        PaymentService.FreelancerFinancialSummary finSummary = paymentService.getFinancialSummaryForFreelancer(freelancerUser.getId());
        assertTrue(finSummary.totalEarned > 0, "Freelancer should have earned revenue from completed milestones");
        assertTrue(finSummary.availableBalance > 0, "Available balance should be positive");

        double withdrawAmount = Math.min(1000.0, finSummary.availableBalance);
        Transaction withdrawalTx = paymentService.withdrawEarnings(
                freelancerUser.getId(),
                withdrawAmount,
                "BANK_TRANSFER",
                "ACC-123456"
        );
        assertNotNull(withdrawalTx, "Withdrawal transaction must succeed");
        assertEquals(Transaction.Type.WITHDRAWAL, withdrawalTx.getType());

        // Verify withdrawal recorded in SQLite transactions
        List<Transaction> freeTx = transactionDAO.findByUserId(freelancerUser.getId());
        assertTrue(freeTx.stream().anyMatch(t -> t.getType() == Transaction.Type.WITHDRAWAL));

        // Available balance is now deducted
        PaymentService.FreelancerFinancialSummary afterWithdraw = paymentService.getFinancialSummaryForFreelancer(freelancerUser.getId());
        assertEquals(finSummary.availableBalance - withdrawAmount, afterWithdraw.availableBalance, 0.01);

        // =====================================================================
        // STEP 12: Direct Messaging & Communication Between Hired Parties
        // =====================================================================
        ChatService chatService = new ChatService();
        Conversation conv = chatService.getOrCreateConversation(clientUser.getId(), freelancerUser.getId(), project.getId());
        assertNotNull(conv, "Conversation should be created with project context");

        ChatMessage msg1 = chatService.sendMessage(conv.getId(), clientUser.getId(), "Welcome to the team! Looking forward to working together.", null);
        assertNotNull(msg1);

        ChatMessage msg2 = chatService.sendMessage(conv.getId(), freelancerUser.getId(), "Thank you! Development has begun on Sprint 1.", null);
        assertNotNull(msg2);

        List<ChatMessage> conversationHistory = chatService.getConversationMessages(conv.getId(), clientUser.getId());
        assertTrue(conversationHistory.size() >= 2, "Conversation history must persist messages in SQLite");

        // Verify all 27 tables remain relational and intact
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT count(*) FROM sqlite_master WHERE type='table';");
             ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next());
            int tableCount = rs.getInt(1);
            assertTrue(tableCount >= 27, "All 27 SQLite tables must remain active");
        } catch (Exception e) {
            fail("Database integrity check failed: " + e.getMessage());
        }
    }
}

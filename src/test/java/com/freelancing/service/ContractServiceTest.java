package com.freelancing.service;

import com.freelancing.dao.common.UserDAO;
import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.dao.company.ContractDAO;
import com.freelancing.dao.company.MilestoneDAO;
import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.dao.company.ProposalDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.model.common.User;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.company.Contract;
import com.freelancing.model.company.Deliverable;
import com.freelancing.model.company.Milestone;
import com.freelancing.model.company.Project;
import com.freelancing.model.company.Proposal;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.service.company.ContractService;
import com.freelancing.service.company.MilestoneService;

import com.freelancing.config.SecurityConfig;
import com.freelancing.db.DatabaseInitializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ContractServiceTest {

    private static UserDAO userDAO;
    private static ClientProfileDAO clientProfileDAO;
    private static FreelancerProfileDAO freelancerProfileDAO;
    private static ProjectDAO projectDAO;
    private static ProposalDAO proposalDAO;
    private static ContractDAO contractDAO;
    private static MilestoneDAO milestoneDAO;

    private ContractService contractService;
    private MilestoneService milestoneService;

    private User clientUser;
    private ClientProfile clientProfile;
    private User freelancerUser;
    private FreelancerProfile freelancerProfile;
    private Project project;
    private Proposal proposal;

    public static void initDatabase() {
        DatabaseInitializer.initialize();
        userDAO = new UserDAO();
        clientProfileDAO = new ClientProfileDAO();
        freelancerProfileDAO = new FreelancerProfileDAO();
        projectDAO = new ProjectDAO();
        proposalDAO = new ProposalDAO();
        contractDAO = new ContractDAO();
        milestoneDAO = new MilestoneDAO();
    }

    public void setup() {
        contractService = new ContractService();
        milestoneService = new MilestoneService();

        String runId = UUID.randomUUID().toString().substring(0, 8);

        // 1. Create client user & profile
        clientUser = new User();
        clientUser.setId("usr_cl_" + runId);
        clientUser.setUsername("client_" + runId);
        clientUser.setEmail("client_" + runId + "@test.com");
        clientUser.setPassword(SecurityConfig.hashPassword("ClientPass123!"));
        clientUser.setRole(User.Role.CLIENT);
        userDAO.create(clientUser);

        clientProfile = new ClientProfile();
        clientProfile.setId("cl_prof_" + runId);
        clientProfile.setUserId(clientUser.getId());
        clientProfile.setCompanyName("Apex Solutions " + runId);
        clientProfileDAO.create(clientProfile);

        // 2. Create freelancer user & profile
        freelancerUser = new User();
        freelancerUser.setId("usr_fr_" + runId);
        freelancerUser.setUsername("freelancer_" + runId);
        freelancerUser.setEmail("free_" + runId + "@test.com");
        freelancerUser.setPassword(SecurityConfig.hashPassword("FreePass123!"));
        freelancerUser.setRole(User.Role.FREELANCER);
        userDAO.create(freelancerUser);

        freelancerProfile = new FreelancerProfile();
        freelancerProfile.setId("fr_prof_" + runId);
        freelancerProfile.setUserId(freelancerUser.getId());
        freelancerProfile.setTitle("Senior Java Architect");
        freelancerProfileDAO.create(freelancerProfile);

        // 3. Create open project
        project = new Project();
        project.setId("proj_" + runId);
        project.setClientId(clientProfile.getId());
        project.setTitle("High-Concurrency Microservice " + runId);
        project.setDescription("Build an event-driven system with SQLite persistence");
        project.setCategory("Backend Development");
        project.setBudgetMin(2000.0);
        project.setBudgetMax(4000.0);
        project.setBudgetType("FIXED");
        project.setDeadline("2026-10-30");
        project.setExperienceLevel("EXPERT");
        project.setStatus(Project.Status.OPEN);
        projectDAO.create(project);

        // 4. Create proposal
        proposal = new Proposal();
        proposal.setId("prop_" + runId);
        proposal.setProjectId(project.getId());
        proposal.setFreelancerId(freelancerProfile.getId());
        proposal.setBidAmount(3500.0);
        proposal.setDeliveryDays(21);
        proposal.setCoverLetter("I will build this robust microservice with atomic transaction guarantees.");
        proposal.setStatus(Proposal.Status.SUBMITTED);
        proposalDAO.create(proposal);
    }

    public void testAtomicHiringTransactionSuccess() {
        Contract contract = contractService.hireFreelancer(proposal.getId(), clientUser.getId(), null);

        assertNotNull(contract, "Contract should be generated");
        assertEquals(Contract.Status.ACTIVE, contract.getStatus());
        assertEquals(3500.0, contract.getTotalAmount(), 0.001);
        assertEquals(3500.0, contract.getEscrowBalance(), 0.001);
        assertTrue(contract.isClientSigned());
        assertTrue(contract.isFreelancerSigned());

        // Verify SQLite persistence for contract
        Contract dbContract = contractDAO.findById(contract.getId());
        assertNotNull(dbContract, "Contract should be persisted in SQLite");
        assertEquals(contract.getId(), dbContract.getId());
        assertEquals(project.getId(), dbContract.getProjectId());
        assertEquals(clientProfile.getId(), dbContract.getClientId());
        assertEquals(freelancerProfile.getId(), dbContract.getFreelancerId());

        // Verify milestones created in SQLite
        List<Milestone> milestones = milestoneDAO.findByContractId(contract.getId());
        assertEquals(3, milestones.size(), "Should create 3-tier sprint milestones by default");
        assertEquals(1, milestones.get(0).getSequenceOrder());
        assertEquals(Milestone.Status.IN_PROGRESS, milestones.get(0).getStatus(), "First milestone should be IN_PROGRESS");
        assertEquals(Milestone.Status.PENDING, milestones.get(1).getStatus(), "Subsequent milestones should be PENDING");
        assertEquals(Milestone.Status.PENDING, milestones.get(2).getStatus());

        double totalMsAmount = milestones.stream().mapToDouble(Milestone::getAmount).sum();
        assertEquals(3500.0, totalMsAmount, 0.001, "Milestone amounts sum must equal proposal bid");

        // Verify proposal updated to ACCEPTED
        Proposal dbProposal = proposalDAO.findById(proposal.getId());
        assertEquals(Proposal.Status.ACCEPTED, dbProposal.getStatus());

        // Verify project updated to IN_PROGRESS and awarded to freelancer
        Project dbProject = projectDAO.findById(project.getId());
        assertEquals(Project.Status.IN_PROGRESS, dbProject.getStatus());
        assertEquals(freelancerProfile.getId(), dbProject.getAwardedFreelancerId());
    }

    public void testAtomicHiringTransactionRollbackOnUnauthorizedClient() {
        Exception ex = assertThrows(RuntimeException.class, () -> {
            // Passing freelancerUser instead of clientUser should fail client ownership check
            contractService.hireFreelancer(proposal.getId(), freelancerUser.getId(), null);
        });
        assertTrue(ex.getMessage().contains("client authority") || ex.getCause() instanceof SecurityException);

        // Verify complete rollback: Proposal remains SUBMITTED, Project remains OPEN, 0 contracts created
        Proposal dbProp = proposalDAO.findById(proposal.getId());
        assertEquals(Proposal.Status.SUBMITTED, dbProp.getStatus());

        Project dbProj = projectDAO.findById(project.getId());
        assertEquals(Project.Status.OPEN, dbProj.getStatus());
        assertNull(dbProj.getAwardedFreelancerId());

        Contract dbContract = contractDAO.findByProposalId(proposal.getId());
        assertNull(dbContract, "No contract should be created if transaction failed");
    }

    public void testAtomicHiringTransactionRollbackOnAlreadyAcceptedProposal() {
        // First hire succeeds
        contractService.hireFreelancer(proposal.getId(), clientUser.getId(), null);

        // Second hire attempt on same proposal must fail
        assertThrows(RuntimeException.class, () -> {
            contractService.hireFreelancer(proposal.getId(), clientUser.getId(), null);
        });
    }

    public void testDeliverableSubmissionAndStorage() throws IOException {
        Contract contract = contractService.hireFreelancer(proposal.getId(), clientUser.getId(), null);
        List<Milestone> milestones = milestoneService.getMilestonesByContract(contract.getId());
        Milestone m1 = milestones.get(0);
        assertEquals(Milestone.Status.IN_PROGRESS, m1.getStatus());

        // Create dummy file for upload
        File tempFile = File.createTempFile("deliverable_test_", ".zip");
        tempFile.deleteOnExit();
        try (FileWriter fw = new FileWriter(tempFile)) {
            fw.write("SkillBridge milestone artifact binary payload content");
        }

        Deliverable deliverable = milestoneService.submitDeliverable(
                m1.getId(), freelancerUser.getId(), "Sprint 1 Architecture Codebase",
                "Delivered the SQLite schemas, DAOs, and service interfaces with 100% test coverage.", tempFile);

        assertNotNull(deliverable);
        assertEquals(Deliverable.Status.SUBMITTED, deliverable.getStatus());
        assertNotNull(deliverable.getFilePath());
        assertTrue(new File(deliverable.getFilePath()).exists(), "Deliverable file should be copied to storage/deliverables/");

        // Verify milestone status updated to SUBMITTED
        Milestone updatedM1 = milestoneService.getMilestoneById(m1.getId());
        assertEquals(Milestone.Status.SUBMITTED, updatedM1.getStatus());
        assertNotNull(updatedM1.getLatestDeliverable());
        assertEquals(deliverable.getId(), updatedM1.getLatestDeliverable().getId());
    }

    public void testMilestoneRevisionAndApprovalLifecycle() throws IOException {
        Contract contract = contractService.hireFreelancer(proposal.getId(), clientUser.getId(), null);
        List<Milestone> milestones = milestoneService.getMilestonesByContract(contract.getId());
        Milestone m1 = milestones.get(0);

        // Submit deliverable
        File tempFile = File.createTempFile("m1_v1_", ".txt");
        tempFile.deleteOnExit();
        milestoneService.submitDeliverable(m1.getId(), freelancerUser.getId(), "Draft Design", "Draft specs", tempFile);

        // Client requests revision
        boolean revReq = milestoneService.requestRevision(m1.getId(), clientUser.getId(), "Please add connection pooling");
        assertTrue(revReq);
        assertEquals(Milestone.Status.REVISION_REQUESTED, milestoneService.getMilestoneById(m1.getId()).getStatus());

        // Freelancer re-submits updated deliverable
        milestoneService.submitDeliverable(m1.getId(), freelancerUser.getId(), "Draft Design v2", "Added connection pooling", tempFile);
        assertEquals(Milestone.Status.SUBMITTED, milestoneService.getMilestoneById(m1.getId()).getStatus());

        // Client approves milestone
        boolean approved = milestoneService.approveMilestone(m1.getId(), clientUser.getId());
        assertTrue(approved);
        assertEquals(Milestone.Status.APPROVED, milestoneService.getMilestoneById(m1.getId()).getStatus());

        // Milestone 2 should automatically advance from PENDING to IN_PROGRESS!
        Milestone m2 = milestoneService.getMilestoneById(milestones.get(1).getId());
        assertEquals(Milestone.Status.IN_PROGRESS, m2.getStatus(), "Next milestone should automatically advance to IN_PROGRESS");
    }

    public void testMilestonePaymentAndContractCompletion() throws IOException {
        Contract contract = contractService.hireFreelancer(proposal.getId(), clientUser.getId(), null);
        List<Milestone> milestones = milestoneService.getMilestonesByContract(contract.getId());

        File dummyFile = File.createTempFile("dummy_", ".txt");
        dummyFile.deleteOnExit();

        // Complete all milestones in order
        for (Milestone m : milestones) {
            Milestone currentM = milestoneService.getMilestoneById(m.getId());
            if (currentM.getStatus() == Milestone.Status.PENDING) {
                milestoneService.startMilestone(m.getId(), freelancerUser.getId());
            }
            milestoneService.submitDeliverable(m.getId(), freelancerUser.getId(), "Final: " + m.getTitle(), "Done", dummyFile);
            milestoneService.approveMilestone(m.getId(), clientUser.getId());
            milestoneService.payMilestone(m.getId(), clientUser.getId());
            assertEquals(Milestone.Status.PAID, milestoneService.getMilestoneById(m.getId()).getStatus());
        }

        // When all milestones are paid, contract and project must be COMPLETED
        Contract finalContract = contractService.getContract(contract.getId());
        assertEquals(Contract.Status.COMPLETED, finalContract.getStatus(), "Contract should be COMPLETED when all milestones are paid");

        Project finalProject = projectDAO.findById(project.getId());
        assertEquals(Project.Status.COMPLETED, finalProject.getStatus(), "Project should be COMPLETED");
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        System.out.println("Running ContractServiceTest...");
        int passed = 0;
        int total = 6;
        try {
            initDatabase();
        } catch (Throwable t) {
            System.err.println("Setup failed for ContractServiceTest: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        try {
            ContractServiceTest test = new ContractServiceTest();
            test.setup();
            test.testAtomicHiringTransactionSuccess();
            passed++;
            System.out.println("  [PASS] testAtomicHiringTransactionSuccess");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testAtomicHiringTransactionSuccess: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ContractServiceTest test = new ContractServiceTest();
            test.setup();
            test.testAtomicHiringTransactionRollbackOnUnauthorizedClient();
            passed++;
            System.out.println("  [PASS] testAtomicHiringTransactionRollbackOnUnauthorizedClient");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testAtomicHiringTransactionRollbackOnUnauthorizedClient: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ContractServiceTest test = new ContractServiceTest();
            test.setup();
            test.testAtomicHiringTransactionRollbackOnAlreadyAcceptedProposal();
            passed++;
            System.out.println("  [PASS] testAtomicHiringTransactionRollbackOnAlreadyAcceptedProposal");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testAtomicHiringTransactionRollbackOnAlreadyAcceptedProposal: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ContractServiceTest test = new ContractServiceTest();
            test.setup();
            test.testDeliverableSubmissionAndStorage();
            passed++;
            System.out.println("  [PASS] testDeliverableSubmissionAndStorage");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testDeliverableSubmissionAndStorage: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ContractServiceTest test = new ContractServiceTest();
            test.setup();
            test.testMilestoneRevisionAndApprovalLifecycle();
            passed++;
            System.out.println("  [PASS] testMilestoneRevisionAndApprovalLifecycle");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testMilestoneRevisionAndApprovalLifecycle: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ContractServiceTest test = new ContractServiceTest();
            test.setup();
            test.testMilestonePaymentAndContractCompletion();
            passed++;
            System.out.println("  [PASS] testMilestonePaymentAndContractCompletion");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testMilestonePaymentAndContractCompletion: " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("ContractServiceTest: " + passed + "/" + total + " tests passed.\n");
        if (passed != total) {
            throw new RuntimeException("Tests failed in ContractServiceTest");
        }
    }
}

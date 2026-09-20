package com.freelancing.service;

import com.freelancing.dao.common.UserDAO;
import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.dao.company.ProposalDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.model.common.User;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.company.Project;
import com.freelancing.model.company.Proposal;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.service.company.ProposalService;

import com.freelancing.db.DatabaseInitializer;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProposalServiceTest {

    private static ProposalService proposalService;
    private static ProjectDAO projectDAO;
    private static UserDAO userDAO;
    private static FreelancerProfileDAO freelancerProfileDAO;
    private static ClientProfileDAO clientProfileDAO;
    private static ProposalDAO proposalDAO;

    private static User testClientUser;
    private static ClientProfile testClientProfile;
    private static User testFreelancerUser;
    private static FreelancerProfile testFreelancerProfile;

    public static void setUp() {
        DatabaseInitializer.initialize();
        proposalDAO = new ProposalDAO();
        projectDAO = new ProjectDAO();
        userDAO = new UserDAO();
        freelancerProfileDAO = new FreelancerProfileDAO();
        clientProfileDAO = new ClientProfileDAO();
        proposalService = new ProposalService();

        // Create test client user & profile idempotently
        testClientUser = userDAO.findById("usr_prop_client");
        if (testClientUser == null) {
            testClientUser = new User();
            testClientUser.setId("usr_prop_client");
            testClientUser.setUsername("prop_client");
            testClientUser.setEmail("prop_client@skillbridge.io");
            testClientUser.setPassword("Pass123!");
            testClientUser.setRole(User.Role.CLIENT);
            testClientUser.setStatus(User.Status.ACTIVE);
            userDAO.create(testClientUser);
        }

        testClientProfile = clientProfileDAO.findByUserId("usr_prop_client");
        if (testClientProfile == null) {
            testClientProfile = new ClientProfile();
            testClientProfile.setId("cp_prop_test");
            testClientProfile.setUserId(testClientUser.getId());
            testClientProfile.setCompanyName("Prop Enterprise");
            testClientProfile.setIndustry("Tech");
            testClientProfile.setAbout("Verified Enterprise Client");
            clientProfileDAO.create(testClientProfile);
        }

        // Create test freelancer user & profile idempotently
        testFreelancerUser = userDAO.findById("usr_prop_free");
        if (testFreelancerUser == null) {
            testFreelancerUser = new User();
            testFreelancerUser.setId("usr_prop_free");
            testFreelancerUser.setUsername("prop_freelancer");
            testFreelancerUser.setEmail("prop_free@skillbridge.io");
            testFreelancerUser.setPassword("Pass123!");
            testFreelancerUser.setRole(User.Role.FREELANCER);
            testFreelancerUser.setStatus(User.Status.ACTIVE);
            userDAO.create(testFreelancerUser);
        }

        testFreelancerProfile = freelancerProfileDAO.findByUserId("usr_prop_free");
        if (testFreelancerProfile == null) {
            testFreelancerProfile = new FreelancerProfile("fp_prop_test", testFreelancerUser.getId(), "Senior Java Architect", "5+ years enterprise systems", 75.0, 6, 4.9, 12, 10, null, null, null, "AVAILABLE", null);
            testFreelancerProfile.setSkills(Arrays.asList("Java", "SQLite", "JavaFX"));
            freelancerProfileDAO.create(testFreelancerProfile);
        }
    }

    private Project createTestProject(String title) {
        String id = "proj_test_" + java.util.UUID.randomUUID().toString().substring(0, 8);
        Project p = new Project();
        p.setId(id);
        p.setClientId(testClientProfile.getId());
        p.setTitle(title);
        p.setDescription("Comprehensive technical architecture and implementation required.");
        p.setCategory("Software Development");
        p.setBudgetType("FIXED");
        p.setBudgetMin(2000.0);
        p.setBudgetMax(3500.0);
        p.setBudget(3200.0);
        p.setDeadline("2026-10-30");
        p.setExperienceLevel("EXPERT");
        p.setStatus(Project.Status.OPEN);
        p.setRequiredSkills(Arrays.asList("Java", "SQLite"));
        projectDAO.create(p);
        return p;
    }

    public void testSubmitProposalSuccess() {
        Project proj = createTestProject("Desktop Application Development");
        String projId = proj.getId();

        Proposal prop = proposalService.submitProposal(projId, testFreelancerUser.getId(), 3000.0, 14,
                "I have 6+ years of experience with Java and SQLite. I can deliver high quality results.");

        assertNotNull(prop, "Proposal should be created");
        assertNotNull(prop.getId());
        assertEquals(projId, prop.getProjectId());
        assertEquals(testFreelancerProfile.getId(), prop.getFreelancerId());
        assertEquals(3000.0, prop.getBidAmount(), 0.01);
        assertEquals(14, prop.getDeliveryDays());
        assertEquals(Proposal.Status.SUBMITTED, prop.getStatus());
        assertTrue(prop.getAiMatchScore() > 0, "AI match score should be computed (> 0)");

        // Verify direct SQLite retrieval
        Proposal fromDb = proposalDAO.findById(prop.getId());
        assertNotNull(fromDb);
        assertEquals("Desktop Application Development", fromDb.getProjectTitle());
        assertEquals(testClientUser.getUsername(), fromDb.getClientName());
        assertEquals(testFreelancerUser.getUsername(), fromDb.getFreelancerName());
    }

    public void testDuplicateProposalPrevention() {
        Project proj = createTestProject("Unique Bid Constraint Project");
        String projId = proj.getId();

        // First proposal succeeds
        Proposal first = proposalService.submitProposal(projId, testFreelancerUser.getId(), 2800.0, 10, "First proposal.");
        assertNotNull(first);

        // Second proposal for same project by same freelancer must fail
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            proposalService.submitProposal(projId, testFreelancerUser.getId(), 2500.0, 8, "Duplicate bid attempt.");
        });

        assertTrue(ex.getMessage().contains("already submitted"), "Exception message should indicate duplicate proposal");
    }

    public void testShortlistAndAcceptProposal() {
        Project proj = createTestProject("Lifecycle Test Project");
        String projId = proj.getId();

        Proposal prop = proposalService.submitProposal(projId, testFreelancerUser.getId(), 3100.0, 12, "Lifecycle proposal.");

        // Client shortlists proposal
        boolean shortlisted = proposalService.shortlistProposal(prop.getId(), testClientUser.getId());
        assertTrue(shortlisted);

        Proposal pShort = proposalDAO.findById(prop.getId());
        assertEquals(Proposal.Status.SHORTLISTED, pShort.getStatus());

        // Client accepts proposal
        boolean accepted = proposalService.acceptProposal(prop.getId(), testClientUser.getId());
        assertTrue(accepted);

        Proposal pAcc = proposalDAO.findById(prop.getId());
        assertEquals(Proposal.Status.ACCEPTED, pAcc.getStatus());

        // Project must be awarded and IN_PROGRESS
        Project projDb = projectDAO.findById(projId);
        assertEquals(Project.Status.IN_PROGRESS, projDb.getStatus());
        assertEquals(testFreelancerProfile.getId(), projDb.getAwardedFreelancerId());
    }

    public void testRejectProposal() {
        Project proj = createTestProject("Rejection Project Test");
        String projId = proj.getId();

        Proposal prop = proposalService.submitProposal(projId, testFreelancerUser.getId(), 3400.0, 20, "Bid to reject.");

        boolean rejected = proposalService.rejectProposal(prop.getId(), testClientUser.getId());
        assertTrue(rejected);

        Proposal pRej = proposalDAO.findById(prop.getId());
        assertEquals(Proposal.Status.REJECTED, pRej.getStatus());
    }

    public void testWithdrawProposal() {
        Project proj = createTestProject("Withdrawal Project Test");
        String projId = proj.getId();

        Proposal prop = proposalService.submitProposal(projId, testFreelancerUser.getId(), 2900.0, 15, "Bid to withdraw.");

        boolean withdrawn = proposalService.withdrawProposal(prop.getId(), testFreelancerUser.getId());
        assertTrue(withdrawn);

        Proposal pWith = proposalDAO.findById(prop.getId());
        assertEquals(Proposal.Status.WITHDRAWN, pWith.getStatus());
    }

    public void testProposalQueries() {
        Project proj = createTestProject("Query Testing Project");
        String projId = proj.getId();

        proposalService.submitProposal(projId, testFreelancerUser.getId(), 2750.0, 11, "Query test cover letter.");

        List<Proposal> byProj = proposalService.getProposalsForProject(projId);
        assertFalse(byProj.isEmpty());

        List<Proposal> byClient = proposalService.getProposalsForClient(testClientUser.getId());
        assertFalse(byClient.isEmpty());

        List<Proposal> byFree = proposalService.getProposalsForFreelancer(testFreelancerUser.getId());
        assertFalse(byFree.isEmpty());
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        System.out.println("Running ProposalServiceTest...");
        int passed = 0;
        int total = 6;
        try {
            setUp();
        } catch (Throwable t) {
            System.err.println("Setup failed for ProposalServiceTest: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        try {
            ProposalServiceTest test = new ProposalServiceTest();
            test.testSubmitProposalSuccess();
            passed++;
            System.out.println("  [PASS] testSubmitProposalSuccess");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testSubmitProposalSuccess: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ProposalServiceTest test = new ProposalServiceTest();
            test.testDuplicateProposalPrevention();
            passed++;
            System.out.println("  [PASS] testDuplicateProposalPrevention");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testDuplicateProposalPrevention: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ProposalServiceTest test = new ProposalServiceTest();
            test.testShortlistAndAcceptProposal();
            passed++;
            System.out.println("  [PASS] testShortlistAndAcceptProposal");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testShortlistAndAcceptProposal: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ProposalServiceTest test = new ProposalServiceTest();
            test.testRejectProposal();
            passed++;
            System.out.println("  [PASS] testRejectProposal");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testRejectProposal: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ProposalServiceTest test = new ProposalServiceTest();
            test.testWithdrawProposal();
            passed++;
            System.out.println("  [PASS] testWithdrawProposal");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testWithdrawProposal: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ProposalServiceTest test = new ProposalServiceTest();
            test.testProposalQueries();
            passed++;
            System.out.println("  [PASS] testProposalQueries");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testProposalQueries: " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("ProposalServiceTest: " + passed + "/" + total + " tests passed.\n");
        if (passed != total) {
            throw new RuntimeException("Tests failed in ProposalServiceTest");
        }
    }
}

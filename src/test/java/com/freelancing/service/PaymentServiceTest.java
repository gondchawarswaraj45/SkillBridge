package com.freelancing.service;

import com.freelancing.dao.common.TransactionDAO;
import com.freelancing.dao.common.UserDAO;
import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.dao.company.ContractDAO;
import com.freelancing.dao.company.MilestoneDAO;
import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.dao.company.ProposalDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.model.common.Transaction;
import com.freelancing.model.common.User;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.company.Contract;
import com.freelancing.model.company.Milestone;
import com.freelancing.model.company.Project;
import com.freelancing.model.company.Proposal;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.service.common.PaymentService;
import com.freelancing.service.company.ContractService;
import com.freelancing.service.company.MilestoneService;

import com.freelancing.config.SecurityConfig;
import com.freelancing.db.DatabaseInitializer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {

    private static UserDAO userDAO;
    private static ClientProfileDAO clientProfileDAO;
    private static FreelancerProfileDAO freelancerProfileDAO;
    private static ProjectDAO projectDAO;
    private static ProposalDAO proposalDAO;
    private static ContractDAO contractDAO;
    private static MilestoneDAO milestoneDAO;
    private static TransactionDAO transactionDAO;

    private ContractService contractService;
    private MilestoneService milestoneService;
    private PaymentService paymentService;

    private User clientUser;
    private ClientProfile clientProfile;
    private User freelancerUser;
    private FreelancerProfile freelancerProfile;
    private Project project;
    private Proposal proposal;
    private Contract contract;

    public static void initDatabase() {
        DatabaseInitializer.initialize();
        userDAO = new UserDAO();
        clientProfileDAO = new ClientProfileDAO();
        freelancerProfileDAO = new FreelancerProfileDAO();
        projectDAO = new ProjectDAO();
        proposalDAO = new ProposalDAO();
        contractDAO = new ContractDAO();
        milestoneDAO = new MilestoneDAO();
        transactionDAO = new TransactionDAO();
    }

    public void setup() {
        contractService = new ContractService();
        milestoneService = new MilestoneService();
        paymentService = new PaymentService();

        String runId = UUID.randomUUID().toString().substring(0, 8);

        // 1. Client setup
        clientUser = new User();
        clientUser.setId("usr_pay_cl_" + runId);
        clientUser.setUsername("client_pay_" + runId);
        clientUser.setEmail("cl_pay_" + runId + "@test.com");
        clientUser.setPassword(SecurityConfig.hashPassword("ClientPass123!"));
        clientUser.setRole(User.Role.CLIENT);
        userDAO.create(clientUser);

        clientProfile = new ClientProfile();
        clientProfile.setId("cl_pay_prof_" + runId);
        clientProfile.setUserId(clientUser.getId());
        clientProfile.setCompanyName("FinTech Ventures " + runId);
        clientProfileDAO.create(clientProfile);

        // 2. Freelancer setup
        freelancerUser = new User();
        freelancerUser.setId("usr_pay_fr_" + runId);
        freelancerUser.setUsername("free_pay_" + runId);
        freelancerUser.setEmail("fr_pay_" + runId + "@test.com");
        freelancerUser.setPassword(SecurityConfig.hashPassword("FreePass123!"));
        freelancerUser.setRole(User.Role.FREELANCER);
        userDAO.create(freelancerUser);

        freelancerProfile = new FreelancerProfile();
        freelancerProfile.setId("fr_pay_prof_" + runId);
        freelancerProfile.setUserId(freelancerUser.getId());
        freelancerProfile.setTitle("Full Stack Payment Specialist");
        freelancerProfileDAO.create(freelancerProfile);

        // 3. Project & Proposal setup
        project = new Project();
        project.setId("proj_pay_" + runId);
        project.setClientId(clientProfile.getId());
        project.setTitle("Payment Gateway Integration " + runId);
        project.setDescription("Build high-reliability financial payment simulation.");
        project.setCategory("Finance & Web");
        project.setBudgetMin(3000.0);
        project.setBudgetMax(5000.0);
        project.setBudgetType("FIXED");
        project.setDeadline("2026-11-15");
        project.setStatus(Project.Status.OPEN);
        projectDAO.create(project);

        proposal = new Proposal();
        proposal.setId("prop_pay_" + runId);
        proposal.setProjectId(project.getId());
        proposal.setFreelancerId(freelancerProfile.getId());
        proposal.setBidAmount(4500.0);
        proposal.setDeliveryDays(20);
        proposal.setCoverLetter("I have built multiple financial ledgers with double-entry and transaction guarantees.");
        proposal.setStatus(Proposal.Status.SUBMITTED);
        proposalDAO.create(proposal);

        // 4. Atomic hire creates contract and 3 sprint milestones
        contract = contractService.hireFreelancer(proposal.getId(), clientUser.getId(), null);
    }

    public void testProcessMilestonePaymentSuccess() throws IOException {
        List<Milestone> milestones = milestoneService.getMilestonesByContract(contract.getId());
        Milestone m1 = milestones.get(0);

        // Freelancer delivers and client approves
        File tempFile = File.createTempFile("pay_test_", ".zip");
        tempFile.deleteOnExit();
        milestoneService.submitDeliverable(m1.getId(), freelancerUser.getId(), "Milestone 1 Core", "Delivered", tempFile);
        milestoneService.approveMilestone(m1.getId(), clientUser.getId());

        // Client processes payment
        PaymentService.PaymentReceipt receipt = paymentService.processMilestonePayment(m1.getId(), clientUser.getId());

        assertNotNull(receipt);
        assertTrue(receipt.isDemoPayment, "Must be clearly flagged as demo simulation");
        assertEquals("COMPLETED", receipt.status);
        assertEquals(m1.getAmount(), receipt.amount, 0.001);
        assertTrue(receipt.reference.startsWith("DEMO-ESCROW-"));

        // Verify milestone status in DB
        Milestone dbM1 = milestoneDAO.findById(m1.getId());
        assertEquals(Milestone.Status.PAID, dbM1.getStatus());

        // Verify Transaction persisted in SQLite
        List<Transaction> txs = transactionDAO.findByMilestoneId(m1.getId());
        assertEquals(1, txs.size());
        Transaction tx = txs.get(0);
        assertEquals(Transaction.Type.PAYMENT, tx.getType());
        assertEquals(clientUser.getId(), tx.getSenderId());
        assertEquals(freelancerUser.getId(), tx.getReceiverId());
        assertEquals(m1.getAmount(), tx.getAmount(), 0.001);
        assertEquals(Transaction.Status.COMPLETED, tx.getStatus());

        // Verify client total spent updated
        ClientProfile dbCp = clientProfileDAO.findById(clientProfile.getId());
        assertEquals(m1.getAmount(), dbCp.getTotalSpent(), 0.001);
    }

    public void testProcessMilestonePaymentUnauthorizedClientFails() {
        List<Milestone> milestones = milestoneService.getMilestonesByContract(contract.getId());
        Milestone m1 = milestones.get(0);

        assertThrows(SecurityException.class, () -> {
            // Freelancer cannot authorize release of client escrow payments
            paymentService.processMilestonePayment(m1.getId(), freelancerUser.getId());
        });
    }

    public void testProcessMilestonePaymentAlreadyPaidFails() throws IOException {
        List<Milestone> milestones = milestoneService.getMilestonesByContract(contract.getId());
        Milestone m1 = milestones.get(0);

        File tempFile = File.createTempFile("pay_dup_", ".txt");
        tempFile.deleteOnExit();
        milestoneService.submitDeliverable(m1.getId(), freelancerUser.getId(), "M1 Artifact", "Done", tempFile);
        milestoneService.approveMilestone(m1.getId(), clientUser.getId());

        // First payment succeeds
        paymentService.processMilestonePayment(m1.getId(), clientUser.getId());

        // Duplicate payment must fail
        assertThrows(IllegalStateException.class, () -> {
            paymentService.processMilestonePayment(m1.getId(), clientUser.getId());
        });
    }

    public void testFreelancerEarningsAndWithdrawalSimulation() throws IOException {
        List<Milestone> milestones = milestoneService.getMilestonesByContract(contract.getId());
        Milestone m1 = milestones.get(0);

        File tempFile = File.createTempFile("earn_test_", ".txt");
        tempFile.deleteOnExit();
        milestoneService.submitDeliverable(m1.getId(), freelancerUser.getId(), "M1 Artifact", "Done", tempFile);
        milestoneService.approveMilestone(m1.getId(), clientUser.getId());
        paymentService.processMilestonePayment(m1.getId(), clientUser.getId());

        // Check freelancer financial summary
        PaymentService.FreelancerFinancialSummary summary = paymentService.getFinancialSummaryForFreelancer(freelancerUser.getId());
        assertEquals(m1.getAmount(), summary.totalEarned, 0.001);
        assertEquals(m1.getAmount(), summary.availableBalance, 0.001);
        assertEquals(0.0, summary.totalWithdrawn, 0.001);

        // Withdraw partial amount (e.g. $400)
        Transaction wdTx = paymentService.withdrawEarnings(freelancerUser.getId(), 400.0, "Bank Transfer (ACH)", "ACCT-12345");
        assertNotNull(wdTx);
        assertEquals(Transaction.Type.WITHDRAWAL, wdTx.getType());
        assertEquals(400.0, wdTx.getAmount(), 0.001);

        // Check updated summary
        PaymentService.FreelancerFinancialSummary updatedSummary = paymentService.getFinancialSummaryForFreelancer(freelancerUser.getId());
        assertEquals(m1.getAmount(), updatedSummary.totalEarned, 0.001);
        assertEquals(400.0, updatedSummary.totalWithdrawn, 0.001);
        assertEquals(m1.getAmount() - 400.0, updatedSummary.availableBalance, 0.001);
    }

    public void testWithdrawalExcessiveAmountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            // Freelancer has $0 balance initially, withdrawing $100 must fail
            paymentService.withdrawEarnings(freelancerUser.getId(), 100.0, "UPI", "dev@upi");
        });
    }

    public void testContractAndProjectCompletionOnAllMilestonesPaid() throws IOException {
        List<Milestone> milestones = milestoneService.getMilestonesByContract(contract.getId());
        File dummyFile = File.createTempFile("fin_all_", ".txt");
        dummyFile.deleteOnExit();

        for (Milestone m : milestones) {
            Milestone currentM = milestoneService.getMilestoneById(m.getId());
            if (currentM.getStatus() == Milestone.Status.PENDING) {
                milestoneService.startMilestone(m.getId(), freelancerUser.getId());
            }
            milestoneService.submitDeliverable(m.getId(), freelancerUser.getId(), "Complete: " + m.getTitle(), "Notes", dummyFile);
            milestoneService.approveMilestone(m.getId(), clientUser.getId());
            paymentService.processMilestonePayment(m.getId(), clientUser.getId());
        }

        // All milestones paid: contract and project must be COMPLETED
        Contract finalContract = contractDAO.findById(contract.getId());
        assertEquals(Contract.Status.COMPLETED, finalContract.getStatus());

        Project finalProject = projectDAO.findById(project.getId());
        assertEquals(Project.Status.COMPLETED, finalProject.getStatus());

        // Client summary should reflect 1 completed contract
        PaymentService.ClientFinancialSummary clientSummary = paymentService.getFinancialSummaryForClient(clientUser.getId());
        assertEquals(1, clientSummary.completedContractsCount);
        assertEquals(4500.0, clientSummary.totalSpent, 0.001);
        assertEquals(0.0, clientSummary.activeEscrowCommitted, 0.001);
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        System.out.println("Running PaymentServiceTest...");
        int passed = 0;
        int total = 6;
        try {
            initDatabase();
        } catch (Throwable t) {
            System.err.println("Setup failed for PaymentServiceTest: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        try {
            PaymentServiceTest test = new PaymentServiceTest();
            test.setup();
            test.testProcessMilestonePaymentSuccess();
            passed++;
            System.out.println("  [PASS] testProcessMilestonePaymentSuccess");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testProcessMilestonePaymentSuccess: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            PaymentServiceTest test = new PaymentServiceTest();
            test.setup();
            test.testProcessMilestonePaymentUnauthorizedClientFails();
            passed++;
            System.out.println("  [PASS] testProcessMilestonePaymentUnauthorizedClientFails");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testProcessMilestonePaymentUnauthorizedClientFails: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            PaymentServiceTest test = new PaymentServiceTest();
            test.setup();
            test.testProcessMilestonePaymentAlreadyPaidFails();
            passed++;
            System.out.println("  [PASS] testProcessMilestonePaymentAlreadyPaidFails");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testProcessMilestonePaymentAlreadyPaidFails: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            PaymentServiceTest test = new PaymentServiceTest();
            test.setup();
            test.testFreelancerEarningsAndWithdrawalSimulation();
            passed++;
            System.out.println("  [PASS] testFreelancerEarningsAndWithdrawalSimulation");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testFreelancerEarningsAndWithdrawalSimulation: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            PaymentServiceTest test = new PaymentServiceTest();
            test.setup();
            test.testWithdrawalExcessiveAmountFails();
            passed++;
            System.out.println("  [PASS] testWithdrawalExcessiveAmountFails");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testWithdrawalExcessiveAmountFails: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            PaymentServiceTest test = new PaymentServiceTest();
            test.setup();
            test.testContractAndProjectCompletionOnAllMilestonesPaid();
            passed++;
            System.out.println("  [PASS] testContractAndProjectCompletionOnAllMilestonesPaid");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testContractAndProjectCompletionOnAllMilestonesPaid: " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("PaymentServiceTest: " + passed + "/" + total + " tests passed.\n");
        if (passed != total) {
            throw new RuntimeException("Tests failed in PaymentServiceTest");
        }
    }
}

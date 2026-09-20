package com.freelancing.dao;

import com.freelancing.dao.common.SkillDAO;
import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.dao.freelancer.CertificationDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.dao.freelancer.FreelancerSkillDAO;
import com.freelancing.dao.freelancer.PortfolioDAO;
import com.freelancing.model.common.Skill;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.freelancer.Certification;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.model.freelancer.PortfolioItem;

import com.freelancing.db.DatabaseInitializer;
import com.freelancing.util.StorageManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileAndSkillsTest {

    private static FreelancerProfileDAO freelancerDAO;
    private static ClientProfileDAO clientDAO;
    private static SkillDAO skillDAO;
    private static FreelancerSkillDAO freelancerSkillDAO;
    private static PortfolioDAO portfolioDAO;
    private static CertificationDAO certDAO;

    public static void setUp() {
        DatabaseInitializer.initialize();
        freelancerDAO = new FreelancerProfileDAO();
        clientDAO = new ClientProfileDAO();
        skillDAO = new SkillDAO();
        freelancerSkillDAO = new FreelancerSkillDAO();
        portfolioDAO = new PortfolioDAO();
        certDAO = new CertificationDAO();
    }

    public void testFreelancerProfileCrud() {
        FreelancerProfile alex = freelancerDAO.findByUserId("usr_free1");
        assertNotNull(alex, "Seed freelancer alex should exist in SQLite");
        assertEquals("fp_alex", alex.getId());

        // Ensure rate is set to initial baseline for test idempotency
        alex.setHourlyRate(65.0);
        freelancerDAO.update(alex);
        alex = freelancerDAO.findByUserId("usr_free1");
        assertEquals(65.0, alex.getHourlyRate());
        assertEquals(6, alex.getExperienceYears());
        assertEquals("AVAILABLE", alex.getAvailability());

        // Update hourly rate
        alex.setHourlyRate(75.0);
        boolean updated = freelancerDAO.update(alex);
        assertTrue(updated, "Profile update should succeed");

        FreelancerProfile reFetched = freelancerDAO.findByUserId("usr_free1");
        assertEquals(75.0, reFetched.getHourlyRate());
    }

    public void testClientProfileCrud() {
        ClientProfile sarah = clientDAO.findByUserId("usr_client1");
        assertNotNull(sarah, "Seed client sarah should exist in SQLite");
        assertEquals("cp_sarah", sarah.getId());
        assertTrue(sarah.getCompanyName().contains("NovaTech"));
        assertEquals(24500.0, sarah.getTotalSpent());

        // Update about info
        sarah.setAbout("Updated FinTech enterprise mission.");
        boolean updated = clientDAO.update(sarah);
        assertTrue(updated, "Client update should succeed");

        ClientProfile reFetched = clientDAO.findByUserId("usr_client1");
        assertEquals("Updated FinTech enterprise mission.", reFetched.getAbout());
    }

    public void testSkillCatalog() {
        List<Skill> javaSkills = skillDAO.search("Java");
        assertFalse(javaSkills.isEmpty(), "Searching for Java should yield results");

        Skill java17 = skillDAO.findByName("Java 17");
        assertNotNull(java17);
        assertEquals("Software Development", java17.getCategory());

        // Clean up test skill if existing, then create
        skillDAO.delete("sk_test_quantum");
        Skill aiSkill = new Skill("sk_test_quantum", "Quantum Computing", "Advanced Tech");
        boolean created = skillDAO.create(aiSkill);
        assertTrue(created);

        Skill fetched = skillDAO.findByName("Quantum Computing");
        assertNotNull(fetched);
    }

    public void testFreelancerSkillsAssociation() {
        List<String> skillNames = freelancerSkillDAO.findSkillNamesByFreelancerId("fp_alex");
        assertFalse(skillNames.isEmpty(), "Alex should have associated skills");
        assertTrue(skillNames.contains("Java 17"));
        assertTrue(skillNames.contains("JavaFX"));

        // Add Python skill
        boolean added = freelancerSkillDAO.addSkill("fp_alex", "sk_5", "EXPERT");
        assertTrue(added);
        assertTrue(freelancerSkillDAO.hasSkill("fp_alex", "sk_5"));

        // Remove Python skill
        boolean removed = freelancerSkillDAO.removeSkill("fp_alex", "sk_5");
        assertTrue(removed);
        assertFalse(freelancerSkillDAO.hasSkill("fp_alex", "sk_5"));
    }

    public void testPortfolioCrud() {
        List<PortfolioItem> items = portfolioDAO.findByFreelancerId("fp_alex");
        assertFalse(items.isEmpty(), "Alex should have seed portfolio item");

        PortfolioItem newItem = new PortfolioItem("port_test_junit", "fp_alex", "Crypto Wallet Desktop App",
                "Self-custodial JavaFX desktop wallet.", "https://github.com/alex/wallet", null);
        boolean created = portfolioDAO.create(newItem);
        assertTrue(created);

        PortfolioItem fetched = portfolioDAO.findById("port_test_junit");
        assertNotNull(fetched);
        assertEquals("Crypto Wallet Desktop App", fetched.getTitle());

        boolean deleted = portfolioDAO.delete("port_test_junit");
        assertTrue(deleted);
        assertNull(portfolioDAO.findById("port_test_junit"));
    }

    public void testCertificationCrud() {
        Certification cert = new Certification("cert_test_1", "fp_alex", "AWS Solutions Architect",
                "Amazon Web Services", "2026-03", "https://aws.cert/123", null);
        boolean created = certDAO.create(cert);
        assertTrue(created);

        List<Certification> certs = certDAO.findByFreelancerId("fp_alex");
        assertTrue(certs.stream().anyMatch(c -> c.getName().equals("AWS Solutions Architect")));

        boolean deleted = certDAO.delete("cert_test_1");
        assertTrue(deleted);
    }

    public void testStorageManager() throws IOException {
        StorageManager.initStorage();
        assertTrue(new File(StorageManager.PROFILES_DIR).exists());
        assertTrue(new File(StorageManager.RESUMES_DIR).exists());
        assertTrue(new File(StorageManager.PORTFOLIOS_DIR).exists());
        assertTrue(new File(StorageManager.CERTIFICATIONS_DIR).exists());

        // Create a temporary file and save via StorageManager
        File tempFile = File.createTempFile("test_avatar", ".png");
        try (FileWriter fw = new FileWriter(tempFile)) {
            fw.write("dummy avatar image data");
        }

        String savedPath = StorageManager.saveProfileAvatar("usr_test_storage", tempFile);
        assertNotNull(savedPath);
        File savedFile = new File(savedPath);
        assertTrue(savedFile.exists(), "Saved avatar file must exist on disk");
        assertTrue(savedFile.length() > 0);

        // Cleanup temp file
        tempFile.delete();
        savedFile.delete();
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        System.out.println("Running ProfileAndSkillsTest...");
        int passed = 0;
        int total = 7;
        try {
            setUp();
        } catch (Throwable t) {
            System.err.println("Setup failed for ProfileAndSkillsTest: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        try {
            ProfileAndSkillsTest test = new ProfileAndSkillsTest();
            test.testFreelancerProfileCrud();
            passed++;
            System.out.println("  [PASS] testFreelancerProfileCrud");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testFreelancerProfileCrud: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ProfileAndSkillsTest test = new ProfileAndSkillsTest();
            test.testClientProfileCrud();
            passed++;
            System.out.println("  [PASS] testClientProfileCrud");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testClientProfileCrud: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ProfileAndSkillsTest test = new ProfileAndSkillsTest();
            test.testSkillCatalog();
            passed++;
            System.out.println("  [PASS] testSkillCatalog");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testSkillCatalog: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ProfileAndSkillsTest test = new ProfileAndSkillsTest();
            test.testFreelancerSkillsAssociation();
            passed++;
            System.out.println("  [PASS] testFreelancerSkillsAssociation");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testFreelancerSkillsAssociation: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ProfileAndSkillsTest test = new ProfileAndSkillsTest();
            test.testPortfolioCrud();
            passed++;
            System.out.println("  [PASS] testPortfolioCrud");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testPortfolioCrud: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ProfileAndSkillsTest test = new ProfileAndSkillsTest();
            test.testCertificationCrud();
            passed++;
            System.out.println("  [PASS] testCertificationCrud");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testCertificationCrud: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ProfileAndSkillsTest test = new ProfileAndSkillsTest();
            test.testStorageManager();
            passed++;
            System.out.println("  [PASS] testStorageManager");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testStorageManager: " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("ProfileAndSkillsTest: " + passed + "/" + total + " tests passed.\n");
        if (passed != total) {
            throw new RuntimeException("Tests failed in ProfileAndSkillsTest");
        }
    }
}

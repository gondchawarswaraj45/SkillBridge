package com.freelancing.service;

import com.freelancing.model.company.Project;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.model.freelancer.MatchingResult;
import com.freelancing.service.freelancer.AIProposalAssistant;
import com.freelancing.service.freelancer.MatchingService;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatchingServiceTest {

    private MatchingService matchingService;
    private AIProposalAssistant proposalAssistant;

    public void setUp() {
        matchingService = new MatchingService();
        proposalAssistant = new AIProposalAssistant();
    }

    public void testPerfectMatchScoreAndWeights() {
        FreelancerProfile f = new FreelancerProfile();
        f.setId("free_test_1");
        f.setUserId("user_test_1");
        f.setTitle("Senior Full-Stack Engineer");
        f.setSkills(Arrays.asList("Java", "JavaFX", "SQLite", "JDBC"));
        f.setExperienceYears(8); // >= 6 -> 15 pts
        f.setRating(5.0); // 5.0 -> 10 pts
        f.setTotalReviews(15);
        f.setHourlyRate(80.0);
        f.setAvailability("AVAILABLE"); // 10 pts
        f.setBio("Extensive experience building high-performance desktop applications with clean architecture."); // +1 pt
        f.setAvatarPath("storage/profiles/avatar.png"); // +1 pt
        f.setGithubUrl("https://github.com/developer"); // +1 pt
        f.setLinkedinUrl("https://linkedin.com/in/developer"); // +1 pt
        f.setCompletedProjects(10); // +1 pt -> Total Completeness: 5 pts

        Project p = new Project();
        p.setId("proj_test_1");
        p.setTitle("Enterprise Desktop Platform");
        p.setRequiredSkills(Arrays.asList("Java", "JavaFX", "SQLite", "JDBC"));
        p.setExperienceLevel("EXPERT");
        p.setBudgetType("HOURLY");
        p.setBudgetMin(60.0);
        p.setBudgetMax(90.0);
        p.setBudget(85.0);

        MatchingResult res = matchingService.calculateDetailedMatch(f, p);

        assertNotNull(res);
        assertEquals(50.0, res.getSkillScore(), 0.01, "Skill match must be 50 pts");
        assertEquals(15.0, res.getExperienceScore(), 0.01, "Experience match must be 15 pts");
        assertEquals(10.0, res.getRatingScore(), 0.01, "Rating score must be 10 pts");
        assertEquals(10.0, res.getBudgetScore(), 0.01, "Budget score must be 10 pts");
        assertEquals(10.0, res.getAvailabilityScore(), 0.01, "Availability score must be 10 pts");
        assertEquals(5.0, res.getCompletenessScore(), 0.01, "Completeness score must be 5 pts");

        assertEquals(100, res.getOverallScore(), "Total score must be exactly 100%");
        assertEquals("EXCELLENT", res.getCompatibilityLevel());
        assertTrue(res.getMissingSkills().isEmpty());
        assertEquals(4, res.getMatchedSkills().size());
        assertTrue(res.getExplanation().contains("100% Overall Match"));
    }

    public void testPartialSkillMatch() {
        FreelancerProfile f = new FreelancerProfile();
        f.setSkills(Arrays.asList("Java", "SQLite"));
        f.setExperienceYears(3);
        f.setRating(4.0);
        f.setTotalReviews(5);
        f.setHourlyRate(50.0);
        f.setAvailability("AVAILABLE");

        Project p = new Project();
        p.setRequiredSkills(Arrays.asList("Java", "SQLite", "JavaFX", "Docker"));
        p.setExperienceLevel("INTERMEDIATE");
        p.setBudgetType("FIXED");
        p.setBudget(2000.0);

        MatchingResult res = matchingService.calculateDetailedMatch(f, p);

        assertNotNull(res);
        // 2 out of 4 skills matched -> (2/4) * 50 = 25 pts
        assertEquals(25.0, res.getSkillScore(), 0.01);
        assertEquals(2, res.getMatchedSkills().size());
        assertTrue(res.getMatchedSkills().contains("Java"));
        assertTrue(res.getMatchedSkills().contains("SQLite"));

        assertEquals(2, res.getMissingSkills().size());
        assertTrue(res.getMissingSkills().contains("JavaFX"));
        assertTrue(res.getMissingSkills().contains("Docker"));

        assertTrue(res.getOverallScore() > 0 && res.getOverallScore() < 100);
    }

    public void testEmptySkillsRequirementFallback() {
        FreelancerProfile f = new FreelancerProfile();
        f.setSkills(Arrays.asList("Python", "Django"));
        f.setExperienceYears(2);
        f.setRating(0.0); // No reviews yet
        f.setTotalReviews(0);
        f.setAvailability("AVAILABLE");

        Project p = new Project();
        p.setRequiredSkills(List.of()); // No skills required
        p.setExperienceLevel("ENTRY");

        MatchingResult res = matchingService.calculateDetailedMatch(f, p);

        assertNotNull(res);
        assertEquals(50.0, res.getSkillScore(), 0.01, "No skills required should yield 50 pts baseline");
        assertEquals(8.0, res.getRatingScore(), 0.01, "No reviews should yield 8.0 baseline rating");
        assertTrue(res.getOverallScore() >= 70);
    }

    public void testProposalAssistantCoverLetter() {
        FreelancerProfile f = new FreelancerProfile();
        f.setTitle("Lead Java Architect");
        f.setSkills(Arrays.asList("Java", "JavaFX", "SQLite"));
        f.setExperienceYears(7);
        f.setRating(4.9);
        f.setCompletedProjects(18);

        Project p = new Project();
        p.setTitle("SkillBridge Desktop App");
        p.setClientName("Apex Corp");
        p.setRequiredSkills(Arrays.asList("Java", "JavaFX", "SQLite"));
        p.setExperienceLevel("EXPERT");
        p.setBudgetType("FIXED");
        p.setBudgetMin(3000.0);
        p.setBudgetMax(5000.0);
        p.setBudget(4500.0);

        MatchingResult match = matchingService.calculateDetailedMatch(f, p);
        String cover = proposalAssistant.generateProposalCoverLetter(p, f, match);

        assertNotNull(cover);
        assertTrue(cover.contains("Dear Apex Corp"), "Should address client by name");
        assertTrue(cover.contains("SkillBridge Desktop App"), "Should reference project title");
        assertTrue(cover.contains("Lead Java Architect"), "Should reference freelancer title");
        assertTrue(cover.contains("Java, JavaFX, SQLite"), "Should reference matched tech stack skills");
        assertTrue(cover.contains("ARCHITECTURAL STRATEGY & DELIVERABLES"), "Should include 4-step delivery methodology");
        assertTrue(cover.contains("PROPOSED SPRINT MILESTONES"), "Should include milestone roadmap");
    }

    public void testMilestoneBreakdownCalculation() {
        Project p = new Project();
        p.setExperienceLevel("INTERMEDIATE");

        double totalBid = 4000.0;
        List<AIProposalAssistant.MilestoneDraft> drafts = proposalAssistant.generateMilestoneDrafts(p, totalBid);

        assertNotNull(drafts);
        assertEquals(3, drafts.size(), "Should produce 3 milestones");

        double sum = drafts.stream().mapToDouble(AIProposalAssistant.MilestoneDraft::getAmount).sum();
        assertEquals(totalBid, sum, 0.01, "Milestone amounts must sum up to exact bid total");

        for (AIProposalAssistant.MilestoneDraft d : drafts) {
            assertTrue(d.getAmount() > 0);
            assertTrue(d.getDays() > 0);
            assertNotNull(d.getTitle());
            assertNotNull(d.getDescription());
        }
    }

    public void testSuggestedBidAndDays() {
        Project pFixed = new Project();
        pFixed.setBudgetType("FIXED");
        pFixed.setBudgetMin(2000.0);
        pFixed.setBudgetMax(4000.0);
        pFixed.setExperienceLevel("INTERMEDIATE");

        FreelancerProfile f = new FreelancerProfile();
        f.setHourlyRate(50.0);

        double bidFixed = proposalAssistant.suggestBidAmount(pFixed, f);
        assertTrue(bidFixed >= 2000.0 && bidFixed <= 4000.0, "Suggested bid should be within budget range");
        assertEquals(18, proposalAssistant.suggestDeliveryDays(pFixed), "Intermediate projects suggest 18 days");

        Project pHourly = new Project();
        pHourly.setBudgetType("HOURLY");
        pHourly.setBudgetMax(75.0);
        f.setHourlyRate(65.0);

        double bidHourly = proposalAssistant.suggestBidAmount(pHourly, f);
        assertEquals(65.0, bidHourly, 0.01, "Hourly bid should match freelancer rate within ceiling");
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        System.out.println("Running MatchingServiceTest...");
        int passed = 0;
        int total = 6;
        try {
            MatchingServiceTest test = new MatchingServiceTest();
            test.setUp();
            test.testPerfectMatchScoreAndWeights();
            passed++;
            System.out.println("  [PASS] testPerfectMatchScoreAndWeights");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testPerfectMatchScoreAndWeights: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            MatchingServiceTest test = new MatchingServiceTest();
            test.setUp();
            test.testPartialSkillMatch();
            passed++;
            System.out.println("  [PASS] testPartialSkillMatch");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testPartialSkillMatch: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            MatchingServiceTest test = new MatchingServiceTest();
            test.setUp();
            test.testEmptySkillsRequirementFallback();
            passed++;
            System.out.println("  [PASS] testEmptySkillsRequirementFallback");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testEmptySkillsRequirementFallback: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            MatchingServiceTest test = new MatchingServiceTest();
            test.setUp();
            test.testProposalAssistantCoverLetter();
            passed++;
            System.out.println("  [PASS] testProposalAssistantCoverLetter");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testProposalAssistantCoverLetter: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            MatchingServiceTest test = new MatchingServiceTest();
            test.setUp();
            test.testMilestoneBreakdownCalculation();
            passed++;
            System.out.println("  [PASS] testMilestoneBreakdownCalculation");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testMilestoneBreakdownCalculation: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            MatchingServiceTest test = new MatchingServiceTest();
            test.setUp();
            test.testSuggestedBidAndDays();
            passed++;
            System.out.println("  [PASS] testSuggestedBidAndDays");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testSuggestedBidAndDays: " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("MatchingServiceTest: " + passed + "/" + total + " tests passed.\n");
        if (passed != total) {
            throw new RuntimeException("Tests failed in MatchingServiceTest");
        }
    }
}

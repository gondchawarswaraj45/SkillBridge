package com.freelancing;

public class AllTestsRunner {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("          SKILLBRIDGE ZERO-ANNOTATION TEST SUITE RUNNER              ");
        System.out.println("======================================================================\n");

        long start = System.currentTimeMillis();
        int totalSuites = 16;
        int passedSuites = 0;

        try {
            com.freelancing.app.NavigationManagerTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: NavigationManagerTest -> " + t.getMessage());
        }

        try {
            com.freelancing.dao.ProfileAndSkillsTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: ProfileAndSkillsTest -> " + t.getMessage());
        }

        try {
            com.freelancing.dao.ProjectServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: ProjectServiceTest -> " + t.getMessage());
        }

        try {
            com.freelancing.db.DatabaseInitializerTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: DatabaseInitializerTest -> " + t.getMessage());
        }

        try {
            com.freelancing.e2e.EndToEndWorkflowTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: EndToEndWorkflowTest -> " + t.getMessage());
        }

        try {
            com.freelancing.service.AdminServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: AdminServiceTest -> " + t.getMessage());
        }

        try {
            com.freelancing.service.AuthServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: AuthServiceTest -> " + t.getMessage());
        }

        try {
            com.freelancing.service.CalendarServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: CalendarServiceTest -> " + t.getMessage());
        }

        try {
            com.freelancing.service.ChatServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: ChatServiceTest -> " + t.getMessage());
        }

        try {
            com.freelancing.service.CommunityServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: CommunityServiceTest -> " + t.getMessage());
        }

        try {
            com.freelancing.service.ContractServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: ContractServiceTest -> " + t.getMessage());
        }

        try {
            com.freelancing.service.MatchingServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: MatchingServiceTest -> " + t.getMessage());
        }

        try {
            com.freelancing.service.NotificationServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: NotificationServiceTest -> " + t.getMessage());
        }

        try {
            com.freelancing.service.PaymentServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: PaymentServiceTest -> " + t.getMessage());
        }

        try {
            com.freelancing.service.ProposalServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: ProposalServiceTest -> " + t.getMessage());
        }

        try {
            com.freelancing.service.SkillExchangeServiceTest.runTests();
            passedSuites++;
        } catch (Throwable t) {
            System.err.println("Suite Failed: SkillExchangeServiceTest -> " + t.getMessage());
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("======================================================================");
        System.out.println("  SUMMARY: " + passedSuites + "/" + totalSuites + " TEST SUITES PASSED (" + duration + " ms)");
        System.out.println("======================================================================");

        System.exit(passedSuites == totalSuites ? 0 : 1);
    }
}

package com.freelancing.dao;

import com.freelancing.dao.common.SkillDAO;
import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.dao.company.ProjectSkillDAO;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.company.Project;
import com.freelancing.service.company.ProjectService;

import com.freelancing.db.DatabaseInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectServiceTest {

    private static ProjectDAO projectDAO;
    private static ProjectSkillDAO projectSkillDAO;
    private static SkillDAO skillDAO;
    private static ClientProfileDAO clientProfileDAO;
    private static ProjectService projectService;

    private static final String TEST_PROJECT_ID = "proj_test_junit_5";

    @BeforeAll
    public static void setUp() {
        DatabaseInitializer.initialize();
        projectDAO = new ProjectDAO();
        projectSkillDAO = new ProjectSkillDAO();
        skillDAO = new SkillDAO();
        clientProfileDAO = new ClientProfileDAO();
        projectService = new ProjectService(projectDAO, projectSkillDAO, skillDAO, clientProfileDAO);
    }

    @AfterEach
    public void tearDown() {
        projectDAO.delete(TEST_PROJECT_ID);
    }

    @Test
    @DisplayName("Verify Project creation with skills and client profile stats update")
    public void testCreateProjectWithSkills() {
        projectDAO.delete(TEST_PROJECT_ID);

        ClientProfile clientBefore = clientProfileDAO.findById("cp_sarah");
        assertNotNull(clientBefore, "Seed client cp_sarah must exist in SQLite");
        int countBefore = clientBefore.getPostedProjects();

        Project project = new Project();
        project.setId(TEST_PROJECT_ID);
        project.setClientId("cp_sarah");
        project.setTitle("High-Performance SQLite ORM for JavaFX");
        project.setDescription("Develop an asynchronous, zero-overhead SQLite persistence layer for high-throughput UI applications.");
        project.setCategory("Desktop Development");
        project.setBudgetType("FIXED");
        project.setBudgetMin(3500.0);
        project.setBudgetMax(5000.0);
        project.setDeadline("2026-12-15");
        project.setExperienceLevel("EXPERT");
        project.setStatus(Project.Status.OPEN);

        List<String> skills = Arrays.asList("Java 17", "SQLite", "Concurrency");
        Project created = projectService.createProject(project, skills);
        assertNotNull(created);

        // Verify SQLite persistence
        Project fetched = projectService.getProject(TEST_PROJECT_ID);
        assertNotNull(fetched, "Project must exist in SQLite");
        assertEquals("High-Performance SQLite ORM for JavaFX", fetched.getTitle());
        assertEquals("FIXED", fetched.getBudgetType());
        assertEquals(3500.0, fetched.getBudgetMin());
        assertEquals(5000.0, fetched.getBudgetMax());
        assertEquals("EXPERT", fetched.getExperienceLevel());
        assertEquals(Project.Status.OPEN, fetched.getStatus());

        // Verify skills in join table
        List<String> fetchedSkills = fetched.getRequiredSkills();
        assertTrue(fetchedSkills.contains("Java 17"));
        assertTrue(fetchedSkills.contains("SQLite"));
        assertTrue(fetchedSkills.contains("Concurrency"));

        // Verify client posted projects counter incremented
        ClientProfile clientAfter = clientProfileDAO.findById("cp_sarah");
        assertEquals(countBefore + 1, clientAfter.getPostedProjects());
    }

    @Test
    @DisplayName("Verify Project Marketplace search, filtering, and sorting in SQLite")
    public void testMarketplaceSearchAndFilters() {
        // Keyword Search
        List<Project> searchResult = projectService.searchProjects("Financial", null, null, null, null, null, "OPEN", "NEWEST");
        assertFalse(searchResult.isEmpty(), "Should find financial terminal seed project");
        assertTrue(searchResult.stream().anyMatch(p -> p.getId().equals("proj_1")));

        // Category Filter
        List<Project> categoryResult = projectService.searchProjects(null, "Desktop Development", null, null, null, null, "OPEN", "NEWEST");
        assertFalse(categoryResult.isEmpty());
        assertTrue(categoryResult.stream().allMatch(p -> "Desktop Development".equalsIgnoreCase(p.getCategory())));

        // Skill Filter
        List<Project> skillResult = projectService.searchProjects(null, null, "SQLite", null, null, null, "OPEN", "NEWEST");
        assertFalse(skillResult.isEmpty());
        assertTrue(skillResult.stream().anyMatch(p -> p.getId().equals("proj_1")));

        // Budget Filter
        List<Project> budgetResult = projectService.searchProjects(null, null, null, 2500.0, 5000.0, null, "OPEN", "BUDGET_HIGH");
        assertFalse(budgetResult.isEmpty());

        // Experience Level Filter
        List<Project> expResult = projectService.searchProjects(null, null, null, null, null, "EXPERT", "OPEN", "NEWEST");
        assertFalse(expResult.isEmpty());
        assertTrue(expResult.stream().anyMatch(p -> "EXPERT".equalsIgnoreCase(p.getExperienceLevel())));
    }

    @Test
    @DisplayName("Verify Project update and status lifecycle transitions")
    public void testProjectUpdateAndLifecycle() {
        projectDAO.delete(TEST_PROJECT_ID);

        Project project = new Project();
        project.setId(TEST_PROJECT_ID);
        project.setClientId("cp_sarah");
        project.setTitle("Initial Lifecycle Test Project");
        project.setDescription("Project to verify updates and status changes.");
        project.setCategory("Data & AI");
        project.setBudgetType("HOURLY");
        project.setBudgetMin(50.0);
        project.setBudgetMax(80.0);
        project.setDeadline("2026-11-30");
        project.setExperienceLevel("INTERMEDIATE");
        project.setStatus(Project.Status.OPEN);

        projectService.createProject(project, Arrays.asList("Java 17", "Python"));

        // Update Project details
        project.setTitle("Updated Lifecycle Test Project");
        project.setBudgetMax(95.0);
        boolean updated = projectService.updateProject(project, Arrays.asList("Java 17", "Python", "Docker"));
        assertTrue(updated);

        Project refetched = projectService.getProject(TEST_PROJECT_ID);
        assertEquals("Updated Lifecycle Test Project", refetched.getTitle());
        assertEquals(95.0, refetched.getBudgetMax());
        assertTrue(refetched.getRequiredSkills().contains("Docker"));

        // Transition Status
        boolean statusChanged = projectService.updateProjectStatus(TEST_PROJECT_ID, "IN_PROGRESS");
        assertTrue(statusChanged);

        Project inProgress = projectService.getProject(TEST_PROJECT_ID);
        assertEquals(Project.Status.IN_PROGRESS, inProgress.getStatus());
    }
}

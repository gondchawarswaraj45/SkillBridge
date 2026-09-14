package com.freelancing.service.company;

import com.freelancing.dao.common.SkillDAO;
import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.dao.company.ProjectSkillDAO;
import com.freelancing.model.common.Skill;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.company.Project;

import com.freelancing.db.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service managing project creation, marketplace search, skills binding, and lifecycle transitions.
 */
public class ProjectService {

    private final ProjectDAO projectDAO;
    private final ProjectSkillDAO projectSkillDAO;
    private final SkillDAO skillDAO;
    private final ClientProfileDAO clientProfileDAO;

    public ProjectService() {
        this.projectDAO = new ProjectDAO();
        this.projectSkillDAO = new ProjectSkillDAO();
        this.skillDAO = new SkillDAO();
        this.clientProfileDAO = new ClientProfileDAO();
    }

    public ProjectService(ProjectDAO projectDAO, ProjectSkillDAO projectSkillDAO, SkillDAO skillDAO, ClientProfileDAO clientProfileDAO) {
        this.projectDAO = projectDAO;
        this.projectSkillDAO = projectSkillDAO;
        this.skillDAO = skillDAO;
        this.clientProfileDAO = clientProfileDAO;
    }

    /**
     * Creates a new project in SQLite with skills association and updates the client's posted projects count.
     */
    public Project createProject(Project project, List<String> skillNames) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null.");
        }
        if (project.getTitle() == null || project.getTitle().isBlank()) {
            throw new IllegalArgumentException("Project title is required.");
        }
        if (project.getDescription() == null || project.getDescription().isBlank()) {
            throw new IllegalArgumentException("Project description is required.");
        }

        if (project.getId() == null || project.getId().isBlank()) {
            project.setId("proj_" + UUID.randomUUID().toString().substring(0, 8));
        }

        // Resolve clientId: if it's a user ID, link to client_profiles.id
        if (project.getClientId() != null) {
            ClientProfile profile = clientProfileDAO.findByUserId(project.getClientId());
            if (profile != null) {
                project.setClientId(profile.getId());
                if (project.getClientName() == null || project.getClientName().isBlank()) {
                    project.setClientName(profile.getCompanyName() != null ? profile.getCompanyName() : "Client");
                }
            }
        }

        boolean created = projectDAO.create(project);
        if (!created) {
            throw new RuntimeException("Failed to persist project in SQLite database.");
        }

        // Attach Skills
        List<String> attachedSkills = new ArrayList<>();
        if (skillNames != null) {
            for (String sName : skillNames) {
                if (sName == null || sName.isBlank()) continue;
                String trimmed = sName.trim();
                Skill skill = skillDAO.findByName(trimmed);
                if (skill == null) {
                    String sId = "sk_" + UUID.randomUUID().toString().substring(0, 8);
                    skill = new Skill(sId, trimmed, project.getCategory() != null ? project.getCategory() : "General");
                    skillDAO.create(skill);
                }
                projectSkillDAO.addSkillToProject(project.getId(), skill.getId());
                attachedSkills.add(trimmed);
            }
        }
        project.setRequiredSkills(attachedSkills);

        // Update Client's posted_projects counter in SQLite
        if (project.getClientId() != null) {
            ClientProfile cp = clientProfileDAO.findById(project.getClientId());
            if (cp != null) {
                clientProfileDAO.updateStats(cp.getId(), cp.getTotalSpent(), cp.getPostedProjects() + 1);
            }
        }

        // Sync with in-memory map for backwards compatibility with any remaining views
        try {
            DatabaseManager.getInstance().getProjects().put(project.getId(), project);
        } catch (Exception ignored) {}

        return project;
    }

    /**
     * Retrieves a project by ID with its required skills loaded.
     */
    public Project getProject(String id) {
        Project project = projectDAO.findById(id);
        if (project != null) {
            populateSkills(project);
        }
        return project;
    }

    /**
     * Returns all projects with skills populated.
     */
    public List<Project> getAllProjects() {
        List<Project> list = projectDAO.findAll();
        for (Project p : list) {
            populateSkills(p);
        }
        return list;
    }

    /**
     * Returns all open projects with skills populated.
     */
    public List<Project> getOpenProjects() {
        List<Project> list = projectDAO.findByStatus("OPEN");
        for (Project p : list) {
            populateSkills(p);
        }
        return list;
    }

    /**
     * Returns all projects belonging to a client profile ID or user ID.
     */
    public List<Project> getProjectsByClient(String clientIdOrUserId) {
        List<Project> list = projectDAO.findByClientId(clientIdOrUserId);
        for (Project p : list) {
            populateSkills(p);
        }
        return list;
    }

    /**
     * Advanced marketplace search across SQLite with multi-criteria filtering and sorting.
     */
    public List<Project> searchProjects(String keyword, String category, String skill,
                                       Double minBudget, Double maxBudget, String expLevel,
                                       String statusFilter, String sortBy) {
        List<Project> list = projectDAO.search(keyword, category, skill, minBudget, maxBudget, expLevel, statusFilter, sortBy);
        for (Project p : list) {
            populateSkills(p);
        }
        return list;
    }

    /**
     * Updates an existing project.
     */
    public boolean updateProject(Project project, List<String> skillNames) {
        if (project == null) return false;
        boolean updated = projectDAO.update(project);
        if (updated && skillNames != null) {
            List<String> skillIds = new ArrayList<>();
            for (String sName : skillNames) {
                if (sName == null || sName.isBlank()) continue;
                String trimmed = sName.trim();
                Skill skill = skillDAO.findByName(trimmed);
                if (skill == null) {
                    String sId = "sk_" + UUID.randomUUID().toString().substring(0, 8);
                    skill = new Skill(sId, trimmed, project.getCategory());
                    skillDAO.create(skill);
                }
                skillIds.add(skill.getId());
            }
            projectSkillDAO.setProjectSkills(project.getId(), skillIds);
            project.setRequiredSkills(skillNames);
        }
        return updated;
    }

    public boolean updateProjectStatus(String projectId, String status) {
        return projectDAO.updateStatus(projectId, status);
    }

    public boolean deleteProject(String projectId) {
        return projectDAO.delete(projectId);
    }

    public int getTotalProjectCount() {
        return projectDAO.countAll();
    }

    public int getOpenProjectCount() {
        return projectDAO.countByStatus("OPEN");
    }

    public int getClientProjectCount(String clientId) {
        return projectDAO.countByClientId(clientId);
    }

    private void populateSkills(Project project) {
        if (project == null) return;
        List<String> skillNames = projectSkillDAO.findSkillNamesByProjectId(project.getId());
        project.setRequiredSkills(skillNames);
    }
}

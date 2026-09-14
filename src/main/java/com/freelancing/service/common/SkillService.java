package com.freelancing.service.common;

import com.freelancing.dao.common.SkillDAO;
import com.freelancing.model.common.Skill;

import java.util.List;
import java.util.UUID;

/**
 * Service managing skill catalog queries, categorizations, and dynamic skill registrations.
 */
public class SkillService {

    private final SkillDAO skillDAO;

    public SkillService() {
        this.skillDAO = new SkillDAO();
    }

    public SkillService(SkillDAO skillDAO) {
        this.skillDAO = skillDAO;
    }

    public List<Skill> getAllSkills() {
        return skillDAO.findAll();
    }

    public List<Skill> getSkillsByCategory(String category) {
        return skillDAO.findByCategory(category);
    }

    public List<Skill> searchSkills(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllSkills();
        }
        return skillDAO.search(query.trim());
    }

    public Skill getOrCreateSkill(String name, String category) {
        if (name == null || name.trim().isEmpty()) return null;
        String trimmed = name.trim();
        Skill existing = skillDAO.findByName(trimmed);
        if (existing != null) {
            return existing;
        }

        String id = "sk_" + UUID.randomUUID().toString().substring(0, 8);
        Skill newSkill = new Skill(id, trimmed, category != null ? category.trim() : "General");
        boolean created = skillDAO.create(newSkill);
        return created ? newSkill : null;
    }
}

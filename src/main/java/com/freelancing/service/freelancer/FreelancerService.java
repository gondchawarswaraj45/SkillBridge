package com.freelancing.service.freelancer;

import com.freelancing.dao.freelancer.CertificationDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.dao.freelancer.FreelancerSkillDAO;
import com.freelancing.dao.freelancer.PortfolioDAO;
import com.freelancing.model.common.Skill;
import com.freelancing.model.freelancer.Certification;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.model.freelancer.PortfolioItem;

import com.freelancing.util.StorageManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Service managing Freelancer profiles, skills, portfolios, and certifications.
 */
public class FreelancerService {

    private final FreelancerProfileDAO profileDAO;
    private final FreelancerSkillDAO skillDAO;
    private final PortfolioDAO portfolioDAO;
    private final CertificationDAO certDAO;

    public FreelancerService() {
        this.profileDAO = new FreelancerProfileDAO();
        this.skillDAO = new FreelancerSkillDAO();
        this.portfolioDAO = new PortfolioDAO();
        this.certDAO = new CertificationDAO();
    }

    public FreelancerService(FreelancerProfileDAO profileDAO, FreelancerSkillDAO skillDAO,
                             PortfolioDAO portfolioDAO, CertificationDAO certDAO) {
        this.profileDAO = profileDAO;
        this.skillDAO = skillDAO;
        this.portfolioDAO = portfolioDAO;
        this.certDAO = certDAO;
    }

    public FreelancerProfile getProfileByUserId(String userId) {
        FreelancerProfile profile = profileDAO.findByUserId(userId);
        if (profile != null) {
            hydrateSkills(profile);
        }
        return profile;
    }

    public FreelancerProfile getProfile(String userId) {
        return getProfileByUserId(userId);
    }

    public FreelancerProfile getProfileById(String profileId) {
        FreelancerProfile profile = profileDAO.findById(profileId);
        if (profile != null) {
            hydrateSkills(profile);
        }
        return profile;
    }

    public boolean updateProfile(FreelancerProfile profile) {
        return profileDAO.update(profile);
    }

    public boolean addSkill(String freelancerId, String skillId, String proficiency) {
        return skillDAO.addSkill(freelancerId, skillId, proficiency);
    }

    public boolean removeSkill(String freelancerId, String skillId) {
        return skillDAO.removeSkill(freelancerId, skillId);
    }

    public List<Skill> getSkills(String freelancerId) {
        return skillDAO.findSkillsByFreelancerId(freelancerId);
    }

    public List<String> getSkillNames(String freelancerId) {
        return skillDAO.findSkillNamesByFreelancerId(freelancerId);
    }

    public PortfolioItem addPortfolioItem(String freelancerId, String title, String description, String projectUrl, File imageFile) throws IOException {
        String itemId = "port_" + UUID.randomUUID().toString().substring(0, 8);
        String imagePath = null;
        if (imageFile != null && imageFile.exists()) {
            imagePath = StorageManager.savePortfolioImage(itemId, imageFile);
        }

        PortfolioItem item = new PortfolioItem(itemId, freelancerId, title, description, projectUrl, imagePath);
        boolean success = portfolioDAO.create(item);
        return success ? item : null;
    }

    public List<PortfolioItem> getPortfolioItems(String freelancerId) {
        return portfolioDAO.findByFreelancerId(freelancerId);
    }

    public boolean deletePortfolioItem(String itemId) {
        return portfolioDAO.delete(itemId);
    }

    public Certification addCertification(String freelancerId, String name, String issuer, String issueDate, String credentialUrl, File certFile) throws IOException {
        String certId = "cert_" + UUID.randomUUID().toString().substring(0, 8);
        String certPath = null;
        if (certFile != null && certFile.exists()) {
            certPath = StorageManager.saveCertificationFile(certId, certFile);
        }

        Certification cert = new Certification(certId, freelancerId, name, issuer, issueDate, credentialUrl, certPath);
        boolean success = certDAO.create(cert);
        return success ? cert : null;
    }

    public List<Certification> getCertifications(String freelancerId) {
        return certDAO.findByFreelancerId(freelancerId);
    }

    public boolean deleteCertification(String certId) {
        return certDAO.delete(certId);
    }

    public String uploadAvatar(String userId, File avatarFile) throws IOException {
        String relativePath = StorageManager.saveProfileAvatar(userId, avatarFile);
        FreelancerProfile profile = profileDAO.findByUserId(userId);
        if (profile != null) {
            profile.setAvatarPath(relativePath);
            profileDAO.update(profile);
        }
        return relativePath;
    }

    public String uploadResume(String userId, File resumeFile) throws IOException {
        String relativePath = StorageManager.saveResume(userId, resumeFile);
        FreelancerProfile profile = profileDAO.findByUserId(userId);
        if (profile != null) {
            profile.setResumePath(relativePath);
            profileDAO.update(profile);
        }
        return relativePath;
    }

    public List<FreelancerProfile> getAllFreelancers() {
        List<FreelancerProfile> profiles = profileDAO.findAll();
        for (FreelancerProfile fp : profiles) {
            hydrateSkills(fp);
        }
        return profiles;
    }

    private void hydrateSkills(FreelancerProfile profile) {
        List<String> names = skillDAO.findSkillNamesByFreelancerId(profile.getId());
        profile.setSkills(names);
    }
}

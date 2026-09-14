package com.freelancing.service.company;

import com.freelancing.dao.company.ClientProfileDAO;
import com.freelancing.model.company.ClientProfile;

import com.freelancing.util.StorageManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Service managing Client profiles, enterprise metadata, and branding assets.
 */
public class ClientService {

    private final ClientProfileDAO clientProfileDAO;

    public ClientService() {
        this.clientProfileDAO = new ClientProfileDAO();
    }

    public ClientService(ClientProfileDAO clientProfileDAO) {
        this.clientProfileDAO = clientProfileDAO;
    }

    public ClientProfile getProfileByUserId(String userId) {
        return clientProfileDAO.findByUserId(userId);
    }

    public ClientProfile getProfile(String userId) {
        return getProfileByUserId(userId);
    }

    public ClientProfile getProfileById(String id) {
        return clientProfileDAO.findById(id);
    }

    public boolean updateProfile(ClientProfile profile) {
        return clientProfileDAO.update(profile);
    }

    public String uploadAvatar(String userId, File avatarFile) throws IOException {
        String relativePath = StorageManager.saveProfileAvatar(userId, avatarFile);
        ClientProfile profile = clientProfileDAO.findByUserId(userId);
        if (profile != null) {
            profile.setAvatarPath(relativePath);
            clientProfileDAO.update(profile);
        }
        return relativePath;
    }

    public List<ClientProfile> getAllClients() {
        return clientProfileDAO.findAll();
    }
}

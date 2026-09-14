package com.freelancing.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Local file storage manager for SkillBridge.
 * Handles profile avatars, PDF resumes, portfolio assets, and certification proofs.
 */
public class StorageManager {

    public static final String ROOT_DIR = "storage";
    public static final String PROFILES_DIR = ROOT_DIR + File.separator + "profiles";
    public static final String RESUMES_DIR = ROOT_DIR + File.separator + "resumes";
    public static final String PORTFOLIOS_DIR = ROOT_DIR + File.separator + "portfolios";
    public static final String CERTIFICATIONS_DIR = ROOT_DIR + File.separator + "certifications";
    public static final String DELIVERABLES_DIR = ROOT_DIR + File.separator + "deliverables";

    static {
        initStorage();
    }

    private StorageManager() {}

    public static void initStorage() {
        createDirIfNotExists(ROOT_DIR);
        createDirIfNotExists(PROFILES_DIR);
        createDirIfNotExists(RESUMES_DIR);
        createDirIfNotExists(PORTFOLIOS_DIR);
        createDirIfNotExists(CERTIFICATIONS_DIR);
        createDirIfNotExists(DELIVERABLES_DIR);
    }

    public static String saveProfileAvatar(String userId, File sourceFile) throws IOException {
        String ext = getFileExtension(sourceFile);
        String targetName = "avatar_" + sanitizeFilename(userId) + (ext.isEmpty() ? ".jpg" : "." + ext);
        File targetFile = new File(PROFILES_DIR, targetName);
        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return targetFile.getPath();
    }

    public static String saveResume(String userId, File sourceFile) throws IOException {
        String ext = getFileExtension(sourceFile);
        String targetName = "resume_" + sanitizeFilename(userId) + (ext.isEmpty() ? ".pdf" : "." + ext);
        File targetFile = new File(RESUMES_DIR, targetName);
        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return targetFile.getPath();
    }

    public static String savePortfolioImage(String portfolioId, File sourceFile) throws IOException {
        String ext = getFileExtension(sourceFile);
        String targetName = "portfolio_" + sanitizeFilename(portfolioId) + (ext.isEmpty() ? ".png" : "." + ext);
        File targetFile = new File(PORTFOLIOS_DIR, targetName);
        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return targetFile.getPath();
    }

    public static String saveCertificationFile(String certId, File sourceFile) throws IOException {
        String ext = getFileExtension(sourceFile);
        String targetName = "cert_" + sanitizeFilename(certId) + (ext.isEmpty() ? ".pdf" : "." + ext);
        File targetFile = new File(CERTIFICATIONS_DIR, targetName);
        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return targetFile.getPath();
    }

    public static String saveDeliverableFile(String deliverableId, File sourceFile) throws IOException {
        initStorage();
        String ext = getFileExtension(sourceFile);
        String targetName = "deliverable_" + sanitizeFilename(deliverableId) + (ext.isEmpty() ? ".zip" : "." + ext);
        File targetFile = new File(DELIVERABLES_DIR, targetName);
        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return targetFile.getPath();
    }

    public static File getFile(String path) {
        if (path == null || path.trim().isEmpty()) return null;
        return new File(path);
    }

    private static void createDirIfNotExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        return (lastIndex > 0) ? name.substring(lastIndex + 1).toLowerCase() : "";
    }

    private static String sanitizeFilename(String input) {
        return input.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}

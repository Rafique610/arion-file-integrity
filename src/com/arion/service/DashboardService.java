package com.arion.service;

import com.arion.dao.FileDAO;
import com.arion.dao.UserDAO;
import com.arion.dao.AuditLogDAO;
import com.arion.model.File;
import com.arion.model.User;
import com.arion.model.AuditLog;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DashboardService {
    
    private FileDAO fileDAO;
    private UserDAO userDAO;
    private AuditLogDAO auditLogDAO;
    
    public DashboardService() {
        this.fileDAO = new FileDAO();
        this.userDAO = new UserDAO();
        this.auditLogDAO = new AuditLogDAO();
    }
    
    // Get dashboard statistics
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        
        stats.put("totalFiles", fileDAO.getFileCount());
        stats.put("verifiedFiles", fileDAO.getFileCountByStatus("Verified"));
        stats.put("protectedFiles", fileDAO.getFileCountByStatus("Protected"));
        stats.put("tamperedFiles", fileDAO.getFileCountByStatus("Tampered"));
        stats.put("totalUsers", userDAO.getUserCount());
        stats.put("totalLogs", auditLogDAO.getAuditLogCount());
        
        return stats;
    }
    
    // Get recent files for dashboard
    public List<File> getRecentFiles(int limit) {
        List<File> allFiles = fileDAO.getAllFiles();
        
        // Return only the specified number of files
        if (allFiles.size() > limit) {
            return allFiles.subList(0, limit);
        }
        
        return allFiles;
    }
    
    // Get files by user for user dashboard
    public List<File> getUserFiles(int userId) {
        return fileDAO.getFilesByUser(userId);
    }
    
    // Get recent activity for dashboard
    public List<AuditLog> getRecentActivity(int limit) {
        return auditLogDAO.getRecentAuditLogs(limit);
    }
    
    // Get user activity
    public List<AuditLog> getUserActivity(int userId) {
        return auditLogDAO.getAuditLogsByUser(userId);
    }
}
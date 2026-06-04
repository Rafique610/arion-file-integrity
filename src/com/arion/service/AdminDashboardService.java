package com.arion.service;

import com.arion.dao.FileDAO;
import com.arion.dao.UserDAO;
import com.arion.dao.AuditLogDAO;
import com.arion.dao.FileIntegrityCheckDAO;
import com.arion.model.AuditLog;
import com.arion.model.User;
import com.arion.model.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardService {
    
    private FileDAO fileDAO;
    private UserDAO userDAO;
    private AuditLogDAO auditLogDAO;
    private FileIntegrityCheckDAO checkDAO;
    
    public AdminDashboardService() {
        this.fileDAO = new FileDAO();
        this.userDAO = new UserDAO();
        this.auditLogDAO = new AuditLogDAO();
        this.checkDAO = new FileIntegrityCheckDAO();
    }
    
    /**
     * Get comprehensive statistics for admin dashboard
     */
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // User statistics
            int totalUsers = userDAO.getUserCount();
            List<User> allUsers = userDAO.getAllUsers();
            int activeUsers = (int) allUsers.stream()
                .filter(User::isActive)
                .count();
            
            stats.put("totalUsers", totalUsers);
            stats.put("activeUsers", activeUsers);
            
            // File statistics
            int totalFiles = fileDAO.getFileCount();
            int verifiedFiles = fileDAO.getFileCountByStatus("Verified");
            int tamperedFiles = fileDAO.getFileCountByStatus("Tampered");
            int pendingFiles = fileDAO.getFileCountByStatus("Pending");
            
            stats.put("totalFiles", totalFiles);
            stats.put("verifiedFiles", verifiedFiles);
            stats.put("tamperedFiles", tamperedFiles);
            stats.put("pendingFiles", pendingFiles);
            
            // Security metrics
            int totalChecks = checkDAO.getCheckCount();
            int failedChecks = checkDAO.getCheckCountByResult("Tampered");
            int successfulChecks = checkDAO.getCheckCountByResult("Verified");
            
            stats.put("totalChecks", totalChecks);
            stats.put("failedChecks", failedChecks);
            stats.put("successfulChecks", successfulChecks);
            
            // Audit log count
            int totalLogs = auditLogDAO.getAuditLogCount();
            stats.put("totalLogs", totalLogs);
            
            // Calculate system health
            double systemHealth = calculateSystemHealth(verifiedFiles, tamperedFiles, totalFiles);
            stats.put("systemHealth", systemHealth);
            
            // Security policies (placeholder - can be dynamic from DB)
            stats.put("securityPolicies", 18);
            
            System.out.println("Admin stats loaded successfully");
            
        } catch (Exception e) {
            System.err.println("Error loading admin stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Get recent admin actions from audit logs
     */
    public List<AuditLog> getRecentAdminActions(int limit) {
        try {
            return auditLogDAO.getRecentAuditLogs(limit);
        } catch (Exception e) {
            System.err.println("Error loading recent actions: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Get system performance metrics
     */
    public Map<String, Integer> getSystemPerformance() {
        Map<String, Integer> performance = new HashMap<>();
        
        try {
            // Calculate based on actual data
            int totalUsers = userDAO.getUserCount();
            int totalFiles = fileDAO.getFileCount();
            int totalLogs = auditLogDAO.getAuditLogCount();
            
            // CPU Usage (simulated based on activity)
            int cpuUsage = Math.min(100, (totalLogs % 100));
            
            // Memory Usage (simulated based on files)
            int memoryUsage = Math.min(100, (totalFiles * 2) % 100);
            
            // Database Load (simulated based on records)
            int dbLoad = Math.min(100, ((totalUsers + totalFiles) % 100));
            
            // Network Traffic (simulated)
            int networkTraffic = Math.min(100, (totalLogs % 100));
            
            performance.put("cpuUsage", cpuUsage);
            performance.put("memoryUsage", memoryUsage);
            performance.put("databaseLoad", dbLoad);
            performance.put("networkTraffic", networkTraffic);
            
        } catch (Exception e) {
            System.err.println("Error calculating performance: " + e.getMessage());
            // Return defaults
            performance.put("cpuUsage", 34);
            performance.put("memoryUsage", 62);
            performance.put("databaseLoad", 28);
            performance.put("networkTraffic", 45);
        }
        
        return performance;
    }
    
    /**
     * Get all users for management
     */
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(String role) {
        return userDAO.getUsersByRole(role);
    }
    
    /**
     * Get all files for monitoring
     */
    public List<File> getAllFiles() {
        return fileDAO.getAllFiles();
    }
    
    /**
     * Get files by status
     */
    public List<File> getFilesByStatus(String status) {
        return fileDAO.getFilesByStatus(status);
    }
    
    /**
     * Get all audit logs
     */
    public List<AuditLog> getAllAuditLogs() {
        return auditLogDAO.getAllAuditLogs();
    }
    
    /**
     * Get audit logs by action type
     */
    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogDAO.getAuditLogsByAction(action);
    }
    
    /**
     * Calculate system health percentage
     */
    private double calculateSystemHealth(int verified, int tampered, int total) {
        if (total == 0) return 100.0;
        
        double healthScore = ((double) verified / total) * 100;
        
        // Penalize for tampered files
        double penalty = ((double) tampered / total) * 50;
        
        return Math.max(0, Math.min(100, healthScore - penalty));
    }
    
    /**
     * Get user count by role
     */
    public Map<String, Integer> getUserCountByRole() {
        Map<String, Integer> roleCounts = new HashMap<>();
        
        try {
            List<User> allUsers = userDAO.getAllUsers();
            
            for (User user : allUsers) {
                String role = user.getRole();
                roleCounts.put(role, roleCounts.getOrDefault(role, 0) + 1);
            }
            
        } catch (Exception e) {
            System.err.println("Error getting user counts by role: " + e.getMessage());
        }
        
        return roleCounts;
    }
    
    /**
     * Get critical alerts (tampered files + failed checks)
     */
    public int getCriticalAlerts() {
        try {
            int tamperedFiles = fileDAO.getFileCountByStatus("Tampered");
            int failedChecks = checkDAO.getCheckCountByResult("Error");
            return tamperedFiles + failedChecks;
        } catch (Exception e) {
            System.err.println("Error getting critical alerts: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Calculate system uptime percentage (based on successful operations)
     */
    public double getSystemUptime() {
        try {
            int totalChecks = checkDAO.getCheckCount();
            int successfulChecks = checkDAO.getCheckCountByResult("Verified");
            
            if (totalChecks == 0) return 99.8;
            
            return ((double) successfulChecks / totalChecks) * 100;
            
        } catch (Exception e) {
            System.err.println("Error calculating uptime: " + e.getMessage());
            return 99.8;
        }
    }
}
package com.arion.service;

import com.arion.dao.SecurityReportsDAO;
import com.arion.dao.FileDAO;
import com.arion.dao.FileIntegrityCheckDAO;
import com.arion.dao.FileUploadHistoryDAO;
import com.arion.model.SecurityReport;
import com.arion.model.File;
import com.arion.model.FileIntegrityCheck;
import com.arion.model.FileUploadHistory;
import com.arion.model.User;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class SecurityReportService {
    
    private SecurityReportsDAO reportDAO;
    private FileDAO fileDAO;
    private FileIntegrityCheckDAO integrityCheckDAO;
    private FileUploadHistoryDAO uploadHistoryDAO;
    
    public SecurityReportService() {
        this.reportDAO = new SecurityReportsDAO();
        this.fileDAO = new FileDAO();
        this.integrityCheckDAO = new FileIntegrityCheckDAO();
        this.uploadHistoryDAO = new FileUploadHistoryDAO();
    }
    
    /**
     * Generate a Daily Report
     */
    public ReportGenerationResult generateDailyReport(int userId) {
        System.out.println("[SecurityReportService] Generating Daily Report for user: " + userId);
        
        try {
            StringBuilder content = new StringBuilder();
            content.append("DAILY SECURITY REPORT\n");
            content.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("\n\n");
            
            // Get today's activities
            List<FileUploadHistory> todayActivities = uploadHistoryDAO.getHistoryByActionType(userId, "Upload");
            List<FileIntegrityCheck> todayChecks = integrityCheckDAO.getChecksByUser(userId, 100);
            
            content.append("=== TODAY'S ACTIVITIES ===\n");
            content.append("Files Uploaded: ").append(todayActivities.size()).append("\n");
            content.append("Integrity Checks: ").append(todayChecks.size()).append("\n\n");
            
            // Integrity check summary
            long verifiedCount = todayChecks.stream()
                .filter(c -> "Verified".equals(c.getCheckResult()))
                .count();
            long tamperedCount = todayChecks.stream()
                .filter(c -> "Tampered".equals(c.getCheckResult()))
                .count();
            
            content.append("=== INTEGRITY CHECK SUMMARY ===\n");
            content.append("Verified Files: ").append(verifiedCount).append("\n");
            content.append("Tampered Files: ").append(tamperedCount).append("\n");
            
            String reportName = "Daily Report - " + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            return saveReport(reportName, "Daily", userId, content.toString(), null);
            
        } catch (Exception e) {
            System.err.println("[SecurityReportService] Error generating daily report: " + e.getMessage());
            return new ReportGenerationResult(false, "Error generating report: " + e.getMessage(), -1);
        }
    }
    
    /**
     * Generate a Weekly Summary
     */
    public ReportGenerationResult generateWeeklySummary(int userId) {
        System.out.println("[SecurityReportService] Generating Weekly Summary for user: " + userId);
        
        try {
            StringBuilder content = new StringBuilder();
            content.append("WEEKLY SECURITY SUMMARY\n");
            content.append("Week of: ").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("\n\n");
            
            List<File> userFiles = fileDAO.getFilesByUser(userId);
            List<FileUploadHistory> uploadHistory = uploadHistoryDAO.getRecentHistory(userId, 1000);
            
            content.append("=== WEEKLY OVERVIEW ===\n");
            content.append("Total Files: ").append(userFiles.size()).append("\n");
            content.append("Total Activities: ").append(uploadHistory.size()).append("\n\n");
            
            // File status breakdown
            long verifiedFiles = userFiles.stream().filter(f -> "Verified".equals(f.getStatus())).count();
            long tamperedFiles = userFiles.stream().filter(f -> "Tampered".equals(f.getStatus())).count();
            long protectedFiles = userFiles.stream().filter(f -> "Protected".equals(f.getStatus())).count();
            
            content.append("=== FILE STATUS BREAKDOWN ===\n");
            content.append("Verified Files: ").append(verifiedFiles).append("\n");
            content.append("Protected Files: ").append(protectedFiles).append("\n");
            content.append("Tampered Files: ").append(tamperedFiles).append("\n");
            
            String reportName = "Weekly Summary - Week of " + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            return saveReport(reportName, "Weekly", userId, content.toString(), null);
            
        } catch (Exception e) {
            System.err.println("[SecurityReportService] Error generating weekly summary: " + e.getMessage());
            return new ReportGenerationResult(false, "Error generating report: " + e.getMessage(), -1);
        }
    }
    
    /**
     * Generate a Monthly Analysis
     */
    public ReportGenerationResult generateMonthlyAnalysis(int userId) {
        System.out.println("[SecurityReportService] Generating Monthly Analysis for user: " + userId);
        
        try {
            StringBuilder content = new StringBuilder();
            content.append("MONTHLY SECURITY ANALYSIS\n");
            content.append("Month: ").append(new SimpleDateFormat("MMMM yyyy").format(new Date())).append("\n\n");
            
            List<File> userFiles = fileDAO.getFilesByUser(userId);
            List<FileUploadHistory> allActivities = uploadHistoryDAO.getRecentHistory(userId, 10000);
            
            content.append("=== MONTHLY STATISTICS ===\n");
            content.append("Total Files Managed: ").append(userFiles.size()).append("\n");
            content.append("Total Activities: ").append(allActivities.size()).append("\n\n");
            
            // Activity breakdown
            long uploadCount = allActivities.stream().filter(a -> "Upload".equals(a.getActionType())).count();
            long verifyCount = allActivities.stream().filter(a -> "Verify".equals(a.getActionType())).count();
            
            content.append("=== ACTIVITY BREAKDOWN ===\n");
            content.append("Files Uploaded: ").append(uploadCount).append("\n");
            content.append("Files Verified: ").append(verifyCount).append("\n");
            
            // Alerts
            long failedActivities = allActivities.stream().filter(a -> "Failed".equals(a.getActionStatus())).count();
            
            content.append("\n=== ALERTS ===\n");
            content.append("Failed Operations: ").append(failedActivities).append("\n");
            
            String reportName = "Monthly Analysis - " + new SimpleDateFormat("MMMM yyyy").format(new Date());
            return saveReport(reportName, "Monthly", userId, content.toString(), null);
            
        } catch (Exception e) {
            System.err.println("[SecurityReportService] Error generating monthly analysis: " + e.getMessage());
            return new ReportGenerationResult(false, "Error generating report: " + e.getMessage(), -1);
        }
    }
    
    /**
     * Generate Tampering Report
     */
    public ReportGenerationResult generateTamperingReport(int userId) {
        System.out.println("[SecurityReportService] Generating Tampering Report for user: " + userId);
        
        try {
            StringBuilder content = new StringBuilder();
            content.append("TAMPERING DETECTION REPORT\n");
            content.append("Generated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");
            
            List<File> userFiles = fileDAO.getFilesByUser(userId);
            List<File> tamperedFiles = fileDAO.getFilesByStatus("Tampered");
            
            content.append("=== TAMPERING SUMMARY ===\n");
            content.append("Total Files Monitored: ").append(userFiles.size()).append("\n");
            content.append("Tampered Files Detected: ").append(tamperedFiles.size()).append("\n\n");
            
            if (!tamperedFiles.isEmpty()) {
                content.append("=== TAMPERED FILES ===\n");
                for (File file : tamperedFiles) {
                    if (file.getUploadedBy() == userId) {
                        content.append("File: ").append(file.getFileName()).append("\n");
                        content.append("  Status: ").append(file.getStatus()).append("\n");
                        content.append("  Last Verified: ").append(file.getLastVerified()).append("\n");
                        content.append("  Hash: ").append(file.getHashValue().substring(0, 20)).append("...\n\n");
                    }
                }
            }
            
            String reportName = "Tampering Report - " + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            return saveReport(reportName, "Tampering", userId, content.toString(), null);
            
        } catch (Exception e) {
            System.err.println("[SecurityReportService] Error generating tampering report: " + e.getMessage());
            return new ReportGenerationResult(false, "Error generating report: " + e.getMessage(), -1);
        }
    }
    
    /**
     * Generate PDF Report
     */
    public SecurityReportPDFGenerator.PDFGenerationResult generatePDFReport(int reportId, int userId, User user) {
        System.out.println("[SecurityReportService] Generating PDF for report: " + reportId);
        SecurityReportPDFGenerator pdfGenerator = new SecurityReportPDFGenerator();
        return pdfGenerator.generatePDFReport(reportId, userId, user);
    }
    
    /**
     * Get all reports for a user
     */
    public List<SecurityReport> getUserReports(int userId) {
        System.out.println("[SecurityReportService] Fetching reports for user: " + userId);
        return reportDAO.getReportsByUser(userId);
    }
    
    /**
     * Get recent reports
     */
    public List<SecurityReport> getRecentReports(int limit) {
        System.out.println("[SecurityReportService] Fetching " + limit + " recent reports");
        return reportDAO.getRecentReports(limit);
    }
    
    /**
     * Get reports by type
     */
    public List<SecurityReport> getReportsByType(String reportType) {
        System.out.println("[SecurityReportService] Fetching reports of type: " + reportType);
        return reportDAO.getReportsByType(reportType);
    }
    
    /**
     * Get reports by status
     */
    public List<SecurityReport> getReportsByStatus(String status) {
        System.out.println("[SecurityReportService] Fetching reports with status: " + status);
        return reportDAO.getReportsByStatus(status);
    }
    
    /**
     * Mark report as reviewed
     */
    public boolean markReportAsReviewed(int reportId, int reviewedBy, String notes) {
        System.out.println("[SecurityReportService] Marking report " + reportId + " as reviewed");
        return reportDAO.markAsReviewed(reportId, reviewedBy, notes);
    }
    
    /**
     * Get report statistics
     */
    public ReportStatistics getReportStatistics() {
        ReportStatistics stats = new ReportStatistics();
        stats.setTotalReports(reportDAO.getReportCount());
        stats.setNewReports(reportDAO.getReportCountByStatus("New"));
        stats.setReviewedReports(reportDAO.getReportCountByStatus("Reviewed"));
        stats.setFlaggedReports(reportDAO.getReportCountByStatus("Flagged"));
        
        return stats;
    }
    
    /**
     * Save a report to database
     */
    private ReportGenerationResult saveReport(String reportName, String reportType, 
                                             int userId, String content, String notes) {
        try {
            SecurityReport report = new SecurityReport();
            report.setReportName(reportName);
            report.setReportType(reportType);
            report.setReportDate(new Date());
            report.setStatus("New");
            report.setContent(content);
            report.setGeneratedBy(userId);
            report.setNotes(notes);
            
            int reportId = reportDAO.addReport(report);
            
            if (reportId > 0) {
                System.out.println("[SecurityReportService] Report saved successfully with ID: " + reportId);
                return new ReportGenerationResult(true, "Report generated successfully!", reportId);
            } else {
                System.err.println("[SecurityReportService] Failed to save report to database");
                return new ReportGenerationResult(false, "Failed to save report", -1);
            }
            
        } catch (Exception e) {
            System.err.println("[SecurityReportService] Error saving report: " + e.getMessage());
            return new ReportGenerationResult(false, "Error saving report: " + e.getMessage(), -1);
        }
    }
    
    /**
     * Report Generation Result Class
     */
    public static class ReportGenerationResult {
        private boolean success;
        private String message;
        private int reportId;
        
        public ReportGenerationResult(boolean success, String message, int reportId) {
            this.success = success;
            this.message = message;
            this.reportId = reportId;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getReportId() { return reportId; }
    }
    
    /**
     * Report Statistics Class
     */
    public static class ReportStatistics {
        private int totalReports;
        private int newReports;
        private int reviewedReports;
        private int flaggedReports;
        
        public int getTotalReports() { return totalReports; }
        public void setTotalReports(int total) { this.totalReports = total; }
        
        public int getNewReports() { return newReports; }
        public void setNewReports(int newReports) { this.newReports = newReports; }
        
        public int getReviewedReports() { return reviewedReports; }
        public void setReviewedReports(int reviewed) { this.reviewedReports = reviewed; }
        
        public int getFlaggedReports() { return flaggedReports; }
        public void setFlaggedReports(int flagged) { this.flaggedReports = flagged; }
    }
}
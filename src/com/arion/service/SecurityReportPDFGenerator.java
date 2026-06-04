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

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityReportPDFGenerator {
    
    private static final String UPLOAD_DIRECTORY = "uploads";
    private SecurityReportsDAO reportDAO;
    private FileDAO fileDAO;
    private FileIntegrityCheckDAO integrityCheckDAO;
    private FileUploadHistoryDAO uploadHistoryDAO;
    
    public SecurityReportPDFGenerator() {
        this.reportDAO = new SecurityReportsDAO();
        this.fileDAO = new FileDAO();
        this.integrityCheckDAO = new FileIntegrityCheckDAO();
        this.uploadHistoryDAO = new FileUploadHistoryDAO();
    }
    
    /**
     * Generate PDF report and save to uploads folder
     */
    public PDFGenerationResult generatePDFReport(int reportId, int userId, User user) {
        System.out.println("[SecurityReportPDFGenerator] Generating PDF for report: " + reportId);
        
        try {
            SecurityReport report = reportDAO.getReportById(reportId);
            if (report == null) {
                return new PDFGenerationResult(false, "Report not found", null);
            }
            
            // Create PDF document
            StringBuilder pdfContent = new StringBuilder();
            
            // Header
            pdfContent.append(generateHeader(report, user));
            
            // Executive Summary
            pdfContent.append(generateExecutiveSummary(userId, report));
            
            // File Integrity Section
            pdfContent.append(generateFileIntegritySection(userId));
            
            // Activity Log
            pdfContent.append(generateActivityLog(userId));
            
            // Hash Algorithm Usage
            pdfContent.append(generateHashAlgorithmSection(userId));
            
            // Security Alerts
            pdfContent.append(generateSecurityAlerts(userId));
            
            // Statistics
            pdfContent.append(generateStatistics(userId));
            
            // Footer
            pdfContent.append(generateFooter());
            
            // Save to file
            String fileName = generateFileName(report, reportId);
            String filePath = UPLOAD_DIRECTORY + java.io.File.separator + fileName;
            
            savePDFFile(pdfContent.toString(), filePath);
            
            System.out.println("[SecurityReportPDFGenerator] PDF generated successfully: " + filePath);
            return new PDFGenerationResult(true, "PDF generated successfully", filePath);
            
        } catch (Exception e) {
            System.err.println("[SecurityReportPDFGenerator] Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return new PDFGenerationResult(false, "Error: " + e.getMessage(), null);
        }
    }
    
    private String generateHeader(SecurityReport report, User user) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        return "╔════════════════════════════════════════════════════════════════╗\n" +
               "║                    ARION SECURITY REPORT                       ║\n" +
               "║                    File Integrity System                       ║\n" +
               "╚════════════════════════════════════════════════════════════════╝\n\n" +
               
               "REPORT DETAILS:\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "Report ID:          " + report.getReportId() + "\n" +
               "Report Type:        " + report.getReportType() + "\n" +
               "Report Status:      " + report.getStatus() + "\n" +
               "Generated Date:     " + dateFormat.format(new Date()) + "\n" +
               "Report Date:        " + new SimpleDateFormat("yyyy-MM-dd").format(report.getReportDate()) + "\n\n" +
               
               "USER INFORMATION:\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "Full Name:          " + user.getFullName() + "\n" +
               "Username:           " + user.getUsername() + "\n" +
               "Email:              " + user.getEmail() + "\n" +
               "Role:               " + user.getRole() + "\n\n";
    }
    
    private String generateExecutiveSummary(int userId, SecurityReport report) {
        List<File> userFiles = fileDAO.getFilesByUser(userId);
        List<FileUploadHistory> activities = uploadHistoryDAO.getRecentHistory(userId, 1000);
        
        long verifiedCount = userFiles.stream().filter(f -> "Verified".equals(f.getStatus())).count();
        long tamperedCount = userFiles.stream().filter(f -> "Tampered".equals(f.getStatus())).count();
        long protectedCount = userFiles.stream().filter(f -> "Protected".equals(f.getStatus())).count();
        long pendingCount = userFiles.stream().filter(f -> "Pending".equals(f.getStatus())).count();
        
        return "EXECUTIVE SUMMARY:\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "Total Files Monitored:      " + userFiles.size() + "\n" +
               "Verified Files:             " + verifiedCount + "\n" +
               "Tampered Files:             " + tamperedCount + "\n" +
               "Protected Files:            " + protectedCount + "\n" +
               "Pending Files:              " + pendingCount + "\n" +
               "Total Activities:           " + activities.size() + "\n" +
               "Security Issues Found:      " + tamperedCount + "\n\n";
    }
    
    private String generateFileIntegritySection(int userId) {
        List<File> userFiles = fileDAO.getFilesByUser(userId);
        
        List<File> verifiedFiles = userFiles.stream()
            .filter(f -> "Verified".equals(f.getStatus()))
            .collect(Collectors.toList());
        
        List<File> tamperedFiles = userFiles.stream()
            .filter(f -> "Tampered".equals(f.getStatus()))
            .collect(Collectors.toList());
        
        StringBuilder section = new StringBuilder();
        section.append("FILE INTEGRITY ANALYSIS:\n");
        section.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        section.append("Total Files Scanned:        ").append(userFiles.size()).append("\n");
        section.append("Verified Files:             ").append(verifiedFiles.size()).append("\n");
        section.append("Tampered Files:             ").append(tamperedFiles.size()).append("\n\n");
        
        if (!verifiedFiles.isEmpty()) {
            section.append("VERIFIED FILES (Safe & Unmodified):\n");
            section.append("────────────────────────────────────────────────────────────\n");
            for (File file : verifiedFiles.stream().limit(10).collect(Collectors.toList())) {
                section.append("  • ").append(file.getFileName())
                       .append(" (").append(formatFileSize(file.getFileSize())).append(")")
                       .append(" - Algorithm: ").append(file.getHashAlgorithm()).append("\n");
            }
            if (verifiedFiles.size() > 10) {
                section.append("  ... and ").append(verifiedFiles.size() - 10).append(" more files\n");
            }
            section.append("\n");
        }
        
        if (!tamperedFiles.isEmpty()) {
            section.append("⚠️  TAMPERED FILES (Potential Security Risk):\n");
            section.append("────────────────────────────────────────────────────────────\n");
            for (File file : tamperedFiles) {
                section.append("  ⚠ ").append(file.getFileName()).append("\n");
                section.append("    Size: ").append(formatFileSize(file.getFileSize())).append("\n");
                section.append("    Algorithm: ").append(file.getHashAlgorithm()).append("\n");
                section.append("    Last Verified: ").append(file.getLastVerified()).append("\n");
                section.append("    Hash: ").append(file.getHashValue().substring(0, 32)).append("...\n");
                section.append("    ACTION REQUIRED: Verify and restore from backup\n\n");
            }
        }
        
        return section.toString();
    }
    
    private String generateActivityLog(int userId) {
        List<FileUploadHistory> activities = uploadHistoryDAO.getRecentHistory(userId, 100);
        
        StringBuilder section = new StringBuilder();
        section.append("ACTIVITY LOG:\n");
        section.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        section.append("Total Activities: ").append(activities.size()).append("\n");
        section.append("────────────────────────────────────────────────────────────\n");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (FileUploadHistory activity : activities.stream().limit(20).collect(Collectors.toList())) {
            section.append("[").append(dateFormat.format(activity.getTimestamp())).append("] ");
            section.append(activity.getActionType()).append(" - ");
            section.append(activity.getActionStatus()).append(" - ");
            section.append(activity.getDetails()).append("\n");
        }
        
        if (activities.size() > 20) {
            section.append("\n... and ").append(activities.size() - 20).append(" more activities\n");
        }
        
        section.append("\n");
        return section.toString();
    }
    
    private String generateHashAlgorithmSection(int userId) {
        List<File> userFiles = fileDAO.getFilesByUser(userId);
        
        long sha256Count = userFiles.stream().filter(f -> "SHA-256".equals(f.getHashAlgorithm())).count();
        long sha512Count = userFiles.stream().filter(f -> "SHA-512".equals(f.getHashAlgorithm())).count();
        long sha1Count = userFiles.stream().filter(f -> "SHA-1".equals(f.getHashAlgorithm())).count();
        long md5Count = userFiles.stream().filter(f -> "MD5".equals(f.getHashAlgorithm())).count();
        
        return "HASH ALGORITHM USAGE:\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "SHA-256 (Recommended):     " + sha256Count + " files\n" +
               "SHA-512 (Most Secure):    " + sha512Count + " files\n" +
               "SHA-1 (Legacy):           " + sha1Count + " files\n" +
               "MD5 (Not Recommended):    " + md5Count + " files\n\n";
    }
    
    private String generateSecurityAlerts(int userId) {
        List<File> tamperedFiles = fileDAO.getFilesByStatus("Tampered");
        List<FileUploadHistory> failedActivities = uploadHistoryDAO.getRecentHistory(userId, 1000)
            .stream()
            .filter(a -> "Failed".equals(a.getActionStatus()))
            .collect(Collectors.toList());
        
        StringBuilder section = new StringBuilder();
        section.append("SECURITY ALERTS & RECOMMENDATIONS:\n");
        section.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        if (tamperedFiles.isEmpty() && failedActivities.isEmpty()) {
            section.append("✓ No security alerts detected\n");
            section.append("✓ All files are protected and verified\n");
            section.append("✓ System is operating normally\n\n");
        } else {
            if (!tamperedFiles.isEmpty()) {
                section.append("⚠️  ALERT: ").append(tamperedFiles.size()).append(" tampered files detected\n");
                section.append("   Recommendation: Verify and restore from backup immediately\n\n");
            }
            
            if (!failedActivities.isEmpty()) {
                section.append("⚠️  ALERT: ").append(failedActivities.size()).append(" failed operations detected\n");
                section.append("   Recommendation: Check logs and retry operations\n\n");
            }
        }
        
        section.append("BEST PRACTICES:\n");
        section.append("• Always use SHA-256 or SHA-512 for new files\n");
        section.append("• Regularly verify file integrity\n");
        section.append("• Maintain backups of critical files\n");
        section.append("• Monitor tampered files closely\n");
        section.append("• Review activity logs periodically\n\n");
        
        return section.toString();
    }
    
    private String generateStatistics(int userId) {
        List<File> userFiles = fileDAO.getFilesByUser(userId);
        List<FileUploadHistory> activities = uploadHistoryDAO.getRecentHistory(userId, 1000);
        
        long uploadCount = activities.stream().filter(a -> "Upload".equals(a.getActionType())).count();
        long verifyCount = activities.stream().filter(a -> "Verify".equals(a.getActionType())).count();
        long successCount = activities.stream().filter(a -> "Success".equals(a.getActionStatus())).count();
        long failureCount = activities.stream().filter(a -> "Failed".equals(a.getActionStatus())).count();
        
        return "STATISTICS:\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "Total File Operations:      " + activities.size() + "\n" +
               "Successful Operations:      " + successCount + "\n" +
               "Failed Operations:          " + failureCount + "\n" +
               "Success Rate:               " + (activities.isEmpty() ? "0" : String.format("%.1f", (successCount * 100.0 / activities.size()))) + "%\n" +
               "Total Files Uploaded:       " + uploadCount + "\n" +
               "Total Verifications:        " + verifyCount + "\n\n";
    }
    
    private String generateFooter() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        return "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "COMPLIANCE & AUDIT:\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "This report has been generated by ARION Security System\n" +
               "Report Generated: " + dateFormat.format(new Date()) + "\n" +
               "Report Format: PDF\n\n" +
               
               "This security report contains sensitive information and should be\n" +
               "stored securely. Unauthorized access or distribution is prohibited.\n\n" +
               
               "For more information, visit: www.arion-security.com\n" +
               "Contact: security@arion-security.com\n\n" +
               
               "╔════════════════════════════════════════════════════════════════╗\n" +
               "║                      END OF REPORT                            ║\n" +
               "╚════════════════════════════════════════════════════════════════╝\n";
    }
    
    private String generateFileName(SecurityReport report, int reportId) {
        String reportType = report.getReportType().replace(" ", "_");
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        return "Report_" + reportType + "_" + reportId + "_" + timestamp + ".txt";
    }
    
    private void savePDFFile(String content, String filePath) throws IOException {
        // Ensure upload directory exists
        java.io.File uploadDir = new java.io.File(UPLOAD_DIRECTORY);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(content.getBytes());
            fos.flush();
            System.out.println("[SecurityReportPDFGenerator] File saved: " + filePath);
        }
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.2f %s", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * PDF Generation Result Class
     */
    public static class PDFGenerationResult {
        private boolean success;
        private String message;
        private String filePath;
        
        public PDFGenerationResult(boolean success, String message, String filePath) {
            this.success = success;
            this.message = message;
            this.filePath = filePath;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getFilePath() { return filePath; }
    }
}
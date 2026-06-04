package com.arion.service;

import com.arion.dao.FileDAO;
import com.arion.dao.FileIntegrityCheckDAO;
import com.arion.dao.AuditLogDAO;
import com.arion.dao.FileUploadHistoryDAO;
import com.arion.model.File;
import com.arion.model.FileIntegrityCheck;
import com.arion.model.AuditLog;
import com.arion.model.FileUploadHistory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class FileVerificationService {
    
    private FileDAO fileDAO;
    private FileIntegrityCheckDAO checkDAO;
    private AuditLogDAO auditLogDAO;
    private FileUploadHistoryDAO historyDAO;
    private HashService hashService;
    
    public FileVerificationService() {
        this.fileDAO = new FileDAO();
        this.checkDAO = new FileIntegrityCheckDAO();
        this.auditLogDAO = new AuditLogDAO();
        this.historyDAO = new FileUploadHistoryDAO();
        this.hashService = new HashService();
    }
    
    /**
     * Verify a file's integrity
     */
    public VerificationResult verifyFile(int fileId, int userId) {
        VerificationResult result = new VerificationResult();
        
        try {
            System.out.println("=== VERIFICATION DEBUG ===");
            System.out.println("File ID: " + fileId);
            System.out.println("User ID: " + userId);
            
            // Step 1: Get file from database
            File file = fileDAO.getFileById(fileId);
            if (file == null) {
                result.setSuccess(false);
                result.setMessage("File not found in database");
                return result;
            }
            
            System.out.println("File found: " + file.getFileName());
            System.out.println("Stored hash: " + file.getHashValue());
            System.out.println("File path: " + file.getFilePath());
            
            // Step 2: Check if physical file exists
            java.io.File physicalFile = new java.io.File(file.getFilePath());
            if (!physicalFile.exists()) {
                result.setSuccess(false);
                result.setMessage("Physical file not found at: " + file.getFilePath());
                result.setVerificationStatus("Error");
                
                // Update file status
                fileDAO.updateFileStatus(fileId, "Tampered");
                
                // Log the check
                logIntegrityCheck(fileId, userId, file.getHashValue(), "FILE_NOT_FOUND", "Error");
                
                return result;
            }
            
            System.out.println("Physical file exists");
            
            // Step 3: Calculate current hash
            String currentHash = hashService.generateFileHash(physicalFile, file.getHashAlgorithm());
            System.out.println("Current hash: " + currentHash);
            
            // Step 4: Compare hashes
            boolean isIntact = currentHash.equalsIgnoreCase(file.getHashValue());
            System.out.println("Hashes match: " + isIntact);
            
            String status = isIntact ? "Verified" : "Tampered";
            
            // Step 5: Update file status and last verified
            fileDAO.updateFileStatus(fileId, status);
            fileDAO.updateLastVerified(fileId);
            
            // Step 6: Log the integrity check
            int checkId = logIntegrityCheck(fileId, userId, file.getHashValue(), currentHash, status);
            
            // Step 7: Log audit
            logAuditAction(userId, fileId, file.getFileName(), status);
            
            // Step 8: Build result
            result.setSuccess(true);
            result.setFileId(fileId);
            result.setFileName(file.getFileName());
            result.setOriginalHash(file.getHashValue());
            result.setCurrentHash(currentHash);
            result.setVerificationStatus(status);
            result.setMessage(isIntact ? "File integrity verified successfully" : "File has been tampered with!");
            result.setCheckId(checkId);
            
            System.out.println("Verification complete: " + status);
            
        } catch (NoSuchAlgorithmException e) {
            result.setSuccess(false);
            result.setMessage("Hash algorithm error: " + e.getMessage());
            System.err.println("Algorithm error: " + e.getMessage());
        } catch (IOException e) {
            result.setSuccess(false);
            result.setMessage("Error reading file: " + e.getMessage());
            System.err.println("IO error: " + e.getMessage());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Verification error: " + e.getMessage());
            System.err.println("Verification error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Log integrity check to database
     */
    private int logIntegrityCheck(int fileId, int userId, String previousHash, 
                                   String currentHash, String result) {
        try {
            FileIntegrityCheck check = new FileIntegrityCheck();
            check.setFileId(fileId);
            check.setCheckedBy(userId);
            check.setPreviousHash(previousHash);
            check.setCurrentHash(currentHash);
            check.setCheckResult(result);
            
            return checkDAO.addIntegrityCheck(check);
        } catch (Exception e) {
            System.err.println("Error logging integrity check: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Log audit action
     */
    private void logAuditAction(int userId, int fileId, String fileName, String status) {
        try {
            AuditLog log = new AuditLog();
            log.setUserId(userId);
            log.setAction("FILE_VERIFICATION");
            log.setTargetType("FILE");
            log.setTargetId(fileId);
            log.setIpAddress("127.0.0.1"); // Get actual IP if needed
            log.setDetails("Verified file: " + fileName + " - Result: " + status);
            
            auditLogDAO.addAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error logging audit: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Verify all files for a user
     */
    public BatchVerificationResult verifyAllUserFiles(int userId) {
        BatchVerificationResult batchResult = new BatchVerificationResult();
        
        try {
            java.util.List<File> userFiles = fileDAO.getFilesByUser(userId);
            
            for (File file : userFiles) {
                VerificationResult result = verifyFile(file.getFileId(), userId);
                batchResult.addResult(result);
                
                // Small delay to prevent overwhelming the system
                Thread.sleep(100);
            }
            
        } catch (Exception e) {
            System.err.println("Error in batch verification: " + e.getMessage());
        }
        
        return batchResult;
    }
    
    /**
     * Result class for single verification
     */
    public static class VerificationResult {
        private boolean success;
        private int fileId;
        private String fileName;
        private String originalHash;
        private String currentHash;
        private String verificationStatus; // Verified, Tampered, Error
        private String message;
        private int checkId;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public int getFileId() { return fileId; }
        public void setFileId(int fileId) { this.fileId = fileId; }
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getOriginalHash() { return originalHash; }
        public void setOriginalHash(String originalHash) { this.originalHash = originalHash; }
        
        public String getCurrentHash() { return currentHash; }
        public void setCurrentHash(String currentHash) { this.currentHash = currentHash; }
        
        public String getVerificationStatus() { return verificationStatus; }
        public void setVerificationStatus(String status) { this.verificationStatus = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public int getCheckId() { return checkId; }
        public void setCheckId(int checkId) { this.checkId = checkId; }
    }
    
    /**
     * Result class for batch verification
     */
    public static class BatchVerificationResult {
        private java.util.List<VerificationResult> results = new java.util.ArrayList<>();
        
        public void addResult(VerificationResult result) {
            results.add(result);
        }
        
        public java.util.List<VerificationResult> getResults() {
            return results;
        }
        
        public int getTotalCount() {
            return results.size();
        }
        
        public int getVerifiedCount() {
            return (int) results.stream()
                .filter(r -> "Verified".equals(r.getVerificationStatus()))
                .count();
        }
        
        public int getTamperedCount() {
            return (int) results.stream()
                .filter(r -> "Tampered".equals(r.getVerificationStatus()))
                .count();
        }
        
        public int getErrorCount() {
            return (int) results.stream()
                .filter(r -> "Error".equals(r.getVerificationStatus()))
                .count();
        }
    }
}
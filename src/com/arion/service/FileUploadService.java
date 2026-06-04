package com.arion.service;

import com.arion.dao.FileDAO;
import com.arion.model.File;
import com.arion.dao.FileUploadHistoryDAO;
import com.arion.model.FileUploadHistory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;

public class FileUploadService {
    
    private static final String UPLOAD_DIRECTORY = "uploads";
    
    private FileDAO fileDAO;
    private HashService hashService;
    private FileUploadHistoryDAO historyDAO;
    
    public FileUploadService() {
        this.fileDAO = new FileDAO();
        this.hashService = new HashService();
        createUploadDirectory();
        this.fileDAO = new FileDAO();
        this.hashService = new HashService();
        this.historyDAO = new FileUploadHistoryDAO();  // ADD THIS LINE
        createUploadDirectory();
        
        // Print absolute path
        System.out.println("Upload folder location: " + 
            java.nio.file.Paths.get("uploads").toAbsolutePath());
    }
    
    private void createUploadDirectory() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error creating upload directory: " + e.getMessage());
        }
    }
    
    public FileUploadResult uploadFile(java.io.File sourceFile, String algorithm, int userId) {
        FileUploadResult result = new FileUploadResult();
        
        try {
            System.out.println("=== UPLOAD DEBUG ===");
            System.out.println("File: " + sourceFile.getAbsolutePath());
            System.out.println("Algorithm: " + algorithm);
            System.out.println("User ID: " + userId);
            
            // Step 1: Generate hash
            System.out.println("Step 1: Generating hash...");
            String hashValue = hashService.generateFileHash(sourceFile, algorithm);
            System.out.println("Hash generated: " + hashValue);
            result.setHashValue(hashValue);
            
            // Step 2: Copy file
            System.out.println("Step 2: Copying file...");
            String storedFileName = System.currentTimeMillis() + "_" + sourceFile.getName();
            Path destinationPath = Paths.get(UPLOAD_DIRECTORY, storedFileName);
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied to: " + destinationPath.toAbsolutePath());
            
            // Step 3: Get file type
            String fileType = getFileExtension(sourceFile.getName());
            System.out.println("File type: " + fileType);
            
            // Step 4: Create File model
            System.out.println("Step 3: Creating file model...");
            File fileModel = new File();
            fileModel.setFileName(sourceFile.getName());
            fileModel.setFilePath(destinationPath.toString());
            fileModel.setFileSize(sourceFile.length());
            fileModel.setHashValue(hashValue);
            fileModel.setHashAlgorithm(algorithm);
            fileModel.setUploadedBy(userId);
            fileModel.setFileType(fileType);
            fileModel.setStatus("Pending");
            
            // Step 5: Save to database
            System.out.println("Step 4: Saving to database...");
            int fileId = fileDAO.addFile(fileModel);
            System.out.println("Database returned fileId: " + fileId);
            
            if (fileId > 0) {
                result.setSuccess(true);
                result.setFileId(fileId);
                result.setMessage("File uploaded successfully!");
                
                // LOG TO HISTORY TABLE - ADD THESE LINES:
                logUploadHistory(fileId, userId, sourceFile.getName(), 
                                hashValue, algorithm, "Success");
                
                System.out.println("SUCCESS!");
            } else {
                Files.deleteIfExists(destinationPath);
                result.setSuccess(false);
                result.setMessage("Database insert failed (returned -1)");
                System.out.println("FAILED: Database returned -1");
            }
            
            if (fileId > 0) {
                result.setSuccess(true);
                result.setFileId(fileId);
                result.setMessage("File uploaded successfully!");
                System.out.println("SUCCESS!");
            } else {
                Files.deleteIfExists(destinationPath);
                result.setSuccess(false);
                result.setMessage("Database insert failed (returned -1)");
                System.out.println("FAILED: Database returned -1");
            }
            
        } catch (NoSuchAlgorithmException e) {
            result.setSuccess(false);
            result.setMessage("Hash algorithm error: " + e.getMessage());
            System.err.println("HASH ERROR: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            result.setSuccess(false);
            result.setMessage("File IO error: " + e.getMessage());
            System.err.println("IO ERROR: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Error: " + e.getMessage());
            System.err.println("UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toUpperCase();
        }
        return "UNKNOWN";
    }
    
    public static class FileUploadResult {
        private boolean success;
        private String message;
        private String hashValue;
        private int fileId;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getHashValue() { return hashValue; }
        public void setHashValue(String hashValue) { this.hashValue = hashValue; }
        
        public int getFileId() { return fileId; }
        public void setFileId(int fileId) { this.fileId = fileId; }
    }
 // 4. Add this method at the end of the class:
    /**
     * Log file upload to history table
     */
    private void logUploadHistory(int fileId, int userId, String fileName, 
                                   String hashValue, String algorithm, String status) {
        try {
            FileUploadHistory history = new FileUploadHistory();
            history.setFileId(fileId);
            history.setUserId(userId);
            history.setActionType("Upload");
            history.setActionStatus(status);
            history.setHashValue(hashValue);
            history.setAlgorithmUsed(algorithm);
            history.setDetails("Uploaded file: " + fileName);
            
            historyDAO.addHistory(history);
        } catch (Exception e) {
            System.err.println("Error logging upload history: " + e.getMessage());
        }
    }
}
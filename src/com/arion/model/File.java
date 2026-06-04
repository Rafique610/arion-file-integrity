package com.arion.model;

import java.sql.Timestamp;

public class File {
    
    private int fileId;
    private String fileName;
    private String filePath;
    private long fileSize;
    private String hashValue;
    private String hashAlgorithm; // MD5, SHA-1, SHA-256, SHA-512
    private int uploadedBy;
    private String fileType;
    private String status; // Protected, Verified, Tampered
    private Timestamp uploadDate;
    private Timestamp lastVerified;
    
    // Additional display fields
    private String uploaderName;
    
    // Constructors
    public File() {}
    
    public File(int fileId, String fileName, String hashValue, String hashAlgorithm, String status) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.hashValue = hashValue;
        this.hashAlgorithm = hashAlgorithm;
        this.status = status;
    }
    
    // Getters and Setters
    public int getFileId() {
        return fileId;
    }
    
    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getHashValue() {
        return hashValue;
    }
    
    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }
    
    public String getHashAlgorithm() {
        return hashAlgorithm;
    }
    
    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }
    
    public int getUploadedBy() {
        return uploadedBy;
    }
    
    public void setUploadedBy(int uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getUploadDate() {
        return uploadDate;
    }
    
    public void setUploadDate(Timestamp uploadDate) {
        this.uploadDate = uploadDate;
    }
    
    public Timestamp getLastVerified() {
        return lastVerified;
    }
    
    public void setLastVerified(Timestamp lastVerified) {
        this.lastVerified = lastVerified;
    }
    
    public String getUploaderName() {
        return uploaderName;
    }
    
    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }
    
    // Utility method to format file size
    public String getFormattedFileSize() {
        if (fileSize < 1024) return fileSize + " B";
        int exp = (int) (Math.log(fileSize) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.2f %s", fileSize / Math.pow(1024, exp), pre);
    }
    
    @Override
    public String toString() {
        return "File{" +
                "fileId=" + fileId +
                ", fileName='" + fileName + '\'' +
                ", hashAlgorithm='" + hashAlgorithm + '\'' +
                ", status='" + status + '\'' +
                ", uploadDate=" + uploadDate +
                '}';
    }
}
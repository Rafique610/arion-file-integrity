package com.arion.model;

import java.sql.Timestamp;

public class FileIntegrityCheck {
    
    private int checkId;
    private int fileId;
    private int checkedBy;
    private String previousHash;
    private String currentHash;
    private String checkResult; // Verified, Tampered, Error
    private Timestamp checkDate;
    
    // Additional fields for display
    private String fileName;
    private String checkerName;
    
    // Constructors
    public FileIntegrityCheck() {}
    
    public FileIntegrityCheck(int fileId, int checkedBy, String previousHash, 
                             String currentHash, String checkResult) {
        this.fileId = fileId;
        this.checkedBy = checkedBy;
        this.previousHash = previousHash;
        this.currentHash = currentHash;
        this.checkResult = checkResult;
    }
    
    // Getters and Setters
    public int getCheckId() {
        return checkId;
    }
    
    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }
    
    public int getFileId() {
        return fileId;
    }
    
    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
    
    public int getCheckedBy() {
        return checkedBy;
    }
    
    public void setCheckedBy(int checkedBy) {
        this.checkedBy = checkedBy;
    }
    
    public String getPreviousHash() {
        return previousHash;
    }
    
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    
    public String getCurrentHash() {
        return currentHash;
    }
    
    public void setCurrentHash(String currentHash) {
        this.currentHash = currentHash;
    }
    
    public String getCheckResult() {
        return checkResult;
    }
    
    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }
    
    public Timestamp getCheckDate() {
        return checkDate;
    }
    
    public void setCheckDate(Timestamp checkDate) {
        this.checkDate = checkDate;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getCheckerName() {
        return checkerName;
    }
    
    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
    }
    
    @Override
    public String toString() {
        return "FileIntegrityCheck{" +
                "checkId=" + checkId +
                ", fileId=" + fileId +
                ", checkResult='" + checkResult + '\'' +
                ", checkDate=" + checkDate +
                '}';
    }
}
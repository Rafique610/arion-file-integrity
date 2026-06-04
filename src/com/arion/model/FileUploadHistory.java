package com.arion.model;

import java.sql.Timestamp;

public class FileUploadHistory {
    
    private int historyId;
    private int fileId;
    private int userId;
    private String actionType; // Upload, Verify, Modify, Download, Delete
    private String actionStatus; // Success, Failed, Warning
    private String hashValue;
    private String algorithmUsed; // MD5, SHA-1, SHA-256, SHA-512
    private String details;
    private Timestamp timestamp;
    
    // Additional fields for display (from JOINs)
    private String fileName;
    private String username;
    private String userFullName;
    
    // Constructors
    public FileUploadHistory() {}
    
    public FileUploadHistory(int fileId, int userId, String actionType, String actionStatus) {
        this.fileId = fileId;
        this.userId = userId;
        this.actionType = actionType;
        this.actionStatus = actionStatus;
    }
    
    // Getters and Setters
    public int getHistoryId() {
        return historyId;
    }
    
    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }
    
    public int getFileId() {
        return fileId;
    }
    
    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public String getActionStatus() {
        return actionStatus;
    }
    
    public void setActionStatus(String actionStatus) {
        this.actionStatus = actionStatus;
    }
    
    public String getHashValue() {
        return hashValue;
    }
    
    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }
    
    public String getAlgorithmUsed() {
        return algorithmUsed;
    }
    
    public void setAlgorithmUsed(String algorithmUsed) {
        this.algorithmUsed = algorithmUsed;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUserFullName() {
        return userFullName;
    }
    
    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
    
    @Override
    public String toString() {
        return "FileUploadHistory{" +
                "historyId=" + historyId +
                ", fileId=" + fileId +
                ", actionType='" + actionType + '\'' +
                ", actionStatus='" + actionStatus + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
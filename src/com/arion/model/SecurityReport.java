package com.arion.model;

import java.sql.Timestamp;
import java.util.Date;

public class SecurityReport {
    
    private int reportId;
    private String reportName;
    private String reportType; // Daily, Weekly, Monthly, Tampering, User Activity, System Performance
    private Date reportDate;
    private String status; // New, Reviewed, Flagged
    private String content;
    private int generatedBy;
    private Timestamp createdDate;
    private Integer reviewedBy; // Nullable
    private Timestamp reviewedDate; // Nullable
    private String notes;
    
    // Additional fields for display (from JOINs)
    private String generatorName;
    private String reviewerName;
    
    // Constructors
    public SecurityReport() {}
    
    public SecurityReport(String reportName, String reportType, Date reportDate, 
                         int generatedBy) {
        this.reportName = reportName;
        this.reportType = reportType;
        this.reportDate = reportDate;
        this.generatedBy = generatedBy;
        this.status = "New";
    }
    
    // Getters and Setters
    public int getReportId() {
        return reportId;
    }
    
    public void setReportId(int reportId) {
        this.reportId = reportId;
    }
    
    public String getReportName() {
        return reportName;
    }
    
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
    
    public String getReportType() {
        return reportType;
    }
    
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    
    public Date getReportDate() {
        return reportDate;
    }
    
    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public int getGeneratedBy() {
        return generatedBy;
    }
    
    public void setGeneratedBy(int generatedBy) {
        this.generatedBy = generatedBy;
    }
    
    public Timestamp getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
    
    public Integer getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    public Timestamp getReviewedDate() {
        return reviewedDate;
    }
    
    public void setReviewedDate(Timestamp reviewedDate) {
        this.reviewedDate = reviewedDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getGeneratorName() {
        return generatorName;
    }
    
    public void setGeneratorName(String generatorName) {
        this.generatorName = generatorName;
    }
    
    public String getReviewerName() {
        return reviewerName;
    }
    
    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
    
    @Override
    public String toString() {
        return "SecurityReport{" +
                "reportId=" + reportId +
                ", reportName='" + reportName + '\'' +
                ", reportType='" + reportType + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
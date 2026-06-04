package com.arion.model;

import java.sql.Timestamp;

public class AttackSimulation {
    
    private int simulationId;
    private String attackType;
    private String targetFile;
    private int intensityLevel;
    private double detectionTime;
    private String systemResponse; // Successful, Failed, Partial
    private int affectedFiles;
    private int alertsTriggered;
    private double successRate;
    private int totalDuration;
    private String simulationLog;
    private int startedBy;
    private Timestamp startTime;
    private Timestamp endTime;
    
    // Additional field from JOIN
    private String startedByName;
    
    // Constructors
    public AttackSimulation() {}
    
    public AttackSimulation(String attackType, String targetFile, int intensityLevel, int startedBy) {
        this.attackType = attackType;
        this.targetFile = targetFile;
        this.intensityLevel = intensityLevel;
        this.startedBy = startedBy;
        this.systemResponse = "Successful";
        this.affectedFiles = 0;
        this.alertsTriggered = 0;
        this.successRate = 0.0;
        this.totalDuration = 0;
    }
    
    // Getters and Setters
    public int getSimulationId() {
        return simulationId;
    }
    
    public void setSimulationId(int simulationId) {
        this.simulationId = simulationId;
    }
    
    public String getAttackType() {
        return attackType;
    }
    
    public void setAttackType(String attackType) {
        this.attackType = attackType;
    }
    
    public String getTargetFile() {
        return targetFile;
    }
    
    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }
    
    public int getIntensityLevel() {
        return intensityLevel;
    }
    
    public void setIntensityLevel(int intensityLevel) {
        this.intensityLevel = intensityLevel;
    }
    
    public double getDetectionTime() {
        return detectionTime;
    }
    
    public void setDetectionTime(double detectionTime) {
        this.detectionTime = detectionTime;
    }
    
    public String getSystemResponse() {
        return systemResponse;
    }
    
    public void setSystemResponse(String systemResponse) {
        this.systemResponse = systemResponse;
    }
    
    public int getAffectedFiles() {
        return affectedFiles;
    }
    
    public void setAffectedFiles(int affectedFiles) {
        this.affectedFiles = affectedFiles;
    }
    
    public int getAlertsTriggered() {
        return alertsTriggered;
    }
    
    public void setAlertsTriggered(int alertsTriggered) {
        this.alertsTriggered = alertsTriggered;
    }
    
    public double getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }
    
    public int getTotalDuration() {
        return totalDuration;
    }
    
    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }
    
    public String getSimulationLog() {
        return simulationLog;
    }
    
    public void setSimulationLog(String simulationLog) {
        this.simulationLog = simulationLog;
    }
    
    public int getStartedBy() {
        return startedBy;
    }
    
    public void setStartedBy(int startedBy) {
        this.startedBy = startedBy;
    }
    
    public Timestamp getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }
    
    public Timestamp getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
    
    public String getStartedByName() {
        return startedByName;
    }
    
    public void setStartedByName(String startedByName) {
        this.startedByName = startedByName;
    }
    
    @Override
    public String toString() {
        return "AttackSimulation{" +
                "simulationId=" + simulationId +
                ", attackType='" + attackType + '\'' +
                ", targetFile='" + targetFile + '\'' +
                ", intensityLevel=" + intensityLevel +
                ", detectionTime=" + detectionTime +
                ", systemResponse='" + systemResponse + '\'' +
                ", affectedFiles=" + affectedFiles +
                ", alertsTriggered=" + alertsTriggered +
                ", successRate=" + successRate +
                ", totalDuration=" + totalDuration +
                ", startedBy=" + startedBy +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
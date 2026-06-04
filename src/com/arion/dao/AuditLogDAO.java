package com.arion.dao;

import com.arion.model.AuditLog;
import com.arion.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {
    
    // Create - Add new audit log entry
    public boolean addAuditLog(AuditLog log) {
        String sql = "INSERT INTO audit_logs (user_id, action, target_type, target_id, ip_address, details) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, log.getUserId());
            pstmt.setString(2, log.getAction());
            pstmt.setString(3, log.getTargetType());
            pstmt.setInt(4, log.getTargetId());
            pstmt.setString(5, log.getIpAddress());
            pstmt.setString(6, log.getDetails());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding audit log: " + e.getMessage());
            return false;
        }
    }
    
    // Read - Get all audit logs
    public List<AuditLog> getAllAuditLogs() {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT a.*, u.username " +
                     "FROM audit_logs a " +
                     "LEFT JOIN users u ON a.user_id = u.user_id " +
                     "ORDER BY a.timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                logs.add(extractAuditLogFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all audit logs: " + e.getMessage());
        }
        
        return logs;
    }
    
    // Read - Get audit logs by user
    public List<AuditLog> getAuditLogsByUser(int userId) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT a.*, u.username " +
                     "FROM audit_logs a " +
                     "LEFT JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.user_id = ? " +
                     "ORDER BY a.timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(extractAuditLogFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting audit logs by user: " + e.getMessage());
        }
        
        return logs;
    }
    
    // Read - Get audit logs by action
    public List<AuditLog> getAuditLogsByAction(String action) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT a.*, u.username " +
                     "FROM audit_logs a " +
                     "LEFT JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.action = ? " +
                     "ORDER BY a.timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, action);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(extractAuditLogFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting audit logs by action: " + e.getMessage());
        }
        
        return logs;
    }
    
    // Read - Get recent audit logs (limit)
    public List<AuditLog> getRecentAuditLogs(int limit) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT a.*, u.username " +
                     "FROM audit_logs a " +
                     "LEFT JOIN users u ON a.user_id = u.user_id " +
                     "ORDER BY a.timestamp DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(extractAuditLogFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting recent audit logs: " + e.getMessage());
        }
        
        return logs;
    }
    
    // Get audit log count
    public int getAuditLogCount() {
        String sql = "SELECT COUNT(*) FROM audit_logs";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting audit log count: " + e.getMessage());
        }
        
        return 0;
    }
    
    // Helper method to extract AuditLog from ResultSet
    private AuditLog extractAuditLogFromResultSet(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setLogId(rs.getInt("log_id"));
        log.setUserId(rs.getInt("user_id"));
        log.setAction(rs.getString("action"));
        log.setTargetType(rs.getString("target_type"));
        log.setTargetId(rs.getInt("target_id"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setDetails(rs.getString("details"));
        log.setTimestamp(rs.getTimestamp("timestamp"));
        
        // Additional field from JOIN
        log.setUsername(rs.getString("username"));
        
        return log;
    }
}

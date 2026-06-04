package com.arion.dao;

import com.arion.model.FileIntegrityCheck;
import com.arion.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileIntegrityCheckDAO {
    
    // Create - Add new integrity check
    public int addIntegrityCheck(FileIntegrityCheck check) {
        String sql = "INSERT INTO file_integrity_checks (file_id, checked_by, previous_hash, " +
                     "current_hash, check_result) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, check.getFileId());
            pstmt.setInt(2, check.getCheckedBy());
            pstmt.setString(3, check.getPreviousHash());
            pstmt.setString(4, check.getCurrentHash());
            pstmt.setString(5, check.getCheckResult());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding integrity check: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    // Read - Get check by ID
    public FileIntegrityCheck getCheckById(int checkId) {
        String sql = "SELECT * FROM file_integrity_checks WHERE check_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, checkId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractCheckFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting check by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Read - Get all checks for a file
    public List<FileIntegrityCheck> getChecksByFileId(int fileId) {
        List<FileIntegrityCheck> checks = new ArrayList<>();
        String sql = "SELECT * FROM file_integrity_checks WHERE file_id = ? ORDER BY check_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fileId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                checks.add(extractCheckFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting checks by file: " + e.getMessage());
        }
        
        return checks;
    }
    
    // Read - Get recent checks by user
    public List<FileIntegrityCheck> getChecksByUser(int userId, int limit) {
        List<FileIntegrityCheck> checks = new ArrayList<>();
        String sql = "SELECT * FROM file_integrity_checks WHERE checked_by = ? " +
                     "ORDER BY check_date DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                checks.add(extractCheckFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting checks by user: " + e.getMessage());
        }
        
        return checks;
    }
    
    // Read - Get latest check for a file
    public FileIntegrityCheck getLatestCheckForFile(int fileId) {
        String sql = "SELECT * FROM file_integrity_checks WHERE file_id = ? " +
                     "ORDER BY check_date DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fileId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractCheckFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting latest check: " + e.getMessage());
        }
        
        return null;
    }
    
    // Get total check count
    public int getCheckCount() {
        String sql = "SELECT COUNT(*) FROM file_integrity_checks";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting check count: " + e.getMessage());
        }
        
        return 0;
    }
    
    // Get count by result type
    public int getCheckCountByResult(String result) {
        String sql = "SELECT COUNT(*) FROM file_integrity_checks WHERE check_result = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, result);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting check count by result: " + e.getMessage());
        }
        
        return 0;
    }
    
    // Helper method
    private FileIntegrityCheck extractCheckFromResultSet(ResultSet rs) throws SQLException {
        FileIntegrityCheck check = new FileIntegrityCheck();
        check.setCheckId(rs.getInt("check_id"));
        check.setFileId(rs.getInt("file_id"));
        check.setCheckedBy(rs.getInt("checked_by"));
        check.setPreviousHash(rs.getString("previous_hash"));
        check.setCurrentHash(rs.getString("current_hash"));
        check.setCheckResult(rs.getString("check_result"));
        check.setCheckDate(rs.getTimestamp("check_date"));
        return check;
    }
}
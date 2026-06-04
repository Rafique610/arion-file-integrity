package com.arion.dao;

import com.arion.model.FileUploadHistory;
import com.arion.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileUploadHistoryDAO {
    
    // Create - Add new history entry
    public int addHistory(FileUploadHistory history) {
        String sql = "INSERT INTO file_upload_history (file_id, user_id, action_type, " +
                     "action_status, hash_value, algorithm_used, details) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, history.getFileId());
            pstmt.setInt(2, history.getUserId());
            pstmt.setString(3, history.getActionType());
            pstmt.setString(4, history.getActionStatus());
            pstmt.setString(5, history.getHashValue());
            pstmt.setString(6, history.getAlgorithmUsed());
            pstmt.setString(7, history.getDetails());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    // Read - Get all history (with file and user info)
    public List<FileUploadHistory> getAllHistory() {
        List<FileUploadHistory> historyList = new ArrayList<>();
        String sql = "SELECT h.*, f.file_name, u.username, u.full_name " +
                     "FROM file_upload_history h " +
                     "LEFT JOIN files f ON h.file_id = f.file_id " +
                     "LEFT JOIN users u ON h.user_id = u.user_id " +
                     "ORDER BY h.timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                historyList.add(extractHistoryFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all history: " + e.getMessage());
        }
        
        return historyList;
    }
    
    // Read - Get history by user
    public List<FileUploadHistory> getHistoryByUser(int userId) {
        List<FileUploadHistory> historyList = new ArrayList<>();
        String sql = "SELECT h.*, f.file_name, u.username, u.full_name " +
                     "FROM file_upload_history h " +
                     "LEFT JOIN files f ON h.file_id = f.file_id " +
                     "LEFT JOIN users u ON h.user_id = u.user_id " +
                     "WHERE h.user_id = ? " +
                     "ORDER BY h.timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                historyList.add(extractHistoryFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting history by user: " + e.getMessage());
        }
        
        return historyList;
    }
    
    // Read - Get history by file
    public List<FileUploadHistory> getHistoryByFile(int fileId) {
        List<FileUploadHistory> historyList = new ArrayList<>();
        String sql = "SELECT h.*, f.file_name, u.username, u.full_name " +
                     "FROM file_upload_history h " +
                     "LEFT JOIN files f ON h.file_id = f.file_id " +
                     "LEFT JOIN users u ON h.user_id = u.user_id " +
                     "WHERE h.file_id = ? " +
                     "ORDER BY h.timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fileId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                historyList.add(extractHistoryFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting history by file: " + e.getMessage());
        }
        
        return historyList;
    }
    
    // Read - Get history by action type
    public List<FileUploadHistory> getHistoryByActionType(int userId, String actionType) {
        List<FileUploadHistory> historyList = new ArrayList<>();
        String sql = "SELECT h.*, f.file_name, u.username, u.full_name " +
                     "FROM file_upload_history h " +
                     "LEFT JOIN files f ON h.file_id = f.file_id " +
                     "LEFT JOIN users u ON h.user_id = u.user_id " +
                     "WHERE h.user_id = ? AND h.action_type = ? " +
                     "ORDER BY h.timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, actionType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                historyList.add(extractHistoryFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting history by action type: " + e.getMessage());
        }
        
        return historyList;
    }
    
    // Read - Get history with filters
    public List<FileUploadHistory> getHistoryWithFilters(int userId, Integer fileId, 
                                                          String actionType, String dateFilter) {
        List<FileUploadHistory> historyList = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT h.*, f.file_name, u.username, u.full_name " +
            "FROM file_upload_history h " +
            "LEFT JOIN files f ON h.file_id = f.file_id " +
            "LEFT JOIN users u ON h.user_id = u.user_id " +
            "WHERE h.user_id = ? "
        );
        
        // Build dynamic WHERE clause
        if (fileId != null && fileId > 0) {
            sql.append("AND h.file_id = ? ");
        }
        if (actionType != null && !actionType.isEmpty()) {
            sql.append("AND h.action_type = ? ");
        }
        if (dateFilter != null && !dateFilter.isEmpty()) {
            switch (dateFilter) {
                case "Today":
                    sql.append("AND DATE(h.timestamp) = CURDATE() ");
                    break;
                case "Last Week":
                    sql.append("AND h.timestamp >= DATE_SUB(NOW(), INTERVAL 7 DAY) ");
                    break;
                case "Last Month":
                    sql.append("AND h.timestamp >= DATE_SUB(NOW(), INTERVAL 30 DAY) ");
                    break;
            }
        }
        
        sql.append("ORDER BY h.timestamp DESC");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            pstmt.setInt(paramIndex++, userId);
            
            if (fileId != null && fileId > 0) {
                pstmt.setInt(paramIndex++, fileId);
            }
            if (actionType != null && !actionType.isEmpty()) {
                pstmt.setString(paramIndex++, actionType);
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                historyList.add(extractHistoryFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting filtered history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return historyList;
    }
    
    // Read - Get recent history (limit)
    public List<FileUploadHistory> getRecentHistory(int userId, int limit) {
        List<FileUploadHistory> historyList = new ArrayList<>();
        String sql = "SELECT h.*, f.file_name, u.username, u.full_name " +
                     "FROM file_upload_history h " +
                     "LEFT JOIN files f ON h.file_id = f.file_id " +
                     "LEFT JOIN users u ON h.user_id = u.user_id " +
                     "WHERE h.user_id = ? " +
                     "ORDER BY h.timestamp DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                historyList.add(extractHistoryFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting recent history: " + e.getMessage());
        }
        
        return historyList;
    }
    
    // Get history count
    public int getHistoryCount(int userId) {
        String sql = "SELECT COUNT(*) FROM file_upload_history WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting history count: " + e.getMessage());
        }
        
        return 0;
    }
    
    // Get unique file names for filter dropdown
    public List<String> getUniqueFileNames(int userId) {
        List<String> fileNames = new ArrayList<>();
        String sql = "SELECT DISTINCT f.file_name " +
                     "FROM file_upload_history h " +
                     "JOIN files f ON h.file_id = f.file_id " +
                     "WHERE h.user_id = ? " +
                     "ORDER BY f.file_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                fileNames.add(rs.getString("file_name"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting unique file names: " + e.getMessage());
        }
        
        return fileNames;
    }
    
    // Helper method to extract History from ResultSet
    private FileUploadHistory extractHistoryFromResultSet(ResultSet rs) throws SQLException {
        FileUploadHistory history = new FileUploadHistory();
        history.setHistoryId(rs.getInt("history_id"));
        history.setFileId(rs.getInt("file_id"));
        history.setUserId(rs.getInt("user_id"));
        history.setActionType(rs.getString("action_type"));
        history.setActionStatus(rs.getString("action_status"));
        history.setHashValue(rs.getString("hash_value"));
        history.setAlgorithmUsed(rs.getString("algorithm_used"));
        history.setDetails(rs.getString("details"));
        history.setTimestamp(rs.getTimestamp("timestamp"));
        
        // Additional fields from JOINs
        history.setFileName(rs.getString("file_name"));
        history.setUsername(rs.getString("username"));
        history.setUserFullName(rs.getString("full_name"));
        
        return history;
    }
}
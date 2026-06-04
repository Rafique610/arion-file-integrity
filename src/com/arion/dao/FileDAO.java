package com.arion.dao;

import com.arion.model.File;
import com.arion.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileDAO {
    
   
	public int addFile(File file) {
	    // Removed file_type column since it doesn't exist in database
	    String sql = "INSERT INTO files (file_name, file_path, file_size, hash_value, " +
	                 "hash_algorithm, uploaded_by, status) " +
	                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        
	        pstmt.setString(1, file.getFileName());
	        pstmt.setString(2, file.getFilePath());
	        pstmt.setLong(3, file.getFileSize());
	        pstmt.setString(4, file.getHashValue());
	        pstmt.setString(5, file.getHashAlgorithm());
	        pstmt.setInt(6, file.getUploadedBy());
	        pstmt.setString(7, file.getStatus());
	        
	        int rowsAffected = pstmt.executeUpdate();
	        
	        if (rowsAffected > 0) {
	            ResultSet rs = pstmt.getGeneratedKeys();
	            if (rs.next()) {
	                return rs.getInt(1); // Return generated file_id
	            }
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error adding file: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return -1;
	}
    
    // Read - Get file by ID
    public File getFileById(int fileId) {
        String sql = "SELECT f.*, u.full_name as uploader_name " +
                     "FROM files f " +
                     "LEFT JOIN users u ON f.uploaded_by = u.user_id " +
                     "WHERE f.file_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fileId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractFileFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting file by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Read - Get all files
    public List<File> getAllFiles() {
        List<File> files = new ArrayList<>();
        String sql = "SELECT f.*, u.full_name as uploader_name " +
                     "FROM files f " +
                     "LEFT JOIN users u ON f.uploaded_by = u.user_id " +
                     "ORDER BY f.upload_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                files.add(extractFileFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all files: " + e.getMessage());
        }
        
        return files;
    }
    
    // Read - Get files by user
    public List<File> getFilesByUser(int userId) {
    	 List<File> files = new ArrayList<>();
    	    String sql = "SELECT f.*, u.full_name as uploader_name " +
    	                 "FROM files f " +
    	                 "LEFT JOIN users u ON f.uploaded_by = u.user_id " +
    	                 "WHERE f.uploaded_by = ? " +
    	                 "ORDER BY f.upload_date DESC";
    	    
    	    System.out.println("=== Fetching files for user: " + userId + " ===");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                files.add(extractFileFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting files by user: " + e.getMessage());
        }
        System.out.println("Found " + files.size() + " files");
        
        
        return files;
    }
    
    // Read - Get files by status
    public List<File> getFilesByStatus(String status) {
        List<File> files = new ArrayList<>();
        String sql = "SELECT f.*, u.full_name as uploader_name " +
                     "FROM files f " +
                     "LEFT JOIN users u ON f.uploaded_by = u.user_id " +
                     "WHERE f.status = ? " +
                     "ORDER BY f.upload_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                files.add(extractFileFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting files by status: " + e.getMessage());
        }
        
        return files;
    }
    
    // Update - Update file status
    public boolean updateFileStatus(int fileId, String newStatus) {
        String sql = "UPDATE files SET status = ? WHERE file_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, fileId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating file status: " + e.getMessage());
            return false;
        }
    }
    
    // Update - Update file hash
    public boolean updateFileHash(int fileId, String newHash, String algorithm) {
        String sql = "UPDATE files SET hash_value = ?, hash_algorithm = ? WHERE file_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newHash);
            pstmt.setString(2, algorithm);
            pstmt.setInt(3, fileId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating file hash: " + e.getMessage());
            return false;
        }
    }
    
    // Update - Update last verified timestamp
    public boolean updateLastVerified(int fileId) {
        String sql = "UPDATE files SET last_verified = CURRENT_TIMESTAMP WHERE file_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fileId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating last verified: " + e.getMessage());
            return false;
        }
    }
    
    // Delete - Delete file
    public boolean deleteFile(int fileId) {
        String sql = "DELETE FROM files WHERE file_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fileId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return false;
        }
    }
    
    // Get file count
    public int getFileCount() {
        String sql = "SELECT COUNT(*) FROM files";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting file count: " + e.getMessage());
        }
        
        return 0;
    }
    
    // Get file count by status
    public int getFileCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM files WHERE status = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting file count by status: " + e.getMessage());
        }
        
        return 0;
    }
    
 

    private File extractFileFromResultSet(ResultSet rs) throws SQLException {
        File file = new File();
        file.setFileId(rs.getInt("file_id"));
        file.setFileName(rs.getString("file_name"));
        file.setFilePath(rs.getString("file_path"));
        file.setFileSize(rs.getLong("file_size"));
        file.setHashValue(rs.getString("hash_value"));
        file.setHashAlgorithm(rs.getString("hash_algorithm"));
        file.setUploadedBy(rs.getInt("uploaded_by"));
        file.setStatus(rs.getString("status"));
        file.setUploadDate(rs.getTimestamp("upload_date"));
        file.setLastVerified(rs.getTimestamp("last_verified"));
        
        // Additional field from JOIN
        file.setUploaderName(rs.getString("uploader_name"));
        
        return file;
    }
}
package com.arion.dao;

import com.arion.model.AttackSimulation;
import com.arion.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttackSimulationDAO {
    
    // Create - Add new simulation
    public int addSimulation(AttackSimulation simulation) {
        String sql = "INSERT INTO attack_simulations (attack_type, target_file, intensity_level, " +
                     "detection_time, system_response, affected_files, alerts_triggered, " +
                     "success_rate, total_duration, simulation_log, started_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, simulation.getAttackType());
            pstmt.setString(2, simulation.getTargetFile());
            pstmt.setInt(3, simulation.getIntensityLevel());
            pstmt.setDouble(4, simulation.getDetectionTime());
            pstmt.setString(5, simulation.getSystemResponse());
            pstmt.setInt(6, simulation.getAffectedFiles());
            pstmt.setInt(7, simulation.getAlertsTriggered());
            pstmt.setDouble(8, simulation.getSuccessRate());
            pstmt.setInt(9, simulation.getTotalDuration());
            pstmt.setString(10, simulation.getSimulationLog());
            pstmt.setInt(11, simulation.getStartedBy());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding simulation: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    // Update - Update simulation end time and results
    public boolean updateSimulation(AttackSimulation simulation) {
        String sql = "UPDATE attack_simulations SET detection_time = ?, system_response = ?, " +
                     "affected_files = ?, alerts_triggered = ?, success_rate = ?, " +
                     "total_duration = ?, simulation_log = ?, end_time = CURRENT_TIMESTAMP " +
                     "WHERE simulation_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, simulation.getDetectionTime());
            pstmt.setString(2, simulation.getSystemResponse());
            pstmt.setInt(3, simulation.getAffectedFiles());
            pstmt.setInt(4, simulation.getAlertsTriggered());
            pstmt.setDouble(5, simulation.getSuccessRate());
            pstmt.setInt(6, simulation.getTotalDuration());
            pstmt.setString(7, simulation.getSimulationLog());
            pstmt.setInt(8, simulation.getSimulationId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating simulation: " + e.getMessage());
            return false;
        }
    }
    
    // Read - Get simulation by ID
    public AttackSimulation getSimulationById(int simulationId) {
        String sql = "SELECT s.*, u.username as started_by_name " +
                     "FROM attack_simulations s " +
                     "LEFT JOIN users u ON s.started_by = u.user_id " +
                     "WHERE s.simulation_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, simulationId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractSimulationFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting simulation by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Read - Get all simulations
    public List<AttackSimulation> getAllSimulations() {
        List<AttackSimulation> simulations = new ArrayList<>();
        String sql = "SELECT s.*, u.username as started_by_name " +
                     "FROM attack_simulations s " +
                     "LEFT JOIN users u ON s.started_by = u.user_id " +
                     "ORDER BY s.start_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                simulations.add(extractSimulationFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all simulations: " + e.getMessage());
        }
        
        return simulations;
    }
    
    // Read - Get simulations by user
    public List<AttackSimulation> getSimulationsByUser(int userId) {
        List<AttackSimulation> simulations = new ArrayList<>();
        String sql = "SELECT s.*, u.username as started_by_name " +
                     "FROM attack_simulations s " +
                     "LEFT JOIN users u ON s.started_by = u.user_id " +
                     "WHERE s.started_by = ? " +
                     "ORDER BY s.start_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                simulations.add(extractSimulationFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting simulations by user: " + e.getMessage());
        }
        
        return simulations;
    }
    
    // Read - Get simulations by attack type
    public List<AttackSimulation> getSimulationsByAttackType(String attackType) {
        List<AttackSimulation> simulations = new ArrayList<>();
        String sql = "SELECT s.*, u.username as started_by_name " +
                     "FROM attack_simulations s " +
                     "LEFT JOIN users u ON s.started_by = u.user_id " +
                     "WHERE s.attack_type = ? " +
                     "ORDER BY s.start_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, attackType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                simulations.add(extractSimulationFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting simulations by attack type: " + e.getMessage());
        }
        
        return simulations;
    }
    
    // Read - Get simulations by response status
    public List<AttackSimulation> getSimulationsByResponse(String response) {
        List<AttackSimulation> simulations = new ArrayList<>();
        String sql = "SELECT s.*, u.username as started_by_name " +
                     "FROM attack_simulations s " +
                     "LEFT JOIN users u ON s.started_by = u.user_id " +
                     "WHERE s.system_response = ? " +
                     "ORDER BY s.start_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, response);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                simulations.add(extractSimulationFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting simulations by response: " + e.getMessage());
        }
        
        return simulations;
    }
    
    // Delete - Delete simulation
    public boolean deleteSimulation(int simulationId) {
        String sql = "DELETE FROM attack_simulations WHERE simulation_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, simulationId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting simulation: " + e.getMessage());
            return false;
        }
    }
    
    // Get simulation count
    public int getSimulationCount() {
        String sql = "SELECT COUNT(*) FROM attack_simulations";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting simulation count: " + e.getMessage());
        }
        
        return 0;
    }
    
    // Get average detection time
    public double getAverageDetectionTime() {
        String sql = "SELECT AVG(detection_time) FROM attack_simulations WHERE detection_time > 0";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting average detection time: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    // Get success rate statistics
    public double getOverallSuccessRate() {
        String sql = "SELECT AVG(success_rate) FROM attack_simulations WHERE success_rate > 0";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting overall success rate: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    // Helper method to extract AttackSimulation from ResultSet
    private AttackSimulation extractSimulationFromResultSet(ResultSet rs) throws SQLException {
        AttackSimulation simulation = new AttackSimulation();
        simulation.setSimulationId(rs.getInt("simulation_id"));
        simulation.setAttackType(rs.getString("attack_type"));
        simulation.setTargetFile(rs.getString("target_file"));
        simulation.setIntensityLevel(rs.getInt("intensity_level"));
        simulation.setDetectionTime(rs.getDouble("detection_time"));
        simulation.setSystemResponse(rs.getString("system_response"));
        simulation.setAffectedFiles(rs.getInt("affected_files"));
        simulation.setAlertsTriggered(rs.getInt("alerts_triggered"));
        simulation.setSuccessRate(rs.getDouble("success_rate"));
        simulation.setTotalDuration(rs.getInt("total_duration"));
        simulation.setSimulationLog(rs.getString("simulation_log"));
        simulation.setStartedBy(rs.getInt("started_by"));
        simulation.setStartTime(rs.getTimestamp("start_time"));
        simulation.setEndTime(rs.getTimestamp("end_time"));
        
        // Additional field from JOIN
        simulation.setStartedByName(rs.getString("started_by_name"));
        
        return simulation;
    }
}
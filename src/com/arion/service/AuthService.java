package com.arion.service;

import com.arion.dao.UserDAO;
import com.arion.dao.AuditLogDAO;
import com.arion.model.User;
import com.arion.model.AuditLog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthService {

    private UserDAO userDAO;
    private AuditLogDAO auditLogDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.auditLogDAO = new AuditLogDAO();
    }

    // Authenticate user with username and password
    public User login(String username, String password) {
        System.out.println("[AuthService] Login attempt for user: " + username);
        
        // Hash the password
        String passwordHash = hashPassword(password);
        System.out.println("[AuthService] Password hash: " + passwordHash.substring(0, 10) + "...");

        // Authenticate
        User user = userDAO.authenticateUser(username, passwordHash);

        if (user != null) {
            System.out.println("[AuthService] Login successful for: " + username);
            
            // Log successful login
            try {
                AuditLog log = new AuditLog(
                    user.getUserId(),
                    "Login",
                    "User",
                    user.getUserId(),
                    "127.0.0.1",
                    "User logged in successfully"
                );
                auditLogDAO.addAuditLog(log);
            } catch (Exception e) {
                System.err.println("[AuthService] Error logging audit: " + e.getMessage());
            }

            return user;
        }

        System.out.println("[AuthService] Login failed - invalid credentials");
        return null;
    }

    // Hash password using SHA-256
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Validate credentials format
    public boolean validateCredentials(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        if (username.length() < 3) {
            return false;
        }
        if (password.length() < 6) {
            return false;
        }
        return true;
    }
}
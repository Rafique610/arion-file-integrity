package com.arion.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashService {
    
   
    public String generateFileHash(java.io.File file, String algorithm) 
            throws NoSuchAlgorithmException, IOException {
        
        // Map user-friendly names to Java algorithm names
        String javaAlgorithm = mapAlgorithm(algorithm);
        
        MessageDigest digest = MessageDigest.getInstance(javaAlgorithm);
        
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192]; // 8KB buffer
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        
        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }
    
    /**
     * Map UI algorithm names to Java MessageDigest names
     */
    private String mapAlgorithm(String algorithm) {
        switch (algorithm.toUpperCase()) {
            case "SHA-256":
            case "SHA256":
                return "SHA-256";
            case "SHA-512":
            case "SHA512":
                return "SHA-512";
            case "SHA-1":
            case "SHA1":
                return "SHA-1";
            case "MD5":
                return "MD5";
            default:
                return "SHA-256"; // Default to SHA-256
        }
    }
    
    /**
     * Convert byte array to hexadecimal string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    /**
     * Verify a file's hash against an expected value
     */
    public boolean verifyFileHash(java.io.File file, String expectedHash, String algorithm) {
        try {
            String actualHash = generateFileHash(file, algorithm);
            return actualHash.equalsIgnoreCase(expectedHash);
        } catch (Exception e) {
            System.err.println("Error verifying hash: " + e.getMessage());
            return false;
        }
    }
}
package com.arion.ui.common;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import com.arion.service.AuthService;
import com.arion.dao.UserDAO;
import com.arion.model.User;

public class SignupPage extends JFrame {
    
    private static final Color BG_PRIMARY = new Color(25, 28, 45);
    private static final Color BG_CARD = new Color(45, 52, 85);
    private static final Color ACCENT_PURPLE = new Color(138, 99, 255);
    private static final Color ACCENT_CYAN = new Color(0, 230, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 163);
    private static final Color TEXT_PRIMARY = new Color(240, 242, 255);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 200);
    private static final Color ACCENT_RED = new Color(255, 70, 100);
    
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;
    private AuthService authService;
    private UserDAO userDAO;
    
    public SignupPage() {
        authService = new AuthService();
        userDAO = new UserDAO();
        
        setTitle("ARION - Sign Up");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(BG_PRIMARY);
        
        // Left side - Simple Branding
        JPanel leftPanel = createBrandingPanel();
        
        // Right side - Signup Form
        JPanel rightPanel = createSignupPanel();
        
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        add(mainPanel);
    }
    
    private JPanel createBrandingPanel() {
        JPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(80, 60, 80, 60));
        
        // Logo
        JLabel logoLabel = new JLabel("ARION");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("File Integrity System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalGlue());
        panel.add(logoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PRIMARY);
        panel.setLayout(new BorderLayout());
        
        // Scrollable content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_PRIMARY);
        contentPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        // LOGIN LINK AT TOP
        JPanel topLoginPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        topLoginPanel.setOpaque(false);
        topLoginPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel alreadyHaveText = new JLabel("Already have an account?");
        alreadyHaveText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        alreadyHaveText.setForeground(TEXT_SECONDARY);
        
        JLabel signInLink = new JLabel("Sign In");
        signInLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signInLink.setForeground(ACCENT_CYAN);
        signInLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signInLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginPage();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                signInLink.setForeground(ACCENT_PURPLE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                signInLink.setForeground(ACCENT_CYAN);
            }
        });
        
        topLoginPanel.add(alreadyHaveText);
        topLoginPanel.add(signInLink);
        
        contentPanel.add(topLoginPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Fill in your details to get started");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Full Name field
        JLabel fullNameLabel = new JLabel("Full Name");
        fullNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fullNameLabel.setForeground(TEXT_PRIMARY);
        fullNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        fullNameField = createStyledTextField();
        
        contentPanel.add(fullNameLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        contentPanel.add(fullNameField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(TEXT_PRIMARY);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        usernameField = createStyledTextField();
        
        contentPanel.add(usernameLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        contentPanel.add(usernameField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Email field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(TEXT_PRIMARY);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        emailField = createStyledTextField();
        
        contentPanel.add(emailLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        contentPanel.add(emailField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Phone field
        JLabel phoneLabel = new JLabel("Phone Number (Optional)");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        phoneLabel.setForeground(TEXT_PRIMARY);
        phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        phoneField = createStyledTextField();
        
        contentPanel.add(phoneLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        contentPanel.add(phoneField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(TEXT_PRIMARY);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        passwordField = createStyledPasswordField();
        
        contentPanel.add(passwordLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        contentPanel.add(passwordField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Confirm Password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confirmPasswordLabel.setForeground(TEXT_PRIMARY);
        confirmPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        confirmPasswordField = createStyledPasswordField();
        
        contentPanel.add(confirmPasswordLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        contentPanel.add(confirmPasswordField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        errorLabel.setForeground(ACCENT_RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(errorLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Signup button
        JButton signupButton = createGradientButton("Create Account");
        signupButton.addActionListener(e -> handleSignup());
        
        contentPanel.add(signupButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        field.setBackground(new Color(35, 40, 65));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_CYAN);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 20), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setOpaque(false);
        
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        field.setBackground(new Color(35, 40, 65));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_CYAN);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 20), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setOpaque(false);
        
        return field;
    }
    
    private JButton createGradientButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, ACCENT_PURPLE,
                    getWidth(), getHeight(), ACCENT_CYAN
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return button;
    }
    
    private void handleSignup() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Clear error
        errorLabel.setText(" ");
        
        // Validate inputs
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all required fields");
            return;
        }
        
        if (username.length() < 3) {
            showError("Username must be at least 3 characters");
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address");
            return;
        }
        
        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            showError("Username already exists");
            return;
        }
        
        // Create new user
        try {
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPhone(phone.isEmpty() ? null : phone);
            newUser.setPasswordHash(authService.hashPassword(password));
            newUser.setRole("User"); // Default role
            newUser.setActive(true);
            
            boolean success = userDAO.addUser(newUser);
            
            if (success) {
                showSuccess("Account created successfully!");
                
                // Wait a moment then redirect to login
                Timer timer = new Timer(1500, e -> {
                    dispose();
                    new LoginPage();
                });
                timer.setRepeats(false);
                timer.start();
                
            } else {
                showError("Failed to create account. Please try again.");
            }
            
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setForeground(ACCENT_RED);
    }
    
    private void showSuccess(String message) {
        errorLabel.setText("✅ " + message);
        errorLabel.setForeground(ACCENT_GREEN);
    }
    
    // Custom Components
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            
            GradientPaint gradient = new GradientPaint(
                0, 0, ACCENT_PURPLE,
                getWidth(), getHeight(), new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), 200)
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.dispose();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SignupPage();
        });
    }
}
package com.arion.ui.common;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import com.arion.service.AuthService;
import com.arion.model.User;
import com.arion.ui.user.UserDashboard;

public class LoginPage extends JFrame {
    
    private static final Color BG_PRIMARY = new Color(25, 28, 45);
    private static final Color BG_CARD = new Color(45, 52, 85);
    private static final Color ACCENT_PURPLE = new Color(138, 99, 255);
    private static final Color ACCENT_CYAN = new Color(0, 230, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 163);
    private static final Color TEXT_PRIMARY = new Color(240, 242, 255);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 200);
    private static final Color ACCENT_RED = new Color(255, 70, 100);
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private AuthService authService;
    
    public LoginPage() {
        authService = new AuthService();
        
        setTitle("ARION - Login");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(BG_PRIMARY);
        
        // Left side - Branding
        JPanel leftPanel = createBrandingPanel();
        
        // Right side - Login Form
        JPanel rightPanel = createLoginPanel();
        
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        add(mainPanel);
    }
    
    private JPanel createBrandingPanel() {
        JPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(100, 80, 100, 80));
        
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
        
        // Features
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setOpaque(false);
        featuresPanel.setBorder(new EmptyBorder(60, 0, 0, 0));
        
        addFeature(featuresPanel, "🔒", "Secure File Protection");
        addFeature(featuresPanel, "🛡️", "Real-time Integrity Monitoring");
        addFeature(featuresPanel, "📊", "Comprehensive Audit Logs");
        addFeature(featuresPanel, "⚡", "Advanced Hash Algorithms");
        
        panel.add(Box.createVerticalGlue());
        panel.add(logoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitleLabel);
        panel.add(featuresPanel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void addFeature(JPanel panel, String icon, String text) {
        JPanel featurePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        featurePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textLabel.setForeground(new Color(255, 255, 255, 200));
        
        featurePanel.add(iconLabel);
        featurePanel.add(textLabel);
        
        panel.add(featurePanel);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PRIMARY);
        panel.setLayout(new GridBagLayout());
        
        JPanel loginCard = new RoundedPanel(30);
        loginCard.setBackground(BG_CARD);
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBorder(new EmptyBorder(60, 60, 60, 60));
        loginCard.setPreferredSize(new Dimension(400, 500));
        
        // Title
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginCard.add(titleLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 10)));
        loginCard.add(subtitleLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usernameLabel.setForeground(TEXT_PRIMARY);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        usernameField = createStyledTextField("Enter your username");
        
        loginCard.add(usernameLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(usernameField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(TEXT_PRIMARY);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        passwordField = createStyledPasswordField("Enter your password");
        
        loginCard.add(passwordLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(passwordField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(ACCENT_RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginCard.add(errorLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Login button
        JButton loginButton = createGradientButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        
        loginCard.add(loginButton);
        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Signup link
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signupPanel.setOpaque(false);
        
        JLabel signupText = new JLabel("Don't have an account?");
        signupText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        signupText.setForeground(TEXT_SECONDARY);
        
        JLabel signupLink = new JLabel("Sign up here");
        signupLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signupLink.setForeground(ACCENT_CYAN);
        signupLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new SignupPage();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                signupLink.setForeground(ACCENT_PURPLE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                signupLink.setForeground(ACCENT_CYAN);
            }
        });
        
        signupPanel.add(signupText);
        signupPanel.add(signupLink);
        
        loginCard.add(signupPanel);
        
        panel.add(loginCard);
        
        return panel;
    }
    
    private JTextField createStyledTextField(String placeholder) {
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
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 20), 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setOpaque(false);
        
        return field;
    }
    
    private JPasswordField createStyledPasswordField(String placeholder) {
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
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 20), 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setOpaque(false);
        
        // Add enter key listener
        field.addActionListener(e -> handleLogin());
        
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
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return button;
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Clear error
        errorLabel.setText(" ");
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }
        
        // Attempt login
        try {
            User user = authService.login(username, password);
            
            if (user != null) {
                // Login successful
                showSuccess("Login successful!");
                
                // Wait a moment before opening dashboard
                Timer timer = new Timer(500, e -> {
                    openDashboard(user);
                    dispose();
                });
                timer.setRepeats(false);
                timer.start();
                
            } else {
                showError("Invalid username or password");
            }
            
        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            // For now, only open UserDashboard
            String role = user.getRole();
            
            if (role.equals("Admin")) {
                // TODO: Create AdminDashboard later
                JOptionPane.showMessageDialog(this, 
                    "Admin Dashboard coming soon!\nOpening User Dashboard for now.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
                new UserDashboard(user);
            } else if (role.equals("Analyst")) {
                // TODO: Create AnalystDashboard later
                JOptionPane.showMessageDialog(this, 
                    "Analyst Dashboard coming soon!\nOpening User Dashboard for now.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
                new UserDashboard(user);
            } else {
                // Regular User
                new UserDashboard(user);
            }
        });
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
    
    class RoundedPanel extends JPanel {
        private int cornerRadius;
        
        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginPage();
        });
    }
}
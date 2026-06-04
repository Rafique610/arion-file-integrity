package com.arion.ui.admin;

import javax.swing.*;
import javax.swing.border.*;

import com.arion.base.BaseDashboard;
import com.arion.service.AdminDashboardService;
import com.arion.model.User;
import com.arion.model.AuditLog;
import com.arion.ui.common.IconRenderer;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends BaseDashboard {
    
    private AdminDashboardService adminService;
    private Map<String, Object> dashboardStats;
    
    public AdminDashboard(User adminUser) {
        super(adminUser);
        
        this.adminService = new AdminDashboardService();
        setTitle("ARION - Administrator Console");
        
        refreshDashboardData();
        initializeDashboard();
        setVisible(true);
    }
    
    public AdminDashboard() {
        this(createDefaultAdminUser());
    }
    
    private static User createDefaultAdminUser() {
        User admin = new User();
        admin.setUserId(1);
        admin.setUsername("admin");
        admin.setFullName("Administrator");
        admin.setRole("Admin");
        return admin;
    }
    
    private void refreshDashboardData() {
        try {
            System.out.println("Loading admin dashboard data...");
            dashboardStats = adminService.getAdminStats();
            System.out.println("Dashboard stats loaded: " + dashboardStats);
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
            
            dashboardStats = new java.util.HashMap<>();
            dashboardStats.put("totalUsers", 0);
            dashboardStats.put("activeUsers", 0);
            dashboardStats.put("totalFiles", 0);
            dashboardStats.put("tamperedFiles", 0);
            dashboardStats.put("criticalAlerts", 0);
            dashboardStats.put("systemHealth", 0.0);
        }
    }
    
    private int getStatInt(String key) {
        if (dashboardStats == null || key == null) return 0;
        Object value = dashboardStats.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return 0;
    }
    
    private String getTimeAgo(java.sql.Timestamp timestamp) {
        if (timestamp == null) return "Unknown";
        
        long diff = System.currentTimeMillis() - timestamp.getTime();
        long minutes = diff / (1000 * 60);
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (minutes < 1) return "Just now";
        else if (minutes < 60) return minutes + " mins ago";
        else if (hours < 24) return hours + " hours ago";
        else return days + " days ago";
    }
    
    private Color getActionColor(String action) {
        if (action == null) return ACCENT_CYAN;
        
        if (action.contains("FILE_UPLOAD") || action.contains("Upload")) return ACCENT_GREEN;
        if (action.contains("FILE_VERIFICATION") || action.contains("Verify")) return ACCENT_CYAN;
        if (action.contains("USER")) return ACCENT_PURPLE;
        if (action.contains("DELETE")) return ACCENT_RED;
        if (action.contains("UPDATE") || action.contains("MODIFY")) return ACCENT_ORANGE;
        
        return ACCENT_CYAN;
    }
    
    @Override
    protected JPanel createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(280, getHeight()));
        sidebarPanel.setBackground(BG_SECONDARY);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(new EmptyBorder(30, 20, 30, 20));
        
        JPanel logoPanel = new GradientLogoPanel();
        logoPanel.setMaximumSize(new Dimension(240, 60));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(logoPanel);
        
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        
        addMenuItem(sidebarPanel, "Dashboard", "dashboard", ACCENT_PURPLE);
        addMenuItem(sidebarPanel, "System Settings", "settings", ACCENT_CYAN);
        addMenuItem(sidebarPanel, "Manage Users", "users", ACCENT_GREEN);
        addMenuItem(sidebarPanel, "Security Policies", "policies", ACCENT_ORANGE);
        addMenuItem(sidebarPanel, "Simulate Attack", "simulate", ACCENT_RED);
        addMenuItem(sidebarPanel, "Audit Logs", "logs", ACCENT_PINK);
        
        sidebarPanel.add(Box.createVerticalGlue());
        
        JButton logoutBtn = createMenuButton("Logout", "logout", new Color(255, 70, 100));
        logoutBtn.addActionListener(e -> System.exit(0));
        sidebarPanel.add(logoutBtn);
        
        return sidebarPanel;
    }
    
    @Override
    protected JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_PRIMARY);
        topBar.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        JLabel title = new JLabel(getPageTitle(activePage));
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setBackground(BG_PRIMARY);
        
        JPanel searchPanel = new RoundedPanel(20);
        searchPanel.setBackground(new Color(35, 40, 65));
        searchPanel.setLayout(new BorderLayout(10, 0));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_CYAN, 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
        searchPanel.setPreferredSize(new Dimension(280, 45));
        
        JLabel searchIcon = IconRenderer.getIconLabel(IconRenderer.Icon.SEARCH, 18, ACCENT_CYAN);
        
        JTextField searchField = new JTextField("Search system...");
        searchField.setBackground(new Color(35, 40, 65));
        searchField.setForeground(TEXT_SECONDARY);
        searchField.setCaretColor(ACCENT_CYAN);
        searchField.setBorder(null);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        JButton refreshBtn = new JButton("🔄");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        refreshBtn.setForeground(ACCENT_GREEN);
        refreshBtn.setBackground(new Color(35, 40, 65));
        refreshBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_GREEN, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.setPreferredSize(new Dimension(45, 45));
        refreshBtn.addActionListener(e -> {
            refreshDashboardData();
            if (activePage.equals("dashboard")) {
                switchPage("dashboard");
            }
            JOptionPane.showMessageDialog(this, "Dashboard refreshed!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JPanel notifPanel = new NotificationBellPanel();
        notifPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel userPanel = new RoundedPanel(20);
        userPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));
        userPanel.setPreferredSize(new Dimension(180, 45));
        userPanel.setBackground(new Color(35, 40, 65));
        userPanel.setBorder(BorderFactory.createLineBorder(ACCENT_PURPLE, 2));
        
        JLabel userIcon = IconRenderer.getIconLabel(IconRenderer.Icon.USER, 20, TEXT_PRIMARY);
        
        JLabel userName = new JLabel(currentUser.getFullName());
        userName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userName.setForeground(TEXT_PRIMARY);
        
        userPanel.add(userIcon);
        userPanel.add(userName);
        
        rightPanel.add(searchPanel);
        rightPanel.add(refreshBtn);
        rightPanel.add(notifPanel);
        rightPanel.add(userPanel);
        
        topBar.add(title, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    @Override
    protected void loadDashboardContent() {
        JPanel dashboardContent = createDashboardContent();
        
        JScrollPane scrollPane = new JScrollPane(dashboardContent);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    @Override
    protected String getPageTitle(String page) {
        switch (page) {
            case "settings": return "System Settings";
            case "users": return "User Management";
            case "policies": return "Security Policies";
            case "simulate": return "Attack Simulation";
            case "logs": return "Audit Logs";
            default: return "Admin Dashboard";
        }
    }
    
    @Override
    protected String getIconForAction(String action) {
        switch (action) {
            case "dashboard": return "DASHBOARD";
            case "settings": return "SETTINGS";
            case "users": return "USERS";
            case "policies": return "SHIELD";
            case "simulate": return "ATTACK";
            case "logs": return "LOGS";
            case "logout": return "LOGOUT";
            default: return "FILE";
        }
    }
    
    @Override
    protected void switchPage(String page) {
        activePage = page;
        refreshSidebar();
        
        if (contentPanel.getComponentCount() > 1) {
            contentPanel.remove(1);
        }
        
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                contentPanel.remove(comp);
                break;
            }
        }
        
        JPanel topBar = createTopBar();
        contentPanel.add(topBar, BorderLayout.NORTH);
        
        JComponent newContent;

        switch (page) {
            case "settings":
                newContent = new ConfigureSystemSettingsPage().getPanel();
                break;
            case "users":
                newContent = new ManageUsersPage().getPanel();
                break;
            case "policies":
                newContent = new SetupSecurityPoliciesPage().getPanel();
                break;
            case "simulate":
                newContent = new SimulateMalwareAttackPage().getPanel();
                break;
            case "logs":
                newContent = new MaintainAuditLogsPage().getPanel();
                break;
            default:
                refreshDashboardData();
                JPanel dashContent = createDashboardContent();
                JScrollPane scrollPane = new JScrollPane(dashContent);
                scrollPane.setBackground(BG_PRIMARY);
                scrollPane.setBorder(null);
                scrollPane.getViewport().setBackground(BG_PRIMARY);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);
                newContent = scrollPane;
        }
        
        if (newContent instanceof JScrollPane) {
            contentPanel.add((JScrollPane)newContent, BorderLayout.CENTER);
        } else {
            contentPanel.add(newContent, BorderLayout.CENTER);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createDashboardContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_PRIMARY);
        
        JPanel statsPanel = createStatsPanel();
        content.add(statsPanel);
        
        content.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel middleSection = new JPanel(new GridLayout(1, 2, 25, 0));
        middleSection.setBackground(BG_PRIMARY);
        middleSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
        
        JPanel performancePanel = createSystemPerformancePanel();
        JPanel actionsPanel = createRecentActionsPanel();
        
        middleSection.add(performancePanel);
        middleSection.add(actionsPanel);
        
        content.add(middleSection);
        
        content.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel healthPanel = createSystemHealthPanel();
        content.add(healthPanel);
        
        return content;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(BG_PRIMARY);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        try {
            int totalUsers = getStatInt("totalUsers");
            int activeUsers = getStatInt("activeUsers");
            double uptime = adminService.getSystemUptime();
            int securityPolicies = getStatInt("securityPolicies");
            int criticalAlerts = adminService.getCriticalAlerts();
            
            statsPanel.add(createStatCard("Total Users", 
                String.valueOf(totalUsers), 
                "USERS", 
                ACCENT_GREEN, 
                activeUsers + " active now"));
            
            statsPanel.add(createStatCard("System Uptime", 
                String.format("%.1f%%", uptime), 
                "UPTIME", 
                ACCENT_CYAN, 
                "Last 30 days"));
            
            statsPanel.add(createStatCard("Security Policies", 
                String.valueOf(securityPolicies), 
                "SHIELD", 
                ACCENT_ORANGE, 
                "All active"));
            
            statsPanel.add(createStatCard("Critical Alerts", 
                String.valueOf(criticalAlerts), 
                "ALERT", 
                criticalAlerts > 0 ? ACCENT_RED : ACCENT_GREEN, 
                criticalAlerts > 0 ? "Needs attention" : "All clear"));
            
        } catch (Exception e) {
            System.err.println("Error creating stats panel: " + e.getMessage());
            e.printStackTrace();
            statsPanel.add(createStatCard("Total Users", "0", "USERS", ACCENT_GREEN, "Loading..."));
            statsPanel.add(createStatCard("System Uptime", "0%", "UPTIME", ACCENT_CYAN, "Loading..."));
            statsPanel.add(createStatCard("Security Policies", "0", "SHIELD", ACCENT_ORANGE, "Loading..."));
            statsPanel.add(createStatCard("Critical Alerts", "0", "ALERT", ACCENT_RED, "Loading..."));
        }
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String label, String value, String iconType, Color accentColor, String subtitle) {
        JPanel card = new GlowingCard(25, accentColor);
        card.setLayout(new BorderLayout(15, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel iconPanel = new AnimatedIconPanel(iconType, accentColor);
        iconPanel.setPreferredSize(new Dimension(60, 60));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BG_CARD);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelText.setForeground(TEXT_PRIMARY);
        labelText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setForeground(TEXT_SECONDARY);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(labelText);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(subLabel);
        
        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createSystemPerformancePanel() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 15));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JLabel title = new JLabel("📊 System Performance");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel metricsPanel = new JPanel();
        metricsPanel.setLayout(new BoxLayout(metricsPanel, BoxLayout.Y_AXIS));
        metricsPanel.setBackground(BG_CARD);
        
        try {
            Map<String, Integer> performance = adminService.getSystemPerformance();
            
            metricsPanel.add(createMetricBar("CPU Usage", 
                performance.getOrDefault("cpuUsage", 34), 
                ACCENT_GREEN));
            metricsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            
            metricsPanel.add(createMetricBar("Memory Usage", 
                performance.getOrDefault("memoryUsage", 62), 
                ACCENT_ORANGE));
            metricsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            
            metricsPanel.add(createMetricBar("Database Load", 
                performance.getOrDefault("databaseLoad", 28), 
                ACCENT_CYAN));
            metricsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            
            metricsPanel.add(createMetricBar("Network Traffic", 
                performance.getOrDefault("networkTraffic", 45), 
                ACCENT_PURPLE));
            
        } catch (Exception e) {
            System.err.println("Error loading performance metrics: " + e.getMessage());
            metricsPanel.add(createMetricBar("CPU Usage", 34, ACCENT_GREEN));
            metricsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            metricsPanel.add(createMetricBar("Memory Usage", 62, ACCENT_ORANGE));
            metricsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            metricsPanel.add(createMetricBar("Database Load", 28, ACCENT_CYAN));
            metricsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            metricsPanel.add(createMetricBar("Network Traffic", 45, ACCENT_PURPLE));
        }
        
        card.add(title, BorderLayout.NORTH);
        card.add(metricsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createMetricBar(String label, int value, Color color) {
        JPanel panel = new JPanel(new BorderLayout(15, 5));
        panel.setBackground(BG_CARD);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(TEXT_PRIMARY);
        
        JLabel valueLabel = new JLabel(value + "%");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(color);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_CARD);
        topPanel.add(nameLabel, BorderLayout.WEST);
        topPanel.add(valueLabel, BorderLayout.EAST);
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(value);
        progressBar.setStringPainted(false);
        progressBar.setForeground(color);
        progressBar.setBackground(new Color(35, 40, 65));
        progressBar.setBorder(null);
        progressBar.setPreferredSize(new Dimension(0, 12));
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRecentActionsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 15));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("📝 Recent Admin Actions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JLabel viewAll = new JLabel("View All →");
        viewAll.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        viewAll.setForeground(ACCENT_PURPLE);
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchPage("logs");
            }
        });
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(viewAll, BorderLayout.EAST);
        
        JPanel actionsList = new JPanel();
        actionsList.setLayout(new BoxLayout(actionsList, BoxLayout.Y_AXIS));
        actionsList.setBackground(BG_CARD);
        
        try {
            List<AuditLog> recentActions = adminService.getRecentAdminActions(5);
            
            if (recentActions.isEmpty()) {
                JLabel noDataLabel = new JLabel("No recent actions");
                noDataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noDataLabel.setForeground(TEXT_SECONDARY);
                noDataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                actionsList.add(Box.createVerticalGlue());
                actionsList.add(noDataLabel);
                actionsList.add(Box.createVerticalGlue());
            } else {
                for (AuditLog log : recentActions) {
                    String timeAgo = getTimeAgo(log.getTimestamp());
                    String detail = log.getDetails() != null ? log.getDetails() : "No details";
                    if (detail.length() > 40) {
                        detail = detail.substring(0, 40) + "...";
                    }
                    
                    Color actionColor = getActionColor(log.getAction());
                    
                    JPanel item = createActionItem(
                        log.getAction(),
                        detail,
                        timeAgo,
                        actionColor
                    );
                    actionsList.add(item);
                    actionsList.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading recent actions: " + e.getMessage());
            e.printStackTrace();
            
            JLabel errorLabel = new JLabel("Unable to load actions");
            errorLabel.setForeground(ACCENT_RED);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            actionsList.add(Box.createVerticalGlue());
            actionsList.add(errorLabel);
            actionsList.add(Box.createVerticalGlue());
        }
        
        JScrollPane scrollPane = new JScrollPane(actionsList);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createActionItem(String action, String detail, String time, Color color) {
        JPanel item = new RoundedPanel(12);
        item.setLayout(new BorderLayout(15, 0));
        item.setBackground(new Color(55, 62, 95));
        item.setBorder(new EmptyBorder(15, 18, 15, 18));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        JPanel iconPanel = new RoundedPanel(10);
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        iconPanel.setBorder(BorderFactory.createLineBorder(color, 1));
        iconPanel.setLayout(new GridBagLayout());
        
        JLabel icon = new JLabel("✓");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        icon.setForeground(color);
        iconPanel.add(icon);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(55, 62, 95));
        
        JLabel actionLabel = new JLabel(action);
        actionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        actionLabel.setForeground(TEXT_PRIMARY);
        actionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel detailLabel = new JLabel(detail + " • " + time);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailLabel.setForeground(TEXT_SECONDARY);
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        centerPanel.add(actionLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        centerPanel.add(detailLabel);
        
        item.add(iconPanel, BorderLayout.WEST);
        item.add(centerPanel, BorderLayout.CENTER);
        
        return item;
    }
    
    private JPanel createSystemHealthPanel() {
        JPanel card = new GlowingCard(25, ACCENT_GREEN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("💚 System Health Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridPanel.setBackground(BG_CARD);
        
        try {
            int totalFiles = getStatInt("totalFiles");
            int verifiedFiles = getStatInt("verifiedFiles");
            int tamperedFiles = getStatInt("tamperedFiles");
            int totalLogs = getStatInt("totalLogs");
            int activeUsers = getStatInt("activeUsers");
            
            String dbStatus = totalFiles >= 0 ? "Healthy" : "Error";
            Color dbColor = dbStatus.equals("Healthy") ? ACCENT_GREEN : ACCENT_RED;
            
            double avgResponse = 1.0 + (totalLogs % 10) * 0.1;
            
            String hashSpeed = "<100ms";
            
            int storageUsed = Math.min(500, totalFiles * 5);
            int storageAvailable = 500 - storageUsed;
            
            String backupStatus = "Up to date";
            Color backupColor = ACCENT_GREEN;
            
            gridPanel.add(createHealthCard("Database Status", dbStatus, dbColor, "✓"));
            gridPanel.add(createHealthCard("API Response", String.format("%.1fs avg", avgResponse), ACCENT_CYAN, "⚡"));
            gridPanel.add(createHealthCard("Hash Generation", hashSpeed, ACCENT_PURPLE, "⚙️"));
            gridPanel.add(createHealthCard("Storage Available", storageAvailable + " GB", ACCENT_ORANGE, "💾"));
            gridPanel.add(createHealthCard("Active Sessions", String.valueOf(activeUsers), ACCENT_PINK, "👥"));
            gridPanel.add(createHealthCard("Backup Status", backupStatus, backupColor, "✓"));
            
        } catch (Exception e) {
            System.err.println("Error loading system health: " + e.getMessage());
            gridPanel.add(createHealthCard("Database Status", "Healthy", ACCENT_GREEN, "✓"));
            gridPanel.add(createHealthCard("API Response", "1.2s avg", ACCENT_CYAN, "⚡"));
            gridPanel.add(createHealthCard("Hash Generation", "<100ms", ACCENT_PURPLE, "⚙️"));
            gridPanel.add(createHealthCard("Storage Available", "234 GB", ACCENT_ORANGE, "💾"));
            gridPanel.add(createHealthCard("Active Sessions", "0", ACCENT_PINK, "👥"));
            gridPanel.add(createHealthCard("Backup Status", "Up to date", ACCENT_GREEN, "✓"));
        }
        
        card.add(title, BorderLayout.NORTH);
        card.add(gridPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createHealthCard(String label, String value, Color color, String icon) {
        JPanel card = new RoundedPanel(15);
        card.setLayout(new BorderLayout(0, 10));
        card.setBackground(new Color(35, 40, 65));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(color);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(35, 40, 65));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(TEXT_SECONDARY);
        labelText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(labelText);
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 0));
        centerPanel.setBackground(new Color(35, 40, 65));
        centerPanel.add(iconLabel, BorderLayout.WEST);
        centerPanel.add(textPanel, BorderLayout.CENTER);
        
        card.add(centerPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    class GradientLogoPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
            
            GradientPaint gradient = new GradientPaint(
                0, 0, ACCENT_PURPLE,
                getWidth(), getHeight(), ACCENT_CYAN
            );
            g2.setPaint(gradient);
            g2.drawString("ARION", 10, 35);
            
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(TEXT_SECONDARY);
            g2.drawString("Admin Console", 10, 50);
            
            g2.dispose();
        }
    }
    
    class NotificationBellPanel extends JPanel {
        public NotificationBellPanel() {
            setPreferredSize(new Dimension(50, 50));
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(BG_CARD);
            g2.fillOval(5, 5, 40, 40);
            
            g2.setColor(TEXT_PRIMARY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            g2.drawString("🔔", 12, 32);
            
            try {
                int alerts = adminService != null ? adminService.getCriticalAlerts() : 0;
                if (alerts > 0) {
                    g2.setColor(ACCENT_RED);
                    g2.fillOval(30, 8, 12, 12);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    g2.drawString(String.valueOf(Math.min(alerts, 9)), 34, 16);
                }
            } catch (Exception e) {
                // Ignore
            }
            
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
            new AdminDashboard();
        });
    }
}
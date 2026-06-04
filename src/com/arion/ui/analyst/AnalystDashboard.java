package com.arion.ui.analyst;

import com.arion.base.BaseDashboard;
import com.arion.ui.common.IconRenderer;
import com.arion.service.DashboardService;
import com.arion.model.File;
import com.arion.model.AuditLog;
import com.arion.model.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

public class AnalystDashboard extends BaseDashboard {
    
    private DashboardService dashboardService;
    
    public AnalystDashboard(User user) {
        super(user);
        
        this.dashboardService = new DashboardService();
        setTitle("ARION - Security Analyst Console");
        
        initializeDashboard();
        setVisible(true);
    }
    
    public AnalystDashboard() {
        this(createDefaultAnalystUser());
    }
    
    private static User createDefaultAnalystUser() {
        User analyst = new User();
        analyst.setUserId(2);
        analyst.setUsername("analyst");
        analyst.setFullName("Security Analyst");
        analyst.setRole("Security Analyst");
        return analyst;
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
        addMenuItem(sidebarPanel, "Security Reports", "reports", ACCENT_CYAN);
        addMenuItem(sidebarPanel, "Analyze Tampering", "tampering", ACCENT_RED);
        addMenuItem(sidebarPanel, "Monitor Activity", "monitor", ACCENT_GREEN);
        addMenuItem(sidebarPanel, "Export Reports", "export", ACCENT_ORANGE);
        
        sidebarPanel.add(Box.createVerticalGlue());
        
        JButton logoutBtn = createMenuButton("Logout", "logout", ACCENT_RED);
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
        
        JTextField searchField = new JTextField("Search incidents...");
        searchField.setBackground(new Color(35, 40, 65));
        searchField.setForeground(TEXT_SECONDARY);
        searchField.setCaretColor(ACCENT_CYAN);
        searchField.setBorder(null);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
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
            case "reports": return "Security Reports";
            case "tampering": return "Tampering Analysis";
            case "monitor": return "System Monitor";
            case "export": return "Export Reports";
            default: return "Security Dashboard";
        }
    }
    
    @Override
    protected String getIconForAction(String action) {
        switch (action) {
            case "dashboard": return "DASHBOARD";
            case "reports": return "REPORT";
            case "tampering": return "ALERT";
            case "monitor": return "MONITOR";
            case "export": return "EXPORT";
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
            case "reports":
                newContent = new ReviewSecurityReportsPage().getPanel();
                break;
            case "tampering":
                newContent = new AnalyzeTamperingPage().getPanel();
                break;
            case "monitor":
                newContent = new MonitorSystemActivityPage().getPanel();
                break;
            case "export":
                newContent = new ExportReportsPage().getPanel();
                break;
            default:
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
        
        JPanel chartPanel = createTamperingTrendChart();
        JPanel alertsPanel = createCriticalAlertsPanel();
        
        middleSection.add(chartPanel);
        middleSection.add(alertsPanel);
        
        content.add(middleSection);
        
        content.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel systemOverview = createSystemOverviewPanel();
        content.add(systemOverview);
        
        return content;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(BG_PRIMARY);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        try {
            Map<String, Integer> stats = dashboardService.getDashboardStats();
            List<File> allFiles = dashboardService.getRecentFiles(100);
            
            int tamperedFiles = stats.get("tamperedFiles");
            int filesAtRisk = (int) allFiles.stream()
                .filter(f -> f.getStatus().equals("Tampered") || f.getLastVerified() == null)
                .count();
            int totalFiles = stats.get("totalFiles");
            int activeUsers = stats.get("totalUsers");
            
            statsPanel.add(createStatCard("Tampering Incidents", 
                String.valueOf(tamperedFiles), "ALERT", ACCENT_RED, 
                tamperedFiles > 0 ? tamperedFiles + " detected" : "All secure"));
            
            statsPanel.add(createStatCard("Files at Risk", 
                String.valueOf(filesAtRisk), "RISK", ACCENT_ORANGE, 
                filesAtRisk > 0 ? "Requires review" : "All protected"));
            
            statsPanel.add(createStatCard("System Alerts", 
                String.valueOf(tamperedFiles + filesAtRisk), "NOTIFICATION", ACCENT_CYAN, 
                "Real-time monitoring"));
            
            statsPanel.add(createStatCard("Active Users", 
                String.valueOf(activeUsers), "USERS", ACCENT_GREEN, 
                "All authenticated"));
                
        } catch (Exception e) {
            System.err.println("Error loading stats: " + e.getMessage());
            statsPanel.add(createStatCard("Tampering Incidents", "0", "ALERT", ACCENT_RED, "Loading..."));
            statsPanel.add(createStatCard("Files at Risk", "0", "RISK", ACCENT_ORANGE, "Loading..."));
            statsPanel.add(createStatCard("System Alerts", "0", "NOTIFICATION", ACCENT_CYAN, "Loading..."));
            statsPanel.add(createStatCard("Active Users", "0", "USERS", ACCENT_GREEN, "Loading..."));
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
    
    private JPanel createTamperingTrendChart() {
        JPanel card = new GlowingCard(25, ACCENT_RED);
        card.setLayout(new BorderLayout(0, 15));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("📊 Tampering Trend");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(BG_CARD);
        
        String[] periods = {"24H", "7D", "1M", "3M"};
        for (String period : periods) {
            JButton btn = new JButton(period);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setForeground(ACCENT_RED);
            btn.setBackground(new Color(35, 40, 65));
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_RED, 2),
                new EmptyBorder(5, 12, 5, 12)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            filterPanel.add(btn);
        }
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);
        
        JPanel chartArea = new TamperingChart();
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(chartArea, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createCriticalAlertsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_ORANGE);
        card.setLayout(new BorderLayout(0, 15));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("⚠️ Critical Alerts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JLabel viewAll = new JLabel("View All →");
        viewAll.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        viewAll.setForeground(ACCENT_ORANGE);
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(viewAll, BorderLayout.EAST);
        
        JPanel alertsList = new JPanel();
        alertsList.setLayout(new BoxLayout(alertsList, BoxLayout.Y_AXIS));
        alertsList.setBackground(BG_CARD);
        
        try {
            List<File> allFiles = dashboardService.getRecentFiles(100);
            List<File> tamperedFiles = allFiles.stream()
                .filter(f -> f.getStatus().equals("Tampered"))
                .limit(5)
                .toList();
            
            if (tamperedFiles.isEmpty()) {
                JLabel noAlertsLabel = new JLabel("No critical alerts");
                noAlertsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noAlertsLabel.setForeground(TEXT_SECONDARY);
                noAlertsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                alertsList.add(Box.createVerticalGlue());
                alertsList.add(noAlertsLabel);
                alertsList.add(Box.createVerticalGlue());
            } else {
                for (File file : tamperedFiles) {
                    String timeAgo = "Recently";
                    if (file.getLastVerified() != null) {
                        long diff = System.currentTimeMillis() - file.getLastVerified().getTime();
                        long minutes = diff / (1000 * 60);
                        long hours = minutes / 60;
                        
                        if (minutes < 1) timeAgo = "Just now";
                        else if (minutes < 60) timeAgo = minutes + " mins ago";
                        else if (hours < 24) timeAgo = hours + " hours ago";
                        else timeAgo = (hours / 24) + " days ago";
                    }
                    
                    JPanel item = createAlertItem(
                        file.getFileName(),
                        "Hash Mismatch Detected",
                        timeAgo,
                        "CRITICAL",
                        ACCENT_RED
                    );
                    alertsList.add(item);
                    alertsList.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading alerts: " + e.getMessage());
            e.printStackTrace();
            
            JLabel errorLabel = new JLabel("Unable to load alerts");
            errorLabel.setForeground(ACCENT_RED);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            alertsList.add(Box.createVerticalGlue());
            alertsList.add(errorLabel);
            alertsList.add(Box.createVerticalGlue());
        }
        
        JScrollPane scrollPane = new JScrollPane(alertsList);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createAlertItem(String fileName, String issue, String time, String severity, Color severityColor) {
        JPanel item = new RoundedPanel(12);
        item.setLayout(new BorderLayout(15, 0));
        item.setBackground(new Color(55, 62, 95));
        item.setBorder(new EmptyBorder(15, 18, 15, 18));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        
        JPanel iconPanel = new FancyIconPanel("ALERT", severityColor);
        iconPanel.setPreferredSize(new Dimension(40, 40));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(55, 62, 95));
        
        JLabel fileLabel = new JLabel(fileName);
        fileLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fileLabel.setForeground(TEXT_PRIMARY);
        fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel issueLabel = new JLabel(issue + " • " + time);
        issueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        issueLabel.setForeground(TEXT_SECONDARY);
        issueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        centerPanel.add(fileLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        centerPanel.add(issueLabel);
        
        JPanel severityBadge = new RoundedPanel(10);
        severityBadge.setBackground(new Color(severityColor.getRed(), severityColor.getGreen(), severityColor.getBlue(), 30));
        severityBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(severityColor, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        severityBadge.setPreferredSize(new Dimension(90, 30));
        
        JLabel severityLabel = new JLabel(severity);
        severityLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        severityLabel.setForeground(severityColor);
        severityBadge.add(severityLabel);
        
        item.add(iconPanel, BorderLayout.WEST);
        item.add(centerPanel, BorderLayout.CENTER);
        item.add(severityBadge, BorderLayout.EAST);
        
        return item;
    }
    
    private JPanel createSystemOverviewPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("🖥️ System Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridPanel.setBackground(BG_CARD);
        
        try {
            Map<String, Integer> stats = dashboardService.getDashboardStats();
            List<AuditLog> recentActivity = dashboardService.getRecentActivity(100);
            
            int activeUsers = stats.get("totalUsers");
            int totalFiles = stats.get("totalFiles");
            int verifiedFiles = stats.get("verifiedFiles");
            int tamperedFiles = stats.get("tamperedFiles");
            
            double detectionRate = totalFiles > 0 ? (double) tamperedFiles / totalFiles * 100 : 0;
            
            gridPanel.add(createMetricCard("Active Sessions", String.valueOf(activeUsers), ACCENT_GREEN));
            gridPanel.add(createMetricCard("Files Processed", String.valueOf(totalFiles), ACCENT_CYAN));
            gridPanel.add(createMetricCard("Avg Response Time", "1.2s", ACCENT_PURPLE));
            gridPanel.add(createMetricCard("System Uptime", "99.8%", ACCENT_GREEN));
            gridPanel.add(createMetricCard("Total Verifications", String.valueOf(verifiedFiles), ACCENT_ORANGE));
            gridPanel.add(createMetricCard("Detection Rate", String.format("%.1f%%", detectionRate), ACCENT_PINK));
            
        } catch (Exception e) {
            System.err.println("Error loading system overview: " + e.getMessage());
            gridPanel.add(createMetricCard("Active Sessions", "0", ACCENT_GREEN));
            gridPanel.add(createMetricCard("Files Processed", "0", ACCENT_CYAN));
            gridPanel.add(createMetricCard("Avg Response Time", "N/A", ACCENT_PURPLE));
            gridPanel.add(createMetricCard("System Uptime", "N/A", ACCENT_GREEN));
            gridPanel.add(createMetricCard("Total Verifications", "0", ACCENT_ORANGE));
            gridPanel.add(createMetricCard("Detection Rate", "0%", ACCENT_PINK));
        }
        
        card.add(title, BorderLayout.NORTH);
        card.add(gridPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createMetricCard(String label, String value, Color color) {
        JPanel card = new RoundedPanel(15);
        card.setLayout(new BorderLayout(0, 10));
        card.setBackground(new Color(35, 40, 65));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(TEXT_SECONDARY);
        
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(labelText, BorderLayout.SOUTH);
        
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
            g2.drawString("Analyst Console", 10, 50);
            
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
                Map<String, Integer> stats = dashboardService.getDashboardStats();
                int alerts = stats.get("tamperedFiles");
                if (alerts > 0) {
                    g2.setColor(ACCENT_RED);
                    g2.fillOval(30, 8, 12, 12);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    g2.drawString(String.valueOf(alerts), alerts > 9 ? 32 : 34, 16);
                }
            } catch (Exception e) {
                // Ignore
            }
            
            g2.dispose();
        }
    }
    
    class TamperingChart extends JPanel {
        private int[] data = {2, 5, 3, 8, 6, 11, 9, 14, 12, 10, 15, 13};
        
        public TamperingChart() {
            setBackground(BG_CARD);
            setPreferredSize(new Dimension(400, 280));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int padding = 40;
            
            g2.setColor(new Color(255, 255, 255, 5));
            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= 5; i++) {
                int y = padding + (height - 2 * padding) * i / 5;
                g2.drawLine(padding, y, width - padding, y);
            }
            
            int segmentWidth = (width - 2 * padding) / (data.length - 1);
            GeneralPath path = new GeneralPath();
            path.moveTo(padding, height - padding);
            
            for (int i = 0; i < data.length; i++) {
                int x = padding + i * segmentWidth;
                int y = height - padding - (data[i] * (height - 2 * padding) / 20);
                if (i == 0) {
                    path.lineTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            
            path.lineTo(width - padding, height - padding);
            path.closePath();
            
            GradientPaint gradient = new GradientPaint(
                0, padding, new Color(ACCENT_RED.getRed(), ACCENT_RED.getGreen(), ACCENT_RED.getBlue(), 80),
                0, height, new Color(ACCENT_RED.getRed(), ACCENT_RED.getGreen(), ACCENT_RED.getBlue(), 10)
            );
            g2.setPaint(gradient);
            g2.fill(path);
            
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(ACCENT_RED);
            
            for (int i = 0; i < data.length - 1; i++) {
                int x1 = padding + i * segmentWidth;
                int y1 = height - padding - (data[i] * (height - 2 * padding) / 20);
                int x2 = padding + (i + 1) * segmentWidth;
                int y2 = height - padding - (data[i + 1] * (height - 2 * padding) / 20);
                
                g2.drawLine(x1, y1, x2, y2);
            }
            
            for (int i = 0; i < data.length; i++) {
                int x = padding + i * segmentWidth;
                int y = height - padding - (data[i] * (height - 2 * padding) / 20);
                
                RadialGradientPaint glow = new RadialGradientPaint(
                    x, y, 8,
                    new float[]{0f, 1f},
                    new Color[]{
                        new Color(ACCENT_ORANGE.getRed(), ACCENT_ORANGE.getGreen(), ACCENT_ORANGE.getBlue(), 100),
                        new Color(ACCENT_ORANGE.getRed(), ACCENT_ORANGE.getGreen(), ACCENT_ORANGE.getBlue(), 0)
                    }
                );
                g2.setPaint(glow);
                g2.fillOval(x - 8, y - 8, 16, 16);
                
                g2.setColor(ACCENT_ORANGE);
                g2.fillOval(x - 5, y - 5, 10, 10);
                g2.setColor(BG_CARD);
                g2.fillOval(x - 3, y - 3, 6, 6);
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
            new AnalystDashboard();
        });
    }
}
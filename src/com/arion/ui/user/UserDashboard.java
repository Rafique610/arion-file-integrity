package com.arion.ui.user;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.arion.base.BaseDashboard;
import com.arion.ui.common.IconRenderer;
import com.arion.ui.common.LoginPage;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.sql.Timestamp;

import com.arion.service.DashboardService;
import com.arion.model.File;
import com.arion.model.AuditLog;
import com.arion.model.User;
import java.util.List;
import java.util.Map;

public class UserDashboard extends BaseDashboard {
    
    private DashboardService dashboardService;
    private int currentUserId;
    
    public UserDashboard(User user) {
        super(user);
        
        this.currentUserId = user.getUserId();
        this.dashboardService = new DashboardService();
        setTitle("ARION - File Integrity System");
        
        initializeDashboard();
        setVisible(true);
    }

    public UserDashboard() {
        this(createDefaultUser());
    }
    
    private static User createDefaultUser() {
        User user = new User();
        user.setUserId(3);
        user.setUsername("jdoe");
        user.setFullName("John Doe");
        user.setEmail("jdoe@arion.com");
        user.setRole("User");
        return user;
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
        addMenuItem(sidebarPanel, "Upload File", "upload", ACCENT_GREEN);
        addMenuItem(sidebarPanel, "Verify Integrity", "verify", ACCENT_CYAN);
        addMenuItem(sidebarPanel, "File History", "history", ACCENT_ORANGE);
        addMenuItem(sidebarPanel, "Hash Algorithm", "algorithm", ACCENT_PINK);
        addMenuItem(sidebarPanel, "Security Reports", "reports", new Color(100, 200, 255));
        
        sidebarPanel.add(Box.createVerticalGlue());
        
        JButton logoutBtn = createMenuButton("Logout", "logout", ACCENT_RED);
        logoutBtn.addActionListener(e -> handleLogout());
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
        
        JTextField searchField = new JTextField("Search files...");
        searchField.setBackground(new Color(35, 40, 65));
        searchField.setForeground(TEXT_SECONDARY);
        searchField.setCaretColor(ACCENT_CYAN);
        searchField.setBorder(null);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search files...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search files...");
                    searchField.setForeground(TEXT_SECONDARY);
                }
            }
        });
        
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        JPanel notifPanel = new NotificationBellPanel();
        notifPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel userPanel = new RoundedPanel(20);
        userPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));
        userPanel.setPreferredSize(new Dimension(150, 45));
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
        contentPanel.add(dashboardContent, BorderLayout.CENTER);
    }
    
    @Override
    protected String getPageTitle(String page) {
        switch (page) {
            case "upload": return "Upload File";
            case "verify": return "Verify File Integrity";
            case "history": return "File History";
            case "algorithm": return "Hash Algorithm";
            case "reports": return "Security Reports";
            default: return "Dashboard";
        }
    }
    
    @Override
    protected String getIconForAction(String action) {
        switch (action) {
            case "dashboard": return "DASHBOARD";
            case "upload": return "UPLOAD";
            case "verify": return "SHIELD";
            case "history": return "CLOCK";
            case "algorithm": return "HASH";
            case "reports": return "REPORT";
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
        
        JPanel newContent;
        switch (page) {
            case "upload":
                newContent = new UploadFilePage(currentUserId, this::refreshDashboard).getPanel();
                break;
            case "verify":
                newContent = new VerifyFilePage(currentUserId).getPanel();
                break;
            case "history":
                newContent = new FileHistoryPage(currentUserId).getPanel();
                break;
            case "algorithm":
                newContent = new SelectHashAlgorithmPage().getPanel();
                break;
            case "reports":
                newContent = new SecurityReportsPage(currentUser).getPanel();
                break;
            default:
                newContent = createDashboardContent();
        }
        
        contentPanel.add(newContent, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void refreshDashboard() {
        if ("dashboard".equals(activePage)) {
            if (contentPanel.getComponentCount() > 1) {
                contentPanel.remove(1);
            }
            
            JPanel newDashboard = createDashboardContent();
            contentPanel.add(newDashboard, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }
    
    private void handleLogout() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            new LoginPage();
        });
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
        
        JPanel chartPanel = createEnhancedChartCard();
        JPanel activityPanel = createEnhancedActivityCard();
        
        middleSection.add(chartPanel);
        middleSection.add(activityPanel);
        
        content.add(middleSection);
        
        content.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel filesPanel = createEnhancedFilesTable();
        content.add(filesPanel);
        
        return content;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(BG_PRIMARY);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        try {
            Map<String, Integer> stats = dashboardService.getDashboardStats();
            List<File> userFiles = dashboardService.getUserFiles(currentUserId);
            
            int totalFiles = userFiles.size();
            int verifiedToday = (int) userFiles.stream()
                .filter(f -> f.getStatus().equals("Verified"))
                .count();
            int alerts = (int) userFiles.stream()
                .filter(f -> f.getStatus().equals("Tampered"))
                .count();
            int protectedFiles = (int) userFiles.stream()
                .filter(f -> f.getStatus().equals("Protected"))
                .count();
            
            statsPanel.add(createEnhancedStatCard("Total Files", 
                String.valueOf(totalFiles), "FILE", ACCENT_PURPLE, 
                totalFiles > 0 ? "+" + totalFiles + " in your account" : "No files yet"));
            
            statsPanel.add(createEnhancedStatCard("Verified Today", 
                String.valueOf(verifiedToday), "SHIELD", ACCENT_GREEN, 
                "All secure"));
            
            statsPanel.add(createEnhancedStatCard("Recent Alerts", 
                String.valueOf(alerts), "ALERT", ACCENT_ORANGE, 
                alerts > 0 ? "Requires attention" : "No alerts"));
            
            statsPanel.add(createEnhancedStatCard("Protected Files", 
                String.valueOf(protectedFiles), "LOCK", ACCENT_CYAN, 
                "Active protection"));
                
        } catch (Exception e) {
            System.err.println("Error loading stats: " + e.getMessage());
            statsPanel.add(createEnhancedStatCard("Total Files", "0", "FILE", ACCENT_PURPLE, "Loading..."));
            statsPanel.add(createEnhancedStatCard("Verified Today", "0", "SHIELD", ACCENT_GREEN, "Loading..."));
            statsPanel.add(createEnhancedStatCard("Recent Alerts", "0", "ALERT", ACCENT_ORANGE, "Loading..."));
            statsPanel.add(createEnhancedStatCard("Protected Files", "0", "LOCK", ACCENT_CYAN, "Loading..."));
        }
        
        return statsPanel;
    }
    
    private JPanel createEnhancedStatCard(String label, String value, String iconType, Color accentColor, String subtitle) {
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
    
    private JPanel createEnhancedChartCard() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 15));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("File Verification Trend");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(BG_CARD);
        
        String[] periods = {"7D", "1M", "3M", "1Y"};
        for (String period : periods) {
            JButton btn = new JButton(period);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setForeground(ACCENT_CYAN);
            btn.setBackground(new Color(35, 40, 65));
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_CYAN, 2),
                new EmptyBorder(5, 12, 5, 12)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            filterPanel.add(btn);
        }
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);
        
        JPanel chartArea = new EnhancedLineChart();
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(chartArea, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createEnhancedActivityCard() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 15));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("Recent Activity");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JLabel viewAll = new JLabel("View All →");
        viewAll.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        viewAll.setForeground(ACCENT_PURPLE);
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(viewAll, BorderLayout.EAST);
        
        JPanel activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setBackground(BG_CARD);
        
        try {
            List<AuditLog> recentActivity = dashboardService.getRecentActivity(5);
            
            for (AuditLog log : recentActivity) {
                String status = "SUCCESS";
                Color statusColor = ACCENT_GREEN;
                
                if (log.getAction().contains("Verify") && log.getDetails().contains("mismatch")) {
                    status = "WARNING";
                    statusColor = ACCENT_ORANGE;
                }
                
                String timeAgo = "Recently";
                if (log.getTimestamp() != null) {
                    long diff = System.currentTimeMillis() - log.getTimestamp().getTime();
                    long minutes = diff / (1000 * 60);
                    long hours = minutes / 60;
                    long days = hours / 24;
                    
                    if (minutes < 1) timeAgo = "Just now";
                    else if (minutes < 60) timeAgo = minutes + " mins ago";
                    else if (hours < 24) timeAgo = hours + " hours ago";
                    else timeAgo = days + " days ago";
                }
                
                String fileName = log.getDetails();
                if (fileName != null && fileName.length() > 30) {
                    fileName = fileName.substring(0, 30) + "...";
                }
                
                JPanel item = createFancyActivityItem(
                    fileName != null ? fileName : "Unknown",
                    log.getAction(),
                    timeAgo,
                    status,
                    statusColor
                );
                activityList.add(item);
                activityList.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
            if (recentActivity.isEmpty()) {
                JLabel noActivityLabel = new JLabel("No recent activity");
                noActivityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noActivityLabel.setForeground(TEXT_SECONDARY);
                noActivityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                activityList.add(Box.createVerticalGlue());
                activityList.add(noActivityLabel);
                activityList.add(Box.createVerticalGlue());
            }
            
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Unable to load activity");
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setForeground(ACCENT_RED);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            activityList.add(Box.createVerticalGlue());
            activityList.add(errorLabel);
            activityList.add(Box.createVerticalGlue());
            
            System.err.println("Error loading activity: " + e.getMessage());
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(activityList);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createFancyActivityItem(String fileName, String action, String time, String status, Color statusColor) {
        JPanel item = new RoundedPanel(12);
        item.setLayout(new BorderLayout(15, 0));
        item.setBackground(new Color(55, 62, 95));
        item.setBorder(new EmptyBorder(15, 18, 15, 18));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        
        JPanel fileIcon = new FancyIconPanel("FILE", ACCENT_CYAN);
        fileIcon.setPreferredSize(new Dimension(40, 40));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(55, 62, 95));
        
        JLabel fileLabel = new JLabel(fileName);
        fileLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fileLabel.setForeground(TEXT_PRIMARY);
        fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel actionLabel = new JLabel(action + " • " + time);
        actionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        actionLabel.setForeground(TEXT_SECONDARY);
        actionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        centerPanel.add(fileLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        centerPanel.add(actionLabel);
        
        JPanel statusBadge = new RoundedPanel(10);
        statusBadge.setBackground(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 30));
        statusBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(statusColor, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        statusBadge.setPreferredSize(new Dimension(80, 30));
        
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(statusColor);
        statusBadge.add(statusLabel);
        
        item.add(fileIcon, BorderLayout.WEST);
        item.add(centerPanel, BorderLayout.CENTER);
        item.add(statusBadge, BorderLayout.EAST);
        
        return item;
    }
    
    private JPanel createEnhancedFilesTable() {
        JPanel card = new GlowingCard(25, ACCENT_PINK);
        card.setLayout(new BorderLayout(0, 15));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("Recent Files");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setBackground(BG_CARD);
        
        JButton filterBtn = createIconButton("Filter", ACCENT_CYAN);
        JButton exportBtn = createIconButton("Export", ACCENT_GREEN);
        
        controlsPanel.add(filterBtn);
        controlsPanel.add(exportBtn);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(controlsPanel, BorderLayout.EAST);
        
        try {
            List<File> userFiles = dashboardService.getUserFiles(currentUserId);
            
            String[] columns = {"File Name", "Upload Date", "Algorithm", "Status", "Action"};
            Object[][] data = new Object[userFiles.size()][5];
            
            for (int i = 0; i < userFiles.size(); i++) {
                File file = userFiles.get(i);
                data[i][0] = file.getFileName();
                data[i][1] = file.getUploadDate() != null ? 
                    new java.text.SimpleDateFormat("yyyy-MM-dd").format(file.getUploadDate()) : "N/A";
                data[i][2] = file.getHashAlgorithm();
                data[i][3] = file.getStatus();
                data[i][4] = file.getStatus().equals("Protected") ? "Verify" : "View";
            }
            
            DefaultTableModel model = new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 4;
                }
            };
            
            JTable table = new JTable(model);
            table.setBackground(BG_CARD);
            table.setForeground(TEXT_PRIMARY);
            table.setGridColor(new Color(255, 255, 255, 5));
            table.setRowHeight(55);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.setShowGrid(false);
            table.setIntercellSpacing(new Dimension(0, 5));
            
            JTableHeader header = table.getTableHeader();
            header.setBackground(new Color(35, 40, 65));
            header.setForeground(ACCENT_CYAN);
            header.setFont(new Font("Segoe UI", Font.BOLD, 13));
            header.setPreferredSize(new Dimension(header.getWidth(), 45));
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_CYAN));
            
            table.setSelectionBackground(new Color(138, 99, 255, 30));
            table.setSelectionForeground(TEXT_PRIMARY);
            
            table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBackground(BG_CARD);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(BG_CARD);
            
            scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
                @Override
                protected void configureScrollBarColors() {
                    this.thumbColor = ACCENT_PURPLE;
                    this.trackColor = new Color(35, 40, 65);
                }
                
                @Override
                protected JButton createDecreaseButton(int orientation) {
                    return createZeroButton();
                }
                
                @Override
                protected JButton createIncreaseButton(int orientation) {
                    return createZeroButton();
                }
                
                private JButton createZeroButton() {
                    JButton button = new JButton();
                    button.setPreferredSize(new Dimension(0, 0));
                    return button;
                }
            });
            
            card.add(headerPanel, BorderLayout.NORTH);
            card.add(scrollPane, BorderLayout.CENTER);
            
        } catch (Exception e) {
            System.err.println("Error loading files: " + e.getMessage());
            e.printStackTrace();
            
            JLabel errorLabel = new JLabel("Unable to load files");
            errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            errorLabel.setForeground(ACCENT_RED);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            card.add(headerPanel, BorderLayout.NORTH);
            card.add(errorLabel, BorderLayout.CENTER);
        }
        
        return card;
    }
    
    private JButton createIconButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(color);
        btn.setBackground(new Color(35, 40, 65));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(8, 15, 8, 15)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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
            g2.drawString("File Integrity", 10, 50);
            
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
            
            g2.setColor(ACCENT_RED);
            g2.fillOval(30, 8, 12, 12);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            g2.drawString("3", 34, 16);
            
            g2.dispose();
        }
    }
    
    class EnhancedLineChart extends JPanel {
        private int[] data = {45, 52, 48, 65, 58, 72, 68, 75, 82, 78, 88, 92};
        
        public EnhancedLineChart() {
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
                int y = height - padding - (data[i] * (height - 2 * padding) / 100);
                if (i == 0) {
                    path.lineTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            
            path.lineTo(width - padding, height - padding);
            path.closePath();
            
            GradientPaint gradient = new GradientPaint(
                0, padding, new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), 80),
                0, height, new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), 10)
            );
            g2.setPaint(gradient);
            g2.fill(path);
            
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(ACCENT_CYAN);
            
            for (int i = 0; i < data.length - 1; i++) {
                int x1 = padding + i * segmentWidth;
                int y1 = height - padding - (data[i] * (height - 2 * padding) / 100);
                int x2 = padding + (i + 1) * segmentWidth;
                int y2 = height - padding - (data[i + 1] * (height - 2 * padding) / 100);
                
                g2.drawLine(x1, y1, x2, y2);
            }
            
            for (int i = 0; i < data.length; i++) {
                int x = padding + i * segmentWidth;
                int y = height - padding - (data[i] * (height - 2 * padding) / 100);
                
                RadialGradientPaint glow = new RadialGradientPaint(
                    x, y, 8,
                    new float[]{0f, 1f},
                    new Color[]{
                        new Color(ACCENT_GREEN.getRed(), ACCENT_GREEN.getGreen(), ACCENT_GREEN.getBlue(), 100),
                        new Color(ACCENT_GREEN.getRed(), ACCENT_GREEN.getGreen(), ACCENT_GREEN.getBlue(), 0)
                    }
                );
                g2.setPaint(glow);
                g2.fillOval(x - 8, y - 8, 16, 16);
                
                g2.setColor(ACCENT_GREEN);
                g2.fillOval(x - 5, y - 5, 10, 10);
                g2.setColor(BG_CARD);
                g2.fillOval(x - 3, y - 3, 6, 6);
            }
            
            g2.dispose();
        }
    }
    
    class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 3) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(false);
                
                JLabel statusLabel = new JLabel(value.toString());
                statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                Color statusColor;
                if (value.toString().equals("Protected")) {
                    statusColor = ACCENT_CYAN;
                } else if (value.toString().equals("Verified")) {
                    statusColor = ACCENT_GREEN;
                } else {
                    statusColor = ACCENT_ORANGE;
                }
                
                statusLabel.setForeground(statusColor);
                statusLabel.setOpaque(true);
                statusLabel.setBackground(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 30));
                statusLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(statusColor, 1),
                    new EmptyBorder(5, 10, 5, 10)
                ));
                
                panel.add(statusLabel);
                return panel;
            }
            
            if (column == 4) {
                JButton actionBtn = new JButton(value.toString());
                actionBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                actionBtn.setForeground(ACCENT_PURPLE);
                actionBtn.setBackground(new Color(ACCENT_PURPLE.getRed(), ACCENT_PURPLE.getGreen(), ACCENT_PURPLE.getBlue(), 30));
                actionBtn.setBorder(new EmptyBorder(8, 15, 8, 15));
                actionBtn.setFocusPainted(false);
                actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return actionBtn;
            }
            
            setBackground(isSelected ? new Color(138, 99, 255, 30) : BG_CARD);
            setForeground(TEXT_PRIMARY);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            
            return c;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new UserDashboard();
        });
    }
}
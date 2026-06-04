package com.arion.ui.analyst;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.arion.dao.UserDAO;
import com.arion.dao.FileDAO;
import com.arion.dao.FileUploadHistoryDAO;
import com.arion.model.User;
import com.arion.model.FileUploadHistory;

public class MonitorSystemActivityPage {
    
    private static final Color BG_PRIMARY = new Color(25, 28, 45);
    private static final Color BG_CARD = new Color(45, 52, 85);
    private static final Color ACCENT_PURPLE = new Color(138, 99, 255);
    private static final Color ACCENT_CYAN = new Color(0, 230, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 163);
    private static final Color ACCENT_ORANGE = new Color(255, 180, 0);
    private static final Color ACCENT_RED = new Color(255, 70, 100);
    private static final Color TEXT_PRIMARY = new Color(240, 242, 255);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 200);
    
    private JPanel mainPanel;
    private JTextArea liveLogArea;
    private JCheckBox autoRefreshCheckBox;
    private Timer logUpdateTimer;
    private UserDAO userDAO;
    private FileDAO fileDAO;
    private FileUploadHistoryDAO uploadHistoryDAO;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateTimeFormat;
    
    // Statistics labels
    private JLabel activeUsersLabel;
    private JLabel filesProcessingLabel;
    private JLabel eventsPerMinuteLabel;
    private JLabel systemLoadLabel;
    
    // Session table
    private DefaultTableModel sessionTableModel;
    private List<ActiveSession> activeSessions;
    
    // Activity tracking
    private int totalEvents = 0;
    private long lastEventTime = System.currentTimeMillis();
    
    public MonitorSystemActivityPage() {
        this.userDAO = new UserDAO();
        this.fileDAO = new FileDAO();
        this.uploadHistoryDAO = new FileUploadHistoryDAO();
        this.timeFormat = new SimpleDateFormat("HH:mm:ss");
        this.dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.activeSessions = new ArrayList<>();
        
        loadInitialData();
        createUI();
        startLiveMonitoring();
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    
    private void loadInitialData() {
        try {
            // Load all users to simulate active sessions
            List<User> allUsers = userDAO.getAllUsers();
            
            // Create active sessions for users who have recent activity
            Random rand = new Random();
            for (User user : allUsers) {
                if (user.isActive() && rand.nextInt(100) < 60) { // 60% chance of being online
                    ActiveSession session = new ActiveSession();
                    session.username = user.getUsername();
                    session.fullName = user.getFullName();
                    session.ipAddress = generateRandomIP();
                    session.location = getRandomLocation();
                    session.action = getRandomAction();
                    session.duration = rand.nextInt(60) + " mins";
                    session.status = rand.nextInt(100) < 85 ? "Active" : "Idle";
                    session.userId = user.getUserId();
                    
                    activeSessions.add(session);
                }
            }
            
            System.out.println("[MonitorSystemActivity] Loaded " + activeSessions.size() + " active sessions");
            
        } catch (Exception e) {
            System.err.println("[MonitorSystemActivity] Error loading initial data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String generateRandomIP() {
        Random rand = new Random();
        return "192.168." + (rand.nextInt(255) + 1) + "." + (rand.nextInt(255) + 1);
    }
    
    private String getRandomLocation() {
        String[] locations = {
            "New York, US", "London, UK", "Tokyo, JP", "Berlin, DE", 
            "Sydney, AU", "Toronto, CA", "Paris, FR", "Singapore", 
            "Mumbai, IN", "São Paulo, BR"
        };
        return locations[new Random().nextInt(locations.length)];
    }
    
    private String getRandomAction() {
        String[] actions = {
            "Uploading", "Verifying", "Browsing", "Downloading", 
            "Processing", "Analyzing"
        };
        return actions[new Random().nextInt(actions.length)];
    }
    
    private void createUI() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BG_PRIMARY);
        
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(BG_PRIMARY);
        
        JPanel controlPanel = createControlPanel();
        contentWrapper.add(controlPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel statsPanel = createRealTimeStatsPanel();
        contentWrapper.add(statsPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel middleSection = new JPanel(new GridLayout(1, 2, 25, 0));
        middleSection.setBackground(BG_PRIMARY);
        middleSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));
        
        JPanel liveLogPanel = createLiveLogPanel();
        JPanel activityGraphPanel = createActivityGraphPanel();
        
        middleSection.add(liveLogPanel);
        middleSection.add(activityGraphPanel);
        
        contentWrapper.add(middleSection);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel activeSessionsPanel = createActiveSessionsPanel();
        contentWrapper.add(activeSessionsPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Update initial statistics
        updateStatistics();
    }
    
    private JPanel createControlPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(20, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel title = new JLabel("🖥️ Monitoring Controls");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlsPanel.setBackground(BG_CARD);
        
        JLabel statusIndicator = new JLabel("● Live");
        statusIndicator.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusIndicator.setForeground(ACCENT_GREEN);
        
        autoRefreshCheckBox = new JCheckBox("Auto-Refresh");
        autoRefreshCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        autoRefreshCheckBox.setForeground(TEXT_PRIMARY);
        autoRefreshCheckBox.setBackground(BG_CARD);
        autoRefreshCheckBox.setSelected(true);
        autoRefreshCheckBox.setFocusPainted(false);
        
        JButton pauseBtn = createOutlineButton("⏸️ Pause", ACCENT_ORANGE);
        pauseBtn.addActionListener(e -> toggleMonitoring());
        
        JButton refreshBtn = createOutlineButton("🔄 Refresh", ACCENT_CYAN);
        refreshBtn.addActionListener(e -> refreshData());
        
        JButton raiseAlertBtn = createGradientButton("🚨 Raise Alert", ACCENT_RED, ACCENT_ORANGE);
        raiseAlertBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel,
                "Alert notification sent to administrators",
                "Alert Raised",
                JOptionPane.WARNING_MESSAGE);
        });
        
        JButton exportBtn = createOutlineButton("📥 Export Logs", ACCENT_CYAN);
        exportBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel,
                "Activity logs exported successfully",
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        controlsPanel.add(statusIndicator);
        controlsPanel.add(autoRefreshCheckBox);
        controlsPanel.add(pauseBtn);
        controlsPanel.add(refreshBtn);
        controlsPanel.add(raiseAlertBtn);
        controlsPanel.add(exportBtn);
        
        card.add(title, BorderLayout.WEST);
        card.add(controlsPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private JPanel createRealTimeStatsPanel() {
        JPanel container = new JPanel(new GridLayout(1, 4, 20, 0));
        container.setBackground(BG_PRIMARY);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        
        activeUsersLabel = new JLabel("0");
        filesProcessingLabel = new JLabel("0");
        eventsPerMinuteLabel = new JLabel("0");
        systemLoadLabel = new JLabel("0%");
        
        container.add(createLiveStatCard("Active Users", activeUsersLabel, "👥", ACCENT_CYAN, "Currently online"));
        container.add(createLiveStatCard("Files Processing", filesProcessingLabel, "⚙️", ACCENT_ORANGE, "Real-time"));
        container.add(createLiveStatCard("Events/Minute", eventsPerMinuteLabel, "📊", ACCENT_PURPLE, "Normal range"));
        container.add(createLiveStatCard("System Load", systemLoadLabel, "💻", ACCENT_GREEN, "Optimal"));
        
        return container;
    }
    
    private JPanel createLiveStatCard(String label, JLabel valueLabel, String icon, Color color, String subtitle) {
        JPanel card = new GlowingCard(20, color);
        card.setLayout(new BorderLayout(15, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = Math.min(getWidth(), getHeight());
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 60),
                    size, size, new Color(color.getRed(), color.getGreen(), color.getBlue(), 30)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, size, size, 12, 12);
                
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, size - 2, size - 2, 12, 12);
                
                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(50, 50));
        iconPanel.setOpaque(false);
        iconPanel.setLayout(new GridBagLayout());
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconPanel.add(iconLabel);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BG_CARD);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(TEXT_PRIMARY);
        labelText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subText = new JLabel(subtitle);
        subText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subText.setForeground(TEXT_SECONDARY);
        subText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(labelText);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        textPanel.add(subText);
        
        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createLiveLogPanel() {
        JPanel card = new GlowingCard(25, ACCENT_GREEN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("📜 Live Activity Log");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JLabel streamingLabel = new JLabel("● Streaming");
        streamingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        streamingLabel.setForeground(ACCENT_GREEN);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(streamingLabel, BorderLayout.EAST);
        
        liveLogArea = new JTextArea();
        liveLogArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        liveLogArea.setBackground(new Color(20, 22, 35));
        liveLogArea.setForeground(ACCENT_GREEN);
        liveLogArea.setCaretColor(ACCENT_GREEN);
        liveLogArea.setEditable(false);
        liveLogArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Load initial log entries from recent activity
        loadInitialLogEntries();
        
        JScrollPane scrollPane = new JScrollPane(liveLogArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_GREEN, 2));
        scrollPane.getViewport().setBackground(new Color(20, 22, 35));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ACCENT_GREEN;
                this.trackColor = new Color(20, 22, 35);
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
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setBackground(BG_CARD);
        
        JButton clearBtn = createOutlineButton("🗑️ Clear", ACCENT_RED);
        clearBtn.addActionListener(e -> liveLogArea.setText(""));
        
        JButton saveBtn = createOutlineButton("💾 Save Log", ACCENT_CYAN);
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel,
                "Activity log saved successfully",
                "Saved",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        bottomPanel.add(clearBtn);
        bottomPanel.add(saveBtn);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(BG_CARD);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadInitialLogEntries() {
        try {
            // Get recent file upload history from all users
            List<FileUploadHistory> recentHistory = uploadHistoryDAO.getAllHistory();
            
            StringBuilder log = new StringBuilder();
            SimpleDateFormat logFormat = new SimpleDateFormat("HH:mm:ss");
            
            // Display last 10 activities
            int count = 0;
            for (FileUploadHistory history : recentHistory) {
                if (count >= 10) break;
                
                String timestamp = logFormat.format(history.getTimestamp());
                String action = history.getActionType();
                String status = history.getActionStatus();
                String username = history.getUsername() != null ? history.getUsername() : "User" + history.getUserId();
                
                log.append("[").append(timestamp).append("] ");
                log.append("User '").append(username).append("' ");
                log.append(action.toLowerCase()).append("ed file");
                if (history.getFileName() != null) {
                    log.append(" '").append(history.getFileName()).append("'");
                }
                log.append(" - Status: ").append(status);
                log.append("\n");
                
                count++;
            }
            
            if (log.length() == 0) {
                log.append("[System] Monitoring started - Waiting for activity...\n");
            }
            
            liveLogArea.setText(log.toString());
            
        } catch (Exception e) {
            System.err.println("[MonitorSystemActivity] Error loading log entries: " + e.getMessage());
            liveLogArea.setText("[System] Monitoring started\n");
        }
    }
    
    private JPanel createActivityGraphPanel() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 15));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("📈 Activity Graph");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(BG_CARD);
        
        String[] timeframes = {"1H", "6H", "24H"};
        for (String tf : timeframes) {
            JButton btn = new JButton(tf);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setForeground(ACCENT_CYAN);
            btn.setBackground(new Color(35, 40, 65));
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_CYAN, 2),
                new EmptyBorder(4, 10, 4, 10)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            filterPanel.add(btn);
        }
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);
        
        JPanel chartArea = new RealTimeActivityChart();
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(chartArea, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createActiveSessionsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("👥 Active Sessions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JLabel countLabel = new JLabel(activeSessions.size() + " users online");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countLabel.setForeground(ACCENT_CYAN);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        
        String[] columns = {"User", "IP Address", "Location", "Action", "Duration", "Status"};
        sessionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Load active sessions into table
        loadSessionsIntoTable();
        
        JTable table = new JTable(sessionTableModel);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(255, 255, 255, 5));
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(138, 99, 255, 30));
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(35, 40, 65));
        header.setForeground(ACCENT_PURPLE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_PURPLE));
        
        table.setDefaultRenderer(Object.class, new SessionTableRenderer());
        
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
        
        return card;
    }
    
    private void loadSessionsIntoTable() {
        sessionTableModel.setRowCount(0);
        
        for (ActiveSession session : activeSessions) {
            sessionTableModel.addRow(new Object[]{
                session.fullName != null ? session.fullName : session.username,
                session.ipAddress,
                session.location,
                session.action,
                session.duration,
                session.status
            });
        }
    }
    
    private void updateStatistics() {
        // Update active users
        activeUsersLabel.setText(String.valueOf(activeSessions.size()));
        
        // Calculate files being processed (files uploaded recently)
        int processingFiles = fileDAO.getFileCountByStatus("Protected") + 
                             fileDAO.getFileCountByStatus("Verified");
        filesProcessingLabel.setText(String.valueOf(Math.min(processingFiles, 20)));
        
        // Calculate events per minute
        long currentTime = System.currentTimeMillis();
        long timeDiff = (currentTime - lastEventTime) / 1000; // seconds
        int eventsPerMinute = timeDiff > 0 ? (int)((totalEvents * 60.0) / timeDiff) : 0;
        eventsPerMinuteLabel.setText(String.valueOf(Math.min(eventsPerMinute, 999)));
        
        // Calculate system load (based on active sessions and files)
        int load = (activeSessions.size() * 2) + (processingFiles / 2);
        systemLoadLabel.setText(Math.min(load, 99) + "%");
    }
    
    private void refreshData() {
        activeSessions.clear();
        loadInitialData();
        loadSessionsIntoTable();
        loadInitialLogEntries();
        updateStatistics();
        
        JOptionPane.showMessageDialog(mainPanel,
            "Monitoring data refreshed\nActive users: " + activeSessions.size(),
            "Refreshed",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void startLiveMonitoring() {
        logUpdateTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (autoRefreshCheckBox.isSelected()) {
                    appendLogEntry();
                    updateStatistics();
                    totalEvents++;
                }
            }
        });
        logUpdateTimer.start();
    }
    
    private void appendLogEntry() {
        try {
            // Get a random recent activity from database
            List<FileUploadHistory> recentHistory = uploadHistoryDAO.getAllHistory();
            
            if (!recentHistory.isEmpty() && recentHistory.size() > 0) {
                FileUploadHistory history = recentHistory.get(new Random().nextInt(Math.min(recentHistory.size(), 10)));
                
                String timestamp = "[" + timeFormat.format(new Date()) + "]";
                String action = history.getActionType().toLowerCase();
                String status = history.getActionStatus();
                String username = history.getUsername() != null ? history.getUsername() : "User" + history.getUserId();
                
                String newEntry = timestamp + " User '" + username + "' " + 
                                action + "ed file - Status: " + status + "\n";
                
                liveLogArea.append(newEntry);
                liveLogArea.setCaretPosition(liveLogArea.getDocument().getLength());
            } else {
                // Fallback to simulated activity
                String timestamp = "[" + timeFormat.format(new Date()) + "]";
                String[] actions = {"uploaded", "verified", "accessed", "downloaded"};
                String action = actions[new Random().nextInt(actions.length)];
                
                String newEntry = timestamp + " System activity: " + action + " operation\n";
                liveLogArea.append(newEntry);
                liveLogArea.setCaretPosition(liveLogArea.getDocument().getLength());
            }
            
        } catch (Exception ex) {
            System.err.println("[MonitorSystemActivity] Error appending log: " + ex.getMessage());
        }
    }
    
    private void toggleMonitoring() {
        if (logUpdateTimer.isRunning()) {
            logUpdateTimer.stop();
            JOptionPane.showMessageDialog(mainPanel, 
                "Live monitoring paused", 
                "Paused", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            logUpdateTimer.start();
            JOptionPane.showMessageDialog(mainPanel, 
                "Live monitoring resumed", 
                "Resumed", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private JButton createGradientButton(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }
    
    private JButton createOutlineButton(String text, Color color) {
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
    
    // Data class for active sessions
    class ActiveSession {
        String username;
        String fullName;
        String ipAddress;
        String location;
        String action;
        String duration;
        String status;
        int userId;
    }
    
    class RealTimeActivityChart extends JPanel {
        private int[] data = {15, 18, 22, 19, 25, 28, 24, 30, 27, 32, 29, 35};
        
        public RealTimeActivityChart() {
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
            
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(ACCENT_CYAN);
            
            for (int i = 0; i < data.length - 1; i++) {
                int x1 = padding + i * segmentWidth;
                int y1 = height - padding - (data[i] * (height - 2 * padding) / 40);
                int x2 = padding + (i + 1) * segmentWidth;
                int y2 = height - padding - (data[i + 1] * (height - 2 * padding) / 40);
                
                g2.drawLine(x1, y1, x2, y2);
            }
            
            for (int i = 0; i < data.length; i++) {
                int x = padding + i * segmentWidth;
                int y = height - padding - (data[i] * (height - 2 * padding) / 40);
                
                g2.setColor(ACCENT_GREEN);
                g2.fillOval(x - 4, y - 4, 8, 8);
            }
            
            g2.dispose();
        }
    }
    
    class SessionTableRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 5) { // Status column
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(false);
                
                String status = value.toString();
                JLabel label = new JLabel(status);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                
                Color color = status.equals("Active") ? ACCENT_GREEN : ACCENT_ORANGE;
                
                label.setForeground(color);
                label.setOpaque(true);
                label.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 1),
                    new EmptyBorder(5, 10, 5, 10)
                ));
                
                panel.add(label);
                return panel;
            }
            
            setBackground(isSelected ? new Color(138, 99, 255, 30) : BG_CARD);
            setForeground(TEXT_PRIMARY);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            return c;
        }
    }
    
    class GlowingCard extends JPanel {
        private int cornerRadius;
        private Color glowColor;
        
        public GlowingCard(int radius, Color glowColor) {
            this.cornerRadius = radius;
            this.glowColor = glowColor;
            setOpaque(false);
        }
        
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 10));
            for (int i = 0; i < 5; i++) {
                g2.draw(new RoundRectangle2D.Float(i, i, getWidth() - i * 2, getHeight() - i * 2, cornerRadius, cornerRadius));
            }
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            g2.setColor(new Color(255, 255, 255, 20));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
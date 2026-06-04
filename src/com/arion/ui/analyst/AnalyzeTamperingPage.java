package com.arion.ui.analyst;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.arion.service.DashboardService;
import com.arion.dao.FileIntegrityCheckDAO;
import com.arion.model.File;
import com.arion.model.FileIntegrityCheck;

class StyledTableButtonRenderer extends JButton implements TableCellRenderer {

    private final Color bg = new Color(35, 40, 65);

    public StyledTableButtonRenderer() {
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        String status = value.toString();
        setText(status);

        Color neon;
        if (status.equalsIgnoreCase("Analyze"))
            neon = new Color(255, 90, 90);
        else if (status.equalsIgnoreCase("Review"))
            neon = new Color(0, 200, 255);
        else
            neon = new Color(0, 255, 180);

        putClientProperty("neonColor", neon);
        setForeground(neon);

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color neon = (Color) getClientProperty("neonColor");

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        g2.setColor(neon);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 2;
        g2.setColor(neon);
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}

public class AnalyzeTamperingPage {
    
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
    private JTextArea analysisNotesArea;
    private DashboardService dashboardService;
    private FileIntegrityCheckDAO integrityCheckDAO;
    private List<TamperingIncident> incidents;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat;
    private JLabel totalLabel;
    private JLabel confirmedLabel;
    private JLabel underReviewLabel;
    private TamperingIncident selectedIncident;
    private JLabel fileNameLabel;
    private JLabel uploadDateLabel;
    private JLabel modifiedDateLabel;
    private JLabel ownerLabel;
    private JLabel fileSizeLabel;
    private JLabel originalHashLabel;
    private JLabel currentHashLabel;
    
    public AnalyzeTamperingPage() {
        this.dashboardService = new DashboardService();
        this.integrityCheckDAO = new FileIntegrityCheckDAO();
        this.incidents = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        loadTamperingIncidents();
        createUI();
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    
    private void loadTamperingIncidents() {
        try {
            List<File> allFiles = dashboardService.getRecentFiles(500);
            List<File> tamperedFiles = allFiles.stream()
                .filter(f -> f.getStatus() != null && f.getStatus().equals("Tampered"))
                .collect(Collectors.toList());
            
            System.out.println("[AnalyzeTamperingPage] Found " + tamperedFiles.size() + " tampered files");
            
            for (File file : tamperedFiles) {
                // Get integrity check history using correct method
                List<FileIntegrityCheck> checks = integrityCheckDAO.getChecksByFileId(file.getFileId());
                
                FileIntegrityCheck latestCheck = null;
                if (!checks.isEmpty()) {
                    latestCheck = checks.get(0); // Most recent check
                }
                
                // Determine severity based on file type and status
                String severity = determineSeverity(file);
                
                // Create incident
                TamperingIncident incident = new TamperingIncident(
                    file.getFileName(),
                    file.getLastVerified() != null ? dateFormat.format(file.getLastVerified()) : "Unknown",
                    "User ID " + file.getUploadedBy(),
                    latestCheck != null ? latestCheck.getCheckResult() : "Hash Mismatch",
                    severity,
                    "Confirmed",
                    file,
                    latestCheck
                );
                
                incidents.add(incident);
            }
            
            System.out.println("[AnalyzeTamperingPage] Created " + incidents.size() + " tampering incidents");
            
        } catch (Exception e) {
            System.err.println("[AnalyzeTamperingPage] Error loading tampering incidents: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String determineSeverity(File file) {
        String fileName = file.getFileName().toLowerCase();
        
        // Critical: financial, password, credentials, database files
        if (fileName.contains("financial") || fileName.contains("password") || 
            fileName.contains("credential") || fileName.contains("secret") ||
            fileName.endsWith(".db") || fileName.endsWith(".sql")) {
            return "Critical";
        }
        
        // High: documents, contracts, important files
        if (fileName.contains("contract") || fileName.contains("agreement") ||
            fileName.contains("legal") || fileName.endsWith(".docx") ||
            fileName.endsWith(".pdf")) {
            return "High";
        }
        
        // Medium: other files
        return "Medium";
    }
    
    private void createUI() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BG_PRIMARY);
        
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(BG_PRIMARY);
        
        JPanel filterPanel = createFilterPanel();
        contentWrapper.add(filterPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel tamperingLogsPanel = createTamperingLogsPanel();
        contentWrapper.add(tamperingLogsPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel analysisSection = new JPanel(new GridLayout(1, 2, 25, 0));
        analysisSection.setBackground(BG_PRIMARY);
        analysisSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));
        
        JPanel detailsPanel = createTamperingDetailsPanel();
        JPanel actionsPanel = createAnalystActionsPanel();
        
        analysisSection.add(detailsPanel);
        analysisSection.add(actionsPanel);
        
        contentWrapper.add(analysisSection);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createFilterPanel() {
        JPanel card = new GlowingCard(25, ACCENT_RED);
        card.setLayout(new BorderLayout(20, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel title = new JLabel("🔍 Filter Tampering Incidents");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlsPanel.setBackground(BG_CARD);
        
        JLabel severityLabel = new JLabel("Severity:");
        severityLabel.setForeground(TEXT_PRIMARY);
        severityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JComboBox<String> severityFilter = new JComboBox<>(new String[]{
            "All Levels", "Critical", "High", "Medium"
        });
        styleComboBox(severityFilter, ACCENT_RED);
        severityFilter.addActionListener(e -> filterBySeverity((String) severityFilter.getSelectedItem()));
        
        JButton refreshBtn = createGradientButton("🔄 Refresh", ACCENT_CYAN, ACCENT_GREEN);
        refreshBtn.addActionListener(e -> refreshData());
        
        controlsPanel.add(severityLabel);
        controlsPanel.add(severityFilter);
        controlsPanel.add(refreshBtn);
        
        card.add(title, BorderLayout.WEST);
        card.add(controlsPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private void filterBySeverity(String severity) {
        tableModel.setRowCount(0);
        
        for (TamperingIncident incident : incidents) {
            if (severity.equals("All Levels") || incident.severity.equals(severity)) {
                tableModel.addRow(new Object[]{
                    incident.fileName,
                    incident.detectionTime,
                    incident.user,
                    incident.hashMismatch,
                    incident.severity,
                    incident.status,
                    "Analyze"
                });
            }
        }
        
        updateStatistics();
    }
    
    private void refreshData() {
        incidents.clear();
        loadTamperingIncidents();
        loadAllIncidents();
        updateStatistics();
        JOptionPane.showMessageDialog(mainPanel,
            "Loaded " + incidents.size() + " tampering incidents from database",
            "Refreshed",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatistics() {
        int total = incidents.size();
        int confirmed = (int) incidents.stream()
            .filter(i -> i.status.equals("Confirmed"))
            .count();
        int underReview = total - confirmed;
        
        totalLabel.setText("Total: " + total);
        confirmedLabel.setText("Confirmed: " + confirmed);
        underReviewLabel.setText("Under Review: " + underReview);
    }
    
    private JPanel createTamperingLogsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_ORANGE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("⚠️ Tampering Incidents");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setBackground(BG_CARD);
        
        totalLabel = new JLabel("Total: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(ACCENT_CYAN);
        
        confirmedLabel = new JLabel("Confirmed: 0");
        confirmedLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmedLabel.setForeground(ACCENT_RED);
        
        underReviewLabel = new JLabel("Under Review: 0");
        underReviewLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        underReviewLabel.setForeground(ACCENT_ORANGE);
        
        statsPanel.add(totalLabel);
        statsPanel.add(confirmedLabel);
        statsPanel.add(underReviewLabel);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);
        
        String[] columns = {"File Name", "Detection Time", "User", "Hash Mismatch", "Severity", "Status", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        
        loadAllIncidents();
        updateStatistics();
        
        JTable table = new JTable(tableModel);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(255, 255, 255, 5));
        table.setRowHeight(55);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(138, 99, 255, 30));
        table.getColumnModel().getColumn(6).setCellRenderer(new StyledTableButtonRenderer());

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(35, 40, 65));
        header.setForeground(ACCENT_ORANGE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_ORANGE));
        
        table.setDefaultRenderer(Object.class, new TamperingTableRenderer());
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < incidents.size()) {
                    selectedIncident = incidents.get(selectedRow);
                    updateDetailsPanel();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.setPreferredSize(new Dimension(0, 350));
        
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ACCENT_ORANGE;
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
    
    private void loadAllIncidents() {
        tableModel.setRowCount(0);
        
        if (incidents.isEmpty()) {
            // Show message if no tampering incidents
            System.out.println("[AnalyzeTamperingPage] No tampering incidents to display");
        }
        
        for (TamperingIncident incident : incidents) {
            tableModel.addRow(new Object[]{
                incident.fileName,
                incident.detectionTime,
                incident.user,
                incident.hashMismatch,
                incident.severity,
                incident.status,
                "Analyze"
            });
        }
    }
    
    private JPanel createTamperingDetailsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("🔬 Detailed Analysis");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);
        
        // File info card
        JPanel fileInfoCard = createDynamicInfoCard();
        contentPanel.add(fileInfoCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Hash comparison
        JPanel hashCard = createDynamicHashCard();
        contentPanel.add(hashCard);
        
        card.add(title, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createDynamicInfoCard() {
        JPanel card = new RoundedPanel(15);
        card.setLayout(new BorderLayout(0, 15));
        card.setBackground(new Color(35, 40, 65));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        
        JLabel infoTitle = new JLabel("File Information");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        infoTitle.setForeground(ACCENT_CYAN);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(35, 40, 65));
        
        fileNameLabel = createInfoLabel("Name: Select a file from the table");
        uploadDateLabel = createInfoLabel("Original Upload: N/A");
        modifiedDateLabel = createInfoLabel("Last Modified: N/A");
        ownerLabel = createInfoLabel("Owner: N/A");
        fileSizeLabel = createInfoLabel("Size: N/A");
        
        infoPanel.add(fileNameLabel);
        infoPanel.add(uploadDateLabel);
        infoPanel.add(modifiedDateLabel);
        infoPanel.add(ownerLabel);
        infoPanel.add(fileSizeLabel);
        
        card.add(infoTitle, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JPanel createDynamicHashCard() {
        JPanel hashCard = new RoundedPanel(15);
        hashCard.setLayout(new BorderLayout(0, 15));
        hashCard.setBackground(new Color(35, 40, 65));
        hashCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        hashCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JLabel hashTitle = new JLabel("Hash Comparison");
        hashTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        hashTitle.setForeground(TEXT_SECONDARY);
        
        JPanel hashesPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        hashesPanel.setBackground(new Color(35, 40, 65));
        
        JPanel originalHashPanel = new JPanel(new BorderLayout());
        originalHashPanel.setBackground(new Color(45, 52, 85));
        originalHashPanel.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        JLabel originalLabel = new JLabel("Original Hash:");
        originalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        originalLabel.setForeground(ACCENT_GREEN);
        
        originalHashLabel = new JLabel("Select a file to view hash");
        originalHashLabel.setFont(new Font("Courier New", Font.PLAIN, 10));
        originalHashLabel.setForeground(TEXT_PRIMARY);
        
        originalHashPanel.add(originalLabel, BorderLayout.NORTH);
        originalHashPanel.add(originalHashLabel, BorderLayout.CENTER);
        
        JPanel currentHashPanel = new JPanel(new BorderLayout());
        currentHashPanel.setBackground(new Color(45, 52, 85));
        currentHashPanel.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        JLabel currentLabel = new JLabel("Current Hash:");
        currentLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        currentLabel.setForeground(ACCENT_RED);
        
        currentHashLabel = new JLabel("Select a file to view hash");
        currentHashLabel.setFont(new Font("Courier New", Font.PLAIN, 10));
        currentHashLabel.setForeground(TEXT_PRIMARY);
        
        currentHashPanel.add(currentLabel, BorderLayout.NORTH);
        currentHashPanel.add(currentHashLabel, BorderLayout.CENTER);
        
        hashesPanel.add(originalHashPanel);
        hashesPanel.add(currentHashPanel);
        
        hashCard.add(hashTitle, BorderLayout.NORTH);
        hashCard.add(hashesPanel, BorderLayout.CENTER);
        
        return hashCard;
    }
    
    private void updateDetailsPanel() {
        if (selectedIncident != null && selectedIncident.file != null) {
            File file = selectedIncident.file;
            
            fileNameLabel.setText("Name: " + file.getFileName());
            uploadDateLabel.setText("Original Upload: " + 
                (file.getUploadDate() != null ? dateFormat.format(file.getUploadDate()) : "Unknown"));
            modifiedDateLabel.setText("Last Modified: " + 
                (file.getLastVerified() != null ? dateFormat.format(file.getLastVerified()) : "Unknown"));
            ownerLabel.setText("Owner: User ID " + file.getUploadedBy());
            
            long sizeInKB = file.getFileSize() / 1024;
            String sizeStr = sizeInKB < 1024 ? sizeInKB + " KB" : 
                           String.format("%.2f MB", sizeInKB / 1024.0);
            fileSizeLabel.setText("Size: " + sizeStr);
            
            // Display hash information
            String hash = file.getHashValue();
            if (hash != null && hash.length() > 0) {
                originalHashLabel.setText(formatHash(hash));
                
                // If we have integrity check data, show the current hash
                if (selectedIncident.check != null && selectedIncident.check.getCurrentHash() != null) {
                    currentHashLabel.setText(formatHash(selectedIncident.check.getCurrentHash()));
                } else {
                    // Simulate tampering by showing modified hash
                    String tamperedHash = hash.length() > 10 ? 
                        hash.substring(10) + hash.substring(0, 10) : hash;
                    currentHashLabel.setText(formatHash(tamperedHash));
                }
            } else {
                originalHashLabel.setText("Hash not available");
                currentHashLabel.setText("Hash not available");
            }
        }
    }
    
    private String formatHash(String hash) {
        if (hash == null || hash.isEmpty()) {
            return "N/A";
        }
        
        if (hash.length() > 64) {
            return "<html><div style='width:250px; color:#F0F2FF;'>" + 
                   hash.substring(0, 64) + "...</div></html>";
        }
        return "<html><div style='width:250px; color:#F0F2FF;'>" + hash + "</div></html>";
    }
    
    private JPanel createAnalystActionsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_GREEN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("📝 Analyst Actions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);
        
        JLabel notesLabel = new JLabel("Analysis Notes:");
        notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        notesLabel.setForeground(TEXT_PRIMARY);
        notesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        analysisNotesArea = new JTextArea(6, 30);
        analysisNotesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        analysisNotesArea.setBackground(new Color(35, 40, 65));
        analysisNotesArea.setForeground(TEXT_PRIMARY);
        analysisNotesArea.setCaretColor(ACCENT_GREEN);
        analysisNotesArea.setLineWrap(true);
        analysisNotesArea.setWrapStyleWord(true);
        analysisNotesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_GREEN, 2),
            new EmptyBorder(12, 12, 12, 12)
        ));
        analysisNotesArea.setText("Add your analysis notes here...");
        
        JScrollPane notesScroll = new JScrollPane(analysisNotesArea);
        notesScroll.setBorder(null);
        notesScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        notesScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        contentPanel.add(notesLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(notesScroll);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        buttonsPanel.setBackground(BG_CARD);
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JButton confirmBtn = createActionButton("✅ Confirm Tampering", ACCENT_RED);
        confirmBtn.addActionListener(e -> {
            if (selectedIncident != null) {
                JOptionPane.showMessageDialog(mainPanel,
                    "Tampering confirmed for: " + selectedIncident.fileName,
                    "Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainPanel,
                    "Please select an incident from the table first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton falsePositiveBtn = createActionButton("❌ Mark as False Positive", ACCENT_GREEN);
        falsePositiveBtn.addActionListener(e -> {
            if (selectedIncident != null) {
                JOptionPane.showMessageDialog(mainPanel,
                    "Marked as false positive: " + selectedIncident.fileName,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainPanel,
                    "Please select an incident from the table first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton escalateBtn = createActionButton("⚠️ Escalate to Admin", ACCENT_ORANGE);
        escalateBtn.addActionListener(e -> {
            if (selectedIncident != null) {
                JOptionPane.showMessageDialog(mainPanel,
                    "Escalated to admin: " + selectedIncident.fileName,
                    "Escalated",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainPanel,
                    "Please select an incident from the table first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton exportBtn = createActionButton("📤 Export Analysis", ACCENT_PURPLE);
        exportBtn.addActionListener(e -> {
            if (selectedIncident != null) {
                JOptionPane.showMessageDialog(mainPanel,
                    "Analysis exported for: " + selectedIncident.fileName,
                    "Exported",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainPanel,
                    "Please select an incident from the table first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonsPanel.add(confirmBtn);
        buttonsPanel.add(falsePositiveBtn);
        buttonsPanel.add(escalateBtn);
        buttonsPanel.add(exportBtn);
        
        contentPanel.add(buttonsPanel);
        
        card.add(title, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(35, 40, 65));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(color);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(color);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }
    
    private void styleComboBox(JComboBox<String> combo, Color color) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(new Color(35, 40, 65));
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        combo.setPreferredSize(new Dimension(150, 40));
    }
    
    private JButton createGradientButton(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
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
    
    // Data class for tampering incidents
    class TamperingIncident {
        String fileName;
        String detectionTime;
        String user;
        String hashMismatch;
        String severity;
        String status;
        File file;
        FileIntegrityCheck check;
        
        TamperingIncident(String fileName, String detectionTime, String user, 
                         String hashMismatch, String severity, String status,
                         File file, FileIntegrityCheck check) {
            this.fileName = fileName;
            this.detectionTime = detectionTime;
            this.user = user;
            this.hashMismatch = hashMismatch;
            this.severity = severity;
            this.status = status;
            this.file = file;
            this.check = check;
        }
    }
    
    class TamperingTableRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 4) { // Severity
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(false);
                
                String severity = value.toString();
                JLabel label = new JLabel(severity);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                
                Color color = severity.equals("Critical") ? ACCENT_RED :
                             severity.equals("High") ? ACCENT_ORANGE :
                             severity.equals("Medium") ? new Color(255, 180, 0) : ACCENT_CYAN;
                
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
            
            if (column == 5) { // Status
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(false);
                
                String status = value.toString();
                JLabel label = new JLabel(status);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                
                Color color = status.equals("Confirmed") ? ACCENT_RED : ACCENT_ORANGE;
                
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
    
    class RoundedPanel extends JPanel {
        private int cornerRadius;
        
        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
        }
        
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
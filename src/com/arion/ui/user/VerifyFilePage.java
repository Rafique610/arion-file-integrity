package com.arion.ui.user;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.arion.dao.FileDAO;
import com.arion.model.File;
import com.arion.service.FileVerificationService;
import com.arion.service.FileVerificationService.VerificationResult;
import com.arion.ui.common.IconRenderer;
import com.arion.ui.common.UIConstants;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class VerifyFilePage {
    
    private static final Color BG_PRIMARY = new Color(25, 28, 45);
    private static final Color BG_CARD = new Color(45, 52, 85);
    private static final Color ACCENT_PURPLE = new Color(138, 99, 255);
    private static final Color ACCENT_CYAN = new Color(0, 230, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 163);
    private static final Color ACCENT_RED = new Color(255, 70, 100);
    private static final Color ACCENT_ORANGE = new Color(255, 180, 0);
    private static final Color TEXT_PRIMARY = new Color(240, 242, 255);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 200);
    
    private JPanel mainPanel;
    private JTable filesTable;
    private DefaultTableModel tableModel;
    private JPanel verificationResultPanel;
    private JLabel originalHashLabel;
    private JLabel currentHashLabel;
    private JLabel resultIconLabel;
    private JLabel resultTextLabel;
    private JTextField searchField;
    
    private int currentUserId;
    private FileDAO fileDAO;
    private FileVerificationService verificationService;
    private List<File> userFiles;
    private File selectedFile;
    
    public VerifyFilePage(int userId) {
        this.currentUserId = userId;
        this.fileDAO = new FileDAO();
        this.verificationService = new FileVerificationService();
        createUI();
        loadUserFiles();
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    
    private void createUI() {
        // Create scrollable content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_PRIMARY);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top section with file list
        JPanel filesCard = createFilesListCard();
        contentPanel.add(filesCard);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Bottom section with verification results
        verificationResultPanel = createVerificationResultCard();
        contentPanel.add(verificationResultPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(UIConstants.createNeonScrollBarUI(ACCENT_CYAN));
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_PRIMARY);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadUserFiles() {
        try {
            userFiles = fileDAO.getFilesByUser(currentUserId);
            System.out.println("Loaded " + userFiles.size() + " files for user " + currentUserId);
            updateTable();
        } catch (Exception e) {
            System.err.println("Error loading user files: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainPanel,
                "Error loading files: " + e.getMessage(),
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String searchText = searchField != null ? searchField.getText().toLowerCase() : "";
        
        for (File file : userFiles) {
            // Filter by search text
            if (!searchText.isEmpty() && !file.getFileName().toLowerCase().contains(searchText)) {
                continue;
            }
            
            String uploadDate = file.getUploadDate() != null ? 
                dateFormat.format(file.getUploadDate()) : "N/A";
            
            String lastVerified = "Never";
            if (file.getLastVerified() != null) {
                long diff = System.currentTimeMillis() - file.getLastVerified().getTime();
                long minutes = diff / (1000 * 60);
                long hours = minutes / 60;
                long days = hours / 24;
                
                if (minutes < 1) lastVerified = "Just now";
                else if (minutes < 60) lastVerified = minutes + " mins ago";
                else if (hours < 24) lastVerified = hours + " hours ago";
                else lastVerified = days + " days ago";
            }
            
            // Determine display status based on verification
            String displayStatus = file.getStatus();
            if (displayStatus == null || displayStatus.equals("Protected") || displayStatus.equals("Pending")) {
                displayStatus = "Unverified";
            }
            
            String actionText = file.getStatus().equals("Tampered") ? "Re-check" : "Verify";
            
            tableModel.addRow(new Object[]{
                file.getFileId(), // Hidden column for ID
                file.getFileName(),
                uploadDate,
                file.getHashAlgorithm(),
                displayStatus,
                lastVerified,
                actionText
            });
        }
    }
    
    private JPanel createFilesListCard() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("🛡️ Your Protected Files");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setBackground(BG_CARD);
        
        searchField = new JTextField(20);
        searchField.setBackground(new Color(35, 40, 65));
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(ACCENT_CYAN);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_CYAN, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateTable();
            }
        });
        
        JButton refreshBtn = createIconButton("🔄 Refresh", ACCENT_PURPLE);
        refreshBtn.addActionListener(e -> {
            loadUserFiles();
            JOptionPane.showMessageDialog(mainPanel, 
                "Files refreshed!", 
                "Refresh", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton verifyAllBtn = createIconButton("✅ Verify All", ACCENT_GREEN);
        verifyAllBtn.addActionListener(e -> verifyAllFiles());
        
        controlsPanel.add(searchField);
        controlsPanel.add(refreshBtn);
        controlsPanel.add(verifyAllBtn);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(controlsPanel, BorderLayout.EAST);
        
        // Table
        String[] columns = {"ID", "File Name", "Upload Date", "Algorithm", "Status", "Last Verified", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only action column
            }
        };
        
        filesTable = new JTable(tableModel);
        filesTable.setBackground(BG_CARD);
        filesTable.setForeground(TEXT_PRIMARY);
        filesTable.setGridColor(new Color(255, 255, 255, 5));
        filesTable.setRowHeight(55);
        filesTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filesTable.setShowGrid(false);
        filesTable.setIntercellSpacing(new Dimension(0, 5));
        filesTable.setSelectionBackground(new Color(138, 99, 255, 30));
        filesTable.setSelectionForeground(TEXT_PRIMARY);
        
        // Hide ID column
        filesTable.getColumnModel().getColumn(0).setMinWidth(0);
        filesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        filesTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Custom header
        JTableHeader header = filesTable.getTableHeader();
        header.setBackground(new Color(35, 40, 65));
        header.setForeground(ACCENT_CYAN);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_CYAN));
        
        filesTable.setDefaultRenderer(Object.class, new VerifyTableCellRenderer());
        
        // Add mouse listener for action buttons
        filesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = filesTable.rowAtPoint(e.getPoint());
                int col = filesTable.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == 6) { // Action column
                    int fileId = (int) tableModel.getValueAt(row, 0);
                    verifyFile(fileId, row);
                }
            }
        });
        
        // Add row selection listener
        filesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = filesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int fileId = (int) tableModel.getValueAt(selectedRow, 0);
                    showFileInfo(fileId);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(filesTable);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.setPreferredSize(new Dimension(0, 350));
        scrollPane.getVerticalScrollBar().setUI(UIConstants.createNeonScrollBarUI(ACCENT_CYAN));
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private void verifyFile(int fileId, int tableRow) {
        // Show progress
        JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(mainPanel), "Verifying...", true);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(mainPanel);
        progressDialog.setLayout(new BorderLayout(10, 10));
        
        JLabel messageLabel = new JLabel("Verifying file integrity...", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        progressDialog.add(messageLabel, BorderLayout.CENTER);
        progressDialog.add(progressBar, BorderLayout.SOUTH);
        
        // Verify in background
        SwingWorker<VerificationResult, Void> worker = new SwingWorker<>() {
            @Override
            protected VerificationResult doInBackground() {
                return verificationService.verifyFile(fileId, currentUserId);
            }
            
            @Override
            protected void done() {
                progressDialog.dispose();
                
                try {
                    VerificationResult result = get();
                    
                    if (result.isSuccess()) {
                        // Update table
                        tableModel.setValueAt(result.getVerificationStatus(), tableRow, 4);
                        tableModel.setValueAt("Just now", tableRow, 5);
                        
                        // Show result
                        showVerificationResult(result);
                        
                        // Reload files to get updated data
                        loadUserFiles();
                        
                    } else {
                        JOptionPane.showMessageDialog(mainPanel,
                            result.getMessage(),
                            "Verification Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(mainPanel,
                        "Error during verification: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
        progressDialog.setVisible(true);
    }
    
    private void verifyAllFiles() {
        int confirm = JOptionPane.showConfirmDialog(mainPanel,
            "Verify all " + userFiles.size() + " files?\nThis may take some time.",
            "Verify All Files",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        // Progress dialog
        JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(mainPanel), "Batch Verification", true);
        progressDialog.setSize(400, 150);
        progressDialog.setLocationRelativeTo(mainPanel);
        progressDialog.setLayout(new BorderLayout(10, 10));
        
        JLabel messageLabel = new JLabel("Verifying files...", SwingConstants.CENTER);
        JProgressBar progressBar = new JProgressBar(0, userFiles.size());
        JLabel statusLabel = new JLabel("0 / " + userFiles.size(), SwingConstants.CENTER);
        
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(messageLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        progressDialog.add(panel);
        
        SwingWorker<FileVerificationService.BatchVerificationResult, Integer> worker = new SwingWorker<>() {
            @Override
            protected FileVerificationService.BatchVerificationResult doInBackground() {
                FileVerificationService.BatchVerificationResult batchResult = 
                    new FileVerificationService.BatchVerificationResult();
                
                for (int i = 0; i < userFiles.size(); i++) {
                    File file = userFiles.get(i);
                    VerificationResult result = verificationService.verifyFile(file.getFileId(), currentUserId);
                    batchResult.addResult(result);
                    publish(i + 1);
                    
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                
                return batchResult;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                int latest = chunks.get(chunks.size() - 1);
                progressBar.setValue(latest);
                statusLabel.setText(latest + " / " + userFiles.size());
            }
            
            @Override
            protected void done() {
                progressDialog.dispose();
                
                try {
                    FileVerificationService.BatchVerificationResult result = get();
                    
                    String message = String.format(
                        "Verification Complete!\n\n" +
                        "Total Files: %d\n" +
                        "✅ Verified: %d\n" +
                        "❌ Tampered: %d\n" +
                        "⚠️ Errors: %d",
                        result.getTotalCount(),
                        result.getVerifiedCount(),
                        result.getTamperedCount(),
                        result.getErrorCount()
                    );
                    
                    JOptionPane.showMessageDialog(mainPanel,
                        message,
                        "Batch Verification Results",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Reload table
                    loadUserFiles();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
        progressDialog.setVisible(true);
    }
    
    private void showFileInfo(int fileId) {
        for (File file : userFiles) {
            if (file.getFileId() == fileId) {
                selectedFile = file;
                
                // Update hash displays
                originalHashLabel.setText("<html><div style='width: 250px; word-wrap: break-word;'>" + 
                    file.getHashValue() + "</div></html>");
                originalHashLabel.setForeground(ACCENT_CYAN);
                
                currentHashLabel.setText("<html><div style='width: 250px; word-wrap: break-word;'>" +
                    "Click 'Verify' to check current hash" + "</div></html>");
                currentHashLabel.setForeground(TEXT_SECONDARY);
                
                // Update status display
                switch (file.getStatus()) {
                    case "Verified":
                        resultIconLabel.setIcon(IconRenderer.getIcon(IconRenderer.Icon.CHECKMARK, 72, ACCENT_GREEN));
                        resultTextLabel.setText("File Integrity Verified");
                        resultTextLabel.setForeground(ACCENT_GREEN);
                        break;
                    case "Tampered":
                        resultIconLabel.setIcon(IconRenderer.getIcon(IconRenderer.Icon.ALERT, 72, ACCENT_RED));
                        resultTextLabel.setText("⚠️ File Has Been Modified!");
                        resultTextLabel.setForeground(ACCENT_RED);
                        break;
                    default:
                        resultIconLabel.setIcon(IconRenderer.getIcon(IconRenderer.Icon.LOCK, 72, ACCENT_ORANGE));
                        resultTextLabel.setText("File Not Yet Verified");
                        resultTextLabel.setForeground(ACCENT_ORANGE);
                }
                
                break;
            }
        }
    }
    
    private void showVerificationResult(VerificationResult result) {
        originalHashLabel.setText("<html><div style='width: 250px; word-wrap: break-word;'>" + 
            result.getOriginalHash() + "</div></html>");
        originalHashLabel.setForeground(ACCENT_CYAN);
        
        currentHashLabel.setText("<html><div style='width: 250px; word-wrap: break-word;'>" + 
            result.getCurrentHash() + "</div></html>");
        
        switch (result.getVerificationStatus()) {
            case "Verified":
                resultIconLabel.setIcon(IconRenderer.getIcon(IconRenderer.Icon.CHECKMARK, 72, ACCENT_GREEN));
                resultTextLabel.setText("✅ File Integrity Verified!");
                resultTextLabel.setForeground(ACCENT_GREEN);
                currentHashLabel.setForeground(ACCENT_GREEN);
                break;
            case "Tampered":
                resultIconLabel.setIcon(IconRenderer.getIcon(IconRenderer.Icon.ALERT, 72, ACCENT_RED));
                resultTextLabel.setText("⚠️ File Has Been Modified!");
                resultTextLabel.setForeground(ACCENT_RED);
                currentHashLabel.setForeground(ACCENT_RED);
                break;
            default:
                resultIconLabel.setIcon(IconRenderer.getIcon(IconRenderer.Icon.ALERT, 72, ACCENT_ORANGE));
                resultTextLabel.setText("⚠️ Verification Error");
                resultTextLabel.setForeground(ACCENT_ORANGE);
                currentHashLabel.setForeground(ACCENT_ORANGE);
        }
    }
    
    private JPanel createVerificationResultCard() {
        JPanel card = new GlowingCard(25, ACCENT_GREEN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
        
        JLabel title = new JLabel("🔍 Verification Result");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);
        
        // Result status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(BG_CARD);
        statusPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        resultIconLabel = new JLabel();
        resultIconLabel.setIcon(IconRenderer.getIcon(IconRenderer.Icon.LOCK, 72, TEXT_SECONDARY));
        resultIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        resultTextLabel = new JLabel("Select a file to verify");
        resultTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        resultTextLabel.setForeground(TEXT_SECONDARY);
        resultTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        statusPanel.add(resultIconLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        statusPanel.add(resultTextLabel);
        
        contentPanel.add(statusPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Hash comparison section
        JPanel hashComparisonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        hashComparisonPanel.setBackground(BG_CARD);
        hashComparisonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        JPanel originalHashPanel = createHashDisplayPanel("Original Hash");
        originalHashLabel = (JLabel) originalHashPanel.getComponent(1);
        
        JPanel currentHashPanel = createHashDisplayPanel("Current Hash");
        currentHashLabel = (JLabel) currentHashPanel.getComponent(1);
        
        hashComparisonPanel.add(originalHashPanel);
        hashComparisonPanel.add(currentHashPanel);
        
        contentPanel.add(hashComparisonPanel);
        
        card.add(title, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createHashDisplayPanel(String label) {
        JPanel panel = new RoundedPanel(15);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(new Color(35, 40, 65));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_SECONDARY);
        
        JLabel hashLabel = new JLabel("<html><div style='width: 100%; word-wrap: break-word;'>Not available</div></html>");
        hashLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
        hashLabel.setForeground(TEXT_SECONDARY);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(hashLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createIconButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(color);
        button.setBackground(new Color(35, 40, 65));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    // ==================== CUSTOM COMPONENTS ====================
    
    class VerifyTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 4) { // Status column
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(false);
                
                String status = value.toString();
                JLabel statusLabel = new JLabel(status);
                statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                Color statusColor;
                String icon;
                if (status.equals("Verified")) {
                    statusColor = ACCENT_GREEN;
                    icon = "✅ ";
                } else if (status.equals("Tampered")) {
                    statusColor = ACCENT_RED;
                    icon = "❌ ";
                } else if (status.equals("Protected")) {
                    statusColor = ACCENT_CYAN;
                    icon = "🛡️ ";
                } else {
                    statusColor = ACCENT_ORANGE;
                    icon = "⚠️ ";
                }
                
                statusLabel.setText(icon + status);
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
            
            if (column == 6) { // Action column
                JButton actionBtn = new JButton(value.toString());
                actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                String status = (String) table.getValueAt(row, 4);
                Color btnColor = status.equals("Tampered") ? ACCENT_RED : ACCENT_PURPLE;
                
                actionBtn.setForeground(btnColor);
                actionBtn.setBackground(new Color(35, 40, 65));
                actionBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(btnColor, 2),
                    new EmptyBorder(8, 15, 8, 15)
                ));
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
    
    class GlowingCard extends JPanel {
        private int cornerRadius;
        private Color glowColor;
        
        public GlowingCard(int radius, Color glowColor) {
            this.cornerRadius = radius;
            this.glowColor = glowColor;
            setOpaque(false);
        }
        
        @Override
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
        
        @Override
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
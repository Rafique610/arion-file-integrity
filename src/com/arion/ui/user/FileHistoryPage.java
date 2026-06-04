package com.arion.ui.user;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.arion.dao.FileUploadHistoryDAO;
import com.arion.dao.FileDAO;
import com.arion.model.FileUploadHistory;
import com.arion.model.File;
import com.arion.ui.common.IconRenderer;
import com.arion.ui.common.UIConstants;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class FileHistoryPage {
    
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
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> fileFilter;
    private JComboBox<String> actionFilter;
    private JComboBox<String> dateFilter;
    
    private int currentUserId;
    private FileUploadHistoryDAO historyDAO;
    private FileDAO fileDAO;
    private List<FileUploadHistory> historyList;
    private Map<String, Integer> fileNameToIdMap;
    
    public FileHistoryPage(int userId) {
        this.currentUserId = userId;
        this.historyDAO = new FileUploadHistoryDAO();
        this.fileDAO = new FileDAO();
        this.fileNameToIdMap = new HashMap<>();
        createUI();
        loadHistoryData();
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
        
        // Filter panel
        JPanel filterPanel = createFilterPanel();
        contentPanel.add(filterPanel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Activity logs table
        JPanel logsPanel = createActivityLogsPanel();
        contentPanel.add(logsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(UIConstants.createNeonScrollBarUI(ACCENT_PURPLE));
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_PRIMARY);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadHistoryData() {
        try {
            historyList = historyDAO.getHistoryByUser(currentUserId);
            System.out.println("Loaded " + historyList.size() + " history records");
            
            // Load file names for filter
            List<File> userFiles = fileDAO.getFilesByUser(currentUserId);
            fileNameToIdMap.clear();
            fileFilter.removeAllItems();
            fileFilter.addItem("All Files");
            
            for (File file : userFiles) {
                fileFilter.addItem(file.getFileName());
                fileNameToIdMap.put(file.getFileName(), file.getFileId());
            }
            
            updateTable();
        } catch (Exception e) {
            System.err.println("Error loading history: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (FileUploadHistory history : historyList) {
            String timestamp = history.getTimestamp() != null ? 
                dateFormat.format(history.getTimestamp()) : "N/A";
            
            String fileName = history.getFileName() != null ? 
                history.getFileName() : "Unknown";
            
            String user = history.getUserFullName() != null ? 
                history.getUserFullName() : 
                (history.getUsername() != null ? history.getUsername() : "Unknown");
            
            String details = history.getDetails() != null ? history.getDetails() : "";
            if (history.getAlgorithmUsed() != null) {
                details = history.getAlgorithmUsed() + " - " + details;
            }
            
            tableModel.addRow(new Object[]{
                timestamp,
                fileName,
                history.getActionType(),
                history.getActionStatus(),
                user,
                details
            });
        }
    }
    
    private JPanel createFilterPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(20, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel title = new JLabel("🔍 Filter Logs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlsPanel.setBackground(BG_CARD);
        
        // File Filter
        JLabel fileLabel = new JLabel("File:");
        fileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fileLabel.setForeground(TEXT_SECONDARY);
        
        fileFilter = new JComboBox<>();
        fileFilter.addItem("All Files");
        UIConstants.styleComboBox(fileFilter, ACCENT_CYAN);
        fileFilter.setPreferredSize(new Dimension(180, 45));
        
        // Action Filter
        JLabel actionLabel = new JLabel("Action:");
        actionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        actionLabel.setForeground(TEXT_SECONDARY);
        
        actionFilter = new JComboBox<>(new String[]{
            "All Actions", "Upload", "Verify", "Modify", "Download", "Delete"
        });
        UIConstants.styleComboBox(actionFilter, ACCENT_CYAN);
        actionFilter.setPreferredSize(new Dimension(150, 45));
        
        // Date Filter
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(TEXT_SECONDARY);
        
        dateFilter = new JComboBox<>(new String[]{
            "All Time", "Today", "Last Week", "Last Month"
        });
        UIConstants.styleComboBox(dateFilter, ACCENT_CYAN);
        dateFilter.setPreferredSize(new Dimension(150, 45));
        
        JButton applyBtn = createGradientButton("Apply", ACCENT_CYAN, ACCENT_PURPLE);
        applyBtn.addActionListener(e -> applyFilters());
        
        JButton refreshBtn = createOutlineButton("🔄 Refresh", ACCENT_GREEN);
        refreshBtn.addActionListener(e -> {
            loadHistoryData();
            JOptionPane.showMessageDialog(mainPanel, "History refreshed!", 
                "Refresh", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton exportBtn = createOutlineButton("📥 Export CSV", ACCENT_GREEN);
        exportBtn.addActionListener(e -> exportToCSV());
        
        controlsPanel.add(fileLabel);
        controlsPanel.add(fileFilter);
        controlsPanel.add(actionLabel);
        controlsPanel.add(actionFilter);
        controlsPanel.add(dateLabel);
        controlsPanel.add(dateFilter);
        controlsPanel.add(applyBtn);
        controlsPanel.add(refreshBtn);
        controlsPanel.add(exportBtn);
        
        card.add(title, BorderLayout.WEST);
        card.add(controlsPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private void applyFilters() {
        try {
            String selectedFile = (String) fileFilter.getSelectedItem();
            String selectedAction = (String) actionFilter.getSelectedItem();
            String selectedDate = (String) dateFilter.getSelectedItem();
            
            Integer fileId = null;
            if (selectedFile != null && !selectedFile.equals("All Files")) {
                fileId = fileNameToIdMap.get(selectedFile);
            }
            
            String actionType = null;
            if (selectedAction != null && !selectedAction.equals("All Actions")) {
                actionType = selectedAction;
            }
            
            String dateFilterStr = null;
            if (selectedDate != null && !selectedDate.equals("All Time")) {
                dateFilterStr = selectedDate;
            }
            
            historyList = historyDAO.getHistoryWithFilters(currentUserId, fileId, 
                                                           actionType, dateFilterStr);
            updateTable();
            
        } catch (Exception e) {
            System.err.println("Error applying filters: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("file_history.csv"));
        
        int result = fileChooser.showSaveDialog(mainPanel);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                FileWriter writer = new FileWriter(file);
                
                // Write header
                writer.write("Timestamp,File Name,Action,Status,User,Details\n");
                
                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.write("\"" + tableModel.getValueAt(i, j) + "\"");
                        if (j < tableModel.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.write("\n");
                }
                
                writer.close();
                
                JOptionPane.showMessageDialog(mainPanel,
                    "History exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainPanel,
                    "Error exporting history: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JPanel createActivityLogsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 550));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("📋 Activity History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JLabel countLabel = new JLabel("0 records");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(TEXT_SECONDARY);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        
        String[] columns = {"Timestamp", "File Name", "Action", "Status", "User", "Details"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(tableModel);
        historyTable.setBackground(BG_CARD);
        historyTable.setForeground(TEXT_PRIMARY);
        historyTable.setGridColor(new Color(255, 255, 255, 5));
        historyTable.setRowHeight(50);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyTable.setShowGrid(false);
        historyTable.setSelectionBackground(new Color(138, 99, 255, 30));
        
        // Adjust column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Timestamp
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(200); // File Name
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Action
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(150); // User
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(300); // Details
        
        JTableHeader header = historyTable.getTableHeader();
        header.setBackground(new Color(35, 40, 65));
        header.setForeground(ACCENT_CYAN);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_CYAN));
        
        historyTable.setDefaultRenderer(Object.class, new HistoryTableRenderer());
        
        // Add listener to update count
        tableModel.addTableModelListener(e -> {
            countLabel.setText(tableModel.getRowCount() + " records");
        });
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.getVerticalScrollBar().setUI(UIConstants.createNeonScrollBarUI(ACCENT_CYAN));
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
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
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
    
    class HistoryTableRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 2) { // Action column
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setOpaque(true);
                
                Color actionColor;
                switch (value.toString()) {
                    case "Upload":
                        actionColor = ACCENT_GREEN;
                        break;
                    case "Verify":
                        actionColor = ACCENT_CYAN;
                        break;
                    case "Modify":
                        actionColor = ACCENT_ORANGE;
                        break;
                    case "Delete":
                        actionColor = ACCENT_RED;
                        break;
                    default:
                        actionColor = ACCENT_PURPLE;
                }
                
                label.setForeground(actionColor);
                label.setBackground(isSelected ? new Color(138, 99, 255, 30) : BG_CARD);
                label.setBorder(new EmptyBorder(10, 15, 10, 15));
                return label;
            }
            
            if (column == 3) { // Status column
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(false);
                
                String status = value.toString();
                JLabel label = new JLabel(status);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                Color color = status.equals("Success") ? ACCENT_GREEN :
                             status.equals("Warning") ? ACCENT_ORANGE : ACCENT_RED;
                
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
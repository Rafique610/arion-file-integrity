package com.arion.ui.analyst;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.arion.service.SecurityReportService;
import com.arion.model.SecurityReport;

public class ReviewSecurityReportsPage {
    
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
    private JTextArea reportContentArea;
    private JTable reportSummaryTable;
    private DefaultTableModel tableModel;
    private SecurityReportService reportService;
    private List<SecurityReport> allReports;
    private SimpleDateFormat dateFormat;
    
    class ModernComboBoxUI extends javax.swing.plaf.basic.BasicComboBoxUI {
        private final Color borderColor;
        private final Color textColor = new Color(240, 242, 255);
        private final Color bgColor = new Color(35, 40, 65);

        public ModernComboBoxUI(Color borderColor) {
            this.borderColor = borderColor;
        }

        @Override
        protected JButton createArrowButton() {
            JButton arrow = new JButton("▼");
            arrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
            arrow.setForeground(borderColor);
            arrow.setBackground(bgColor);
            arrow.setBorder(null);
            arrow.setFocusPainted(false);
            arrow.setContentAreaFilled(false);
            arrow.setOpaque(false);
            return arrow;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = c.getWidth();
            int h = c.getHeight();

            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, w, h, 18, 18);

            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, w - 2, h - 2, 18, 18);

            super.paint(g2, c);
        }
    }

    public ReviewSecurityReportsPage() {
        this.reportService = new SecurityReportService();
        this.allReports = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        loadReportsFromDatabase();
        createUI();
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    
    private void loadReportsFromDatabase() {
        try {
            // Fetch all reports from database
            allReports = reportService.getRecentReports(100);
            System.out.println("[ReviewSecurityReportsPage] Loaded " + allReports.size() + " reports from database");
        } catch (Exception e) {
            System.err.println("[ReviewSecurityReportsPage] Error loading reports: " + e.getMessage());
            e.printStackTrace();
            allReports = new ArrayList<>();
        }
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
        
        JPanel contentSection = new JPanel(new GridLayout(1, 2, 25, 0));
        contentSection.setBackground(BG_PRIMARY);
        contentSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        
        JPanel reportListPanel = createReportListPanel();
        JPanel reportPreviewPanel = createReportPreviewPanel();
        
        contentSection.add(reportListPanel);
        contentSection.add(reportPreviewPanel);
        
        contentWrapper.add(contentSection);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel actionsPanel = createActionsPanel();
        contentWrapper.add(actionsPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createFilterPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(20, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel title = new JLabel("📊 Report Filters");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlsPanel.setBackground(BG_CARD);
        
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setForeground(TEXT_SECONDARY);
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JComboBox<String> typeFilter = new JComboBox<>(new String[]{
            "All Reports", "Daily", "Weekly", "Monthly", "Tampering", "User Activity"
        });
        styleComboBox(typeFilter, ACCENT_PURPLE);
        typeFilter.addActionListener(e -> filterReports((String) typeFilter.getSelectedItem()));
        
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{
            "All Status", "New", "Reviewed", "Flagged"
        });
        styleComboBox(statusFilter, ACCENT_CYAN);
        statusFilter.addActionListener(e -> filterReportsByStatus((String) statusFilter.getSelectedItem()));
        
        JButton refreshBtn = createGradientButton("🔄 Refresh", ACCENT_GREEN, ACCENT_CYAN);
        refreshBtn.addActionListener(e -> refreshReports());
        
        controlsPanel.add(typeLabel);
        controlsPanel.add(typeFilter);
        controlsPanel.add(statusLabel);
        controlsPanel.add(statusFilter);
        controlsPanel.add(refreshBtn);
        
        card.add(title, BorderLayout.WEST);
        card.add(controlsPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private void filterReports(String filter) {
        tableModel.setRowCount(0);
        
        for (SecurityReport report : allReports) {
            if (filter.equals("All Reports") || filter.equals(report.getReportType())) {
                tableModel.addRow(new Object[]{
                    report.getReportName(),
                    dateFormat.format(report.getReportDate()),
                    report.getReportType(),
                    report.getStatus()
                });
            }
        }
    }
    
    private void filterReportsByStatus(String status) {
        tableModel.setRowCount(0);
        
        for (SecurityReport report : allReports) {
            if (status.equals("All Status") || status.equals(report.getStatus())) {
                tableModel.addRow(new Object[]{
                    report.getReportName(),
                    dateFormat.format(report.getReportDate()),
                    report.getReportType(),
                    report.getStatus()
                });
            }
        }
    }
    
    private void refreshReports() {
        loadReportsFromDatabase();
        loadAllReports();
        reportContentArea.setText("Reports refreshed! Select a report to preview.");
        JOptionPane.showMessageDialog(mainPanel, 
            "Loaded " + allReports.size() + " reports from database", 
            "Refreshed", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private JPanel createReportListPanel() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("📋 Available Reports (" + allReports.size() + ")");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JTextField searchField = new JTextField(15);
        searchField.setBackground(new Color(35, 40, 65));
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(ACCENT_CYAN);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_CYAN, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                searchReports(searchField.getText());
            }
        });
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(searchField, BorderLayout.EAST);
        
        String[] columns = {"Report Name", "Date", "Type", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Load reports into table
        loadAllReports();
        
        JTable table = new JTable(tableModel);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(255, 255, 255, 5));
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(138, 99, 255, 30));
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(35, 40, 65));
        header.setForeground(ACCENT_CYAN);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_CYAN));
        
        table.setDefaultRenderer(Object.class, new ReportTableRenderer());
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    // Find the corresponding report
                    String reportName = (String) tableModel.getValueAt(selectedRow, 0);
                    for (int i = 0; i < allReports.size(); i++) {
                        if (allReports.get(i).getReportName().equals(reportName)) {
                            loadReportPreview(i);
                            break;
                        }
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ACCENT_CYAN;
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
    
    private void loadAllReports() {
        tableModel.setRowCount(0);
        for (SecurityReport report : allReports) {
            tableModel.addRow(new Object[]{
                report.getReportName(),
                dateFormat.format(report.getReportDate()),
                report.getReportType(),
                report.getStatus()
            });
        }
    }
    
    private void searchReports(String searchText) {
        tableModel.setRowCount(0);
        String search = searchText.toLowerCase();
        
        for (SecurityReport report : allReports) {
            if (report.getReportName().toLowerCase().contains(search) ||
                report.getReportType().toLowerCase().contains(search) ||
                report.getStatus().toLowerCase().contains(search)) {
                
                tableModel.addRow(new Object[]{
                    report.getReportName(),
                    dateFormat.format(report.getReportDate()),
                    report.getReportType(),
                    report.getStatus()
                });
            }
        }
    }
    
    private JPanel createReportPreviewPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("📄 Report Preview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BG_CARD);
        
        JButton fullViewBtn = createIconButton("👁️ Full View", ACCENT_CYAN);
        JButton printBtn = createIconButton("🖨️ Print", ACCENT_GREEN);
        
        buttonPanel.add(fullViewBtn);
        buttonPanel.add(printBtn);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        reportContentArea = new JTextArea();
        reportContentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportContentArea.setBackground(new Color(35, 40, 65));
        reportContentArea.setForeground(TEXT_PRIMARY);
        reportContentArea.setCaretColor(ACCENT_PURPLE);
        reportContentArea.setLineWrap(true);
        reportContentArea.setWrapStyleWord(true);
        reportContentArea.setEditable(false);
        reportContentArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        if (allReports.isEmpty()) {
            reportContentArea.setText("No reports available. Generate a report from the User Dashboard to see it here.");
        } else {
            reportContentArea.setText("Select a report from the list to preview its content...");
        }
        
        JScrollPane scrollPane = new JScrollPane(reportContentArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_PURPLE, 2));
        scrollPane.getViewport().setBackground(new Color(35, 40, 65));
        
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
    
    private JPanel createActionsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_GREEN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JLabel title = new JLabel("⚡ Report Actions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(BG_CARD);
        
        JPanel notesPanel = new JPanel(new BorderLayout(0, 10));
        notesPanel.setBackground(BG_CARD);
        
        JLabel notesLabel = new JLabel("Add Notes:");
        notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        notesLabel.setForeground(TEXT_SECONDARY);
        
        JTextArea notesArea = new JTextArea(3, 40);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notesArea.setBackground(new Color(35, 40, 65));
        notesArea.setForeground(TEXT_PRIMARY);
        notesArea.setCaretColor(ACCENT_GREEN);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_GREEN, 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(null);
        
        notesPanel.add(notesLabel, BorderLayout.NORTH);
        notesPanel.add(notesScroll, BorderLayout.CENTER);
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(BG_CARD);
        
        JButton flagBtn = createGradientButton("🚩 Flag for Follow-up", ACCENT_ORANGE, ACCENT_RED);
        flagBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        flagBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, 
                "Report flagged for follow-up!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton exportBtn = createGradientButton("💾 Export Report", ACCENT_GREEN, ACCENT_CYAN);
        exportBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, 
                "Report exported successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton markReviewedBtn = createOutlineButton("✅ Mark as Reviewed", ACCENT_PURPLE);
        markReviewedBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        markReviewedBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, 
                "Report marked as reviewed!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonsPanel.add(flagBtn);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonsPanel.add(exportBtn);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonsPanel.add(markReviewedBtn);
        
        contentPanel.add(notesPanel, BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.EAST);
        
        card.add(title, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadReportPreview(int index) {
        if (index >= 0 && index < allReports.size()) {
            SecurityReport report = allReports.get(index);
            
            StringBuilder preview = new StringBuilder();
            preview.append("=== ").append(report.getReportName().toUpperCase()).append(" ===\n\n");
            preview.append("Report Type: ").append(report.getReportType()).append("\n");
            preview.append("Date: ").append(dateFormat.format(report.getReportDate())).append("\n");
            preview.append("Status: ").append(report.getStatus()).append("\n");
            preview.append("Generated By: ").append(report.getGeneratorName() != null ? report.getGeneratorName() : "User ID " + report.getGeneratedBy()).append("\n");
            
            if (report.getReviewedBy() != null) {
                preview.append("Reviewed By: ").append(report.getReviewerName() != null ? report.getReviewerName() : "User ID " + report.getReviewedBy()).append("\n");
                preview.append("Reviewed Date: ").append(report.getReviewedDate()).append("\n");
            }
            
            if (report.getNotes() != null && !report.getNotes().isEmpty()) {
                preview.append("Notes: ").append(report.getNotes()).append("\n");
            }
            
            preview.append("\n").append("=".repeat(50)).append("\n\n");
            preview.append(report.getContent());
            
            reportContentArea.setText(preview.toString());
            reportContentArea.setCaretPosition(0);
        }
    }
    
    private void styleComboBox(JComboBox<String> combo, Color accent) {
        combo.setUI(new ModernComboBoxUI(accent));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(new Color(35, 40, 65));
        combo.setFocusable(false);
        combo.setBorder(new EmptyBorder(8, 12, 8, 12));
        combo.setPreferredSize(new Dimension(150, 40));
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
        btn.setMaximumSize(new Dimension(220, 40));
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
        btn.setMaximumSize(new Dimension(220, 40));
        return btn;
    }
    
    private JButton createIconButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(color);
        btn.setBackground(new Color(35, 40, 65));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    class ReportTableRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 3) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(false);
                
                String status = value.toString();
                JLabel label = new JLabel(status);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                
                Color color = status.equals("New") ? ACCENT_GREEN :
                             status.equals("Flagged") ? ACCENT_RED : ACCENT_CYAN;
                
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
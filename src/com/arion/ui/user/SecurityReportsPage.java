package com.arion.ui.user;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.arion.ui.common.UIConstants;
import com.arion.ui.common.IconRenderer;
import com.arion.ui.common.IconRenderer.Icon;
import com.arion.service.SecurityReportService;
import com.arion.service.SecurityReportService.ReportGenerationResult;
import com.arion.service.SecurityReportService.ReportStatistics;
import com.arion.model.SecurityReport;
import com.arion.model.User;

import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.text.SimpleDateFormat;

public class SecurityReportsPage {
    
    private static final Color BG_PRIMARY = new Color(25, 28, 45);
    private static final Color BG_CARD = new Color(45, 52, 85);
    private static final Color ACCENT_PURPLE = new Color(138, 99, 255);
    private static final Color ACCENT_CYAN = new Color(0, 230, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 163);
    private static final Color ACCENT_PINK = new Color(255, 99, 200);
    private static final Color ACCENT_ORANGE = new Color(255, 180, 0);
    private static final Color ACCENT_RED = new Color(255, 70, 100);
    private static final Color TEXT_PRIMARY = new Color(240, 242, 255);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 200);
    
    private JPanel mainPanel;
    private SecurityReportService reportService;
    private User currentUser;
    private JLabel totalReportsLabel;
    private JLabel newReportsLabel;
    private JLabel reviewedReportsLabel;
    private JLabel flaggedReportsLabel;
    private JTable reportsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public SecurityReportsPage(User user) {
        this.currentUser = user;
        this.reportService = new SecurityReportService();
        createUI();
        loadStatistics();
        loadRecentReports();
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
        
        // Report generator panel
        JPanel generatorPanel = createReportGeneratorPanel();
        contentPanel.add(generatorPanel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Statistics overview
        JPanel statsSection = createStatsSection();
        contentPanel.add(statsSection);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Recent reports table
        JPanel recentReportsPanel = createRecentReportsPanel();
        contentPanel.add(recentReportsPanel);
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
    
    private JPanel createReportGeneratorPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        
        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(BG_CARD);
        
        JLabel iconLabel = IconRenderer.getIconLabel(Icon.REPORT, 24, ACCENT_CYAN);
        JLabel title = new JLabel("Generate Security Report");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        
        titlePanel.add(iconLabel);
        titlePanel.add(title);
        
        // Content panel with report options
        JPanel contentPanel = new JPanel(new GridLayout(2, 3, 20, 15));
        contentPanel.setBackground(BG_CARD);
        
        // Report type dropdown
        String[] reportTypes = {"Daily Report", "Weekly Summary", "Monthly Analysis", "Tampering Report"};
        JComboBox<String> typeCombo = new JComboBox<>(reportTypes);
        UIConstants.styleComboBox(typeCombo, ACCENT_CYAN);
        JPanel typePanel = createInputGroup("Report Type", typeCombo);
        
        // Date range dropdown
        JComboBox<String> dateCombo = new JComboBox<>(new String[]{
            "Last 7 Days", "Last 30 Days", "Last Quarter", "Custom"
        });
        UIConstants.styleComboBox(dateCombo, ACCENT_CYAN);
        JPanel datePanel = createInputGroup("Date Range", dateCombo);
        
        // Format dropdown
        JComboBox<String> formatCombo = new JComboBox<>(new String[]{
            "PDF Document", "Excel Spreadsheet", "CSV File", "JSON Data"
        });
        UIConstants.styleComboBox(formatCombo, ACCENT_CYAN);
        JPanel formatPanel = createInputGroup("Export Format", formatCombo);
        
        // Include sections
        JPanel sectionsPanel = new JPanel();
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBackground(BG_CARD);
        
        JLabel sectionsLabel = new JLabel("Include Sections");
        sectionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sectionsLabel.setForeground(TEXT_SECONDARY);
        sectionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox summaryCheck = createStyledCheckbox("Summary", true);
        JCheckBox detailsCheck = createStyledCheckbox("Detailed Logs", true);
        JCheckBox chartsCheck = createStyledCheckbox("Charts & Graphs", false);
        
        sectionsPanel.add(sectionsLabel);
        sectionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sectionsPanel.add(summaryCheck);
        sectionsPanel.add(detailsCheck);
        sectionsPanel.add(chartsCheck);
        
        // Priority dropdown
        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{
            "All Levels", "Critical Only", "High & Critical", "Medium & Above"
        });
        UIConstants.styleComboBox(priorityCombo, ACCENT_CYAN);
        JPanel priorityPanel = createInputGroup("Priority Level", priorityCombo);
        
        // Generate button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(BG_CARD);
        
        JButton generateBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, ACCENT_GREEN,
                    getWidth(), getHeight(), ACCENT_CYAN
                );
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        JPanel btnContent = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnContent.setOpaque(false);
        JLabel btnIcon = IconRenderer.getIconLabel(Icon.EXPORT, 20, Color.WHITE);
        JLabel btnText = new JLabel("Generate Report");
        btnText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnText.setForeground(Color.WHITE);
        btnContent.add(btnIcon);
        btnContent.add(btnText);
        
        generateBtn.setLayout(new BorderLayout());
        generateBtn.add(btnContent, BorderLayout.CENTER);
        generateBtn.setFocusPainted(false);
        generateBtn.setContentAreaFilled(false);
        generateBtn.setBorder(new EmptyBorder(12, 30, 12, 30));
        generateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        generateBtn.addActionListener(e -> {
            String selectedType = (String) typeCombo.getSelectedItem();
            generateReport(selectedType);
        });
        
        buttonPanel.add(generateBtn);
        
        contentPanel.add(typePanel);
        contentPanel.add(datePanel);
        contentPanel.add(formatPanel);
        contentPanel.add(sectionsPanel);
        contentPanel.add(priorityPanel);
        contentPanel.add(buttonPanel);
        
        card.add(titlePanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createStatsSection() {
        JPanel container = new JPanel(new GridLayout(1, 4, 20, 0));
        container.setBackground(BG_PRIMARY);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        
        totalReportsLabel = new JLabel("0");
        newReportsLabel = new JLabel("0");
        reviewedReportsLabel = new JLabel("0");
        flaggedReportsLabel = new JLabel("0");
        
        container.add(createMiniStatCard("Total Reports", totalReportsLabel, Icon.REPORT, ACCENT_PURPLE, "All time"));
        container.add(createMiniStatCard("New Reports", newReportsLabel, Icon.ALERT, ACCENT_CYAN, "Pending review"));
        container.add(createMiniStatCard("Reviewed", reviewedReportsLabel, Icon.REPORT, ACCENT_GREEN, "Completed"));
        container.add(createMiniStatCard("Flagged", flaggedReportsLabel, Icon.ALERT, ACCENT_RED, "Requires action"));
        
        return container;
    }
    
    private JPanel createMiniStatCard(String label, JLabel valueLabel, Icon icon, Color color, String subtitle) {
        JPanel card = new GlowingCard(20, color);
        card.setLayout(new BorderLayout(15, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Icon panel
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = Math.min(getWidth(), getHeight());
                int yOffset = 12;
                
                GradientPaint gradient = new GradientPaint(
                    0, yOffset, new Color(color.getRed(), color.getGreen(), color.getBlue(), 60),
                    size, size + yOffset, new Color(color.getRed(), color.getGreen(), color.getBlue(), 30)
                );
                g2.setPaint(gradient);
                g2.fillOval(0, yOffset, size, size);
                
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(1, yOffset + 1, size - 2, size - 2);
                
                g2.dispose();
            }
            
            @Override
            public void doLayout() {
                super.doLayout();
                if (getComponentCount() > 0) {
                    Component comp = getComponent(0);
                    comp.setBounds(0, 0, getWidth(), getHeight());
                }
            }
        };
        iconPanel.setPreferredSize(new Dimension(55, 55));
        iconPanel.setOpaque(false);
        iconPanel.setLayout(null);
        
        JLabel iconLabel = IconRenderer.getIconLabel(icon, 32, color);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconLabel);
        
        // Text panel
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
    
    private JPanel createRecentReportsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(BG_CARD);
        
        JLabel titleIcon = IconRenderer.getIconLabel(Icon.FILE, 22, ACCENT_CYAN);
        JLabel title = new JLabel("Recent Reports");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        titlePanel.add(titleIcon);
        titlePanel.add(title);
        
        JLabel viewAll = new JLabel("Refresh →");
        viewAll.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewAll.setForeground(ACCENT_CYAN);
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                loadRecentReports();
            }
        });
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(viewAll, BorderLayout.EAST);
        
        // Create table
        String[] columns = {"Report Name", "Type", "Date", "Status", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        
        reportsTable = new JTable(tableModel);
        reportsTable.setBackground(BG_CARD);
        reportsTable.setForeground(TEXT_PRIMARY);
        reportsTable.setGridColor(new Color(255, 255, 255, 5));
        reportsTable.setRowHeight(55);
        reportsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportsTable.setShowGrid(false);
        reportsTable.setIntercellSpacing(new Dimension(0, 5));
        
        JTableHeader header = reportsTable.getTableHeader();
        header.setBackground(new Color(35, 40, 65));
        header.setForeground(ACCENT_CYAN);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_CYAN));
        
        reportsTable.setSelectionBackground(new Color(138, 99, 255, 30));
        reportsTable.setSelectionForeground(TEXT_PRIMARY);
        reportsTable.setDefaultRenderer(Object.class, new ReportTableCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(reportsTable);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUI(UIConstants.createNeonScrollBarUI(ACCENT_CYAN));
        
        // Status label
        statusLabel = new JLabel("Loading reports...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(statusLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void loadStatistics() {
        SwingWorker<ReportStatistics, Void> worker = new SwingWorker<ReportStatistics, Void>() {
            @Override
            protected ReportStatistics doInBackground() throws Exception {
                return reportService.getReportStatistics();
            }
            
            @Override
            protected void done() {
                try {
                    ReportStatistics stats = get();
                    totalReportsLabel.setText(String.valueOf(stats.getTotalReports()));
                    newReportsLabel.setText(String.valueOf(stats.getNewReports()));
                    reviewedReportsLabel.setText(String.valueOf(stats.getReviewedReports()));
                    flaggedReportsLabel.setText(String.valueOf(stats.getFlaggedReports()));
                } catch (Exception e) {
                    System.err.println("Error loading statistics: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void loadRecentReports() {
        SwingWorker<List<SecurityReport>, Void> worker = new SwingWorker<List<SecurityReport>, Void>() {
            @Override
            protected List<SecurityReport> doInBackground() throws Exception {
                return reportService.getRecentReports(10);
            }
            
            @Override
            protected void done() {
                try {
                    List<SecurityReport> reports = get();
                    tableModel.setRowCount(0);
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    
                    for (SecurityReport report : reports) {
                        Object[] row = {
                            report.getReportName(),
                            report.getReportType(),
                            dateFormat.format(report.getReportDate()),
                            report.getStatus(),
                            "View"
                        };
                        tableModel.addRow(row);
                    }
                    
                    if (reports.isEmpty()) {
                        statusLabel.setText("No reports generated yet");
                    } else {
                        statusLabel.setText("Showing " + reports.size() + " recent reports");
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error loading reports: " + e.getMessage());
                    statusLabel.setText("Error loading reports");
                }
            }
        };
        worker.execute();
    }
    
    private void generateReport(String reportType) {
        SwingWorker<ReportGenerationResult, Void> worker = new SwingWorker<ReportGenerationResult, Void>() {
            @Override
            protected ReportGenerationResult doInBackground() throws Exception {
                ReportGenerationResult result = null;
                
                switch (reportType) {
                    case "Daily Report":
                        result = reportService.generateDailyReport(currentUser.getUserId());
                        break;
                    case "Weekly Summary":
                        result = reportService.generateWeeklySummary(currentUser.getUserId());
                        break;
                    case "Monthly Analysis":
                        result = reportService.generateMonthlyAnalysis(currentUser.getUserId());
                        break;
                    case "Tampering Report":
                        result = reportService.generateTamperingReport(currentUser.getUserId());
                        break;
                    default:
                        result = reportService.generateDailyReport(currentUser.getUserId());
                }
                
                // If report generated successfully, generate PDF
                if (result.isSuccess()) {
                    reportService.generatePDFReport(result.getReportId(), currentUser.getUserId(), currentUser);
                }
                
                return result;
            }
            
            @Override
            protected void done() {
                try {
                    ReportGenerationResult result = get();
                    
                    if (result.isSuccess()) {
                        JOptionPane.showMessageDialog(mainPanel,
                            "Report generated successfully!\n" +
                            "Report ID: " + result.getReportId() + "\n" +
                            "PDF saved in 'uploads' folder",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        loadStatistics();
                        loadRecentReports();
                    } else {
                        JOptionPane.showMessageDialog(mainPanel,
                            "Error: " + result.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error generating report: " + e.getMessage());
                    JOptionPane.showMessageDialog(mainPanel,
                        "Error generating report: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private JPanel createInputGroup(String labelText, JComboBox<String> comboBox) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(comboBox);
        
        return panel;
    }
    
    private JCheckBox createStyledCheckbox(String text, boolean selected) {
        JCheckBox checkbox = new JCheckBox(text);
        checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        checkbox.setForeground(TEXT_PRIMARY);
        checkbox.setBackground(BG_CARD);
        checkbox.setSelected(selected);
        checkbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkbox.setFocusPainted(false);
        return checkbox;
    }
    
    // Custom Table Cell Renderer
    class ReportTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 3) { // Status column
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                Color statusColor = ACCENT_GREEN;
                if ("New".equals(value.toString())) {
                    statusColor = ACCENT_CYAN;
                } else if ("Flagged".equals(value.toString())) {
                    statusColor = ACCENT_RED;
                }
                
                label.setForeground(statusColor);
                label.setOpaque(true);
                label.setBackground(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 30));
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(statusColor, 1),
                    new EmptyBorder(5, 10, 5, 10)
                ));
                
                return label;
            }
            
            setBackground(isSelected ? new Color(138, 99, 255, 30) : BG_CARD);
            setForeground(TEXT_PRIMARY);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            
            return c;
        }
    }
    
    // ==================== CUSTOM COMPONENTS ====================
    
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
}
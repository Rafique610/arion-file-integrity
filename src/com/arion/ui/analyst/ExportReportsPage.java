package com.arion.ui.analyst;

import javax.swing.*;
import javax.swing.border.*;

import com.arion.ui.common.IconRenderer;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class ExportReportsPage {
    
    private static final Color BG_PRIMARY = new Color(25, 28, 45);
    private static final Color BG_CARD = new Color(45, 52, 85);
    private static final Color ACCENT_PURPLE = new Color(138, 99, 255);
    private static final Color ACCENT_CYAN = new Color(0, 230, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 163);
    private static final Color ACCENT_ORANGE = new Color(255, 180, 0);
    private static final Color ACCENT_PINK = new Color(255, 99, 200);
    private static final Color TEXT_PRIMARY = new Color(240, 242, 255);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 200);
	private static final Color ACCENT_RED = null;
    
    private JPanel mainPanel;
    private JProgressBar exportProgress;
    private JLabel exportStatusLabel;
    
    public ExportReportsPage() {
        createUI();
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    
    private void createUI() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BG_PRIMARY);
        
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(BG_PRIMARY);
        
        JPanel configPanel = createExportConfigPanel();
        contentWrapper.add(configPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel sectionsPanel = createSectionsPanel();
        contentWrapper.add(sectionsPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel bottomSection = new JPanel(new GridLayout(1, 2, 25, 0));
        bottomSection.setBackground(BG_PRIMARY);
        bottomSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));
        
        JPanel formatPanel = createFormatOptionsPanel();
        JPanel previewPanel = createExportPreviewPanel();
        
        bottomSection.add(formatPanel);
        bottomSection.add(previewPanel);
        
        contentWrapper.add(bottomSection);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel exportPanel = createExportActionPanel();
        contentWrapper.add(exportPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createExportConfigPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 25));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(35, 40, 35, 40));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        
        JLabel title = new JLabel("📤 Export Configuration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel configGrid = new JPanel(new GridLayout(2, 3, 25, 20));
        configGrid.setBackground(BG_CARD);
        
        JPanel reportTypePanel = createInputGroup("Report Type", 
            new JComboBox<>(new String[]{"Security Summary", "Tampering Analysis", "User Activity", "System Performance", "Compliance Report"}));
        
        JPanel dateRangePanel = createInputGroup("Date Range",
            new JComboBox<>(new String[]{"Last 24 Hours", "Last 7 Days", "Last 30 Days", "Last Quarter", "Custom Range"}));
        
        JPanel priorityPanel = createInputGroup("Priority Filter",
            new JComboBox<>(new String[]{"All Priorities", "Critical Only", "High & Critical", "Medium & Above"}));
        
        JPanel fileNamePanel = new JPanel();
        fileNamePanel.setLayout(new BoxLayout(fileNamePanel, BoxLayout.Y_AXIS));
        fileNamePanel.setBackground(BG_CARD);
        
        JLabel fileLabel = new JLabel("File Name");
        fileLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fileLabel.setForeground(TEXT_SECONDARY);
        fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField fileNameField = new JTextField("security_report_" + java.time.LocalDate.now());
        fileNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fileNameField.setBackground(new Color(35, 40, 65));
        fileNameField.setForeground(TEXT_PRIMARY);
        fileNameField.setCaretColor(ACCENT_PURPLE);
        fileNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        fileNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_PURPLE, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        fileNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        fileNamePanel.add(fileLabel);
        fileNamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        fileNamePanel.add(fileNameField);
        
        JPanel locationPanel = new JPanel();
        locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
        locationPanel.setBackground(BG_CARD);
        
        JLabel locationLabel = new JLabel("Save Location");
        locationLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        locationLabel.setForeground(TEXT_SECONDARY);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel locationFieldPanel = new JPanel(new BorderLayout(10, 0));
        locationFieldPanel.setBackground(new Color(35, 40, 65));
        locationFieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_CYAN, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        locationFieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JTextField locationField = new JTextField("C:/Users/Documents/Reports");
        locationField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        locationField.setBackground(new Color(35, 40, 65));
        locationField.setForeground(TEXT_PRIMARY);
        locationField.setCaretColor(ACCENT_CYAN);
        locationField.setBorder(null);
        
        JButton browseBtn = new JButton("📁");
        browseBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        browseBtn.setForeground(ACCENT_CYAN);
        browseBtn.setBackground(new Color(35, 40, 65));
        browseBtn.setBorder(new EmptyBorder(0, 5, 0, 5));
        browseBtn.setFocusPainted(false);
        browseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        locationFieldPanel.add(locationField, BorderLayout.CENTER);
        locationFieldPanel.add(browseBtn, BorderLayout.EAST);
        
        locationPanel.add(locationLabel);
        locationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        locationPanel.add(locationFieldPanel);
        
        JPanel templatePanel = createInputGroup("Template",
            new JComboBox<>(new String[]{"Standard Template", "Detailed Template", "Executive Summary", "Technical Report"}));
        
        configGrid.add(reportTypePanel);
        configGrid.add(dateRangePanel);
        configGrid.add(priorityPanel);
        configGrid.add(fileNamePanel);
        configGrid.add(locationPanel);
        configGrid.add(templatePanel);
        
        card.add(title, BorderLayout.NORTH);
        card.add(configGrid, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createSectionsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JLabel title = new JLabel("📋 Include Sections");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel sectionsGrid = new JPanel(new GridLayout(2, 4, 20, 15));
        sectionsGrid.setBackground(BG_CARD);
        
        JCheckBox summaryCheck = createStyledCheckbox("Executive Summary", true, ACCENT_GREEN);
        JCheckBox detailsCheck = createStyledCheckbox("Detailed Logs", true, ACCENT_CYAN);
        JCheckBox chartsCheck = createStyledCheckbox("Charts & Graphs", true, ACCENT_PURPLE);
        JCheckBox statisticsCheck = createStyledCheckbox("Statistics", true, ACCENT_ORANGE);
        JCheckBox userActivityCheck = createStyledCheckbox("User Activities", false, ACCENT_PINK);
        JCheckBox incidentsCheck = createStyledCheckbox("Security Incidents", true, ACCENT_RED);
        JCheckBox recommendationsCheck = createStyledCheckbox("Recommendations", false, ACCENT_GREEN);
        JCheckBox appendixCheck = createStyledCheckbox("Appendix", false, TEXT_SECONDARY);
        
        sectionsGrid.add(summaryCheck);
        sectionsGrid.add(detailsCheck);
        sectionsGrid.add(chartsCheck);
        sectionsGrid.add(statisticsCheck);
        sectionsGrid.add(userActivityCheck);
        sectionsGrid.add(incidentsCheck);
        sectionsGrid.add(recommendationsCheck);
        sectionsGrid.add(appendixCheck);
        
        card.add(title, BorderLayout.NORTH);
        card.add(sectionsGrid, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createFormatOptionsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_ORANGE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("🎨 Format Options");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);
        
        // Export format
        JPanel formatCard = createFormatCard("PDF Document", "📄", 
            "Professional PDF with charts and formatting", ACCENT_RED, true);
        contentPanel.add(formatCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        JPanel excelCard = createFormatCard("Excel Spreadsheet", "📊",
            "Data tables with formulas and pivot tables", ACCENT_GREEN, false);
        contentPanel.add(excelCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        JPanel csvCard = createFormatCard("CSV File", "📝",
            "Plain text comma-separated values", ACCENT_CYAN, false);
        contentPanel.add(csvCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        JPanel jsonCard = createFormatCard("JSON Data", "{ }",
            "Structured data for API integration", ACCENT_PURPLE, false);
        contentPanel.add(jsonCard);
        
        card.add(title, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createFormatCard(String name, String icon, String description, Color color, boolean selected) {
        JPanel card = new RoundedPanel(12);
        card.setLayout(new BorderLayout(15, 0));
        card.setBackground(selected ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 20) : new Color(35, 40, 65));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(selected ? color : new Color(255, 255, 255, 20), selected ? 2 : 1),
            new EmptyBorder(15, 18, 15, 18)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.Y_AXIS));
        iconPanel.setBackground(selected ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 20) : new Color(35, 40, 65));
        iconPanel.setPreferredSize(new Dimension(50, 50));
        
        IconRenderer.Icon iconType = icon.equals("📄") ? IconRenderer.Icon.FILE : 
            icon.equals("📊") ? IconRenderer.Icon.CHART : IconRenderer.Icon.FILE;
JLabel iconLabel = IconRenderer.getIconLabel(iconType, 28, color);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        iconLabel.setForeground(color);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconPanel.add(Box.createVerticalGlue());
        iconPanel.add(iconLabel);
        iconPanel.add(Box.createVerticalGlue());
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(selected ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 20) : new Color(35, 40, 65));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(nameLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);
        
        JRadioButton radioBtn = new JRadioButton();
        radioBtn.setSelected(selected);
        radioBtn.setBackground(selected ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 20) : new Color(35, 40, 65));
        radioBtn.setFocusPainted(false);
        
        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(radioBtn, BorderLayout.EAST);
        
        return card;
    }
    
    private JPanel createExportPreviewPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PINK);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("👁️ Export Preview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel previewPanel = new RoundedPanel(15);
        previewPanel.setLayout(new BorderLayout(0, 15));
        previewPanel.setBackground(new Color(35, 40, 65));
        previewPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JLabel previewTitle = new JLabel("Security Report - November 2025");
        previewTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        previewTitle.setForeground(ACCENT_CYAN);
        
        JTextArea previewContent = new JTextArea();
        previewContent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        previewContent.setBackground(new Color(35, 40, 65));
        previewContent.setForeground(TEXT_PRIMARY);
        previewContent.setEditable(false);
        previewContent.setLineWrap(true);
        previewContent.setWrapStyleWord(true);
        previewContent.setText(
            "EXECUTIVE SUMMARY\n" +
            "Report Period: November 1-19, 2025\n" +
            "Generated: " + java.time.LocalDateTime.now() + "\n\n" +
            "KEY METRICS:\n" +
            "• Total Files Monitored: 142\n" +
            "• Security Incidents: 7\n" +
            "• System Uptime: 99.8%\n" +
            "• Average Response Time: 1.2s\n\n" +
            "CRITICAL FINDINGS:\n" +
            "• 3 confirmed tampering incidents\n" +
            "• 2 high-priority security alerts\n" +
            "• All incidents resolved within SLA\n\n" +
            "[Additional sections will be included based on your selection...]"
        );
        
        JScrollPane scrollPane = new JScrollPane(previewContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(35, 40, 65));
        
        previewPanel.add(previewTitle, BorderLayout.NORTH);
        previewPanel.add(scrollPane, BorderLayout.CENTER);
        
        card.add(title, BorderLayout.NORTH);
        card.add(previewPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createExportActionPanel() {
        JPanel card = new GlowingCard(25, ACCENT_GREEN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JLabel title = new JLabel("🚀 Export");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);
        
        exportProgress = new JProgressBar(0, 100);
        exportProgress.setStringPainted(true);
        exportProgress.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exportProgress.setForeground(ACCENT_GREEN);
        exportProgress.setBackground(new Color(35, 40, 65));
        exportProgress.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_GREEN, 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        exportProgress.setValue(0);
        exportProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        exportProgress.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        exportStatusLabel = new JLabel("Ready to export");
        exportStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportStatusLabel.setForeground(TEXT_SECONDARY);
        exportStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setBackground(BG_CARD);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton exportBtn = createGradientButton("📤 Export Report", ACCENT_GREEN, ACCENT_CYAN);
        exportBtn.addActionListener(e -> startExport());
        
        JButton scheduleBtn = createOutlineButton("⏰ Schedule Export", ACCENT_PURPLE);
        JButton cancelBtn = createOutlineButton("❌ Cancel", ACCENT_RED);
        
        buttonsPanel.add(exportBtn);
        buttonsPanel.add(scheduleBtn);
        buttonsPanel.add(cancelBtn);
        
        contentPanel.add(exportProgress);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(exportStatusLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(buttonsPanel);
        
        card.add(title, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void startExport() {
        Timer timer = new Timer(100, null);
        timer.addActionListener(new ActionListener() {
            int progress = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += 2;
                exportProgress.setValue(progress);
                
                if (progress <= 30) {
                    exportStatusLabel.setText("Collecting data...");
                    exportStatusLabel.setForeground(ACCENT_CYAN);
                } else if (progress <= 60) {
                    exportStatusLabel.setText("Generating report...");
                    exportStatusLabel.setForeground(ACCENT_PURPLE);
                } else if (progress <= 90) {
                    exportStatusLabel.setText("Formatting document...");
                    exportStatusLabel.setForeground(ACCENT_ORANGE);
                } else if (progress < 100) {
                    exportStatusLabel.setText("Finalizing export...");
                    exportStatusLabel.setForeground(ACCENT_GREEN);
                }
                
                if (progress >= 100) {
                    timer.stop();
                    exportStatusLabel.setText("✅ Export completed successfully!");
                    exportStatusLabel.setForeground(ACCENT_GREEN);
                    JOptionPane.showMessageDialog(mainPanel, 
                        "Report exported successfully!", 
                        "Export Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        timer.start();
    }
    
    private JPanel createInputGroup(String labelText, JComboBox<String> comboBox) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(new Color(35, 40, 65));
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_CYAN, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(comboBox);
        
        return panel;
    }
    
    private JCheckBox createStyledCheckbox(String text, boolean selected, Color color) {
        JCheckBox checkbox = new JCheckBox(text);
        checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        checkbox.setForeground(TEXT_PRIMARY);
        checkbox.setBackground(BG_CARD);
        checkbox.setSelected(selected);
        checkbox.setFocusPainted(false);
        return checkbox;
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(12, 25, 12, 25));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private JButton createOutlineButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(color);
        btn.setBackground(new Color(35, 40, 65));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(10, 20, 10, 20)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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
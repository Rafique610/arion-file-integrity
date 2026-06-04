package com.arion.ui.admin;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.arion.ui.common.IconRenderer;

import java.awt.*;
import java.awt.geom.*;

public class MaintainAuditLogsPage {
    
    private static final Color BG_PRIMARY = new Color(25, 28, 45);
    private static final Color BG_CARD = new Color(45, 52, 85);
    private static final Color ACCENT_PURPLE = new Color(138, 99, 255);
    private static final Color ACCENT_CYAN = new Color(0, 230, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 163);
    private static final Color ACCENT_ORANGE = new Color(255, 180, 0);
    private static final Color ACCENT_RED = new Color(255, 70, 100);
    private static final Color ACCENT_PINK = new Color(255, 99, 200);
    private static final Color TEXT_PRIMARY = new Color(240, 242, 255);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 200);
    
    private JPanel mainPanel;
    private JTable logsTable;
    
    public MaintainAuditLogsPage() {
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
        
        JPanel filterPanel = createFilterPanel();
        contentWrapper.add(filterPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel statsPanel = createStatsPanel();
        contentWrapper.add(statsPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel logsTablePanel = createLogsTablePanel();
        contentWrapper.add(logsTablePanel);
        
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
        
        JLabel title = new JLabel("🔍 Filter Audit Logs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlsPanel.setBackground(BG_CARD);
        
        JLabel dateLabel = new JLabel("Date Range:");
        dateLabel.setForeground(TEXT_PRIMARY);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JComboBox<String> dateFilter = new JComboBox<>(new String[]{
            "Last 24 Hours", "Last 7 Days", "Last 30 Days", "Last Quarter", "Custom Range"
        });
        styleComboBox(dateFilter, ACCENT_CYAN);
        
        JLabel typeLabel = new JLabel("Log Type:");
        typeLabel.setForeground(TEXT_PRIMARY);
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JComboBox<String> typeFilter = new JComboBox<>(new String[]{
            "All Logs", "System Logs", "User Activity", "Security Logs", "Error Logs"
        });
        styleComboBox(typeFilter, ACCENT_GREEN);
        
        JButton applyBtn = createGradientButton("Apply", ACCENT_GREEN, ACCENT_CYAN);
        JButton clearBtn = createOutlineButton("Clear", ACCENT_ORANGE);
        
        controlsPanel.add(dateLabel);
        controlsPanel.add(dateFilter);
        controlsPanel.add(typeLabel);
        controlsPanel.add(typeFilter);
        controlsPanel.add(applyBtn);
        controlsPanel.add(clearBtn);
        
        card.add(title, BorderLayout.WEST);
        card.add(controlsPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private JPanel createStatsPanel() {
        JPanel container = new JPanel(new GridLayout(1, 4, 20, 0));
        container.setBackground(BG_PRIMARY);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        
        container.add(createStatCard("Total Logs", "24,567", "📊", ACCENT_CYAN));
        container.add(createStatCard("Today's Logs", "1,234", "📅", ACCENT_GREEN));
        container.add(createStatCard("Error Logs", "23", "⚠", ACCENT_ORANGE));
        container.add(createStatCard("Security Events", "156", "🔐", ACCENT_RED));
        
        return container;
    }
    
    private JPanel createStatCard(String label, String value, String icon, Color color) {
        JPanel card = new GlowingCard(20, color);
        card.setLayout(new BorderLayout(15, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel iconPanel = new RoundedPanel(12);
        iconPanel.setPreferredSize(new Dimension(50, 50));
        iconPanel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        iconPanel.setBorder(BorderFactory.createLineBorder(color, 2));
        iconPanel.setLayout(new GridBagLayout());
        
        IconRenderer.Icon iconType = null;
        switch(icon) {
            case "📊": iconType = IconRenderer.Icon.CHART; break;
            case "📅": iconType = IconRenderer.Icon.CLOCK; break;
            case "⚠️": iconType = IconRenderer.Icon.ALERT; break;
            case "🔒": iconType = IconRenderer.Icon.LOCK; break;
        }
        JLabel iconLabel = IconRenderer.getIconLabel(iconType, 24, color);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconPanel.add(iconLabel);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BG_CARD);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(TEXT_PRIMARY);
        labelText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(labelText);
        
        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createLogsTablePanel() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 550));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("📋 Audit Log Entries");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(BG_CARD);
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBackground(new Color(35, 40, 65));
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(ACCENT_CYAN);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_CYAN, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        JButton searchBtn = createOutlineButton("🔍 Search", ACCENT_CYAN);
        
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        String[] columns = {"Timestamp", "User", "Action", "Status", "IP Address", "Details"};
        Object[][] data = {
            {"2025-11-19 10:30:15", "admin", "User Created", "Success", "192.168.1.100", "Created user: jdoe"},
            {"2025-11-19 10:25:42", "jsmith", "Login", "Success", "192.168.1.45", "Successful login"},
            {"2025-11-19 10:20:33", "admin", "Policy Updated", "Success", "192.168.1.100", "Modified: Password Policy"},
            {"2025-11-19 10:15:18", "mjones", "File Upload", "Success", "192.168.1.67", "Uploaded: report.pdf"},
            {"2025-11-19 10:10:05", "admin", "Settings Changed", "Success", "192.168.1.100", "Session timeout: 30min"},
            {"2025-11-19 10:05:47", "unknown", "Login", "Failed", "192.168.1.200", "Invalid credentials"},
            {"2025-11-19 10:00:22", "tjohnson", "File Verified", "Success", "192.168.1.89", "Verified: contract.docx"},
            {"2025-11-19 09:55:13", "admin", "Database Backup", "Success", "192.168.1.100", "Backup completed"},
            {"2025-11-19 09:50:38", "swilson", "File Access", "Success", "192.168.1.112", "Accessed: data.xlsx"},
            {"2025-11-19 09:45:29", "admin", "User Role Changed", "Success", "192.168.1.100", "User: analyst@co.com"},
            {"2025-11-19 09:40:15", "ebrown", "File Download", "Success", "192.168.1.134", "Downloaded: archive.zip"},
            {"2025-11-19 09:35:52", "system", "Security Scan", "Success", "127.0.0.1", "Full system scan"},
            {"2025-11-19 09:30:44", "clee", "Login", "Success", "192.168.1.178", "Successful login"},
            {"2025-11-19 09:25:17", "admin", "Policy Created", "Success", "192.168.1.100", "New: Data Backup Policy"}
        };
        
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        logsTable = new JTable(model);
        logsTable.setBackground(BG_CARD);
        logsTable.setForeground(TEXT_PRIMARY);
        logsTable.setGridColor(new Color(255, 255, 255, 5));
        logsTable.setRowHeight(50);
        logsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logsTable.setShowGrid(false);
        logsTable.setSelectionBackground(new Color(138, 99, 255, 30));
        
        JTableHeader header = logsTable.getTableHeader();
        header.setBackground(new Color(35, 40, 65));
        header.setForeground(ACCENT_CYAN);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_CYAN));
        
        logsTable.setDefaultRenderer(Object.class, new LogsTableRenderer());
        
        JScrollPane scrollPane = new JScrollPane(logsTable);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        
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
    
    private JPanel createActionsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_ORANGE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("⚙ Log Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        headerPanel.add(title, BorderLayout.WEST);
        
        JPanel contentPanel = new JPanel(new BorderLayout(25, 0));
        contentPanel.setBackground(BG_CARD);
        
        JPanel archivePanel = new JPanel();
        archivePanel.setLayout(new BoxLayout(archivePanel, BoxLayout.Y_AXIS));
        archivePanel.setBackground(BG_CARD);
        
        JLabel archiveLabel = new JLabel("Archive logs older than:");
        archiveLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        archiveLabel.setForeground(TEXT_PRIMARY);
        archiveLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        spinnerPanel.setBackground(BG_CARD);
        spinnerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        spinnerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        SpinnerNumberModel model = new SpinnerNumberModel(90, 1, 365, 1);
        JSpinner daySpinner = new JSpinner(model);
        daySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        daySpinner.setPreferredSize(new Dimension(80, 40));
        
        JComponent editor = daySpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setBackground(new Color(35, 40, 65));
            textField.setForeground(TEXT_PRIMARY);
            textField.setCaretColor(ACCENT_ORANGE);
        }
        
        daySpinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_ORANGE, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        JLabel daysLabel = new JLabel("days");
        daysLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        daysLabel.setForeground(TEXT_SECONDARY);
        
        spinnerPanel.add(daySpinner);
        spinnerPanel.add(daysLabel);
        
        archivePanel.add(archiveLabel);
        archivePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        archivePanel.add(spinnerPanel);
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(BG_CARD);
        
        JButton exportBtn = createGradientButton("📤 Export Logs", ACCENT_CYAN, ACCENT_PURPLE);
        exportBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, 
                "Logs exported successfully!\n\nFile: audit_logs_" + System.currentTimeMillis() + ".csv\nLocation: /exports/", 
                "Export Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton archiveBtn = createGradientButton("📦 Archive Logs", ACCENT_ORANGE, ACCENT_RED);
        archiveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        archiveBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainPanel, 
                "Archive logs older than 90 days?\nArchived logs will be moved to cold storage.", 
                "Archive Logs", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(mainPanel, "Logs archived successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton clearBtn = createOutlineButton("🗑 Clear Old Logs", ACCENT_RED);
        clearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainPanel, 
                "⚠️ WARNING: This will permanently delete old logs!\n\nAre you sure you want to continue?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(mainPanel, "Old logs cleared successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        buttonsPanel.add(exportBtn);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonsPanel.add(archiveBtn);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonsPanel.add(clearBtn);
        
        contentPanel.add(archivePanel, BorderLayout.WEST);
        contentPanel.add(buttonsPanel, BorderLayout.EAST);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void styleComboBox(JComboBox<String> combo, Color color) {
        combo.setUI(new ModernComboBoxUI(color));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(new Color(35, 40, 65));
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        combo.setPreferredSize(new Dimension(150, 40));
    }
    
    // Inner class for modern ComboBox UI
    class ModernComboBoxUI extends javax.swing.plaf.basic.BasicComboBoxUI {
        private final Color borderColor;
        private final Color bgColor = new Color(35, 40, 65);
        private final Color textColor = new Color(240, 242, 255);
        
        public ModernComboBoxUI(Color borderColor) {
            this.borderColor = borderColor;
        }
        
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("▼") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bgColor);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(borderColor);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth("▼")) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2 - 2;
                    g2.drawString("▼", x, y);
                    g2.dispose();
                }
            };
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            return button;
        }
        
        @Override
        protected javax.swing.plaf.basic.ComboPopup createPopup() {
            return new javax.swing.plaf.basic.BasicComboPopup(comboBox) {
                @Override
                protected void configurePopup() {
                    super.configurePopup();
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 2),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                    setBackground(bgColor);
                }
                
                @Override
                protected void configureList() {
                    super.configureList();
                    list.setBackground(bgColor);
                    list.setForeground(textColor);
                    list.setSelectionBackground(new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 80));
                    list.setSelectionForeground(borderColor);
                    list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    list.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    
                    list.setCellRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(JList<?> list, Object value, 
                                int index, boolean isSelected, boolean cellHasFocus) {
                            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                            if (isSelected) {
                                label.setBackground(new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 80));
                                label.setForeground(borderColor);
                                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                            } else {
                                label.setBackground(bgColor);
                                label.setForeground(textColor);
                                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                            }
                            label.setOpaque(true);
                            return label;
                        }
                    });
                }
            };
        }
        
        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            ListCellRenderer<Object> renderer = comboBox.getRenderer();
            Component c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
            c.setFont(comboBox.getFont());
            c.setForeground(textColor);
            c.setBackground(bgColor);
            currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, c instanceof JPanel);
        }
        
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
            g2.dispose();
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
    
    class LogsTableRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 3) { // Status
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(false);
                
                String status = value.toString();
                JLabel label = new JLabel(status);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                
                Color color = status.equals("Success") ? ACCENT_GREEN : ACCENT_RED;
                
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
            
            // Highlight error rows
            if (column == 2 && value.toString().equals("Login") && table.getValueAt(row, 3).toString().equals("Failed")) {
                setForeground(ACCENT_RED);
            }
            
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
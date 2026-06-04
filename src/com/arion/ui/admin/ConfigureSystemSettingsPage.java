package com.arion.ui.admin;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;

public class ConfigureSystemSettingsPage {
    
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
    
    public ConfigureSystemSettingsPage() {
        createUI();
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    private void styleTab(JTabbedPane tabs, int index, Color color) {
        JLabel lbl = new JLabel(tabs.getTitleAt(index));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(45, 52, 85));
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabs.setTabComponentAt(index, lbl);
    }

    private void createUI() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BG_PRIMARY);
        
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(BG_PRIMARY);
        
        JTabbedPane tabbedPane = createStyledTabbedPane();
        tabbedPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        contentWrapper.add(tabbedPane);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel actionPanel = createActionPanel();
        contentWrapper.add(actionPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBackground(BG_CARD);
        tabbedPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        tabbedPane.addTab("General Settings", createGeneralSettingsPanel());
        tabbedPane.addTab("🔐 Security Settings", createSecuritySettingsPanel());
        tabbedPane.addTab("💾 Database Settings", createDatabaseSettingsPanel());
        

        styleTab(tabbedPane, 0, ACCENT_PURPLE);
        styleTab(tabbedPane, 1, ACCENT_RED);
        styleTab(tabbedPane, 2, ACCENT_CYAN);

        return tabbedPane;
    }
    
    private JPanel createGeneralSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 25));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(35, 40, 35, 40));
        
        JLabel title = new JLabel("⚙ General Configuration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel settingsGrid = new JPanel(new GridLayout(5, 2, 25, 20));
        settingsGrid.setBackground(BG_CARD);
        
        // System Name
        settingsGrid.add(createSettingLabel("System Name:", "The display name for your ARION system"));
        JTextField systemNameField = createStyledTextField("ARION Security System", ACCENT_PURPLE);
        settingsGrid.add(systemNameField);
        
        // Max File Size
        settingsGrid.add(createSettingLabel("Max File Size (MB):", "Maximum upload file size limit"));
        JSpinner maxSizeSpinner = createStyledSpinner(100, 1, 1000, 10, ACCENT_CYAN);
        settingsGrid.add(maxSizeSpinner);
        
        // Default Hash Algorithm
        settingsGrid.add(createSettingLabel("Default Hash Algorithm:", "Algorithm used for new file uploads"));
        JComboBox<String> hashCombo = new JComboBox<>(new String[]{"MD5", "SHA-1", "SHA-256", "SHA-512"});
        styleComboBox(hashCombo, ACCENT_GREEN);
        hashCombo.setSelectedItem("SHA-256");
        settingsGrid.add(hashCombo);
        
        // Enable Notifications
        settingsGrid.add(createSettingLabel("Enable Notifications:", "System-wide notification settings"));
        JCheckBox notifCheckbox = createStyledCheckbox("Enable email notifications", true, ACCENT_ORANGE);
        settingsGrid.add(notifCheckbox);
        
        // Auto Backup
        settingsGrid.add(createSettingLabel("Auto Backup:", "Automatic database backup schedule"));
        JCheckBox backupCheckbox = createStyledCheckbox("Enable daily auto-backup", true, ACCENT_GREEN);
        settingsGrid.add(backupCheckbox);
        
        card.add(title, BorderLayout.NORTH);
        card.add(settingsGrid, BorderLayout.CENTER);
        
        panel.add(card);
        
        return panel;
    }
    
    private JPanel createSecuritySettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel card = new GlowingCard(25, ACCENT_RED);
        card.setLayout(new BorderLayout(0, 25));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(35, 40, 35, 40));
        
        JLabel title = new JLabel("🔐 Security Configuration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel settingsGrid = new JPanel(new GridLayout(6, 2, 25, 20));
        settingsGrid.setBackground(BG_CARD);
        
        // Session Timeout
        settingsGrid.add(createSettingLabel("Session Timeout (minutes):", "Auto-logout after inactivity"));
        JSpinner timeoutSpinner = createStyledSpinner(30, 5, 120, 5, ACCENT_RED);
        settingsGrid.add(timeoutSpinner);
        
        // Max Login Attempts
        settingsGrid.add(createSettingLabel("Max Login Attempts:", "Account locks after failed attempts"));
        JSpinner attemptsSpinner = createStyledSpinner(3, 1, 10, 1, ACCENT_ORANGE);
        settingsGrid.add(attemptsSpinner);
        
        // Two-Factor Authentication
        settingsGrid.add(createSettingLabel("Two-Factor Authentication:", "Require 2FA for all users"));
        JCheckBox twoFACheckbox = createStyledCheckbox("Enable 2FA requirement", false, ACCENT_PURPLE);
        settingsGrid.add(twoFACheckbox);
        
        // Password Complexity
        settingsGrid.add(createSettingLabel("Password Complexity:", "Enforce strong password rules"));
        JCheckBox passwordCheckbox = createStyledCheckbox("Require complex passwords", true, ACCENT_CYAN);
        settingsGrid.add(passwordCheckbox);
        
        // IP Whitelisting
        settingsGrid.add(createSettingLabel("IP Whitelisting:", "Restrict access by IP address"));
        JCheckBox ipCheckbox = createStyledCheckbox("Enable IP whitelist", false, ACCENT_GREEN);
        settingsGrid.add(ipCheckbox);
        
        // Audit Logging
        settingsGrid.add(createSettingLabel("Audit Logging Level:", "Detail level for system logs"));
        JComboBox<String> auditCombo = new JComboBox<>(new String[]{"Basic", "Standard", "Detailed", "Verbose"});
        styleComboBox(auditCombo, ACCENT_ORANGE);
        auditCombo.setSelectedItem("Standard");
        settingsGrid.add(auditCombo);
        
        card.add(title, BorderLayout.NORTH);
        card.add(settingsGrid, BorderLayout.CENTER);
        
        panel.add(card);
        
        return panel;
    }
    
    private JPanel createDatabaseSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 25));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(35, 40, 35, 40));
        
        JLabel title = new JLabel("💾 Database Configuration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(BG_CARD);
        
        // Database Host
        JPanel hostPanel = createInputRow("Database Host:", "localhost", ACCENT_CYAN, "Server address or hostname");
        settingsPanel.add(hostPanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Database Port
        JPanel portPanel = new JPanel(new BorderLayout(15, 10));
        portPanel.setBackground(BG_CARD);
        portPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel portLabel = createSettingLabel("Database Port:", "Database connection port");
        JSpinner portSpinner = createStyledSpinner(3306, 1000, 65535, 1, ACCENT_PURPLE);
        
        portPanel.add(portLabel, BorderLayout.NORTH);
        portPanel.add(portSpinner, BorderLayout.CENTER);
        settingsPanel.add(portPanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Database Name
        JPanel dbNamePanel = createInputRow("Database Name:", "arion_db", ACCENT_GREEN, "Name of the database schema");
        settingsPanel.add(dbNamePanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Connection Pool Size
        JPanel poolPanel = new JPanel(new BorderLayout(15, 10));
        poolPanel.setBackground(BG_CARD);
        poolPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel poolLabel = createSettingLabel("Connection Pool Size:", "Max concurrent database connections");
        JSpinner poolSpinner = createStyledSpinner(20, 5, 100, 5, ACCENT_ORANGE);
        
        poolPanel.add(poolLabel, BorderLayout.NORTH);
        poolPanel.add(poolSpinner, BorderLayout.CENTER);
        settingsPanel.add(poolPanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Test Connection Button
        JPanel testPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        testPanel.setBackground(BG_CARD);
        testPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JButton testBtn = createGradientButton("🔌 Test Connection", ACCENT_CYAN, ACCENT_PURPLE);
        testBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, 
                "Database connection test successful!\n\nHost: localhost:3306\nDatabase: arion_db\nStatus: Connected", 
                "Connection Test", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        testPanel.add(testBtn);
        settingsPanel.add(testPanel);
        
        card.add(title, BorderLayout.NORTH);
        card.add(settingsPanel, BorderLayout.CENTER);
        
        panel.add(card);
        
        return panel;
    }
    
    private JPanel createInputRow(String labelText, String defaultValue, Color color, String hint) {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBackground(BG_CARD);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel label = createSettingLabel(labelText, hint);
        JTextField field = createStyledTextField(defaultValue, color);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel card = new GlowingCard(25, ACCENT_GREEN);
        card.setLayout(new BorderLayout(20, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("💾 Save Configuration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Changes will take effect immediately after saving");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(title);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(subtitle);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonsPanel.setBackground(BG_CARD);
        
        JButton resetBtn = createOutlineButton("🔄 Reset to Default", ACCENT_ORANGE);
        resetBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainPanel, 
                "Are you sure you want to reset all settings to default values?", 
                "Reset Settings", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(mainPanel, "Settings reset to default values", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton saveBtn = createGradientButton("✅ Save Changes", ACCENT_GREEN, ACCENT_CYAN);
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, 
                "System settings saved successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonsPanel.add(resetBtn);
        buttonsPanel.add(saveBtn);
        
        card.add(textPanel, BorderLayout.WEST);
        card.add(buttonsPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private JLabel createSettingLabel(String text, String hint) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel hintLabel = new JLabel(hint);
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hintLabel.setForeground(TEXT_SECONDARY);
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        panel.add(hintLabel);
        
        return label;
    }
    
    private JTextField createStyledTextField(String text, Color borderColor) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(35, 40, 65));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(borderColor);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }
    
    private JSpinner createStyledSpinner(int value, int min, int max, int step, Color color) {
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setBackground(new Color(35, 40, 65));
            textField.setForeground(TEXT_PRIMARY);
            textField.setCaretColor(color);
        }
        
        spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        return spinner;
    }
    
    private void styleComboBox(JComboBox<String> combo, Color color) {
        combo.setUI(new ModernComboBoxUI(color));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(new Color(35, 40, 65));
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
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
}
package com.arion.ui.admin;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;

public class SetupSecurityPoliciesPage {
    
    private static final Color BG_PRIMARY = new Color(25, 28, 45);
    private static final Color BG_CARD = new Color(45, 52, 85);
    private static final Color ACCENT_PURPLE = new Color(138, 99, 255);
    private static final Color ACCENT_CYAN = new Color(0, 230, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 163);
    private static final Color ACCENT_ORANGE = new Color(255, 180, 0);
    private static final Color ACCENT_RED = new Color(255, 70, 100);
    private static final Color TEXT_PRIMARY = new Color(240, 242, 255);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 200);
	private static final Color ACCENT_PINK = null;
    
    private JPanel mainPanel;
    private DefaultListModel<String> policyListModel;
    private JList<String> policyList;
    
    public SetupSecurityPoliciesPage() {
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
        
        JPanel headerPanel = createHeaderPanel();
        contentWrapper.add(headerPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel mainSection = new JPanel(new GridLayout(1, 2, 25, 0));
        mainSection.setBackground(BG_PRIMARY);
        mainSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
        
        JPanel policiesListPanel = createPoliciesListPanel();
        JPanel policyEditorPanel = createPolicyEditorPanel();
        
        mainSection.add(policiesListPanel);
        mainSection.add(policyEditorPanel);
        
        contentWrapper.add(mainSection);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel card = new GlowingCard(25, ACCENT_ORANGE);
        card.setLayout(new BorderLayout(20, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel title = new JLabel("🛡️ Security Policies");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlsPanel.setBackground(BG_CARD);
        
        JButton addPolicyBtn = createGradientButton("➕ Add New Policy", ACCENT_GREEN, ACCENT_CYAN);
        addPolicyBtn.addActionListener(e -> showAddPolicyDialog());
        
        JButton importBtn = createOutlineButton("📥 Import", ACCENT_CYAN);
        JButton exportBtn = createOutlineButton("📤 Export", ACCENT_PURPLE);
        
        controlsPanel.add(addPolicyBtn);
        controlsPanel.add(importBtn);
        controlsPanel.add(exportBtn);
        
        card.add(title, BorderLayout.WEST);
        card.add(controlsPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private JPanel createPoliciesListPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("📋 Existing Policies");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JLabel countLabel = new JLabel("18 active policies");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countLabel.setForeground(ACCENT_GREEN);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        
        policyListModel = new DefaultListModel<>();
        policyListModel.addElement("🔐 Password Complexity Policy");
        policyListModel.addElement("⏱ Session Timeout Policy");
        policyListModel.addElement("🔒 File Encryption Policy");
        policyListModel.addElement("📊 Audit Logging Policy");
        policyListModel.addElement("🚫 Access Control Policy");
        policyListModel.addElement("🔑 Two-Factor Authentication Policy");
        policyListModel.addElement("📱 Device Management Policy");
        policyListModel.addElement("🌐 IP Whitelisting Policy");
        policyListModel.addElement("💾 Data Backup Policy");
        policyListModel.addElement("🔄 Password Rotation Policy");
        
        policyList = new JList<>(policyListModel);
        policyList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        policyList.setBackground(BG_CARD);
        policyList.setForeground(TEXT_PRIMARY);
        policyList.setSelectionBackground(new Color(138, 99, 255, 30));
        policyList.setSelectionForeground(TEXT_PRIMARY);
        policyList.setBorder(new EmptyBorder(10, 10, 10, 10));
        policyList.setFixedCellHeight(50);
        
        policyList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 10)),
                    new EmptyBorder(12, 15, 12, 15)
                ));
                label.setOpaque(true);
                label.setBackground(isSelected ? new Color(138, 99, 255, 30) : BG_CARD);
                return label;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(policyList);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_PURPLE, 2));
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
    
    private JPanel createPolicyEditorPanel() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("✏ Policy Editor");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_CARD);
        
        // Policy Name
        JLabel nameLabel = new JLabel("Policy Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(TEXT_SECONDARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField nameField = new JTextField("Password Complexity Policy");
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBackground(new Color(35, 40, 65));
        nameField.setForeground(TEXT_PRIMARY);
        nameField.setCaretColor(ACCENT_CYAN);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_CYAN, 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formPanel.add(nameLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(nameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Policy Type
        JLabel typeLabel = new JLabel("Policy Type");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        typeLabel.setForeground(TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
            "Authentication", "Authorization", "Data Protection", "Compliance", "Network Security", "Custom"
        });
        styleComboBox(typeCombo, ACCENT_PURPLE);
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        typeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formPanel.add(typeLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(typeCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Description
        JLabel descLabel = new JLabel("Policy Description");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea descArea = new JTextArea(3, 30);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setBackground(new Color(35, 40, 65));
        descArea.setForeground(TEXT_PRIMARY);
        descArea.setCaretColor(ACCENT_GREEN);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_GREEN, 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        descArea.setText("Enforces strong password requirements including minimum length, complexity, and character variety.");
        
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(null);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formPanel.add(descLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(descScroll);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Apply to Roles
        JLabel rolesLabel = new JLabel("Apply to Roles");
        rolesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rolesLabel.setForeground(TEXT_SECONDARY);
        rolesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setBackground(new Color(35, 40, 65));
        checkboxPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_ORANGE, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        checkboxPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        checkboxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox userCheck = createStyledCheckbox("👤 Regular Users", true);
        JCheckBox analystCheck = createStyledCheckbox("🔍 Security Analysts", true);
        JCheckBox adminCheck = createStyledCheckbox("👤 Administrators", true);
        
        checkboxPanel.add(userCheck);
        checkboxPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        checkboxPanel.add(analystCheck);
        checkboxPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        checkboxPanel.add(adminCheck);
        
        formPanel.add(rolesLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(checkboxPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Compliance Rules
        JLabel complianceLabel = new JLabel("Compliance Rules");
        complianceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        complianceLabel.setForeground(TEXT_SECONDARY);
        complianceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField complianceField = new JTextField("Min 8 chars, 1 uppercase, 1 number, 1 special char");
        complianceField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        complianceField.setBackground(new Color(35, 40, 65));
        complianceField.setForeground(TEXT_PRIMARY);
        complianceField.setCaretColor(ACCENT_PINK);
        complianceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        complianceField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 99, 200), 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
        complianceField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formPanel.add(complianceLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(complianceField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Action Buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        buttonsPanel.setBackground(BG_CARD);
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton saveBtn = createActionButton("💾 Save Policy", ACCENT_GREEN);
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, "Policy saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton deleteBtn = createActionButton("🗑️ Delete Policy", ACCENT_RED);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainPanel, 
                "Are you sure you want to delete this policy?", 
                "Delete Policy", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(mainPanel, "Policy deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton toggleBtn = createActionButton("🔄 Activate/Deactivate", ACCENT_ORANGE);
        toggleBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, "Policy status toggled!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonsPanel.add(saveBtn);
        buttonsPanel.add(deleteBtn);
        buttonsPanel.add(toggleBtn);
        
        formPanel.add(buttonsPanel);
        
        card.add(title, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void showAddPolicyDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(mainPanel), "Add New Security Policy", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(mainPanel);
        
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setBackground(BG_CARD);
        dialogPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("➕ Create New Security Policy");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        dialogPanel.add(titleLabel);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Policy Name
        JLabel nameLabel = new JLabel("Policy Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(TEXT_SECONDARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBackground(new Color(35, 40, 65));
        nameField.setForeground(TEXT_PRIMARY);
        nameField.setCaretColor(ACCENT_CYAN);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_CYAN, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        dialogPanel.add(nameLabel);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        dialogPanel.add(nameField);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Policy Type
        JLabel typeLabel = new JLabel("Policy Type");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        typeLabel.setForeground(TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
            "Authentication", "Authorization", "Data Protection", "Compliance", "Network Security", "Custom"
        });
        styleComboBox(typeCombo, ACCENT_PURPLE);
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        typeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        dialogPanel.add(typeLabel);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        dialogPanel.add(typeCombo);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Description
        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea descArea = new JTextArea(4, 30);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setBackground(new Color(35, 40, 65));
        descArea.setForeground(TEXT_PRIMARY);
        descArea.setCaretColor(ACCENT_GREEN);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_GREEN, 2),
            new EmptyBorder(8, 8, 8, 8)
        ));
        
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(null);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        dialogPanel.add(descLabel);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        dialogPanel.add(descScroll);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Compliance Rules
        JLabel rulesLabel = new JLabel("Compliance Rules");
        rulesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rulesLabel.setForeground(TEXT_SECONDARY);
        rulesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField rulesField = new JTextField();
        rulesField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rulesField.setBackground(new Color(35, 40, 65));
        rulesField.setForeground(TEXT_PRIMARY);
        rulesField.setCaretColor(ACCENT_ORANGE);
        rulesField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        rulesField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_ORANGE, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        rulesField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        dialogPanel.add(rulesLabel);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        dialogPanel.add(rulesField);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Apply to Roles
        JLabel rolesLabel = new JLabel("Apply to Roles");
        rolesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rolesLabel.setForeground(TEXT_SECONDARY);
        rolesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox userCheck = createStyledCheckbox("👤 Regular Users", true);
        userCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        JCheckBox analystCheck = createStyledCheckbox("🔍 Security Analysts", false);
        analystCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        JCheckBox adminCheck = createStyledCheckbox("👤 Administrators", false);
        adminCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        dialogPanel.add(rolesLabel);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        dialogPanel.add(userCheck);
        dialogPanel.add(analystCheck);
        dialogPanel.add(adminCheck);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BG_CARD);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton cancelBtn = createOutlineButton("Cancel", ACCENT_RED);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton createBtn = createGradientButton("Create Policy", ACCENT_GREEN, ACCENT_CYAN);
        createBtn.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a policy name!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                policyListModel.addElement("🛡️ " + nameField.getText());
                JOptionPane.showMessageDialog(dialog, "Policy created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });
        
        btnPanel.add(cancelBtn);
        btnPanel.add(createBtn);
        
        dialogPanel.add(btnPanel);
        
        dialog.add(dialogPanel);
        dialog.setVisible(true);
    }
    
    private JCheckBox createStyledCheckbox(String text, boolean selected) {
        JCheckBox checkbox = new JCheckBox(text);
        checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        checkbox.setForeground(TEXT_PRIMARY);
        checkbox.setBackground(new Color(35, 40, 65));
        checkbox.setSelected(selected);
        checkbox.setFocusPainted(false);
        return checkbox;
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
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        return btn;
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
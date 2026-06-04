package com.arion.ui.user;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;

class SelectHashAlgorithmPage {
    
    private static final Color BG_PRIMARY = new Color(25, 28, 45);
    private static final Color BG_CARD = new Color(45, 52, 85);
    private static final Color ACCENT_PURPLE = new Color(138, 99, 255);
    private static final Color ACCENT_CYAN = new Color(0, 230, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 163);
    private static final Color ACCENT_PINK = new Color(255, 99, 200);
    private static final Color ACCENT_ORANGE = new Color(255, 180, 0);
    private static final Color TEXT_PRIMARY = new Color(240, 242, 255);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 200);
    
    private JPanel mainPanel;
    
    public SelectHashAlgorithmPage() {
        createUI();
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    
    private void createUI() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_PRIMARY);
        
        // Algorithm comparison cards
        JPanel cardsPanel = createAlgorithmCards();
        mainPanel.add(cardsPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Files and algorithm assignment
        JPanel filesPanel = createFilesManagementPanel();
        mainPanel.add(filesPanel);
    }
    
    private JPanel createAlgorithmCards() {
        JPanel container = new JPanel(new GridLayout(2, 2, 20, 20));
        container.setBackground(BG_PRIMARY);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));
        
        container.add(createAlgorithmCard("SHA-256", "Recommended", "Fast & Secure", "256-bit", "4.5 GB/s", ACCENT_GREEN));
        container.add(createAlgorithmCard("SHA-512", "Most Secure", "Maximum Security", "512-bit", "3.2 GB/s", ACCENT_PURPLE));
        container.add(createAlgorithmCard("SHA-1", "Legacy", "Moderate Security", "160-bit", "5.1 GB/s", ACCENT_ORANGE));
        container.add(createAlgorithmCard("MD5", "Not Recommended", "Vulnerable", "128-bit", "6.7 GB/s", new Color(255, 70, 100)));
        
        return container;
    }
    
    private JPanel createAlgorithmCard(String name, String tag, String desc, String bits, String speed, Color color) {
        JPanel card = new GlowingCard(20, color);
        card.setLayout(new BorderLayout(0, 15));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(TEXT_PRIMARY);
        
        JLabel tagLabel = new JLabel(tag);
        tagLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tagLabel.setForeground(color);
        tagLabel.setOpaque(true);
        tagLabel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        tagLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            new EmptyBorder(4, 10, 4, 10)
        ));
        
        headerPanel.add(nameLabel, BorderLayout.WEST);
        headerPanel.add(tagLabel, BorderLayout.EAST);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);
        
        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        statsPanel.setBackground(BG_CARD);
        statsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        statsPanel.add(createStatRow("Hash Size:", bits, color));
        statsPanel.add(createStatRow("Speed:", speed, color));
        
        contentPanel.add(descLabel);
        contentPanel.add(statsPanel);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createStatRow(String label, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(55, 62, 95));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(TEXT_SECONDARY);
        
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valueText.setForeground(color);
        
        panel.add(labelText, BorderLayout.WEST);
        panel.add(valueText, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createFilesManagementPanel() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("⚙️ Manage File Algorithms");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setBackground(BG_CARD);
        
        JComboBox<String> algorithmSelect = new JComboBox<>(new String[]{"SHA-256", "SHA-512", "SHA-1", "MD5"});
        algorithmSelect.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        algorithmSelect.setBackground(new Color(35, 40, 65));
        algorithmSelect.setForeground(TEXT_PRIMARY);
        algorithmSelect.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_CYAN, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        JButton applyBtn = createGradientButton("Apply to Selected", ACCENT_GREEN, ACCENT_CYAN);
        JButton defaultBtn = createOutlineButton("Set as Default", ACCENT_PURPLE);
        
        controlsPanel.add(algorithmSelect);
        controlsPanel.add(applyBtn);
        controlsPanel.add(defaultBtn);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(controlsPanel, BorderLayout.EAST);
        
        String[] columns = {"Select", "File Name", "Current Algorithm", "Last Modified", "Status"};
        Object[][] data = {
            {false, "quarterly_report.pdf", "SHA-256", "2025-11-18", "Active"},
            {false, "contract_v2.docx", "SHA-512", "2025-11-17", "Active"},
            {false, "financial_data.xlsx", "SHA-256", "2025-11-16", "Active"},
            {false, "backup_archive.zip", "SHA-1", "2025-11-15", "Active"}
        };
        
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
        };
        
        JTable table = new JTable(model);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(138, 99, 255, 30));
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(35, 40, 65));
        header.setForeground(ACCENT_PURPLE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_PURPLE));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        
        // Neon scrollbar
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
        btn.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
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
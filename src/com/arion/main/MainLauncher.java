package com.arion.main;

import com.arion.ui.common.LoginPage;
import com.arion.ui.analyst.AnalystDashboard;
import com.arion.ui.admin.AdminDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MainLauncher {
    
    // Color scheme
    private static final Color BG_PRIMARY = new Color(35, 39, 65);
    private static final Color BG_SECONDARY = new Color(45, 51, 89);
    private static final Color BG_DARKER = new Color(25, 28, 48);
    private static final Color ACCENT_PURPLE = new Color(124, 110, 229);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(169, 176, 206);
    private static final Color NEON_CYAN = new Color(0, 255, 255);
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Show role selection first
            showRoleSelection();
        });
    }
    
    private static void showRoleSelection() {
        JFrame roleFrame = new JFrame("ARION - Select Role");
        roleFrame.setSize(540, 460);
        roleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        roleFrame.setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BG_PRIMARY);
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        
        // Title
        JLabel titleLabel = new JLabel("ARION");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(ACCENT_PURPLE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("File Integrity & Malware Management");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);
        
        // Select Role Label
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 10, 15, 10);
        JLabel selectLabel = new JLabel("Select User Role:");
        selectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectLabel.setForeground(TEXT_PRIMARY);
        mainPanel.add(selectLabel, gbc);
        
        // Role Dropdown with neon styling
        String[] roles = {"Regular User", "Security Analyst", "Administrator"};
        NeonStyledComboBox<String> roleDropdown = new NeonStyledComboBox<>(roles, "Select Role", NEON_CYAN);
        roleDropdown.setPreferredSize(new Dimension(300, 48));
        
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(roleDropdown, gbc);
        
        // Continue Button
        JButton continueButton = createStyledButton("Continue");
        continueButton.setPreferredSize(new Dimension(300, 48));
        gbc.gridy = 4;
        gbc.insets = new Insets(24, 10, 10, 10);
        mainPanel.add(continueButton, gbc);
        
        continueButton.addActionListener(e -> {
            Object sel = roleDropdown.getSelectedItem();
            String role = sel == null ? null : sel.toString();
            
            if (role == null) {
                JOptionPane.showMessageDialog(roleFrame, "Please select a role", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            roleFrame.dispose();
            
            // Open appropriate dashboard or login based on role
            switch (role) {
                case "Administrator":
                    new AdminDashboard();
                    break;
                    
                case "Security Analyst":
                    new AnalystDashboard();
                    break;
                    
                case "Regular User":
                default:
                    // Open login page for regular users
                    new LoginPage();
                    break;
            }
        });
        
        roleFrame.add(mainPanel);
        roleFrame.setVisible(true);
    }
    
    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_PURPLE, getWidth(), getHeight(), new Color(124, 160, 255));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        return button;
    }
    
    // ============================
    // Neon Styled ComboBox Class
    // ============================
    private static class NeonStyledComboBox<E> extends JComboBox<E> {
        
        private String placeholderText;
        private Color neonColor;
        
        public NeonStyledComboBox(E[] items, String placeholder, Color neonColor) {
            super(items);
            this.placeholderText = placeholder;
            this.neonColor = neonColor != null ? neonColor : NEON_CYAN;
            
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(TEXT_PRIMARY);
            setBackground(BG_DARKER);
            setBorder(new EmptyBorder(8, 12, 8, 12));
            
            setUI(new NeonComboBoxUI());
            setRenderer(new NeonCellRenderer());
            setSelectedIndex(0);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            RoundRectangle2D bg = new RoundRectangle2D.Float(0, 0, w, h, 8, 8);
            g2.setColor(BG_DARKER);
            g2.fill(bg);
            
            for (int i = 6; i >= 1; i--) {
                int alpha = Math.max(15, 60 - i * 10);
                Color glow = new Color(neonColor.getRed(), neonColor.getGreen(), neonColor.getBlue(), alpha);
                g2.setStroke(new BasicStroke(i));
                g2.setColor(glow);
                g2.draw(bg);
            }
            
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(neonColor);
            g2.draw(bg);
            
            g2.dispose();
            super.paintComponent(g);
        }
        
        private class NeonComboBoxUI extends BasicComboBoxUI {
            
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        g2.setColor(BG_DARKER);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        
                        int w = getWidth();
                        int h = getHeight();
                        int[] xPoints = {w/2 - 5, w/2 + 5, w/2};
                        int[] yPoints = {h/2 - 3, h/2 - 3, h/2 + 3};
                        
                        g2.setColor(neonColor);
                        g2.fillPolygon(xPoints, yPoints, 3);
                        g2.dispose();
                    }
                };
                
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setPreferredSize(new Dimension(30, 30));
                return button;
            }
            
            @Override
            protected ComboPopup createPopup() {
                return new BasicComboPopup(comboBox) {
                    @Override
                    protected void configurePopup() {
                        super.configurePopup();
                        setBackground(BG_DARKER);
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(neonColor, 2),
                            BorderFactory.createEmptyBorder(4, 4, 4, 4)
                        ));
                        setOpaque(true);
                    }
                    
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scroller = super.createScroller();
                        scroller.setOpaque(true);
                        scroller.setBackground(BG_DARKER);
                        scroller.getViewport().setOpaque(true);
                        scroller.getViewport().setBackground(BG_DARKER);
                        scroller.setBorder(BorderFactory.createEmptyBorder());
                        return scroller;
                    }
                    
                    @Override
                    protected JList createList() {
                        JList list = super.createList();
                        list.setOpaque(true);
                        list.setBackground(BG_DARKER);
                        list.setSelectionBackground(BG_SECONDARY);
                        list.setSelectionForeground(TEXT_PRIMARY);
                        list.setForeground(TEXT_PRIMARY);
                        return list;
                    }
                };
            }
        }
        
        private class NeonCellRenderer extends DefaultListCellRenderer {
            
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                
                JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        int w = getWidth();
                        int h = getHeight();
                        
                        g2.setColor(BG_DARKER);
                        g2.fillRect(0, 0, w, h);
                        
                        if (index >= 0) {
                            if (isSelected) {
                                g2.setColor(BG_SECONDARY);
                                g2.fillRoundRect(2, 2, w - 4, h - 4, 6, 6);
                                
                                g2.setStroke(new BasicStroke(1.5f));
                                g2.setColor(new Color(neonColor.getRed(), neonColor.getGreen(), 
                                                      neonColor.getBlue(), 80));
                                g2.drawRoundRect(2, 2, w - 4, h - 4, 6, 6);
                            } else {
                                g2.setColor(BG_DARKER);
                                g2.fillRoundRect(2, 2, w - 4, h - 4, 6, 6);
                            }
                        }
                        
                        g2.dispose();
                    }
                };
                
                panel.setLayout(new BorderLayout());
                panel.setOpaque(true);
                panel.setBackground(BG_DARKER);
                panel.setBorder(new EmptyBorder(8, 12, 8, 12));
                
                JLabel label = new JLabel();
                label.setOpaque(false);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                if (value != null) {
                    label.setText(value.toString());
                    label.setForeground(TEXT_PRIMARY);
                }
                
                panel.add(label, BorderLayout.WEST);
                
                return panel;
            }
        }
    }
}
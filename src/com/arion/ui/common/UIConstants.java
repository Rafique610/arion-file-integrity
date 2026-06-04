package com.arion.ui.common;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class UIConstants {
    
    // Color scheme
    public static final Color BG_PRIMARY = new Color(35, 39, 65);
    public static final Color BG_SECONDARY = new Color(45, 51, 89);
    public static final Color BG_CARD = new Color(50, 56, 98);
    public static final Color ACCENT_PURPLE = new Color(124, 110, 229);
    public static final Color ACCENT_CYAN = new Color(0, 230, 255);
    public static final Color ACCENT_GREEN = new Color(76, 217, 100);
    public static final Color ACCENT_RED = new Color(255, 69, 96);
    public static final Color ACCENT_YELLOW = new Color(255, 204, 0);
    public static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    public static final Color TEXT_SECONDARY = new Color(169, 176, 206);
    
    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_NORMAL);
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(ACCENT_PURPLE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(144, 130, 249));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_PURPLE);
            }
        });
        
        return button;
    }
    
    public static JButton createDangerButton(String text) {
        JButton button = createStyledButton(text);
        button.setBackground(ACCENT_RED);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 89, 116));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_RED);
            }
        });
        return button;
    }
    
    public static JButton createSuccessButton(String text) {
        JButton button = createStyledButton(text);
        button.setBackground(ACCENT_GREEN);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(96, 237, 120));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_GREEN);
            }
        });
        return button;
    }
    
    public static JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(FONT_NORMAL);
        textField.setForeground(TEXT_PRIMARY);
        textField.setBackground(BG_SECONDARY);
        textField.setCaretColor(TEXT_PRIMARY);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_PURPLE, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setPreferredSize(new Dimension(300, 40));
        return textField;
    }
    
    public static JTextArea createStyledTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setFont(FONT_NORMAL);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setBackground(BG_SECONDARY);
        textArea.setCaretColor(TEXT_PRIMARY);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_PURPLE, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }
    
    /**
     * Creates a styled combo box with cyan border and dark theme
     */
    public static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        styleComboBox(comboBox, ACCENT_CYAN);
        return comboBox;
    }
    
    /**
     * Styles an existing combo box with specified accent color - COMPLETE DARK THEME
     */
    public static void styleComboBox(JComboBox<?> comboBox, Color accentColor) {
        comboBox.setFont(FONT_NORMAL);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBackground(new Color(25, 28, 45));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        comboBox.setPreferredSize(new Dimension(200, 45));
        
        // Custom UI for complete dark theme
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("▼") {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(25, 28, 45));
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.setColor(accentColor);
                        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                        FontMetrics fm = g2.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth("▼")) / 2;
                        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                        g2.drawString("▼", x, y);
                        g2.dispose();
                    }
                };
                button.setForeground(accentColor);
                button.setBackground(new Color(25, 28, 45));
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                button.setPreferredSize(new Dimension(20, 20));
                return button;
            }
            
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scroller = new JScrollPane(list, 
                            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        scroller.getVerticalScrollBar().setUI(createNeonScrollBarUI(accentColor));
                        return scroller;
                    }
                };
                popup.setBorder(BorderFactory.createLineBorder(accentColor, 2));
                return popup;
            }
            
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(25, 28, 45));
                g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                g2.dispose();
            }
            
            @Override
            protected ListCellRenderer<Object> createRenderer() {
                return new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value,
                            int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        setFont(FONT_NORMAL);
                        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                        
                        if (isSelected) {
                            setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), 
                                                   accentColor.getBlue(), 80));
                            setForeground(TEXT_PRIMARY);
                        } else {
                            setBackground(new Color(35, 40, 65));
                            setForeground(TEXT_PRIMARY);
                        }
                        
                        setOpaque(true);
                        return this;
                    }
                };
            }
        });
        
        // Ensure the popup list is also styled
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(FONT_NORMAL);
                setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                
                if (index == -1) {
                    // This is the selected item shown in the combo box itself
                    setBackground(new Color(25, 28, 45));
                    setForeground(TEXT_PRIMARY);
                } else if (isSelected) {
                    setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), 
                                           accentColor.getBlue(), 80));
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(new Color(35, 40, 65));
                    setForeground(TEXT_PRIMARY);
                }
                
                setOpaque(true);
                return this;
            }
        });
    }
    
    /**
     * Creates a custom scrollbar UI with neon styling
     */
    public static javax.swing.plaf.basic.BasicScrollBarUI createNeonScrollBarUI(Color thumbColor) {
        return new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = thumbColor;
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
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, 
                               thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
                g2.dispose();
            }
            
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        };
    }
    
    public static void styleTable(JTable table) {
        table.setFont(FONT_NORMAL);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_SECONDARY);
        table.setSelectionBackground(ACCENT_PURPLE);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Header styling
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setBackground(BG_CARD);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        
        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(BG_SECONDARY);
        centerRenderer.setForeground(TEXT_PRIMARY);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    public static JLabel createNormalLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_NORMAL);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }
    
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(BG_SECONDARY);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_PURPLE, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }
}
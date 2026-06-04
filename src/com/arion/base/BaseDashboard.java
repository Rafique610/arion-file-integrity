package com.arion.base;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;

import com.arion.model.User;


public abstract class BaseDashboard extends JFrame {
    
    // PROTECTED CONSTANTS (Shared by all dashboards) 
    protected static final Color BG_PRIMARY = new Color(25, 28, 45);
    protected static final Color BG_SECONDARY = new Color(35, 40, 65);
    protected static final Color BG_CARD = new Color(45, 52, 85);
    protected static final Color ACCENT_PURPLE = new Color(138, 99, 255);
    protected static final Color ACCENT_CYAN = new Color(0, 230, 255);
    protected static final Color ACCENT_GREEN = new Color(0, 255, 163);
    protected static final Color ACCENT_PINK = new Color(255, 99, 200);
    protected static final Color ACCENT_ORANGE = new Color(255, 165, 0);
    protected static final Color ACCENT_RED = new Color(255, 70, 100);
    protected static final Color TEXT_PRIMARY = new Color(240, 242, 255);
    protected static final Color TEXT_SECONDARY = new Color(150, 160, 200);
    
    // PROTECTED FIELDS (Inherited by all dashboards) 
    protected JPanel mainPanel;
    protected JPanel contentPanel;
    protected JPanel sidebar;
    protected User currentUser;
    protected String activePage = "dashboard";
    
   
    
    protected BaseDashboard(User user) {
        this.currentUser = user;
        
        // Common frame setup
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    
    protected void initializeDashboard() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_PRIMARY);
        
        sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);
        
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(BG_PRIMARY);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel topBar = createTopBar();
        contentPanel.add(topBar, BorderLayout.NORTH);
        
        loadDashboardContent();
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    // ABSTRACT METHODS (Must be implemented by subclasses) 
    
    
    protected abstract JPanel createSidebar();
    
    
    protected abstract JPanel createTopBar();
    
    
    protected abstract void loadDashboardContent();
    
    
    protected abstract String getPageTitle(String page);
    
    
    protected abstract String getIconForAction(String action);
    
    // CONCRETE METHODS (Inherited and shared) 
    
    
    protected void refreshSidebar() {
        mainPanel.remove(sidebar);
        sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    
    protected void addMenuItem(JPanel panel, String text, String action, Color iconColor) {
        JButton btn = createMenuButton(text, action, iconColor);
        btn.addActionListener(e -> switchPage(action));
        panel.add(btn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    
    protected JButton createMenuButton(String text, String action, Color iconColor) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (activePage.equals(action)) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 60),
                        getWidth(), getHeight(), new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 30)
                    );
                    g2.setPaint(gradient);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                    
                    g2.setColor(iconColor);
                    g2.setStroke(new BasicStroke(2f));
                    g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 15, 15));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 15));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        btn.setLayout(new BorderLayout(15, 0));
        btn.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        JPanel iconPanel = new FancyIconPanel(getIconForAction(action), iconColor);
        iconPanel.setPreferredSize(new Dimension(35, 35));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(activePage.equals(action) ? TEXT_PRIMARY : TEXT_SECONDARY);
        
        btn.add(iconPanel, BorderLayout.WEST);
        btn.add(label, BorderLayout.CENTER);
        
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(240, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
    
    
    protected abstract void switchPage(String page);
    
    //SHARED INNER CLASSES 
   
    protected class FancyIconPanel extends JPanel {
        private String iconType;
        private Color iconColor;
        
        public FancyIconPanel(String iconType, Color iconColor) {
            this.iconType = iconType;
            this.iconColor = iconColor;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = Math.min(getWidth(), getHeight());
            
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 40),
                size, size, new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 20)
            );
            g2.setPaint(gradient);
            g2.fillOval(0, 0, size, size);
            
            g2.setColor(new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 60));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(1, 1, size - 2, size - 2);
            
            g2.setColor(iconColor);
            drawIcon(g2, iconType, size);
            
            g2.dispose();
        }
        
        protected void drawIcon(Graphics2D g2, String type, int size) {
            int centerX = size / 2;
            int centerY = size / 2;
            int iconSize = size / 3;
            
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            switch (type) {
                case "DASHBOARD":
                    g2.drawRect(centerX - iconSize, centerY - iconSize, iconSize - 2, iconSize - 2);
                    g2.drawRect(centerX + 2, centerY - iconSize, iconSize - 2, iconSize - 2);
                    g2.drawRect(centerX - iconSize, centerY + 2, iconSize - 2, iconSize - 2);
                    g2.drawRect(centerX + 2, centerY + 2, iconSize - 2, iconSize - 2);
                    break;
                case "SETTINGS":
                    g2.drawOval(centerX - iconSize/2, centerY - iconSize/2, iconSize, iconSize);
                    for (int i = 0; i < 8; i++) {
                        double angle = i * Math.PI / 4;
                        int x1 = centerX + (int)(iconSize * 0.7 * Math.cos(angle));
                        int y1 = centerY + (int)(iconSize * 0.7 * Math.sin(angle));
                        int x2 = centerX + (int)(iconSize * Math.cos(angle));
                        int y2 = centerY + (int)(iconSize * Math.sin(angle));
                        g2.drawLine(x1, y1, x2, y2);
                    }
                    break;
                case "USERS":
                    g2.drawOval(centerX - iconSize/2, centerY - iconSize, iconSize, iconSize);
                    g2.drawArc(centerX - iconSize, centerY, iconSize * 2, iconSize, 0, 180);
                    break;
                case "SHIELD":
                    int[] xShield = {centerX, centerX - iconSize, centerX - iconSize, centerX, centerX + iconSize, centerX + iconSize};
                    int[] yShield = {centerY - iconSize, centerY - iconSize/2, centerY + iconSize/2, centerY + iconSize, centerY + iconSize/2, centerY - iconSize/2};
                    g2.drawPolygon(xShield, yShield, 6);
                    g2.drawLine(centerX, centerY - iconSize/2, centerX, centerY + iconSize/2);
                    break;
                case "ATTACK":
                    g2.drawLine(centerX - iconSize, centerY + iconSize, centerX, centerY - iconSize);
                    g2.drawLine(centerX, centerY - iconSize, centerX + iconSize/2, centerY);
                    g2.drawLine(centerX + iconSize/2, centerY, centerX - iconSize/2, centerY);
                    break;
                case "LOGS":
                    g2.drawRect(centerX - iconSize, centerY - iconSize, iconSize * 2, iconSize * 2);
                    g2.drawLine(centerX - iconSize/2, centerY - iconSize/2, centerX + iconSize/2, centerY - iconSize/2);
                    g2.drawLine(centerX - iconSize/2, centerY, centerX + iconSize/2, centerY);
                    g2.drawLine(centerX - iconSize/2, centerY + iconSize/2, centerX + iconSize/2, centerY + iconSize/2);
                    break;
                case "LOGOUT":
                    g2.drawLine(centerX - iconSize, centerY, centerX + iconSize, centerY);
                    g2.drawLine(centerX + iconSize, centerY, centerX + iconSize/2, centerY - iconSize/2);
                    g2.drawLine(centerX + iconSize, centerY, centerX + iconSize/2, centerY + iconSize/2);
                    g2.drawRect(centerX - iconSize, centerY - iconSize, iconSize, iconSize * 2);
                    break;
                case "ALERT":
                    int[] xTri = {centerX, centerX - iconSize, centerX + iconSize};
                    int[] yTri = {centerY - iconSize, centerY + iconSize, centerY + iconSize};
                    g2.drawPolygon(xTri, yTri, 3);
                    g2.drawLine(centerX, centerY - iconSize/2, centerX, centerY + iconSize/4);
                    g2.fillOval(centerX - 2, centerY + iconSize/2, 4, 4);
                    break;
                case "UPLOAD":
                    g2.drawLine(centerX, centerY + iconSize, centerX, centerY - iconSize);
                    g2.drawLine(centerX, centerY - iconSize, centerX - iconSize/2, centerY - iconSize/2);
                    g2.drawLine(centerX, centerY - iconSize, centerX + iconSize/2, centerY - iconSize/2);
                    g2.drawLine(centerX - iconSize, centerY + iconSize, centerX + iconSize, centerY + iconSize);
                    break;
                case "CLOCK":
                    g2.drawOval(centerX - iconSize, centerY - iconSize, iconSize * 2, iconSize * 2);
                    g2.drawLine(centerX, centerY, centerX, centerY - iconSize/2);
                    g2.drawLine(centerX, centerY, centerX + iconSize/2, centerY);
                    break;
                case "HASH":
                    g2.drawLine(centerX - iconSize/2, centerY - iconSize, centerX - iconSize/2, centerY + iconSize);
                    g2.drawLine(centerX + iconSize/2, centerY - iconSize, centerX + iconSize/2, centerY + iconSize);
                    g2.drawLine(centerX - iconSize, centerY - iconSize/2, centerX + iconSize, centerY - iconSize/2);
                    g2.drawLine(centerX - iconSize, centerY + iconSize/2, centerX + iconSize, centerY + iconSize/2);
                    break;
                case "REPORT":
                    g2.drawLine(centerX - iconSize, centerY - iconSize, centerX - iconSize, centerY + iconSize);
                    g2.drawLine(centerX - iconSize, centerY + iconSize, centerX + iconSize, centerY + iconSize);
                    g2.drawLine(centerX + iconSize, centerY + iconSize, centerX + iconSize, centerY - iconSize/2);
                    g2.drawLine(centerX + iconSize, centerY - iconSize/2, centerX + iconSize/2, centerY - iconSize);
                    g2.drawLine(centerX + iconSize/2, centerY - iconSize, centerX - iconSize, centerY - iconSize);
                    break;
                case "FILE":
                    g2.drawLine(centerX - iconSize/2, centerY - iconSize, centerX - iconSize/2, centerY + iconSize);
                    g2.drawLine(centerX - iconSize/2, centerY + iconSize, centerX + iconSize/2, centerY + iconSize);
                    g2.drawLine(centerX + iconSize/2, centerY + iconSize, centerX + iconSize/2, centerY - iconSize/3);
                    g2.drawLine(centerX + iconSize/2, centerY - iconSize/3, centerX + iconSize/4, centerY - iconSize);
                    g2.drawLine(centerX + iconSize/4, centerY - iconSize, centerX - iconSize/2, centerY - iconSize);
                    break;
                case "LOCK":
                    g2.drawRoundRect(centerX - iconSize/2, centerY, iconSize, iconSize, 5, 5);
                    g2.drawArc(centerX - iconSize/3, centerY - iconSize, iconSize * 2/3, iconSize, 0, 180);
                    g2.fillOval(centerX - 2, centerY + iconSize/3, 4, 4);
                    break;
                case "MONITOR":
                    g2.drawRect(centerX - iconSize, centerY - iconSize/2, iconSize * 2, iconSize);
                    g2.drawLine(centerX - iconSize/2, centerY, centerX + iconSize/2, centerY - iconSize/3);
                    g2.drawLine(centerX, centerY + iconSize/3, centerX + iconSize/2, centerY);
                    break;
                case "EXPORT":
                    g2.drawLine(centerX, centerY - iconSize, centerX, centerY + iconSize);
                    g2.drawLine(centerX, centerY + iconSize, centerX - iconSize/2, centerY + iconSize/2);
                    g2.drawLine(centerX, centerY + iconSize, centerX + iconSize/2, centerY + iconSize/2);
                    g2.drawRect(centerX - iconSize, centerY - iconSize, iconSize * 2, iconSize * 2);
                    break;
            }
        }
    }
    
    /**
     * Animated Icon Panel - shared by all dashboards
     */
    protected class AnimatedIconPanel extends JPanel {
        private String iconType;
        private Color iconColor;
        private float pulsePhase = 0;
        
        public AnimatedIconPanel(String iconType, Color iconColor) {
            this.iconType = iconType;
            this.iconColor = iconColor;
            setOpaque(false);
            
            Timer timer = new Timer(50, e -> {
                pulsePhase += 0.1f;
                if (pulsePhase > Math.PI * 2) pulsePhase = 0;
                repaint();
            });
            timer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = Math.min(getWidth(), getHeight());
            float pulse = (float)(Math.sin(pulsePhase) * 0.2 + 0.8);
            
            int glowSize = (int)(size * 1.2);
            int offset = (glowSize - size) / 2;
            RadialGradientPaint glow = new RadialGradientPaint(
                size / 2f, size / 2f, glowSize / 2f,
                new float[]{0f, 1f},
                new Color[]{
                    new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), (int)(60 * pulse)),
                    new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 0)
                }
            );
            g2.setPaint(glow);
            g2.fillOval(-offset, -offset, glowSize, glowSize);
            
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 50),
                size, size, new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 30)
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, size, size, 15, 15);
            
            g2.setColor(iconColor);
            drawIconShape(g2, iconType, size);
            
            g2.dispose();
        }
        
        protected void drawIconShape(Graphics2D g2, String type, int size) {
            // Delegate to FancyIconPanel's drawIcon method
            new FancyIconPanel(type, iconColor).drawIcon(g2, type, size);
        }
    }
    
    /**
     * Glowing Card - shared by all dashboards
     */
    protected class GlowingCard extends JPanel {
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
    
    /**
     * Rounded Panel - shared by all dashboards
     */
    protected class RoundedPanel extends JPanel {
        private int cornerRadius;
        
        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
        }
        
        @Override
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
package com.arion.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Programmatically renders icons as SVG-style graphics
 * No external files needed - all icons are drawn in code
 */
public class IconRenderer {
    
    // Icon types
    public enum Icon {
        FILE, UPLOAD, SHIELD, USER, SEARCH, BELL, CLOUD, CHART,
        DASHBOARD, CLOCK, HASH, REPORT, LOGOUT, LOCK, ALERT,
        SETTINGS, MONITOR, EXPORT, CHECKMARK, TRASH, COPY, SAVE
    }
    
    /**
     * Get an icon as a JLabel component
     */
    public static JLabel getIconLabel(Icon icon, int size, Color color) {
        ImageIcon imageIcon = getIcon(icon, size, color);
        return new JLabel(imageIcon);
    }
    
    /**
     * Get an icon as an ImageIcon
     */
    public static ImageIcon getIcon(Icon icon, int size, Color color) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        // Anti-aliasing for smooth icons
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        g2.setColor(color);
        g2.setStroke(new BasicStroke(size / 12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int padding = size / 6;
        int drawSize = size - (padding * 2);
        
        switch (icon) {
            case FILE:
                drawFileIcon(g2, padding, drawSize);
                break;
            case UPLOAD:
                drawUploadIcon(g2, padding, drawSize);
                break;
            case SHIELD:
                drawShieldIcon(g2, padding, drawSize);
                break;
            case USER:
                drawUserIcon(g2, padding, drawSize);
                break;
            case SEARCH:
                drawSearchIcon(g2, padding, drawSize);
                break;
            case BELL:
                drawBellIcon(g2, padding, drawSize);
                break;
            case CLOUD:
                drawCloudIcon(g2, padding, drawSize);
                break;
            case CHART:
                drawChartIcon(g2, padding, drawSize);
                break;
            case DASHBOARD:
                drawDashboardIcon(g2, padding, drawSize);
                break;
            case CLOCK:
                drawClockIcon(g2, padding, drawSize);
                break;
            case HASH:
                drawHashIcon(g2, padding, drawSize);
                break;
            case REPORT:
                drawReportIcon(g2, padding, drawSize);
                break;
            case LOGOUT:
                drawLogoutIcon(g2, padding, drawSize);
                break;
            case LOCK:
                drawLockIcon(g2, padding, drawSize);
                break;
            case ALERT:
                drawAlertIcon(g2, padding, drawSize);
                break;
            case SETTINGS:
                drawSettingsIcon(g2, padding, drawSize);
                break;
            case MONITOR:
                drawMonitorIcon(g2, padding, drawSize);
                break;
            case EXPORT:
                drawExportIcon(g2, padding, drawSize);
                break;
            case CHECKMARK:
                drawCheckmarkIcon(g2, padding, drawSize);
                break;
            case TRASH:
                drawTrashIcon(g2, padding, drawSize);
                break;
            case COPY:
                drawCopyIcon(g2, padding, drawSize);
                break;
            case SAVE:
                drawSaveIcon(g2, padding, drawSize);
                break;
        }
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    // ==================== ICON DRAWING METHODS ====================
    
    private static void drawFileIcon(Graphics2D g2, int p, int s) {
        int w = (int)(s * 0.6);
        int h = s;
        int x = p + (s - w) / 2;
        int y = p;
        int corner = w / 3;
        
        GeneralPath path = new GeneralPath();
        path.moveTo(x, y);
        path.lineTo(x + w - corner, y);
        path.lineTo(x + w, y + corner);
        path.lineTo(x + w, y + h);
        path.lineTo(x, y + h);
        path.closePath();
        g2.draw(path);
        
        // Corner fold
        g2.drawLine(x + w - corner, y, x + w - corner, y + corner);
        g2.drawLine(x + w - corner, y + corner, x + w, y + corner);
        
        // Lines
        g2.drawLine(x + w/4, y + h/2, x + w*3/4, y + h/2);
        g2.drawLine(x + w/4, y + h*2/3, x + w*3/4, y + h*2/3);
    }
    
    private static void drawUploadIcon(Graphics2D g2, int p, int s) {
        int centerX = p + s / 2;
        int centerY = p + s / 2;
        int arrowHeight = (int)(s * 0.6);
        int arrowWidth = (int)(s * 0.4);
        
        // Arrow shaft
        g2.drawLine(centerX, centerY + arrowHeight/2, centerX, centerY - arrowHeight/2);
        
        // Arrow head
        g2.drawLine(centerX, centerY - arrowHeight/2, centerX - arrowWidth/2, centerY - arrowHeight/4);
        g2.drawLine(centerX, centerY - arrowHeight/2, centerX + arrowWidth/2, centerY - arrowHeight/4);
        
        // Base line
        g2.drawLine(centerX - arrowWidth/2, centerY + arrowHeight/2, 
                    centerX + arrowWidth/2, centerY + arrowHeight/2);
    }
    
    private static void drawShieldIcon(Graphics2D g2, int p, int s) {
        int centerX = p + s / 2;
        int top = p;
        int bottom = p + s;
        int width = (int)(s * 0.7);
        
        GeneralPath shield = new GeneralPath();
        shield.moveTo(centerX, top);
        shield.lineTo(centerX + width/2, top + s/4);
        shield.lineTo(centerX + width/2, top + s/2);
        shield.lineTo(centerX, bottom);
        shield.lineTo(centerX - width/2, top + s/2);
        shield.lineTo(centerX - width/2, top + s/4);
        shield.closePath();
        g2.draw(shield);
        
        // Checkmark
        int cx = centerX - width/6;
        int cy = top + s/2;
        g2.drawLine(cx, cy, cx + width/6, cy + width/6);
        g2.drawLine(cx + width/6, cy + width/6, cx + width/3, cy - width/6);
    }
    
    private static void drawUserIcon(Graphics2D g2, int p, int s) {
        int centerX = p + s / 2;
        int centerY = p + s / 2;
        int headRadius = s / 4;
        
        // Head
        g2.drawOval(centerX - headRadius, centerY - s/3, headRadius * 2, headRadius * 2);
        
        // Body (arc)
        g2.drawArc(centerX - s/3, centerY + s/6, s*2/3, s/2, 0, 180);
    }
    
    private static void drawSearchIcon(Graphics2D g2, int p, int s) {
        int circleSize = (int)(s * 0.6);
        int circleX = p + s/6;
        int circleY = p + s/6;
        
        // Magnifying glass circle
        g2.drawOval(circleX, circleY, circleSize, circleSize);
        
        // Handle
        int handleStartX = circleX + circleSize;
        int handleStartY = circleY + circleSize;
        int handleEndX = p + s;
        int handleEndY = p + s;
        g2.drawLine(handleStartX - s/8, handleStartY - s/8, handleEndX - s/8, handleEndY - s/8);
    }
    
    private static void drawBellIcon(Graphics2D g2, int p, int s) {
        int centerX = p + s / 2;
        int top = p + s / 4;
        int bottom = p + s * 3/4;
        int width = (int)(s * 0.6);
        
        // Bell body
        GeneralPath bell = new GeneralPath();
        bell.moveTo(centerX - width/2, bottom);
        bell.curveTo(centerX - width/2, top + s/4, centerX - width/4, top, centerX, top);
        bell.curveTo(centerX + width/4, top, centerX + width/2, top + s/4, centerX + width/2, bottom);
        g2.draw(bell);
        
        // Bottom line
        g2.drawLine(centerX - width/2, bottom, centerX + width/2, bottom);
        
        // Clapper
        g2.drawLine(centerX, top, centerX, top - s/8);
        g2.fillOval(centerX - s/16, top - s/8 - s/16, s/8, s/8);
    }
    
    private static void drawCloudIcon(Graphics2D g2, int p, int s) {
        int y = p + s / 2;
        
        // Main cloud shape using arcs
        g2.drawArc(p + s/6, y - s/3, s/3, s/2, 90, 180);
        g2.drawArc(p + s/3, y - s/2, s/2, s/2, 0, 180);
        g2.drawArc(p + s/2, y - s/3, s/3, s/2, 270, 180);
        g2.drawLine(p + s/6, y + s/6, p + s*5/6, y + s/6);
    }
    
    private static void drawChartIcon(Graphics2D g2, int p, int s) {
        int bottom = p + s;
        int left = p;
        
        // Axes
        g2.drawLine(left, bottom, left, p);
        g2.drawLine(left, bottom, p + s, bottom);
        
        // Bars
        g2.fillRect(left + s/4, bottom - s/3, s/8, s/3);
        g2.fillRect(left + s/2, bottom - s*2/3, s/8, s*2/3);
        g2.fillRect(left + s*3/4, bottom - s/2, s/8, s/2);
    }
    
    private static void drawDashboardIcon(Graphics2D g2, int p, int s) {
        int gap = s / 8;
        int boxSize = (s - gap) / 2;
        
        g2.drawRect(p, p, boxSize, boxSize);
        g2.drawRect(p + boxSize + gap, p, boxSize, boxSize);
        g2.drawRect(p, p + boxSize + gap, boxSize, boxSize);
        g2.drawRect(p + boxSize + gap, p + boxSize + gap, boxSize, boxSize);
    }
    
    private static void drawClockIcon(Graphics2D g2, int p, int s) {
        int centerX = p + s / 2;
        int centerY = p + s / 2;
        int radius = s / 2;
        
        g2.drawOval(p, p, s, s);
        
        // Hour hand
        g2.drawLine(centerX, centerY, centerX, centerY - radius/2);
        
        // Minute hand
        g2.drawLine(centerX, centerY, centerX + radius/2, centerY);
    }
    
    private static void drawHashIcon(Graphics2D g2, int p, int s) {
        int spacing = s / 3;
        
        // Vertical lines
        g2.drawLine(p + spacing, p, p + spacing, p + s);
        g2.drawLine(p + spacing * 2, p, p + spacing * 2, p + s);
        
        // Horizontal lines
        g2.drawLine(p, p + spacing, p + s, p + spacing);
        g2.drawLine(p, p + spacing * 2, p + s, p + spacing * 2);
    }
    
    private static void drawReportIcon(Graphics2D g2, int p, int s) {
        drawFileIcon(g2, p, s);
        
        // Add chart on top
        int chartP = p + s / 4;
        int chartS = s / 2;
        g2.drawLine(chartP, chartP + chartS, chartP, chartP);
        g2.drawLine(chartP, chartP + chartS, chartP + chartS, chartP + chartS);
    }
    
    private static void drawLogoutIcon(Graphics2D g2, int p, int s) {
        int centerY = p + s / 2;
        int arrowX = p + s * 2/3;
        
        // Arrow
        g2.drawLine(p, centerY, arrowX, centerY);
        g2.drawLine(arrowX, centerY, arrowX - s/4, centerY - s/4);
        g2.drawLine(arrowX, centerY, arrowX - s/4, centerY + s/4);
        
        // Door frame
        g2.drawRect(p, p, s/2, s);
    }
    
    private static void drawLockIcon(Graphics2D g2, int p, int s) {
        int centerX = p + s / 2;
        int lockTop = p + s / 2;
        int lockHeight = s / 2;
        int lockWidth = (int)(s * 0.6);
        
        // Lock body
        g2.drawRoundRect(centerX - lockWidth/2, lockTop, lockWidth, lockHeight, s/8, s/8);
        
        // Shackle
        g2.drawArc(centerX - lockWidth/3, p, lockWidth*2/3, s/2, 0, 180);
        
        // Keyhole
        g2.fillOval(centerX - s/12, lockTop + lockHeight/3, s/6, s/6);
    }
    
    private static void drawAlertIcon(Graphics2D g2, int p, int s) {
        int centerX = p + s / 2;
        int top = p;
        int bottom = p + s;
        int width = s;
        
        // Triangle
        GeneralPath triangle = new GeneralPath();
        triangle.moveTo(centerX, top);
        triangle.lineTo(p, bottom);
        triangle.lineTo(p + width, bottom);
        triangle.closePath();
        g2.draw(triangle);
        
        // Exclamation mark
        g2.drawLine(centerX, top + s/3, centerX, top + s*2/3);
        g2.fillOval(centerX - s/16, bottom - s/4, s/8, s/8);
    }
    
    private static void drawSettingsIcon(Graphics2D g2, int p, int s) {
        int centerX = p + s / 2;
        int centerY = p + s / 2;
        int outerRadius = s / 2;
        int innerRadius = s / 4;
        
        // Center circle
        g2.drawOval(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2);
        
        // Gear teeth
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int x1 = centerX + (int)(innerRadius * 0.9 * Math.cos(angle));
            int y1 = centerY + (int)(innerRadius * 0.9 * Math.sin(angle));
            int x2 = centerX + (int)(outerRadius * Math.cos(angle));
            int y2 = centerY + (int)(outerRadius * Math.sin(angle));
            g2.drawLine(x1, y1, x2, y2);
        }
    }
    
    private static void drawMonitorIcon(Graphics2D g2, int p, int s) {
        int monitorWidth = (int)(s * 0.8);
        int monitorHeight = (int)(s * 0.6);
        int x = p + (s - monitorWidth) / 2;
        int y = p;
        
        // Screen
        g2.drawRect(x, y, monitorWidth, monitorHeight);
        
        // Stand
        int standTop = y + monitorHeight;
        g2.drawLine(x + monitorWidth/2, standTop, x + monitorWidth/2, standTop + s/6);
        g2.drawLine(x + monitorWidth/4, standTop + s/6, x + monitorWidth*3/4, standTop + s/6);
    }
    
    private static void drawExportIcon(Graphics2D g2, int p, int s) {
        int centerX = p + s / 2;
        int bottom = p + s;
        int arrowHeight = (int)(s * 0.6);
        int arrowWidth = (int)(s * 0.4);
        
        // Arrow pointing down
        g2.drawLine(centerX, p, centerX, p + arrowHeight);
        g2.drawLine(centerX, p + arrowHeight, centerX - arrowWidth/2, p + arrowHeight - arrowWidth/3);
        g2.drawLine(centerX, p + arrowHeight, centerX + arrowWidth/2, p + arrowHeight - arrowWidth/3);
        
        // Box
        g2.drawRect(p, bottom - s/3, s, s/3);
    }
    
    private static void drawCheckmarkIcon(Graphics2D g2, int p, int s) {
        int startX = p + s / 4;
        int midX = p + s / 2;
        int endX = p + s;
        int midY = p + s * 2/3;
        int topY = p + s / 4;
        
        g2.drawLine(startX, midY - s/6, midX, midY);
        g2.drawLine(midX, midY, endX, topY);
    }
    
    private static void drawTrashIcon(Graphics2D g2, int p, int s) {
        int width = (int)(s * 0.7);
        int x = p + (s - width) / 2;
        int topY = p + s / 4;
        int height = (int)(s * 0.6);
        
        // Lid
        g2.drawLine(x - s/8, topY, x + width + s/8, topY);
        g2.drawLine(x + width/3, topY - s/8, x + width*2/3, topY - s/8);
        
        // Body
        GeneralPath body = new GeneralPath();
        body.moveTo(x, topY);
        body.lineTo(x + width, topY);
        body.lineTo(x + width - s/8, topY + height);
        body.lineTo(x + s/8, topY + height);
        body.closePath();
        g2.draw(body);
        
        // Lines
        g2.drawLine(x + width/3, topY + s/8, x + width/3, topY + height - s/8);
        g2.drawLine(x + width*2/3, topY + s/8, x + width*2/3, topY + height - s/8);
    }
    
    private static void drawCopyIcon(Graphics2D g2, int p, int s) {
        int offset = s / 4;
        int size = (int)(s * 0.6);
        
        // Back rectangle
        g2.drawRect(p + offset, p, size, size);
        
        // Front rectangle
        g2.fillRect(p, p + offset, size, size);
        g2.setColor(Color.WHITE);
        g2.drawRect(p, p + offset, size, size);
    }
    
    private static void drawSaveIcon(Graphics2D g2, int p, int s) {
        // Floppy disk
        g2.drawRect(p, p, s, s);
        
        // Shutter
        g2.fillRect(p, p, s, s/3);
        
        // Label area
        g2.drawRect(p + s/4, p + s/3, s/2, s/3);
    }
}
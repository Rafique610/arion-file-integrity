package com.arion.ui.user;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.arion.service.FileUploadService;
import com.arion.service.FileUploadService.FileUploadResult;
import javax.swing.SwingWorker;

import com.arion.ui.common.IconRenderer;
import com.arion.ui.common.UIConstants;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.File;

public class UploadFilePage {
    
    private static final Color BG_PRIMARY = new Color(25, 28, 45);
    private static final Color BG_CARD = new Color(45, 52, 85);
    private static final Color ACCENT_PURPLE = new Color(138, 99, 255);
    private static final Color ACCENT_CYAN = new Color(0, 230, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 163);
    private static final Color ACCENT_PINK = new Color(255, 99, 200);
    private static final Color TEXT_PRIMARY = new Color(240, 242, 255);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 200);
    
    private JPanel mainPanel;
    private JLabel fileNameLabel;
    private JLabel fileSizeLabel;
    private JComboBox<String> algorithmComboBox;
    private JTextArea hashOutputArea;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private File selectedFile;
    private FileUploadService fileUploadService;
    private int currentUserId = 1;
    private Runnable onUploadSuccess; // Callback for dashboard refresh
    
    public UploadFilePage() {
        this.fileUploadService = new FileUploadService(); // ADD this line
        createUI();
    }
 // Optional: Constructor with user ID
    public UploadFilePage(int userId) {
        this.currentUserId = userId;
        this.fileUploadService = new FileUploadService();
        createUI();
    }
 // Constructor with user ID and refresh callback
    public UploadFilePage(int userId, Runnable onUploadSuccess) {
        this.currentUserId = userId;
        this.onUploadSuccess = onUploadSuccess;
        this.fileUploadService = new FileUploadService();
        createUI();
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    
    private void createUI() {
        // Create scrollable main panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_PRIMARY);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // File Upload Card with drag-drop area
        JPanel uploadCard = createUploadCard();
        contentPanel.add(uploadCard);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Bottom section with algorithm selection and results
        JPanel bottomSection = new JPanel(new GridLayout(1, 2, 25, 0));
        bottomSection.setBackground(BG_PRIMARY);
        bottomSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        
        JPanel algorithmCard = createAlgorithmCard();
        JPanel resultCard = createResultCard();
        
        bottomSection.add(algorithmCard);
        bottomSection.add(resultCard);
        
        contentPanel.add(bottomSection);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(UIConstants.createNeonScrollBarUI(ACCENT_CYAN));
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_PRIMARY);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createUploadCard() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        
        // Drag and drop area
        JPanel dropZone = new DragDropPanel();
        dropZone.setPreferredSize(new Dimension(600, 180));
        
        // File info panel
        JPanel fileInfoPanel = new JPanel();
        fileInfoPanel.setLayout(new BoxLayout(fileInfoPanel, BoxLayout.Y_AXIS));
        fileInfoPanel.setBackground(BG_CARD);
        
        fileNameLabel = new JLabel("No file selected");
        fileNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        fileNameLabel.setForeground(TEXT_SECONDARY);
        fileNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        fileSizeLabel = new JLabel("");
        fileSizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fileSizeLabel.setForeground(TEXT_SECONDARY);
        fileSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        fileInfoPanel.add(fileNameLabel);
        fileInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        fileInfoPanel.add(fileSizeLabel);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BG_CARD);
        
        JButton browseBtn = createGradientButton("Browse Files", ACCENT_CYAN, ACCENT_PURPLE);
        browseBtn.addActionListener(e -> browseFile());
        
        JButton uploadBtn = createGradientButton("Upload & Hash", ACCENT_GREEN, ACCENT_CYAN);
        uploadBtn.addActionListener(e -> uploadFile());
        
        JButton clearBtn = createOutlineButton("Clear", ACCENT_PINK);
        clearBtn.addActionListener(e -> clearSelection());
        
        buttonPanel.add(browseBtn);
        buttonPanel.add(uploadBtn);
        buttonPanel.add(clearBtn);
        
        card.add(dropZone, BorderLayout.NORTH);
        card.add(fileInfoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createAlgorithmCard() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("⚙️ Hash Algorithm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);
        
        // Algorithm selection with styled dropdown
        String[] algorithms = {
            "SHA-256 (Recommended)",
            "SHA-512 (Most Secure)",
            "SHA-1 (Legacy)",
            "MD5 (Not Recommended)"
        };
        
        algorithmComboBox = new JComboBox<>(algorithms);
        UIConstants.styleComboBox(algorithmComboBox, ACCENT_CYAN);
        algorithmComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        contentPanel.add(algorithmComboBox);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Algorithm info cards
        JPanel infoGrid = new JPanel(new GridLayout(4, 1, 0, 10));
        infoGrid.setBackground(BG_CARD);
        infoGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        
        infoGrid.add(createAlgorithmInfoCard("SHA-256", "Fast & Secure", "General Purpose", ACCENT_GREEN));
        infoGrid.add(createAlgorithmInfoCard("SHA-512", "Maximum Security", "Sensitive Data", ACCENT_PURPLE));
        infoGrid.add(createAlgorithmInfoCard("SHA-1", "Moderate Speed", "Legacy Systems", new Color(255, 180, 0)));
        infoGrid.add(createAlgorithmInfoCard("MD5", "Fastest", "Not Secure", new Color(255, 70, 100)));
        
        contentPanel.add(infoGrid);
        
        card.add(title, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createAlgorithmInfoCard(String name, String speed, String use, Color color) {
        JPanel card = new RoundedPanel(12);
        card.setLayout(new BorderLayout(15, 0));
        card.setBackground(new Color(55, 62, 95));
        card.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        // Icon
        JPanel iconPanel = new CircleIconPanel(name.substring(0, 1), color);
        iconPanel.setPreferredSize(new Dimension(40, 40));
        
        // Text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(55, 62, 95));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel detailLabel = new JLabel(speed + " • " + use);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detailLabel.setForeground(TEXT_SECONDARY);
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(nameLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(detailLabel);
        
        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createResultCard() {
        JPanel card = new GlowingCard(25, ACCENT_GREEN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("🔐 Generated Hash");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);
        
        // Hash output area
        hashOutputArea = new JTextArea(6, 30);
        hashOutputArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        hashOutputArea.setBackground(new Color(35, 40, 65));
        hashOutputArea.setForeground(ACCENT_GREEN);
        hashOutputArea.setCaretColor(ACCENT_GREEN);
        hashOutputArea.setLineWrap(true);
        hashOutputArea.setWrapStyleWord(true);
        hashOutputArea.setEditable(false);
        hashOutputArea.setText("Hash will appear here after upload...");
        hashOutputArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_GREEN, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JScrollPane scrollPane = new JScrollPane(hashOutputArea);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(35, 40, 65));
        scrollPane.getVerticalScrollBar().setUI(UIConstants.createNeonScrollBarUI(ACCENT_GREEN));
        
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(BG_CARD);
        
        JButton copyBtn = createOutlineButton("📋 Copy Hash", ACCENT_CYAN);
        copyBtn.addActionListener(e -> copyHash());
        
        JButton saveBtn = createOutlineButton("💾 Save to File", ACCENT_PURPLE);
        saveBtn.addActionListener(e -> saveHash());
        
        actionPanel.add(copyBtn);
        actionPanel.add(saveBtn);
        
        contentPanel.add(actionPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        progressBar.setForeground(ACCENT_GREEN);
        progressBar.setBackground(new Color(35, 40, 65));
        progressBar.setBorder(new EmptyBorder(10, 0, 10, 0));
        progressBar.setValue(0);
        progressBar.setVisible(false);
        
        statusLabel = new JLabel("Ready to upload");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        contentPanel.add(progressBar);
        contentPanel.add(statusLabel);
        
        card.add(title, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
 // REPLACE your current browseFile() method with this:

    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        
        // Option 1: Accept ALL files (remove the filter entirely)
        fileChooser.setAcceptAllFileFilterUsed(true);
        
        // Option 2: Or set specific filters if you want
        // fileChooser.addChoosableFileFilter(
        //     new FileNameExtensionFilter("Documents", "pdf", "doc", "docx", "txt")
        // );
        // fileChooser.addChoosableFileFilter(
        //     new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif")
        // );
        
        // Set starting directory (optional - defaults to user's home)
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        
        // Allow only files (not directories)
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int result = fileChooser.showOpenDialog(mainPanel);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            
            // Verify file exists and is readable
            if (selectedFile.exists() && selectedFile.canRead()) {
                updateFileInfo();
            } else {
                statusLabel.setText("⚠️ Cannot read selected file");
                statusLabel.setForeground(new Color(255, 180, 0));
            }
        }
    }
    
    private void updateFileInfo() {
        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName());
            fileNameLabel.setForeground(TEXT_PRIMARY);
            
            long sizeInBytes = selectedFile.length();
            String size = formatFileSize(sizeInBytes);
            fileSizeLabel.setText("Size: " + size);
            fileSizeLabel.setForeground(ACCENT_CYAN);
            
            statusLabel.setText("File selected, ready to upload");
            statusLabel.setForeground(ACCENT_GREEN);
        }
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.2f %s", bytes / Math.pow(1024, exp), pre);
    }
    
    private void uploadFile() {
        if (selectedFile == null) {
            statusLabel.setText("⚠️ Please select a file first");
            statusLabel.setForeground(new Color(255, 180, 0));
            return;
        }
        
        // Disable buttons during upload
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Uploading and generating hash...");
        statusLabel.setForeground(ACCENT_CYAN);
        
        // Get selected algorithm (extract just the algorithm name)
        String selectedAlgo = algorithmComboBox.getSelectedItem().toString();
        String algorithm = selectedAlgo.split(" ")[0]; // "SHA-256 (Recommended)" -> "SHA-256"
        
        // Use SwingWorker to perform upload in background thread
        SwingWorker<FileUploadResult, Void> worker = new SwingWorker<>() {
            
            @Override
            protected FileUploadResult doInBackground() throws Exception {
                // This runs in background thread - won't freeze UI
                return fileUploadService.uploadFile(selectedFile, algorithm, currentUserId);
            }
            
            @Override
            protected void done() {
                // This runs on EDT after background task completes
                try {
                    FileUploadResult result = get();
                    
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    
                    if (result.isSuccess()) {
                        // Show the generated hash
                        hashOutputArea.setText(result.getHashValue());
                        hashOutputArea.setForeground(ACCENT_GREEN);
                        
                        statusLabel.setText("✅ " + result.getMessage() + " (ID: " + result.getFileId() + ")");
                        statusLabel.setForeground(ACCENT_GREEN);
                        
                        // TRIGGER CALLBACK TO REFRESH DASHBOARD
                        if (onUploadSuccess != null) {
                            onUploadSuccess.run();
                        }
                    } else {
                        // Show error
                        hashOutputArea.setText("Error: " + result.getMessage());
                        hashOutputArea.setForeground(new Color(255, 100, 100));
                        
                        statusLabel.setText("❌ Upload failed: " + result.getMessage());
                        statusLabel.setForeground(new Color(255, 100, 100));
                    }
                    
                    // Hide progress bar after 2 seconds
                    Timer hideTimer = new Timer(2000, evt -> {
                        progressBar.setVisible(false);
                        progressBar.setValue(0);
                    });
                    hideTimer.setRepeats(false);
                    hideTimer.start();
                    
                } catch (Exception e) {
                    progressBar.setVisible(false);
                    statusLabel.setText("❌ Error: " + e.getMessage());
                    statusLabel.setForeground(new Color(255, 100, 100));
                }
            }
        };
        
        worker.execute();
    }
    
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    
    private void clearSelection() {
        selectedFile = null;
        fileNameLabel.setText("No file selected");
        fileNameLabel.setForeground(TEXT_SECONDARY);
        fileSizeLabel.setText("");
        hashOutputArea.setText("Hash will appear here after upload...");
        hashOutputArea.setForeground(TEXT_SECONDARY);
        statusLabel.setText("Ready to upload");
        statusLabel.setForeground(TEXT_SECONDARY);
        progressBar.setValue(0);
        progressBar.setVisible(false);
    }
    
    private void copyHash() {
        if (!hashOutputArea.getText().contains("will appear")) {
            StringSelection selection = new StringSelection(hashOutputArea.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            statusLabel.setText("✅ Hash copied to clipboard!");
            statusLabel.setForeground(ACCENT_GREEN);
        }
    }
    
 // REPLACE your saveHash() method in UploadFilePage.java with this:

    private void saveHash() {
        String hashText = hashOutputArea.getText();
        
        // Check if there's a hash to save
        if (hashText.contains("will appear") || hashText.isEmpty()) {
            statusLabel.setText("⚠️ No hash to save");
            statusLabel.setForeground(new Color(255, 180, 0));
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        
        // Suggest filename based on uploaded file
        String suggestedName = "hash";
        if (selectedFile != null) {
            suggestedName = selectedFile.getName() + "_hash";
        }
        fileChooser.setSelectedFile(new File(suggestedName + ".txt"));
        
        int result = fileChooser.showSaveDialog(mainPanel);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Add .txt extension if not present
            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
            }
            
            try (java.io.FileWriter writer = new java.io.FileWriter(fileToSave)) {
                // Write hash info to file
                writer.write("=== File Hash Information ===\n\n");
                
                if (selectedFile != null) {
                    writer.write("Original File: " + selectedFile.getName() + "\n");
                    writer.write("File Size: " + formatFileSize(selectedFile.length()) + "\n");
                }
                
                String algorithm = algorithmComboBox.getSelectedItem().toString().split(" ")[0];
                writer.write("Algorithm: " + algorithm + "\n");
                writer.write("Hash Value: " + hashText + "\n");
                writer.write("\nGenerated: " + new java.util.Date().toString() + "\n");
                
                statusLabel.setText("✅ Hash saved to: " + fileToSave.getName());
                statusLabel.setForeground(ACCENT_GREEN);
                
            } catch (java.io.IOException e) {
                statusLabel.setText("❌ Error saving file: " + e.getMessage());
                statusLabel.setForeground(new Color(255, 100, 100));
                System.err.println("Save error: " + e.getMessage());
            }
        }
    }
    
    private JButton createGradientButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, color1,
                    getWidth(), getHeight(), color2
                );
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JButton createOutlineButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(color);
        button.setBackground(new Color(35, 40, 65));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    // ==================== CUSTOM COMPONENTS ====================
    
    class DragDropPanel extends JPanel {
        private boolean isDragOver = false;
        
        public DragDropPanel() {
            setBackground(new Color(35, 40, 65));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createDashedBorder(ACCENT_PURPLE, 3, 8, 8, true));
            
            JLabel iconLabel = IconRenderer.getIconLabel(IconRenderer.Icon.CLOUD, 72, ACCENT_PURPLE);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel textLabel = new JLabel("Drag & Drop files here");
            textLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            textLabel.setForeground(TEXT_PRIMARY);
            textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel subLabel = new JLabel("or click Browse button below");
            subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subLabel.setForeground(TEXT_SECONDARY);
            subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            add(Box.createVerticalGlue());
            add(iconLabel);
            add(Box.createRigidArea(new Dimension(0, 15)));
            add(textLabel);
            add(Box.createRigidArea(new Dimension(0, 8)));
            add(subLabel);
            add(Box.createVerticalGlue());
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (isDragOver) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(138, 99, 255, 30));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        }
    }
    
    class CircleIconPanel extends JPanel {
        private String letter;
        private Color color;
        
        public CircleIconPanel(String letter, Color color) {
            this.letter = letter;
            this.color = color;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = Math.min(getWidth(), getHeight());
            
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 60),
                size, size, new Color(color.getRed(), color.getGreen(), color.getBlue(), 30)
            );
            g2.setPaint(gradient);
            g2.fillOval(0, 0, size, size);
            
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(1, 1, size - 2, size - 2);
            
            g2.setColor(color);
            g2.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
            FontMetrics fm = g2.getFontMetrics();
            int x = (size - fm.stringWidth(letter)) / 2;
            int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(letter, x, y);
            
            g2.dispose();
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
    
    class RoundedPanel extends JPanel {
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
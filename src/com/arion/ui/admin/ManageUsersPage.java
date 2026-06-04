package com.arion.ui.admin;

import com.arion.dao.UserDAO;
import com.arion.model.User;
import com.arion.service.AuthService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ManageUsersPage {
    
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
    private JTable usersTable;
    private JTextField usernameField, emailField, fullNameField, phoneField, searchField;
    private JComboBox<String> roleCombo, statusCombo;
    private JLabel countLabel;
    
    private UserDAO userDAO;
    private AuthService authService;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat;
    private User selectedUser;
    
    public ManageUsersPage() {
        this.userDAO = new UserDAO();
        this.authService = new AuthService();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        createUI();
        loadUsers();
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
        
        JPanel searchPanel = createSearchPanel();
        contentWrapper.add(searchPanel);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel mainSection = new JPanel(new GridLayout(1, 2, 25, 0));
        mainSection.setBackground(BG_PRIMARY);
        mainSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
        
        JPanel usersListPanel = createUsersListPanel();
        JPanel userDetailsPanel = createUserDetailsPanel();
        
        mainSection.add(usersListPanel);
        mainSection.add(userDetailsPanel);
        
        contentWrapper.add(mainSection);
        
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createSearchPanel() {
        JPanel card = new GlowingCard(25, ACCENT_CYAN);
        card.setLayout(new BorderLayout(20, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel title = new JLabel("👥 User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlsPanel.setBackground(BG_CARD);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBackground(new Color(35, 40, 65));
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(ACCENT_CYAN);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_CYAN, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        JButton searchBtn = createOutlineButton("Search", ACCENT_CYAN);
        searchBtn.addActionListener(e -> performSearch());
        
        JButton refreshBtn = createOutlineButton("🔄 Refresh", ACCENT_PURPLE);
        refreshBtn.addActionListener(e -> loadUsers());
        
        JButton addUserBtn = createGradientButton("➕ Add New User", ACCENT_GREEN, ACCENT_CYAN);
        addUserBtn.addActionListener(e -> showAddUserDialog());
        
        controlsPanel.add(searchField);
        controlsPanel.add(searchBtn);
        controlsPanel.add(refreshBtn);
        controlsPanel.add(addUserBtn);
        
        card.add(title, BorderLayout.WEST);
        card.add(controlsPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private JPanel createUsersListPanel() {
        JPanel card = new GlowingCard(25, ACCENT_PURPLE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("📋 User List");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        countLabel = new JLabel("Loading...");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countLabel.setForeground(ACCENT_PURPLE);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        
        String[] columns = {"ID", "Username", "Email", "Role", "Status", "Last Login", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Actions column editable
            }
        };
        
        usersTable = new JTable(tableModel);
        usersTable.setBackground(BG_CARD);
        usersTable.setForeground(TEXT_PRIMARY);
        usersTable.setGridColor(new Color(255, 255, 255, 5));
        usersTable.setRowHeight(55);
        usersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usersTable.setShowGrid(false);
        usersTable.setSelectionBackground(new Color(138, 99, 255, 30));
        
        // Hide ID column
        usersTable.getColumnModel().getColumn(0).setMinWidth(0);
        usersTable.getColumnModel().getColumn(0).setMaxWidth(0);
        usersTable.getColumnModel().getColumn(0).setWidth(0);
        
        JTableHeader header = usersTable.getTableHeader();
        header.setBackground(new Color(35, 40, 65));
        header.setForeground(ACCENT_PURPLE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_PURPLE));
        
        usersTable.setDefaultRenderer(Object.class, new UsersTableRenderer());
        
        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = usersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadUserDetails(selectedRow);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBackground(BG_CARD);
        scrollPane.setBorder(null);
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
    
    private JPanel createUserDetailsPanel() {
        JPanel card = new GlowingCard(25, ACCENT_GREEN);
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("👤 User Details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_CARD);
        
        // Username
        formPanel.add(createLabel("Username"));
        usernameField = createTextField(ACCENT_CYAN);
        usernameField.setText("Select a user...");
        usernameField.setEditable(false);
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Email
        formPanel.add(createLabel("Email Address"));
        emailField = createTextField(ACCENT_PURPLE);
        formPanel.add(emailField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Full Name
        formPanel.add(createLabel("Full Name"));
        fullNameField = createTextField(ACCENT_CYAN);
        formPanel.add(fullNameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Phone
        formPanel.add(createLabel("Phone Number"));
        phoneField = createTextField(ACCENT_PURPLE);
        formPanel.add(phoneField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Role
        formPanel.add(createLabel("User Role"));
        roleCombo = new JComboBox<>(new String[]{"User", "Analyst", "Admin"});
        styleComboBox(roleCombo, ACCENT_GREEN);
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(roleCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Status
        formPanel.add(createLabel("Account Status"));
        statusCombo = new JComboBox<>(new String[]{"Active", "Locked"});
        styleComboBox(statusCombo, ACCENT_ORANGE);
        statusCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        statusCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(statusCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Action Buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        buttonsPanel.setBackground(BG_CARD);
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton updateBtn = createActionButton("✓ Update User", ACCENT_GREEN);
        updateBtn.addActionListener(e -> updateUser());
        
        JButton resetPasswordBtn = createActionButton("🔑 Reset Password", ACCENT_CYAN);
        resetPasswordBtn.addActionListener(e -> resetPassword());
        
        JButton toggleLockBtn = createActionButton("🔒 Toggle Lock Status", ACCENT_ORANGE);
        toggleLockBtn.addActionListener(e -> toggleLockStatus());
        
        JButton deleteBtn = createActionButton("🗑️ Delete User", ACCENT_RED);
        deleteBtn.addActionListener(e -> deleteUser());
        
        buttonsPanel.add(updateBtn);
        buttonsPanel.add(resetPasswordBtn);
        buttonsPanel.add(toggleLockBtn);
        buttonsPanel.add(deleteBtn);
        
        formPanel.add(buttonsPanel);
        
        card.add(title, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    // Load all users from database
    private void loadUsers() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return userDAO.getAllUsers();
            }
            
            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    updateTable(users);
                    countLabel.setText(users.size() + " total users");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainPanel, 
                        "Error loading users: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    // Update table with user data
    private void updateTable(List<User> users) {
        tableModel.setRowCount(0);
        for (User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isActive() ? "Active" : "Locked",
                user.getLastLogin() != null ? dateFormat.format(user.getLastLogin()) : "Never",
                "Edit"
            };
            tableModel.addRow(row);
        }
    }
    
    // Search users
    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            loadUsers();
            return;
        }
        
        SwingWorker<List<User>, Void> worker = new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                List<User> allUsers = userDAO.getAllUsers();
                return allUsers.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(searchText) ||
                                u.getEmail().toLowerCase().contains(searchText) ||
                                (u.getFullName() != null && u.getFullName().toLowerCase().contains(searchText)))
                    .toList();
            }
            
            @Override
            protected void done() {
                try {
                    List<User> filteredUsers = get();
                    updateTable(filteredUsers);
                    countLabel.setText(filteredUsers.size() + " users found");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainPanel, 
                        "Error searching users: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    // Load selected user details
    private void loadUserDetails(int row) {
        int userId = (int) usersTable.getValueAt(row, 0);
        
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.getUserById(userId);
            }
            
            @Override
            protected void done() {
                try {
                    selectedUser = get();
                    if (selectedUser != null) {
                        usernameField.setText(selectedUser.getUsername());
                        emailField.setText(selectedUser.getEmail());
                        fullNameField.setText(selectedUser.getFullName() != null ? selectedUser.getFullName() : "");
                        phoneField.setText(selectedUser.getPhone() != null ? selectedUser.getPhone() : "");
                        roleCombo.setSelectedItem(selectedUser.getRole());
                        statusCombo.setSelectedItem(selectedUser.isActive() ? "Active" : "Locked");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainPanel, 
                        "Error loading user details: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    // Update user information
    private void updateUser() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a user first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        selectedUser.setEmail(emailField.getText().trim());
        selectedUser.setFullName(fullNameField.getText().trim());
        selectedUser.setPhone(phoneField.getText().trim());
        selectedUser.setRole((String) roleCombo.getSelectedItem());
        selectedUser.setActive(statusCombo.getSelectedItem().equals("Active"));
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return userDAO.updateUser(selectedUser);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(mainPanel, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "Failed to update user!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainPanel, 
                        "Error updating user: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    // Reset user password
    private void resetPassword() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a user first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JPasswordField passField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(mainPanel, passField, "Enter New Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(passField.getPassword());
            
            if (newPassword.trim().isEmpty() || newPassword.length() < 6) {
                JOptionPane.showMessageDialog(mainPanel, "Password must be at least 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String hashedPassword = authService.hashPassword(newPassword);
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return userDAO.updatePassword(selectedUser.getUserId(), hashedPassword);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(mainPanel, "Password reset successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(mainPanel, "Failed to reset password!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(mainPanel, 
                            "Error resetting password: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    
    // Toggle account lock status
    private void toggleLockStatus() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a user first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(mainPanel, 
            "Toggle account lock status for " + selectedUser.getUsername() + "?", 
            "Confirm", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            selectedUser.setActive(!selectedUser.isActive());
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return userDAO.updateUser(selectedUser);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            statusCombo.setSelectedItem(selectedUser.isActive() ? "Active" : "Locked");
                            JOptionPane.showMessageDialog(mainPanel, "Account status changed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadUsers();
                        } else {
                            JOptionPane.showMessageDialog(mainPanel, "Failed to change status!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(mainPanel, 
                            "Error changing status: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    
    // Delete user
    private void deleteUser() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a user first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(mainPanel, 
            "Are you sure you want to delete user '" + selectedUser.getUsername() + "'?\nThis action cannot be undone!", 
            "Delete User", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return userDAO.deleteUser(selectedUser.getUserId());
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(mainPanel, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            clearForm();
                            loadUsers();
                        } else {
                            JOptionPane.showMessageDialog(mainPanel, "Failed to delete user!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(mainPanel, 
                            "Error deleting user: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    
    // Show add user dialog
    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(mainPanel), "Add New User", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(mainPanel);
        
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setBackground(BG_CARD);
        dialogPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("➕ Create New User Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogPanel.add(titleLabel);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Username
        dialogPanel.add(createLabel("Username"));
        JTextField userField = createTextField(ACCENT_CYAN);
        dialogPanel.add(userField);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Email
        dialogPanel.add(createLabel("Email"));
        JTextField newEmailField = createTextField(ACCENT_PURPLE);
        dialogPanel.add(newEmailField);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Full Name
        dialogPanel.add(createLabel("Full Name"));
        JTextField newFullNameField = createTextField(ACCENT_CYAN);
        dialogPanel.add(newFullNameField);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Phone
        dialogPanel.add(createLabel("Phone"));
        JTextField newPhoneField = createTextField(ACCENT_PURPLE);
        dialogPanel.add(newPhoneField);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Password
        dialogPanel.add(createLabel("Password"));
        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBackground(new Color(35, 40, 65));
        passField.setForeground(TEXT_PRIMARY);
        passField.setCaretColor(ACCENT_GREEN);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_GREEN, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        passField.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogPanel.add(passField);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Role
        dialogPanel.add(createLabel("Role"));
        JComboBox<String> newRoleCombo = new JComboBox<>(new String[]{"User", "Analyst", "Admin"});
        styleComboBox(newRoleCombo, ACCENT_ORANGE);
        newRoleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        newRoleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogPanel.add(newRoleCombo);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BG_CARD);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton cancelBtn = createOutlineButton("Cancel", ACCENT_RED);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton createBtn = createGradientButton("Create User", ACCENT_GREEN, ACCENT_CYAN);
        createBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String email = newEmailField.getText().trim();
            String fullName = newFullNameField.getText().trim();
            String phone = newPhoneField.getText().trim();
            String password = new String(passField.getPassword());
            String role = (String) newRoleCombo.getSelectedItem();
            
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username, email, and password are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(dialog, "Password must be at least 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setFullName(fullName);
            newUser.setPhone(phone);
            newUser.setPasswordHash(authService.hashPassword(password));
            newUser.setRole(role);
            newUser.setActive(true);
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return userDAO.addUser(newUser);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(dialog, "User created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                            loadUsers();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Failed to create user. Username or email may already exist!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Error creating user: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        });
        
        btnPanel.add(cancelBtn);
        btnPanel.add(createBtn);
        
        dialogPanel.add(btnPanel);
        
        dialog.add(dialogPanel);
        dialog.setVisible(true);
    }
    
    // Helper method to create labels
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    // Helper method to create text fields
    private JTextField createTextField(Color borderColor) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(35, 40, 65));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(borderColor);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }
    
    // Clear form fields
    private void clearForm() {
        selectedUser = null;
        usernameField.setText("Select a user...");
        emailField.setText("");
        fullNameField.setText("");
        phoneField.setText("");
        roleCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
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
    
    class UsersTableRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 3) { // Role
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(false);
                
                String role = value.toString();
                JLabel label = new JLabel(role);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                
                Color color = role.equals("Admin") ? ACCENT_RED :
                             role.equals("Analyst") ? ACCENT_CYAN : ACCENT_GREEN;
                
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
            
            if (column == 4) { // Status
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(false);
                
                String status = value.toString();
                JLabel label = new JLabel(status);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                
                Color color = status.equals("Active") ? ACCENT_GREEN : ACCENT_RED;
                
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
            
            if (column == 6) { // Actions
                JButton actionBtn = new JButton("✏ " + value.toString());
                actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                actionBtn.setForeground(ACCENT_PURPLE);
                actionBtn.setBackground(new Color(35, 40, 65));
                actionBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_PURPLE, 2),
                    new EmptyBorder(6, 12, 6, 12)
                ));
                actionBtn.setFocusPainted(false);
                actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return actionBtn;
            }
            
            setBackground(isSelected ? new Color(138, 99, 255, 30) : BG_CARD);
            setForeground(TEXT_PRIMARY);
            setBorder(new EmptyBorder(10, 15, 10, 15));
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
}
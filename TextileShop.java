import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextileShop extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JFrame loginFrame;
    private JTextField nameField, typeField, priceField, quantityField;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private Connection conn;
    private int currentUserId;
    private String currentUsername;
    private JTabbedPane tabbedPane;

    public TextileShop() {
        // Set dark theme using Nimbus LookAndFeel with dark colors
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("control", new Color(50, 50, 50));
                    UIManager.put("info", new Color(50, 50, 50));
                    UIManager.put("nimbusBase", new Color(18, 30, 49));
                    UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
                    UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
                    UIManager.put("nimbusFocus", new Color(115, 164, 209));
                    UIManager.put("nimbusGreen", new Color(176, 179, 50));
                    UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
                    UIManager.put("nimbusLightBackground", new Color(50, 50, 50));
                    UIManager.put("nimbusOrange", new Color(191, 98, 4));
                    UIManager.put("nimbusRed", new Color(169, 46, 34));
                    UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
                    UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
                    UIManager.put("text", new Color(230, 230, 230));
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize dark Nimbus LookAndFeel");
        }

        // Database connection for authentication
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/textile_shop", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }

        showLoginDialog();
    }

    private void showLoginDialog() {
        loginFrame = new JFrame("Login");
        loginFrame.setSize(350, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(userLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(loginButton, gbc);

        JButton registerButton = new JButton("Register");
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(registerButton, gbc);

        JButton cancelButton = new JButton("Cancel");
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(cancelButton, gbc);

        loginFrame.add(panel);
        loginFrame.getRootPane().setDefaultButton(loginButton);

        // Enter key triggers login
        loginButton.addActionListener(e -> authenticate());
        // Register button opens registration
        registerButton.addActionListener(e -> registerUser());
        // Cancel button closes app
        cancelButton.addActionListener(e -> System.exit(0));
        // ESC key closes app
        loginFrame.getRootPane().registerKeyboardAction(e -> System.exit(0),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        loginFrame.setVisible(true);
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(((JPasswordField)passwordField).getPassword()).trim();

        // Input validation
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame, "Please enter a username", "Validation Error", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame, "Please enter a password", "Validation Error", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(loginFrame, "Username must be at least 3 characters long", "Validation Error", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(loginFrame, "Password must be at least 4 characters long", "Validation Error", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        // Database authentication
        try {
            String sql = "SELECT id, username FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("id");
                currentUsername = rs.getString("username");
                loginFrame.dispose();
                initializeMainUI();
                showInventory(); // Auto-refresh inventory on successful login
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText(""); // Clear password field
                usernameField.requestFocus();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(loginFrame, "Database connection error: " + e.getMessage() + "\nPlease check if MySQL is running.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(((JPasswordField)passwordField).getPassword()).trim();

        // Input validation
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame, "Please enter a username", "Validation Error", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame, "Please enter a password", "Validation Error", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(loginFrame, "Username must be at least 3 characters long", "Validation Error", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(loginFrame, "Password must be at least 4 characters long", "Validation Error", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        // Password encryption (simple hash for demonstration, replace with stronger encryption in production)
        String hashedPassword = Integer.toString(password.hashCode());

        try {
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(loginFrame, "User registered successfully! You can now login.", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(loginFrame, "Username already exists. Please choose a different username.", "Registration Failed", JOptionPane.WARNING_MESSAGE);
                usernameField.requestFocus();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Database error during registration: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void initializeMainUI() {
        setTitle("Textile Shop - " + currentUsername);
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(5,5));
        mainPanel.setBorder(new EmptyBorder(5,5,5,5));
        mainPanel.setBackground(new Color(40, 40, 40));
        setContentPane(mainPanel);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(50, 50, 50));
        tabbedPane.setForeground(Color.WHITE);

        // Inventory Tab
        JPanel inventoryPanel = createInventoryPanel();
        tabbedPane.addTab("Inventory", inventoryPanel);

        // Reports Tab
        JPanel reportsPanel = createReportsPanel();
        tabbedPane.addTab("Reports", reportsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

     private JPanel createReportsPanel() {
         JPanel panel = new JPanel(new BorderLayout());
         panel.setBackground(new Color(40, 40, 40));
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Inventory Reports"));

         JTextArea reportArea = new JTextArea();
         reportArea.setEditable(false);
         reportArea.setBackground(new Color(50, 50, 50));
         reportArea.setForeground(Color.WHITE);
         reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
         panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

         JButton generateReportButton = new JButton("Generate Inventory Report");
         panel.add(generateReportButton, BorderLayout.SOUTH);

         generateReportButton.addActionListener(e -> {
             StringBuilder report = new StringBuilder();
             report.append("Inventory Report - Generated on ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");

             try {
                 String sql = "SELECT type, COUNT(*) AS item_count, SUM(quantity) AS total_quantity, SUM(price * quantity) AS total_value FROM items GROUP BY type";
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery();

                 report.append(String.format("%-20s %-12s %-15s %-15s\n", "Type", "Item Count", "Total Quantity", "Total Value (RS)"));
                 report.append("---------------------------------------------------------------------\n");

                 while (rs.next()) {
                     String type = rs.getString("type");
                     int itemCount = rs.getInt("item_count");
                     int totalQuantity = rs.getInt("total_quantity");
                     double totalValue = rs.getDouble("total_value");

                     report.append(String.format("%-20s %-12d %-15d RS %.2f\n", type, itemCount, totalQuantity, totalValue));
                 }
                 reportArea.setText(report.toString());
             } catch (SQLException ex) {
                 JOptionPane.showMessageDialog(panel, "Error generating report: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
             }
         });

         return panel;
     }



    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBackground(new Color(40, 40, 40));

        // Top panel for input and controls (compact)
        JPanel topPanel = new JPanel(new BorderLayout(5,5));
        topPanel.setBackground(new Color(50, 50, 50));
        topPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Item Management"));

        // Input fields and logout button in the same row
        JPanel inputPanel = new JPanel(new BorderLayout(5,2));
        inputPanel.setBackground(new Color(60, 63, 65));

        // Left side: input fields
        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        fieldsPanel.setBackground(new Color(60, 63, 65));

        fieldsPanel.add(new JLabel("Name:"));
        nameField = new JTextField(12);
        fieldsPanel.add(nameField);

        fieldsPanel.add(new JLabel("Type:"));
        typeField = new JTextField(12);
        fieldsPanel.add(typeField);

        fieldsPanel.add(new JLabel("Price:"));
        priceField = new JTextField(8);
        fieldsPanel.add(priceField);

        fieldsPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(8);
        fieldsPanel.add(quantityField);

        inputPanel.add(fieldsPanel, BorderLayout.WEST);

        // Right side: logout button
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        logoutPanel.setBackground(new Color(60, 63, 65));

        JButton logoutButton = new JButton("\u23FB"); // power symbol only
        logoutButton.setPreferredSize(new Dimension(50, 25));
        logoutButton.setBackground(new Color(220, 53, 69)); // Red background for logout
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutPanel.add(logoutButton);

        inputPanel.add(logoutPanel, BorderLayout.EAST);

        topPanel.add(inputPanel, BorderLayout.CENTER);

        // Buttons in a compact layout below
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 2));
        buttonPanel.setBackground(new Color(60, 63, 65));

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton showButton = new JButton("Refresh");
        JButton searchButton = new JButton("Search");

        // Make buttons smaller
        Dimension buttonSize = new Dimension(70, 25);
        addButton.setPreferredSize(buttonSize);
        updateButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        showButton.setPreferredSize(buttonSize);
        searchButton.setPreferredSize(buttonSize);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(showButton);
        buttonPanel.add(searchButton);

        // Add hover effects for all buttons
        addHoverEffect(addButton, new Color(25, 135, 84), new Color(40, 167, 69)); // Green for Add
        addHoverEffect(updateButton, new Color(255, 193, 7), new Color(255, 235, 59)); // Yellow for Update
        addHoverEffect(deleteButton, new Color(220, 53, 69), new Color(255, 71, 87)); // Red for Delete
        addHoverEffect(showButton, new Color(0, 123, 255), new Color(23, 162, 184)); // Blue for Refresh
        addHoverEffect(searchButton, new Color(108, 117, 125), new Color(52, 58, 64)); // Gray for Search
        addHoverEffect(logoutButton, new Color(220, 53, 69), new Color(255, 71, 87)); // Red for Logout

        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table takes up the majority of space
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Type", "Price", "Quantity", "Total Price"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(tableModel);
        inventoryTable.setRowHeight(25);
        inventoryTable.getTableHeader().setReorderingAllowed(false);

        // Set custom cell renderer for Price and Total Price columns to add "RS" prefix
        inventoryTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Number) {
                    setText("RS " + value.toString());
                } else {
                    super.setValue(value);
                }
            }
        });
        inventoryTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Number) {
                    setText("RS " + value.toString());
                } else {
                    super.setValue(value);
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Inventory Items"));
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Button actions
        addButton.addActionListener(e -> addItem());
        updateButton.addActionListener(e -> updateItem());
        deleteButton.addActionListener(e -> deleteItem());
        showButton.addActionListener(e -> showInventory());
        searchButton.addActionListener(e -> searchItems());
        logoutButton.addActionListener(e -> logout());

        // Table row selection fills input fields
        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && inventoryTable.getSelectedRow() != -1) {
                int row = inventoryTable.getSelectedRow();
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                typeField.setText(tableModel.getValueAt(row, 2).toString());
                priceField.setText(tableModel.getValueAt(row, 3).toString());
                quantityField.setText(tableModel.getValueAt(row, 4).toString());
            }
        });

        return panel;
    }



    private void logout() {
        dispose();
        showLoginDialog();
    }

    private void addItem() {
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        String priceText = priceField.getText().trim();
        String quantityText = quantityField.getText().trim();

        // Enhanced input validation
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an item name", "Validation Error", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }

        if (name.length() < 2) {
            JOptionPane.showMessageDialog(this, "Item name must be at least 2 characters long", "Validation Error", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }

        // Validate that name contains only letters and spaces
        if (!name.matches("[a-zA-Z\\s]+")) {
            JOptionPane.showMessageDialog(this, "Item name must contain only letters and spaces", "Validation Error", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }

        if (type.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an item type", "Validation Error", JOptionPane.WARNING_MESSAGE);
            typeField.requestFocus();
            return;
        }

        // Validate that type contains only letters and spaces
        if (!type.matches("[a-zA-Z\\s]+")) {
            JOptionPane.showMessageDialog(this, "Item type must contain only letters and spaces", "Validation Error", JOptionPane.WARNING_MESSAGE);
            typeField.requestFocus();
            return;
        }

        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a price", "Validation Error", JOptionPane.WARNING_MESSAGE);
            priceField.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a quantity", "Validation Error", JOptionPane.WARNING_MESSAGE);
            quantityField.requestFocus();
            return;
        }

        double price;
        int quantity;

        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than 0", "Validation Error", JOptionPane.WARNING_MESSAGE);
                priceField.requestFocus();
                return;
            }
            if (price > 999999.99) {
                JOptionPane.showMessageDialog(this, "Price cannot exceed 999,999.99", "Validation Error", JOptionPane.WARNING_MESSAGE);
                priceField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price (numeric value)", "Validation Error", JOptionPane.WARNING_MESSAGE);
            priceField.requestFocus();
            return;
        }

        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0", "Validation Error", JOptionPane.WARNING_MESSAGE);
                quantityField.requestFocus();
                return;
            }
            if (quantity > 99999) {
                JOptionPane.showMessageDialog(this, "Quantity cannot exceed 99,999", "Validation Error", JOptionPane.WARNING_MESSAGE);
                quantityField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity (whole number)", "Validation Error", JOptionPane.WARNING_MESSAGE);
            quantityField.requestFocus();
            return;
        }

        try {
            String sql = "INSERT INTO items (name, type, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearInputFields();
            showInventory();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, "An item with this name already exists. Please use a different name.", "Duplicate Item", JOptionPane.WARNING_MESSAGE);
                nameField.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Database error while adding item: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearInputFields() {
        nameField.setText("");
        typeField.setText("");
        priceField.setText("");
        quantityField.setText("");
    }

    private void updateItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to update");
            return;
        }

        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        String priceText = priceField.getText().trim();
        String quantityText = quantityField.getText().trim();

        if (name.isEmpty() || type.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        double price;
        int quantity;
        try {
            price = Double.parseDouble(priceText);
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price or quantity");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            String sql = "UPDATE items SET name = ?, type = ?, price = ?, quantity = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.setInt(5, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Item updated successfully");
                showInventory();
            } else {
                JOptionPane.showMessageDialog(this, "Item not found");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating item: " + e.getMessage());
        }
    }

    private void deleteItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to delete");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            String sql = "DELETE FROM items WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Item deleted successfully");
                showInventory();
            } else {
                JOptionPane.showMessageDialog(this, "Item not found");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting item: " + e.getMessage());
        }
    }

    private void showInventory() {
        try {
            String sql = "SELECT * FROM items";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                double totalPrice = price * quantity;
                tableModel.addRow(new Object[]{id, name, type, price, quantity, totalPrice});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching inventory: " + e.getMessage());
        }
    }

    private void searchItems() {
        String searchTerm = JOptionPane.showInputDialog(this, "Enter name or type to search:");
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            showInventory();
            return;
        }
        searchTerm = "%" + searchTerm.trim() + "%";

        try {
            String sql = "SELECT * FROM items WHERE name LIKE ? OR type LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                double totalPrice = price * quantity;
                tableModel.addRow(new Object[]{id, name, type, price, quantity, totalPrice});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching items: " + e.getMessage());
        }
    }

    private void loadInventoryForBilling(DefaultTableModel model) {
        try {
            String sql = "SELECT * FROM items";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                model.addRow(new Object[]{id, name, type, price, quantity});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading inventory: " + e.getMessage());
        }
    }

    private void addItemToBill(DefaultTableModel inventoryModel, int row) {
        // Removed billing related code as per user request
    }

    private void generateBill() {
        // Removed billing related code as per user request
    }

    private void printBill() {
        // Removed billing related code as per user request
    }

    private void clearBill() {
        // Removed billing related code as per user request
    }

    private void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = normalColor;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                originalColor = button.getBackground();
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TextileShop());
    }
}

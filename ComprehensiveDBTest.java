import java.sql.*;

public class ComprehensiveDBTest {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully!");

            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/textile_shop", "root", "");
            System.out.println("Database connection established successfully!");

            // Test 1: User Authentication
            testUserAuthentication(conn);

            // Test 2: Item CRUD Operations
            testItemCRUDOperations(conn);

            // Test 3: Search Functionality
            testSearchFunctionality(conn);

            // Test 4: Reports Generation
            testReportsGeneration(conn);

            System.out.println("\n=== COMPREHENSIVE DATABASE TEST COMPLETED SUCCESSFULLY! ===");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database test failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private static void testUserAuthentication(Connection conn) throws SQLException {
        System.out.println("\n=== Testing User Authentication ===");

        // Test user registration
        String hashedPassword = Integer.toString("password123".hashCode());
        String sql = "INSERT INTO users (username, password) VALUES (?, ?) ON DUPLICATE KEY UPDATE password = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "testuser");
        stmt.setString(2, hashedPassword);
        stmt.setString(3, hashedPassword);
        stmt.executeUpdate();
        System.out.println("✓ User registration test passed");

        // Test user login
        sql = "SELECT id, username FROM users WHERE username = ? AND password = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, "testuser");
        stmt.setString(2, hashedPassword);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            System.out.println("✓ User login test passed - User: " + rs.getString("username"));
        } else {
            System.out.println("✗ User login test failed");
        }
        rs.close();
    }

    private static void testItemCRUDOperations(Connection conn) throws SQLException {
        System.out.println("\n=== Testing Item CRUD Operations ===");

        // CREATE - Insert test item
        String sql = "INSERT INTO items (name, type, price, quantity) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, "Test Jeans");
        stmt.setString(2, "Pants");
        stmt.setDouble(3, 49.99);
        stmt.setInt(4, 5);
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        int itemId = 0;
        if (rs.next()) {
            itemId = rs.getInt(1);
            System.out.println("✓ CREATE operation test passed - Item ID: " + itemId);
        }
        rs.close();

        // READ - Select the item
        sql = "SELECT * FROM items WHERE id = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, itemId);
        rs = stmt.executeQuery();
        if (rs.next()) {
            System.out.println("✓ READ operation test passed - Item: " + rs.getString("name"));
        } else {
            System.out.println("✗ READ operation test failed");
        }
        rs.close();

        // UPDATE - Update the item
        sql = "UPDATE items SET price = ?, quantity = ? WHERE id = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setDouble(1, 59.99);
        stmt.setInt(2, 8);
        stmt.setInt(3, itemId);
        int updateCount = stmt.executeUpdate();
        if (updateCount > 0) {
            System.out.println("✓ UPDATE operation test passed - Rows affected: " + updateCount);
        } else {
            System.out.println("✗ UPDATE operation test failed");
        }

        // DELETE - Delete the item
        sql = "DELETE FROM items WHERE id = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, itemId);
        int deleteCount = stmt.executeUpdate();
        if (deleteCount > 0) {
            System.out.println("✓ DELETE operation test passed - Rows affected: " + deleteCount);
        } else {
            System.out.println("✗ DELETE operation test failed");
        }
    }

    private static void testSearchFunctionality(Connection conn) throws SQLException {
        System.out.println("\n=== Testing Search Functionality ===");

        // Insert test data for search
        String sql = "INSERT INTO items (name, type, price, quantity) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "Blue Shirt");
        stmt.setString(2, "Shirt");
        stmt.setDouble(3, 29.99);
        stmt.setInt(4, 3);
        stmt.executeUpdate();

        stmt.setString(1, "Red Pants");
        stmt.setString(2, "Pants");
        stmt.setDouble(3, 39.99);
        stmt.setInt(4, 7);
        stmt.executeUpdate();

        // Test search by name
        sql = "SELECT COUNT(*) as count FROM items WHERE name LIKE ?";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, "%Shirt%");
        ResultSet rs = stmt.executeQuery();
        if (rs.next() && rs.getInt("count") > 0) {
            System.out.println("✓ Search by name test passed - Found " + rs.getInt("count") + " items");
        } else {
            System.out.println("✗ Search by name test failed");
        }
        rs.close();

        // Test search by type
        sql = "SELECT COUNT(*) as count FROM items WHERE type LIKE ?";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, "%Pants%");
        rs = stmt.executeQuery();
        if (rs.next() && rs.getInt("count") > 0) {
            System.out.println("✓ Search by type test passed - Found " + rs.getInt("count") + " items");
        } else {
            System.out.println("✗ Search by type test failed");
        }
        rs.close();

        // Clean up test data
        sql = "DELETE FROM items WHERE name IN ('Blue Shirt', 'Red Pants')";
        stmt = conn.prepareStatement(sql);
        stmt.executeUpdate();
        System.out.println("✓ Search test cleanup completed");
    }

    private static void testReportsGeneration(Connection conn) throws SQLException {
        System.out.println("\n=== Testing Reports Generation ===");

        // Insert test data for reports
        String sql = "INSERT INTO items (name, type, price, quantity) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);

        String[][] testData = {
            {"Cotton T-Shirt", "Shirt", "19.99", "10"},
            {"Denim Jeans", "Pants", "79.99", "5"},
            {"Wool Sweater", "Sweater", "89.99", "3"},
            {"Leather Jacket", "Jacket", "199.99", "2"}
        };

        for (String[] item : testData) {
            stmt.setString(1, item[0]);
            stmt.setString(2, item[1]);
            stmt.setDouble(3, Double.parseDouble(item[2]));
            stmt.setInt(4, Integer.parseInt(item[3]));
            stmt.executeUpdate();
        }

        // Test inventory report query
        sql = "SELECT type, COUNT(*) AS item_count, SUM(quantity) AS total_quantity, SUM(price * quantity) AS total_value FROM items GROUP BY type";
        stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        int reportRows = 0;
        double totalValue = 0;
        while (rs.next()) {
            reportRows++;
            totalValue += rs.getDouble("total_value");
        }

        if (reportRows > 0 && totalValue > 0) {
            System.out.println("✓ Reports generation test passed - " + reportRows + " categories, Total value: RS " + String.format("%.2f", totalValue));
        } else {
            System.out.println("✗ Reports generation test failed");
        }
        rs.close();

        // Clean up test data
        sql = "DELETE FROM items WHERE name IN ('Cotton T-Shirt', 'Denim Jeans', 'Wool Sweater', 'Leather Jacket')";
        stmt = conn.prepareStatement(sql);
        stmt.executeUpdate();
        System.out.println("✓ Reports test cleanup completed");
    }
}

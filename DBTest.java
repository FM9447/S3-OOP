import java.sql.*;

public class DBTest {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully!");

            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/textile_shop", "root", "");
            System.out.println("Database connection established successfully!");

            // Create statement
            stmt = conn.createStatement();

            // Test users table
            System.out.println("\n=== Testing Users Table ===");
            rs = stmt.executeQuery("SELECT * FROM users");
            System.out.println("Users in database:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Username: " + rs.getString("username"));
            }
            rs.close();

            // Test items table
            System.out.println("\n=== Testing Items Table ===");
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM items");
            if (rs.next()) {
                System.out.println("Total items in database: " + rs.getInt("count"));
            }
            rs.close();

            // Test insert
            String sql = "INSERT INTO items (name, type, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt2 = conn.prepareStatement(sql);
            stmt2.setString(1, "Cotton Shirt");
            stmt2.setString(2, "Shirt");
            stmt2.setDouble(3, 25.99);
            stmt2.setInt(4, 10);
            stmt2.executeUpdate();
            System.out.println("Item inserted successfully!");

            // Test update
            sql = "UPDATE items SET price = ? WHERE name = ?";
            PreparedStatement stmt3 = conn.prepareStatement(sql);
            stmt3.setDouble(1, 29.99);
            stmt3.setString(2, "Cotton Shirt");
            stmt3.executeUpdate();
            System.out.println("Item updated successfully!");

            // Test delete
            sql = "DELETE FROM items WHERE name = ?";
            PreparedStatement stmt4 = conn.prepareStatement(sql);
            stmt4.setString(1, "Cotton Shirt");
            int deletedRows = stmt4.executeUpdate();
            System.out.println("Item deleted successfully! Rows affected: " + deletedRows);

            // Test bills table
            System.out.println("\n=== Testing Bills Table ===");
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM bills");
            if (rs.next()) {
                System.out.println("Total bills in database: " + rs.getInt("count"));
            }
            rs.close();

            // Test bill_items table
            System.out.println("\n=== Testing Bill Items Table ===");
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM bill_items");
            if (rs.next()) {
                System.out.println("Total bill items in database: " + rs.getInt("count"));
            }
            rs.close();

            System.out.println("\n=== Database Test Completed Successfully! ===");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            System.err.println("Please ensure mysql-connector-j-8.0.33.jar is in the classpath");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection/test failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please ensure:");
            System.err.println("1. MySQL Server is running");
            System.err.println("2. Database 'textile_shop' exists");
            System.err.println("3. Username and password are correct");
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("Database resources cleaned up.");
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }
}

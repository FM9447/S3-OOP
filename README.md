# Textile Shop Management System

A comprehensive Java Swing application for managing textile shop inventory and billing with MySQL database integration.

## Features

### Authentication System
- Secure login with database authentication
- User-specific data isolation
- Default users: `fm/9447` and `test/123`

### Inventory Management
- **CRUD Operations**: Add, update, delete, and view items
- **Search Functionality**: Search items by name or type
- **Real-time Updates**: Auto-refresh inventory after operations
- **User Isolation**: Each user sees only their own inventory
- **Currency Formatting**: RS prefix for prices and totals

### Billing System
- **Customer Management**: Customer name input and tracking
- **Item Selection**: Double-click items to add to bill
- **Quantity Management**: Input validation and stock checking
- **Bill Generation**: Automatic bill number generation and database storage
- **Inventory Updates**: Automatic quantity reduction after billing
- **Bill Preview**: Text-based bill display with formatting

### User Interface
- **Dark Theme**: Modern dark Nimbus LookAndFeel
- **Tabbed Interface**: Separate tabs for Inventory and Billing
- **Hover Effects**: Color-changing buttons with smooth transitions
- **Compact Layout**: Optimized space usage with majority for data tables
- **Responsive Design**: Proper component sizing and alignment

### Database Features
- **MySQL Integration**: Full JDBC connectivity
- **Data Persistence**: All operations saved to database
- **Transaction Safety**: Proper error handling and rollback
- **Multi-table Relations**: Users, Items, Bills, and Bill Items tables

## Prerequisites

### Java JDK
- **Version**: JDK 8 or higher
- **Download**: https://adoptium.net/ or https://www.oracle.com/java/technologies/javase-downloads.html
- **Setup**: Set JAVA_HOME and add to PATH
- **Verification**: Run `java -version` and `javac -version`

### MySQL Server
- **Download**: https://dev.mysql.com/downloads/mysql/
- **Installation**: Install with root user (password: 944794)
- **Service**: Ensure MySQL service is running

### MySQL Connector/J
- **Version**: 8.0.33 or compatible
- **Download**: https://dev.mysql.com/downloads/connector/j/
- **Location**: Place `mysql-connector-j-8.0.33.jar` in project directory

## Quick Start

### 1. Database Setup
Run these SQL commands in MySQL:
```sql
CREATE DATABASE textile_shop;
USE textile_shop;

-- Users table for authentication
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(100)
);

-- Items table with user isolation
CREATE TABLE items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(100),
    type VARCHAR(50),
    price DOUBLE,
    quantity INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Bills table for billing functionality
CREATE TABLE bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    bill_number VARCHAR(20),
    customer_name VARCHAR(100),
    total_amount DOUBLE,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Bill items table
CREATE TABLE bill_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT,
    item_name VARCHAR(100),
    quantity INT,
    price DOUBLE,
    total DOUBLE,
    FOREIGN KEY (bill_id) REFERENCES bills(id)
);

-- Insert default users
INSERT INTO users (username, password) VALUES ('fm', '9447');
INSERT INTO users (username, password) VALUES ('test', '123');
```

### 2. Run the Application
Simply double-click `run.bat` or run from command line:
```batch
run.bat
```

The script will:
1. Check for MySQL connector JAR
2. Compile the Java files
3. Test database connection
4. Launch the application

### 3. Manual Compilation (Alternative)
```batch
javac -cp mysql-connector-j-8.0.33.jar TextileShop.java
javac -cp mysql-connector-j-8.0.33.jar DBTest.java
java -cp .;mysql-connector-j-8.0.33.jar TextileShop
```

## Usage Guide

### Login
- Use username: `fm` and password: `9447`
- Or username: `test` and password: `123`

### Inventory Tab
1. **Add Item**: Fill all fields and click "Add"
2. **Update Item**: Select row, modify fields, click "Update"
3. **Delete Item**: Select row and click "Delete"
4. **Search**: Click "Search" and enter name/type
5. **Refresh**: Click "Refresh" to reload data

### Billing Tab
1. **Enter Customer Name**: Fill customer name field
2. **Add Items**: Double-click items from left table
3. **Enter Quantity**: Input quantity when prompted
4. **Generate Bill**: Click "Generate Bill" to save
5. **Print Preview**: Click "Print Bill" to view formatted bill
6. **Clear Bill**: Click "Clear Bill" to reset

### Logout
- Click the power button (⏻) in the top-right corner

## File Structure
```
JAVA-Project/
├── TextileShop.java      # Main application
├── DBTest.java          # Database connection test
├── run.bat             # Compilation and execution script
├── mysql-connector-j-8.0.33.jar  # MySQL JDBC driver
├── README.md           # This documentation
└── TODO.md            # Development progress
```

## Troubleshooting

### Database Connection Issues
- Ensure MySQL service is running
- Verify database name and credentials
- Check if MySQL connector JAR is present

### Compilation Errors
- Verify Java JDK installation
- Ensure correct classpath for MySQL connector
- Check for syntax errors in Java files

### GUI Issues
- Ensure you're running on a graphical environment
- Check Java Swing support
- Verify Nimbus LookAndFeel availability

## Development Notes

- **Database Credentials**: Currently hardcoded for simplicity
- **PDF Generation**: Text-based preview (iText library needed for PDF)
- **Security**: Basic authentication (consider encryption for production)
- **Concurrency**: Single-user focused (add locks for multi-user)

## Future Enhancements

- PDF bill generation with iText
- User registration and password encryption
- Advanced reporting and analytics
- Inventory import/export functionality
- Multi-user session management
- Unit and integration testing

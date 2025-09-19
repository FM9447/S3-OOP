-- Create missing tables for Textile Shop application
USE textile_shop;

-- Users table for login authentication
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

-- Items table (shared across all users)
CREATE TABLE IF NOT EXISTS items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    price DOUBLE NOT NULL,
    quantity INT NOT NULL
);

-- Bills table for billing functionality (removed for now)
-- CREATE TABLE IF NOT EXISTS bills (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     user_id INT,
--     bill_number VARCHAR(20),
--     customer_name VARCHAR(100),
--     total_amount DOUBLE,
--     bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (user_id) REFERENCES users(id)
-- );

-- Bill items table (removed for now)
-- CREATE TABLE IF NOT EXISTS bill_items (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     bill_id INT,
--     item_name VARCHAR(100),
--     quantity INT,
--     price DOUBLE,
--     total DOUBLE,
--     FOREIGN KEY (bill_id) REFERENCES bills(id)
-- );

-- Insert default users for login
INSERT IGNORE INTO users (username, password) VALUES ('fm', '9447');
INSERT IGNORE INTO users (username, password) VALUES ('test', '123');
INSERT IGNORE INTO users (username, password) VALUES ('admin', 'admin');

-- Show created tables
SHOW TABLES;

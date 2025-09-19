#!/bin/bash

echo "Textile Shop Application Runner"
echo "================================"

# Check if MySQL connector JAR exists
if [ ! -f "mysql-connector-j-8.0.33.jar" ]; then
    echo "ERROR: mysql-connector-j-8.0.33.jar not found!"
    echo "Please download MySQL Connector/J from:"
    echo "https://dev.mysql.com/downloads/connector/j/"
    echo "And place it in the same directory as this script."
    exit 1
fi

echo
echo "1. Compile TextileShop.java"
echo "---------------------------"
javac -cp mysql-connector-j-8.0.33.jar TextileShop.java
if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed!"
    exit 1
fi
echo "Compilation successful!"

echo
echo "2. Test Database Connection"
echo "----------------------------"
java -cp .:mysql-connector-j-8.0.33.jar DBTest
if [ $? -ne 0 ]; then
    echo "WARNING: Database test failed. Please check your MySQL setup."
    echo "The application may still work if the database is configured correctly."
fi

echo
echo "3. Run Textile Shop Application"
echo "---------------------------------"
echo "Starting Textile Shop Application..."
java -cp .:mysql-connector-j-8.0.33.jar TextileShop

echo
echo "Application closed."


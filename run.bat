@echo off
echo Textile Shop Application Runner
echo ================================

REM Check if MySQL connector JAR exists
if not exist "mysql-connector-j-8.0.33.jar" (
    echo ERROR: mysql-connector-j-8.0.33.jar not found!
    echo Please download MySQL Connector/J from:
    echo https://dev.mysql.com/downloads/connector/j/
    echo And place it in the same directory as this script.
    pause
    exit /b 1
)

echo.
echo 1. Compile TextileShop.java
echo ---------------------------
javac -cp mysql-connector-j-8.0.33.jar TextileShop.java
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful!

echo.
echo 2. Test Database Connection
echo ----------------------------
java -cp .;mysql-connector-j-8.0.33.jar DBTest
if %errorlevel% neq 0 (
    echo WARNING: Database test failed. Please check your MySQL setup.
    echo The application may still work if the database is configured correctly.
)

echo.
echo 3. Run Textile Shop Application
echo ---------------------------------
echo Starting Textile Shop Application...
java -cp .;mysql-connector-j-8.0.33.jar TextileShop.java

echo.
echo Application closed.
pause

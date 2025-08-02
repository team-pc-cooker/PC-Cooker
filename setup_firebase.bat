@echo off
echo ========================================
echo PC-Cooker Firebase Setup Script
echo ========================================
echo.

echo Step 1: Installing Python dependencies...
pip install -r requirements.txt
if %errorlevel% neq 0 (
    echo Error: Failed to install dependencies
    pause
    exit /b 1
)

echo.
echo Step 2: Checking for service account key...
if not exist "serviceAccountKey.json" (
    echo Warning: serviceAccountKey.json not found!
    echo Please download it from Firebase Console:
    echo 1. Go to Firebase Console
    echo 2. Project Settings ^> Service Accounts
    echo 3. Generate new private key
    echo 4. Save as serviceAccountKey.json in this folder
    echo.
    pause
    exit /b 1
)

echo.
echo Step 3: Running Firebase data populator...
python firebase_data_populator.py
if %errorlevel% neq 0 (
    echo Error: Failed to populate Firebase data
    pause
    exit /b 1
)

echo.
echo ========================================
echo Setup completed successfully!
echo ========================================
echo.
echo Your Firebase database is now populated with:
echo - 35+ PC components
echo - Current Indian market prices
echo - Detailed specifications
echo - Ratings and descriptions
echo.
echo You can now test your PC-Cooker app!
pause 
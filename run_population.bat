@echo off
echo ========================================
echo PC-Cooker Firebase Data Population
echo ========================================
echo.

echo Checking Python installation...
python --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Python is not installed or not in PATH
    echo Please install Python from https://python.org
    pause
    exit /b 1
)

echo Checking Firebase credentials...
if not exist "serviceAccountKey.json" (
    echo ERROR: serviceAccountKey.json not found!
    echo.
    echo Please download your Firebase service account key:
    echo 1. Go to Firebase Console ^> Project Settings ^> Service Accounts
    echo 2. Click "Generate New Private Key"
    echo 3. Save as "serviceAccountKey.json" in this directory
    echo.
    pause
    exit /b 1
)

echo Installing dependencies...
pip install firebase-admin

echo.
echo Running Firebase population script...
echo ========================================
python run_complete_population.py

echo.
echo ========================================
echo Population completed!
echo ========================================
echo.
echo Next steps:
echo 1. Test your Android app
echo 2. Check if components are loading from Firebase
echo 3. Verify prices and images are correct
echo.
pause 
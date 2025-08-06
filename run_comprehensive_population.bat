@echo off
echo.
echo ============================================================
echo    PC-Cooker Comprehensive Indian Market Data Population
echo ============================================================
echo.

REM Check if Python is installed
python --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Python is not installed or not in PATH
    echo Please install Python from https://python.org
    pause
    exit /b 1
)

REM Check if firebase-admin is installed
python -c "import firebase_admin" >nul 2>&1
if errorlevel 1 (
    echo ⚠️  Firebase Admin SDK not found. Installing...
    pip install firebase-admin
    if errorlevel 1 (
        echo ❌ Failed to install firebase-admin
        pause
        exit /b 1
    )
)

REM Check if service account key exists
if not exist "serviceAccountKey.json" (
    echo ❌ Error: serviceAccountKey.json not found!
    echo.
    echo Please download your Firebase service account key:
    echo 1. Go to Firebase Console ^> Project Settings ^> Service Accounts
    echo 2. Click "Generate New Private Key"
    echo 3. Save as "serviceAccountKey.json" in this directory
    echo.
    pause
    exit /b 1
)

echo ✅ All prerequisites checked
echo.
echo 🚀 Starting comprehensive population...
echo This will add 60+ components across all categories:
echo    • PROCESSOR (12 components)
echo    • GRAPHIC CARD (12 components)
echo    • RAM (6 components)
echo    • MOTHERBOARD (4 components)
echo    • STORAGE (4 components)
echo    • POWER SUPPLY (3 components)
echo    • CABINET (3 components)
echo    • COOLING (3 components)
echo    • MONITOR (3 components)
echo    • KEYBOARD (3 components)
echo    • MOUSE (3 components)
echo.

REM Run the population script
python run_comprehensive_population.py

if errorlevel 1 (
    echo.
    echo ❌ Population failed. Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo 🎉 Population completed successfully!
echo ✅ All components have been added to your Firebase database
echo 📱 You can now test your PC-Cooker app with the new data
echo.
pause 
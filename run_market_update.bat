@echo off
echo PC-Cooker Market Update Script
echo Removing Duplicates and Adding Current Market Data
echo ================================================

REM Check if Python is installed
python --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Python is not installed or not in PATH
    echo Please install Python 3.7+ and try again
    pause
    exit /b 1
)

REM Check if serviceAccountKey.json exists
if not exist "serviceAccountKey.json" (
    echo ERROR: serviceAccountKey.json not found
    echo Please place your Firebase service account key in this directory
    pause
    exit /b 1
)

REM Install firebase-admin if not already installed
echo Installing firebase-admin...
pip install firebase-admin

REM Run the market update script
echo.
echo Running complete market update...
python complete_market_update.py

echo.
echo Market update completed!
echo Check your Android app to see the updated data.
pause 
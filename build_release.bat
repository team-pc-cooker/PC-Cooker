@echo off
echo ========================================
echo    PC COOKER - RELEASE BUILD SCRIPT
echo ========================================
echo.

echo Step 1: Cleaning previous builds...
call gradlew clean

echo.
echo Step 2: Building release AAB (App Bundle)...
call gradlew bundleRelease

echo.
echo Step 3: Building release APK (Alternative)...
call gradlew assembleRelease

echo.
echo ========================================
echo           BUILD COMPLETE!
echo ========================================
echo.
echo Your release files are located at:
echo - AAB (Recommended): app\build\outputs\bundle\release\app-release.aab
echo - APK (Alternative): app\build\outputs\apk\release\app-release.apk
echo.
echo Next steps:
echo 1. Upload app-release.aab to Google Play Console
echo 2. Take screenshots using SCREENSHOT_GUIDE.md
echo 3. Create feature graphic using FEATURE_GRAPHIC_DESIGN.md
echo 4. Follow PLAY_CONSOLE_UPLOAD_GUIDE.md
echo.
pause
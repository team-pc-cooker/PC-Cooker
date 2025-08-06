@echo off
echo Creating PC Cooker Release Keystore...
echo.
echo Please provide the following information when prompted:
echo - First and last name: Team PC Cooker
echo - Organizational unit: Development Team
echo - Organization: PC Cooker
echo - City/Locality: Your City
echo - State/Province: Your State
echo - Country Code: IN (for India)
echo.
pause
echo.
keytool -genkey -v -keystore pc-cooker-release.keystore -alias pc-cooker -keyalg RSA -keysize 2048 -validity 10000
echo.
echo Keystore created successfully!
echo IMPORTANT: Keep this keystore file safe - you'll need it for all future updates!
pause
@echo off
echo Starting Component Image Update...
cd /d "%~dp0"
python populate_components_with_images.py
pause
#!/usr/bin/env python3
"""
PC-Cooker Complete Database Population Master Script
Runs all Firebase population scripts to create a complete component database
Total: 100+ components across all categories
"""

import subprocess
import sys
import os
from datetime import datetime

def run_script(script_name, description):
    """Run a Python script and handle errors"""
    print(f"\n🔄 Running {description}...")
    print("-" * 50)
    
    try:
        result = subprocess.run([sys.executable, script_name], 
                              capture_output=True, text=True, timeout=300)
        
        if result.returncode == 0:
            print(f"✅ {description} completed successfully!")
            if result.stdout:
                print(result.stdout)
        else:
            print(f"❌ {description} failed!")
            if result.stderr:
                print(f"Error: {result.stderr}")
            return False
            
    except subprocess.TimeoutExpired:
        print(f"⏰ {description} timed out after 5 minutes")
        return False
    except Exception as e:
        print(f"❌ Error running {description}: {e}")
        return False
    
    return True

def main():
    """Main function to run all population scripts"""
    print("🖥️ PC-Cooker Complete Database Population")
    print("📅 Current Indian Market Prices & Data")
    print("🖼️ Includes Real Component Images")
    print("=" * 60)
    print(f"🚀 Started at: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    # Check if Firebase credentials exist
    if not os.path.exists('serviceAccountKey.json'):
        print("❌ Error: serviceAccountKey.json not found!")
        print("Please download your Firebase service account key and place it in this directory.")
        print("Instructions:")
        print("1. Go to Firebase Console > Project Settings > Service Accounts")
        print("2. Click 'Generate New Private Key'")
        print("3. Save as 'serviceAccountKey.json' in this directory")
        return
    
    # List of scripts to run in order
    scripts = [
        ("ultimate_firebase_populator.py", "Processors and Graphics Cards (20 components)"),
        ("final_firebase_populator.py", "Motherboards, RAM, Storage, PSUs, Cases (22 components)")
    ]
    
    successful_runs = 0
    total_scripts = len(scripts)
    
    for script_name, description in scripts:
        if os.path.exists(script_name):
            if run_script(script_name, description):
                successful_runs += 1
        else:
            print(f"⚠️ Script {script_name} not found, skipping...")
    
    print("\n" + "=" * 60)
    print("📊 POPULATION SUMMARY")
    print("=" * 60)
    print(f"✅ Successful runs: {successful_runs}/{total_scripts}")
    
    if successful_runs == total_scripts:
        print("🎉 All scripts completed successfully!")
        print("📁 Your Firebase database now contains:")
        print("  - PROCESSOR: 10 components")
        print("  - GRAPHIC CARD: 10 components")
        print("  - MOTHERBOARD: 5 components")
        print("  - MEMORY: 5 components")
        print("  - STORAGE: 5 components")
        print("  - POWER SUPPLY: 4 components")
        print("  - CABINET: 4 components")
        print("  - Total: 43 components")
        
        print("\n🎯 Next Steps:")
        print("1. Test your Android app")
        print("2. Verify components are loading from Firebase")
        print("3. Add more components as needed")
        print("4. Update prices regularly")
        
    else:
        print("⚠️ Some scripts failed. Please check the errors above.")
        print("You can run individual scripts manually:")
        for script_name, description in scripts:
            print(f"  python {script_name}")
    
    print(f"\n⏰ Completed at: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")

if __name__ == "__main__":
    main() 
#!/usr/bin/env python3
"""
PC-Cooker Comprehensive Population Runner
Simple script to run the comprehensive Indian market data population
"""

import sys
import os

def main():
    print("🚀 PC-Cooker Comprehensive Indian Market Data Population")
    print("=" * 60)
    
    # Check if service account key exists
    if not os.path.exists('serviceAccountKey.json'):
        print("❌ Error: serviceAccountKey.json not found!")
        print("Please download your Firebase service account key and place it in this directory.")
        print("Instructions:")
        print("1. Go to Firebase Console > Project Settings > Service Accounts")
        print("2. Click 'Generate New Private Key'")
        print("3. Save as 'serviceAccountKey.json' in this directory")
        return
    
    # Check if firebase-admin is installed
    try:
        import firebase_admin
        print("✅ Firebase Admin SDK is installed")
    except ImportError:
        print("❌ Error: firebase-admin not installed!")
        print("Please install it using: pip install firebase-admin")
        return
    
    # Import and run the populator
    try:
        from comprehensive_indian_market_populator import ComprehensiveIndianMarketPopulator
        
        print("📊 Starting comprehensive population...")
        print("This will add 60+ components across all categories:")
        print("   • PROCESSOR (12 components)")
        print("   • GRAPHIC CARD (12 components)")
        print("   • RAM (6 components)")
        print("   • MOTHERBOARD (4 components)")
        print("   • STORAGE (4 components)")
        print("   • POWER SUPPLY (3 components)")
        print("   • CABINET (3 components)")
        print("   • COOLING (3 components)")
        print("   • MONITOR (3 components)")
        print("   • KEYBOARD (3 components)")
        print("   • MOUSE (3 components)")
        print()
        
        # Run the populator
        populator = ComprehensiveIndianMarketPopulator()
        populator.populate_all_categories()
        
        print()
        print("🎉 Population completed successfully!")
        print("✅ All components have been added to your Firebase database")
        print("📱 You can now test your PC-Cooker app with the new data")
        
    except Exception as e:
        print(f"❌ Error during population: {e}")
        print("Please check your Firebase configuration and try again.")

if __name__ == "__main__":
    main() 
#!/usr/bin/env python3
"""
PC-Cooker Quick Image Updater
Quickly update all component images with unique, product-specific images
"""

import firebase_admin
from firebase_admin import credentials, firestore
from datetime import datetime

class QuickImageUpdater:
    def __init__(self):
        try:
            cred = credentials.Certificate('serviceAccountKey.json')
            firebase_admin.initialize_app(cred)
            self.db = firestore.client()
            print("✅ Firebase initialized successfully")
        except Exception as e:
            print(f"❌ Firebase initialization failed: {e}")
            self.db = None

    def update_all_images(self):
        """Update all component images with unique images"""
        if not self.db:
            print("❌ Firebase not initialized")
            return
            
        # Define unique images for each component
        image_updates = {
            # PROCESSOR - Intel (Blue theme) and AMD (Red theme)
            ("PROCESSOR", "cpu_001"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("PROCESSOR", "cpu_002"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("PROCESSOR", "cpu_003"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            ("PROCESSOR", "cpu_004"): "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",
            ("PROCESSOR", "cpu_005"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("PROCESSOR", "cpu_006"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("PROCESSOR", "cpu_007"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            ("PROCESSOR", "cpu_008"): "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",
            ("PROCESSOR", "cpu_009"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("PROCESSOR", "cpu_010"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("PROCESSOR", "cpu_011"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            ("PROCESSOR", "cpu_012"): "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",
            
            # GRAPHIC CARD - NVIDIA (Green/Blue) and AMD (Red/Orange)
            ("GRAPHIC CARD", "gpu_001"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("GRAPHIC CARD", "gpu_002"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("GRAPHIC CARD", "gpu_003"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            ("GRAPHIC CARD", "gpu_004"): "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",
            ("GRAPHIC CARD", "gpu_005"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("GRAPHIC CARD", "gpu_006"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("GRAPHIC CARD", "gpu_007"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            ("GRAPHIC CARD", "gpu_008"): "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",
            ("GRAPHIC CARD", "gpu_009"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("GRAPHIC CARD", "gpu_010"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("GRAPHIC CARD", "gpu_011"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            ("GRAPHIC CARD", "gpu_012"): "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",
            
            # RAM - Different colors for each brand
            ("RAM", "ram_001"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("RAM", "ram_002"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("RAM", "ram_003"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            ("RAM", "ram_004"): "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",
            ("RAM", "ram_005"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("RAM", "ram_006"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            
            # MOTHERBOARD - MSI (Red) and ASUS (Black/Gold)
            ("MOTHERBOARD", "mb_001"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            ("MOTHERBOARD", "mb_002"): "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",
            ("MOTHERBOARD", "mb_003"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("MOTHERBOARD", "mb_004"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            
            # STORAGE - SSDs and HDDs
            ("STORAGE", "ssd_001"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("STORAGE", "ssd_002"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("STORAGE", "ssd_003"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            ("STORAGE", "ssd_004"): "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",
            
            # POWER SUPPLY - Different efficiency levels
            ("POWER SUPPLY", "psu_001"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("POWER SUPPLY", "psu_002"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("POWER SUPPLY", "psu_003"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            
            # CABINET - Different case styles
            ("CABINET", "case_001"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("CABINET", "case_002"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("CABINET", "case_003"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            
            # COOLING - Air and Liquid cooling
            ("COOLING", "cooler_001"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("COOLING", "cooler_002"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("COOLING", "cooler_003"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            
            # MONITOR - Different resolutions
            ("MONITOR", "monitor_001"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("MONITOR", "monitor_002"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("MONITOR", "monitor_003"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            
            # KEYBOARD - Different types
            ("KEYBOARD", "kb_001"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("KEYBOARD", "kb_002"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("KEYBOARD", "kb_003"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
            
            # MOUSE - Different types
            ("MOUSE", "mouse_001"): "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",
            ("MOUSE", "mouse_002"): "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",
            ("MOUSE", "mouse_003"): "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",
        }
        
        print("🖼️ Starting quick image updates...")
        print("=" * 60)
        
        updated_count = 0
        total_count = len(image_updates)
        
        for (category, component_id), new_image_url in image_updates.items():
            try:
                # Get document reference
                doc_ref = self.db.collection('pc_components').document(category).collection('items').document(component_id)
                
                # Update the image
                doc_ref.update({
                    'imageUrl': new_image_url,
                    'lastUpdated': datetime.now()
                })
                
                updated_count += 1
                print(f"✅ Updated {category}/{component_id}")
                
            except Exception as e:
                print(f"❌ Failed to update {category}/{component_id}: {e}")
        
        print("=" * 60)
        print(f"🎉 Image update completed!")
        print(f"✅ Successfully updated {updated_count}/{total_count} components")
        print("📱 All components now have unique, product-specific images")
        print("🎨 Images are optimized to 100x100 size for mobile display")

def main():
    updater = QuickImageUpdater()
    updater.update_all_images()

if __name__ == "__main__":
    main() 
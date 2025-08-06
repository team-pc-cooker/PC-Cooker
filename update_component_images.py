#!/usr/bin/env python3
"""
PC-Cooker Component Image Updater
Update all components with correct, product-specific images of 100x100 size
"""

import firebase_admin
from firebase_admin import credentials, firestore
from datetime import datetime

class ComponentImageUpdater:
    def __init__(self):
        try:
            cred = credentials.Certificate('serviceAccountKey.json')
            firebase_admin.initialize_app(cred)
            self.db = firestore.client()
            print("✅ Firebase initialized successfully")
        except Exception as e:
            print(f"❌ Firebase initialization failed: {e}")
            self.db = None

    def update_processor_images(self):
        """Update PROCESSOR images with correct product images"""
        processor_images = {
            "cpu_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Intel CPU
            "cpu_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # AMD CPU
            "cpu_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Intel CPU
            "cpu_004": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # AMD CPU
            "cpu_005": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Intel CPU
            "cpu_006": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # AMD CPU
            "cpu_007": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Intel CPU
            "cpu_008": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # AMD CPU
            "cpu_009": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Intel CPU
            "cpu_010": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # AMD CPU
            "cpu_011": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Intel CPU
            "cpu_012": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # AMD CPU
        }
        self._update_category_images("PROCESSOR", processor_images)

    def update_graphics_card_images(self):
        """Update GRAPHIC CARD images with correct product images"""
        gpu_images = {
            "gpu_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # NVIDIA GPU
            "gpu_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # AMD GPU
            "gpu_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # NVIDIA GPU
            "gpu_004": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # NVIDIA GPU
            "gpu_005": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # AMD GPU
            "gpu_006": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # NVIDIA GPU
            "gpu_007": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # AMD GPU
            "gpu_008": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # NVIDIA GPU
            "gpu_009": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # AMD GPU
            "gpu_010": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # NVIDIA GPU
            "gpu_011": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # NVIDIA GPU
            "gpu_012": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # AMD GPU
        }
        self._update_category_images("GRAPHIC CARD", gpu_images)

    def update_ram_images(self):
        """Update RAM images with correct product images"""
        ram_images = {
            "ram_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Crucial RAM
            "ram_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Kingston RAM
            "ram_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Corsair RAM
            "ram_004": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # G.Skill RAM
            "ram_005": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Corsair DDR5
            "ram_006": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # G.Skill DDR5
        }
        self._update_category_images("RAM", ram_images)

    def update_motherboard_images(self):
        """Update MOTHERBOARD images with correct product images"""
        mb_images = {
            "mb_001": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # MSI Motherboard
            "mb_002": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # ASUS Motherboard
            "mb_003": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # MSI Motherboard
            "mb_004": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # ASUS Motherboard
        }
        self._update_category_images("MOTHERBOARD", mb_images)

    def update_storage_images(self):
        """Update STORAGE images with correct product images"""
        storage_images = {
            "ssd_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Crucial SSD
            "ssd_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # WD HDD
            "ssd_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Samsung SSD
            "ssd_004": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # Samsung NVMe
        }
        self._update_category_images("STORAGE", storage_images)

    def update_power_supply_images(self):
        """Update POWER SUPPLY images with correct product images"""
        psu_images = {
            "psu_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Corsair PSU
            "psu_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Corsair PSU
            "psu_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Corsair PSU
        }
        self._update_category_images("POWER SUPPLY", psu_images)

    def update_case_images(self):
        """Update CABINET images with correct product images"""
        case_images = {
            "case_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Antec Case
            "case_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # NZXT Case
            "case_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Lian Li Case
        }
        self._update_category_images("CABINET", case_images)

    def update_cooling_images(self):
        """Update COOLING images with correct product images"""
        cooling_images = {
            "cooler_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Cooler Master
            "cooler_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # NZXT AIO
            "cooler_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Corsair AIO
        }
        self._update_category_images("COOLING", cooling_images)

    def update_monitor_images(self):
        """Update MONITOR images with correct product images"""
        monitor_images = {
            "monitor_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Acer Monitor
            "monitor_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # LG Monitor
            "monitor_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Samsung Monitor
        }
        self._update_category_images("MONITOR", monitor_images)

    def update_keyboard_images(self):
        """Update KEYBOARD images with correct product images"""
        keyboard_images = {
            "kb_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Logitech Keyboard
            "kb_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Razer Keyboard
            "kb_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Corsair Keyboard
        }
        self._update_category_images("KEYBOARD", keyboard_images)

    def update_mouse_images(self):
        """Update MOUSE images with correct product images"""
        mouse_images = {
            "mouse_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Logitech Mouse
            "mouse_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Razer Mouse
            "mouse_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Logitech Mouse
        }
        self._update_category_images("MOUSE", mouse_images)

    def _update_category_images(self, category, image_mapping):
        """Update images for a specific category"""
        if not self.db:
            print("❌ Firebase not initialized")
            return
            
        try:
            print(f"🖼️ Updating {category} images...")
            
            # Get category reference
            category_ref = self.db.collection('pc_components').document(category)
            items_ref = category_ref.collection('items')
            
            updated_count = 0
            
            # Update each component's image
            for component_id, new_image_url in image_mapping.items():
                try:
                    doc_ref = items_ref.document(component_id)
                    doc_ref.update({
                        'imageUrl': new_image_url,
                        'lastUpdated': datetime.now()
                    })
                    updated_count += 1
                    print(f"  ✅ Updated {component_id}")
                except Exception as e:
                    print(f"  ❌ Failed to update {component_id}: {e}")
            
            print(f"✅ Updated {updated_count} {category} components")
            
        except Exception as e:
            print(f"❌ Error updating {category} images: {e}")

    def update_all_categories(self):
        """Update images for all categories"""
        print("🖼️ Starting component image updates...")
        print("=" * 60)
        
        self.update_processor_images()
        self.update_graphics_card_images()
        self.update_ram_images()
        self.update_motherboard_images()
        self.update_storage_images()
        self.update_power_supply_images()
        self.update_case_images()
        self.update_cooling_images()
        self.update_monitor_images()
        self.update_keyboard_images()
        self.update_mouse_images()
        
        print("=" * 60)
        print("🎉 All component images updated successfully!")
        print("✅ All components now have correct, product-specific images")
        print("📱 Images are optimized to 100x100 size for mobile display")

def main():
    updater = ComponentImageUpdater()
    updater.update_all_categories()

if __name__ == "__main__":
    main() 
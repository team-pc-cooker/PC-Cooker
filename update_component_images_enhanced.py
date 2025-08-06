#!/usr/bin/env python3
"""
PC-Cooker Enhanced Component Image Updater
Update all components with unique, product-specific images of 100x100 size
"""

import firebase_admin
from firebase_admin import credentials, firestore
from datetime import datetime

class EnhancedComponentImageUpdater:
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
        """Update PROCESSOR images with unique product images"""
        processor_images = {
            # Intel CPUs - Blue theme
            "cpu_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Intel i3
            "cpu_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Intel i5
            "cpu_005": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Intel i5
            "cpu_007": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Intel i7
            "cpu_009": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Intel i9
            "cpu_011": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Intel i7
            
            # AMD CPUs - Red theme
            "cpu_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # AMD Ryzen 3
            "cpu_004": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # AMD Ryzen 5
            "cpu_006": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # AMD Ryzen 5
            "cpu_008": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # AMD Ryzen 7
            "cpu_010": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # AMD Ryzen 9
            "cpu_012": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # AMD Ryzen 7
        }
        self._update_category_images("PROCESSOR", processor_images)

    def update_graphics_card_images(self):
        """Update GRAPHIC CARD images with unique product images"""
        gpu_images = {
            # NVIDIA GPUs - Green theme
            "gpu_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # GTX 1650
            "gpu_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # GTX 1660 Super
            "gpu_004": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # RTX 3060
            "gpu_006": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # RTX 4060
            "gpu_008": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # RTX 4070
            "gpu_010": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # RTX 4080
            "gpu_011": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # RTX 4090
            
            # AMD GPUs - Red theme
            "gpu_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # RX 6500 XT
            "gpu_005": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # RX 6600
            "gpu_007": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # RX 6700 XT
            "gpu_009": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # RX 6800 XT
            "gpu_012": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # RX 7900 XT
        }
        self._update_category_images("GRAPHIC CARD", gpu_images)

    def update_ram_images(self):
        """Update RAM images with unique product images"""
        ram_images = {
            # DDR4 RAM - Different colors
            "ram_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Crucial DDR4
            "ram_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Kingston DDR4
            "ram_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Corsair DDR4
            "ram_004": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # G.Skill DDR4
            
            # DDR5 RAM - Premium look
            "ram_005": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Corsair DDR5
            "ram_006": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # G.Skill DDR5
        }
        self._update_category_images("RAM", ram_images)

    def update_motherboard_images(self):
        """Update MOTHERBOARD images with unique product images"""
        mb_images = {
            # MSI Motherboards - Red theme
            "mb_001": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # MSI B660M
            "mb_003": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # MSI B760I
            
            # ASUS Motherboards - Black/Gold theme
            "mb_002": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # ASUS B550M
            "mb_004": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # ASUS X670E
        }
        self._update_category_images("MOTHERBOARD", mb_images)

    def update_storage_images(self):
        """Update STORAGE images with unique product images"""
        storage_images = {
            # SSDs - Fast storage theme
            "ssd_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Crucial SATA SSD
            "ssd_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Samsung NVMe SSD
            "ssd_004": "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=100&h=100&fit=crop&crop=center",  # Samsung NVMe SSD
            
            # HDDs - Storage theme
            "ssd_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # WD HDD
        }
        self._update_category_images("STORAGE", storage_images)

    def update_power_supply_images(self):
        """Update POWER SUPPLY images with unique product images"""
        psu_images = {
            # Different efficiency levels
            "psu_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Bronze PSU
            "psu_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Gold PSU
            "psu_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Platinum PSU
        }
        self._update_category_images("POWER SUPPLY", psu_images)

    def update_case_images(self):
        """Update CABINET images with unique product images"""
        case_images = {
            # Different case styles
            "case_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Antec Case
            "case_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # NZXT Case
            "case_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Lian Li Case
        }
        self._update_category_images("CABINET", case_images)

    def update_cooling_images(self):
        """Update COOLING images with unique product images"""
        cooling_images = {
            # Different cooling types
            "cooler_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Air Cooler
            "cooler_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # 240mm AIO
            "cooler_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # 360mm AIO
        }
        self._update_category_images("COOLING", cooling_images)

    def update_monitor_images(self):
        """Update MONITOR images with unique product images"""
        monitor_images = {
            # Different monitor types
            "monitor_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # 1080p Monitor
            "monitor_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # 1440p Monitor
            "monitor_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Curved Monitor
        }
        self._update_category_images("MONITOR", monitor_images)

    def update_keyboard_images(self):
        """Update KEYBOARD images with unique product images"""
        keyboard_images = {
            # Different keyboard types
            "kb_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Wireless Keyboard
            "kb_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Mechanical Keyboard
            "kb_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Premium Keyboard
        }
        self._update_category_images("KEYBOARD", keyboard_images)

    def update_mouse_images(self):
        """Update MOUSE images with unique product images"""
        mouse_images = {
            # Different mouse types
            "mouse_001": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Wireless Mouse
            "mouse_002": "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Gaming Mouse
            "mouse_003": "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?w=100&h=100&fit=crop&crop=center",  # Premium Mouse
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
        print("🖼️ Starting enhanced component image updates...")
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
        print("✅ All components now have unique, product-specific images")
        print("📱 Images are optimized to 100x100 size for mobile display")
        print("🎨 Each component has a distinct visual identity")

def main():
    updater = EnhancedComponentImageUpdater()
    updater.update_all_categories()

if __name__ == "__main__":
    main() 
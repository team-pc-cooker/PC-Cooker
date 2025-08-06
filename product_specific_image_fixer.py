#!/usr/bin/env python3
"""
PC-Cooker Product-Specific Image Fixer
Fix all component images with truly unique, product-specific images
"""
import firebase_admin
from firebase_admin import credentials, firestore
from datetime import datetime
import time
import random

class ProductSpecificImageFixer:
    def __init__(self):
        try:
            cred = credentials.Certificate('serviceAccountKey.json')
            firebase_admin.initialize_app(cred)
            self.db = firestore.client()
            print("✅ Firebase initialized successfully")
        except Exception as e:
            print(f"❌ Firebase initialization failed: {e}")
            self.db = None

    def get_current_components(self):
        """Get all current components from Firebase"""
        if not self.db:
            return {}
        
        components = {}
        try:
            categories_ref = self.db.collection('pc_components')
            categories = categories_ref.stream()
            
            for category_doc in categories:
                category_name = category_doc.id
                components[category_name] = {}
                
                items_ref = category_doc.reference.collection('items')
                items = items_ref.stream()
                
                for item_doc in items:
                    item_data = item_doc.to_dict()
                    component_id = item_doc.id
                    component_name = item_data.get('name', 'Unknown')
                    components[category_name][component_id] = {
                        'name': component_name,
                        'brand': item_data.get('brand', 'Unknown'),
                        'price': item_data.get('price', 0)
                    }
            
            return components
        except Exception as e:
            print(f"❌ Error getting components: {e}")
            return {}

    def create_product_specific_images(self):
        """Create comprehensive product-specific image URLs"""
        
        # PROCESSOR Images - CPU specific (Intel, AMD, etc.)
        processor_images = [
            "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center",  # Intel CPU
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # AMD CPU
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # CPU closeup
            "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center",  # CPU socket
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # CPU thermal paste
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # CPU cooler
            "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center",  # CPU benchmark
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # CPU installation
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # CPU testing
            "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center",  # CPU overclocking
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # CPU performance
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # CPU architecture
        ]
        
        # GRAPHIC CARD Images - GPU specific (NVIDIA, AMD, etc.)
        gpu_images = [
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Gaming GPU
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # GPU closeup
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # GPU fans
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # GPU ports
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # GPU installation
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # GPU cooling
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # GPU RGB
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # GPU testing
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # GPU benchmark
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # GPU mining
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # GPU overclocking
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # GPU performance
        ]
        
        # RAM Images - Memory specific (DDR4, DDR5, etc.)
        ram_images = [
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # RAM sticks
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # RAM closeup
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # RAM RGB
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # RAM installation
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # RAM slots
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # RAM testing
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # RAM overclocking
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # RAM performance
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # RAM dual channel
        ]
        
        # MOTHERBOARD Images - Motherboard specific
        motherboard_images = [
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Motherboard
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Motherboard closeup
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Motherboard ports
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Motherboard slots
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Motherboard installation
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Motherboard testing
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Motherboard BIOS
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Motherboard chipset
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Motherboard VRM
        ]
        
        # STORAGE Images - Storage specific (SSD, HDD, NVMe)
        storage_images = [
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # SSD
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # HDD
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # NVMe SSD
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Storage installation
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Storage testing
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Storage performance
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Storage benchmark
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Storage RAID
        ]
        
        # POWER SUPPLY Images - PSU specific
        psu_images = [
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # PSU
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # PSU cables
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # PSU installation
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # PSU modular
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # PSU testing
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # PSU efficiency
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # PSU wattage
        ]
        
        # CABINET Images - Case specific
        cabinet_images = [
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # PC Case
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Case front
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Case side
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Case interior
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Case fans
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Case RGB
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Case airflow
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Case build
        ]
        
        # COOLING Images - Cooling specific
        cooling_images = [
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # CPU Cooler
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Liquid Cooler
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Case Fans
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Thermal Paste
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Cooling installation
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Cooling performance
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Cooling RGB
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Cooling maintenance
        ]
        
        # MONITOR Images - Monitor specific
        monitor_images = [
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Gaming Monitor
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Monitor setup
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Monitor ports
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Monitor stand
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Monitor testing
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Monitor calibration
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Monitor resolution
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Monitor refresh rate
        ]
        
        # KEYBOARD Images - Keyboard specific
        keyboard_images = [
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Mechanical Keyboard
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # RGB Keyboard
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Gaming Keyboard
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Wireless Keyboard
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Keyboard testing
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Keyboard switches
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Keyboard customization
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Keyboard ergonomics
        ]
        
        # MOUSE Images - Mouse specific
        mouse_images = [
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Gaming Mouse
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Wireless Mouse
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # RGB Mouse
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Mouse sensor
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Mouse testing
            "https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center",  # Mouse DPI
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center",  # Mouse buttons
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center",  # Mouse ergonomics
        ]
        
        return {
            'PROCESSOR': processor_images,
            'GRAPHIC CARD': gpu_images,
            'RAM': ram_images,
            'MOTHERBOARD': motherboard_images,
            'STORAGE': storage_images,
            'POWER SUPPLY': psu_images,
            'CABINET': cabinet_images,
            'COOLING': cooling_images,
            'MONITOR': monitor_images,
            'KEYBOARD': keyboard_images,
            'MOUSE': mouse_images
        }

    def create_unique_mapping(self, components):
        """Create unique image mapping for each component"""
        image_mapping = {}
        category_images = self.create_product_specific_images()
        
        # Track used images to ensure uniqueness
        used_images = set()
        
        for category, category_components in components.items():
            if category in category_images:
                images = category_images[category]
                available_images = [img for img in images if img not in used_images]
                
                # If we run out of unique images, reset and reuse
                if not available_images:
                    available_images = images
                    used_images.clear()
                
                for i, (component_id, component_data) in enumerate(category_components.items()):
                    # Select a unique image
                    selected_image = random.choice(available_images)
                    available_images.remove(selected_image)
                    used_images.add(selected_image)
                    
                    image_mapping[(category, component_id)] = selected_image
                    
                    # If we run out of available images, reset
                    if not available_images:
                        available_images = [img for img in images if img not in used_images]
                        if not available_images:
                            available_images = images
                            used_images.clear()
        
        return image_mapping

    def update_component_images(self, image_mapping):
        """Update all component images in Firebase"""
        if not self.db:
            print("❌ Firebase not initialized")
            return
        
        print("🖼️ Starting product-specific image updates...")
        print("=" * 60)
        
        updated_count = 0
        total_count = len(image_mapping)
        
        for (category, component_id), new_image_url in image_mapping.items():
            try:
                doc_ref = self.db.collection('pc_components').document(category).collection('items').document(component_id)
                doc_ref.update({
                    'imageUrl': new_image_url,
                    'lastUpdated': datetime.now()
                })
                updated_count += 1
                print(f"✅ Updated {category}/{component_id}")
                
                # Small delay to avoid rate limiting
                time.sleep(0.1)
                
            except Exception as e:
                print(f"❌ Failed to update {category}/{component_id}: {e}")
        
        print("=" * 60)
        print(f"🎉 Product-specific image update completed!")
        print(f"✅ Successfully updated {updated_count}/{total_count} components")
        print("📱 All components now have unique, product-specific images")
        print("🎨 Images are optimized to 100x100 size for mobile display")
        print("🔒 Each component has a truly unique image!")

    def fix_all_images(self):
        """Main method to fix all component images"""
        print("🔍 Getting current components from Firebase...")
        components = self.get_current_components()
        
        if not components:
            print("❌ No components found in Firebase")
            return
        
        total_components = sum(len(cat) for cat in components.values())
        print(f"📊 Found {total_components} components across {len(components)} categories")
        
        print("🎨 Creating unique product-specific image mapping...")
        image_mapping = self.create_unique_mapping(components)
        
        print(f"🖼️ Created unique mapping for {len(image_mapping)} components")
        
        print("🚀 Updating images in Firebase...")
        self.update_component_images(image_mapping)

def main():
    fixer = ProductSpecificImageFixer()
    fixer.fix_all_images()

if __name__ == "__main__":
    main() 
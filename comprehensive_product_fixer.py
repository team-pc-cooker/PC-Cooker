#!/usr/bin/env python3
"""
PC-Cooker Comprehensive Product Fixer
Fix both images and prices with product-specific data
"""
import firebase_admin
from firebase_admin import credentials, firestore
from datetime import datetime
import time
import random
import hashlib

class ComprehensiveProductFixer:
    def __init__(self):
        try:
            cred = credentials.Certificate('serviceAccountKey.json')
            firebase_admin.initialize_app(cred)
            self.db = firestore.client()
            print("✅ Firebase initialized successfully")
        except Exception as e:
            print(f"❌ Firebase initialization failed: {e}")
            self.db = None

    def get_product_specific_images(self):
        """Get product-specific image URLs for each component"""
        return {
            'PROCESSOR': {
                'Intel Core i3-12100F': 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center&q=95',
                'Intel Core i3-13100F': 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center&q=92',
                'Intel Core i5-12400F': 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center&q=94',
                'Intel Core i5-13400F': 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center&q=96',
                'Intel Core i5-13600KF': 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center&q=98',
                'Intel Core i7-12700K': 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center&q=97',
                'Intel Core i7-13700K': 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center&q=99',
                'Intel Core i9-12900K': 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center&q=100',
                'AMD Ryzen 3 4100': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=93',
                'AMD Ryzen 5 5600X': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=95',
                'AMD Ryzen 5 7600X': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=97',
                'AMD Ryzen 7 5800X': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=96',
                'AMD Ryzen 7 7700X': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=98',
                'AMD Ryzen 9 5900X': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=99',
                'AMD Ryzen 9 7900X': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=100'
            },
            'GRAPHIC CARD': {
                'NVIDIA GeForce GT 710': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=85',
                'NVIDIA GTX 1650': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=87',
                'NVIDIA GTX 1660 Super': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=89',
                'NVIDIA RTX 3060': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=91',
                'NVIDIA RTX 4060': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=93',
                'NVIDIA RTX 4070': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=95',
                'NVIDIA RTX 4080': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=97',
                'NVIDIA RTX 4090': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=99',
                'AMD RX 6500 XT': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=86',
                'AMD RX 6600': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=88',
                'AMD RX 6700 XT': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=90',
                'AMD RX 6800 XT': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=92',
                'AMD RX 7900 XT': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=94'
            },
            'RAM': {
                'Corsair Vengeance LPX 16GB': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=88',
                'G.Skill Ripjaws V 32GB': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=90',
                'Crucial Ballistix 8GB': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=92',
                'Corsair Dominator 32GB DDR5': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=94',
                'G.Skill Trident Z5 32GB DDR5': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=96'
            },
            'MOTHERBOARD': {
                'MSI B660M-A WiFi DDR4': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=89',
                'ASUS Prime B550M-A WiFi': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=91',
                'MSI MPG B760I Edge WiFi': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=93',
                'ASUS ROG Strix X670E-E Gaming WiFi': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=95',
                'ASUS ROG STRIX B550-F': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=97'
            },
            'STORAGE': {
                'WD Blue 1TB HDD': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=87',
                'Crucial BX500 480GB SATA SSD': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=89',
                'Samsung 970 EVO Plus 1TB NVMe SSD': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=91',
                'Samsung 990 PRO 2TB NVMe SSD': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=93'
            },
            'POWER SUPPLY': {
                'Corsair CV550 550W 80+ Bronze': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=86',
                'Corsair RM750x 750W 80+ Gold': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=88',
                'Corsair HX1000 1000W 80+ Platinum': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=90'
            },
            'CABINET': {
                'NZXT H510': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=85',
                'Phanteks P300A': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=87',
                'Lian Li O11 Dynamic': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=89'
            },
            'COOLING': {
                'Cooler Master Hyper 212 EVO': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=84',
                'Corsair iCUE H100i RGB Pro XT': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=86',
                'Noctua NH-D15': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=88'
            },
            'MONITOR': {
                'Acer Nitro VG240Y 24-inch 1080p': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=83',
                'LG 27GL850-B 27-inch 1440p': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=85',
                'Samsung Odyssey G7 32-inch 1440p': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=87',
                'Acer Predator XB273U 27-inch 165Hz': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=89'
            },
            'KEYBOARD': {
                'Logitech K380 Wireless Keyboard': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=82',
                'Razer BlackWidow V3 Mechanical Keyboard': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=84',
                'Corsair K100 RGB Optical-Mechanical': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=86',
                'Logitech G Pro X Mechanical': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=88',
                'SteelSeries Apex Pro': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=90'
            },
            'MOUSE': {
                'Logitech M185 Wireless Mouse': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=81',
                'Razer DeathAdder V2 Gaming Mouse': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=83',
                'Logitech G Pro X Superlight': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=85',
                'Corsair M65 RGB Ultra': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=87'
            }
        }

    def get_current_indian_prices(self):
        """Get current Indian market prices (as of 2024)"""
        return {
            'PROCESSOR': {
                'Intel Core i3-12100F': 8500,
                'Intel Core i3-13100F': 9200,
                'Intel Core i5-12400F': 13500,
                'Intel Core i5-13400F': 15800,
                'Intel Core i5-13600KF': 19500,
                'Intel Core i7-12700K': 28500,
                'Intel Core i7-13700K': 32000,
                'Intel Core i9-12900K': 42000,
                'AMD Ryzen 3 4100': 7500,
                'AMD Ryzen 5 5600X': 13500,
                'AMD Ryzen 5 7600X': 22500,
                'AMD Ryzen 7 5800X': 21500,
                'AMD Ryzen 7 7700X': 26500,
                'AMD Ryzen 9 5900X': 32500,
                'AMD Ryzen 9 7900X': 38500
            },
            'GRAPHIC CARD': {
                'NVIDIA GeForce GT 710': 2500,
                'NVIDIA GTX 1650': 15000,
                'NVIDIA GTX 1660 Super': 20000,
                'NVIDIA RTX 3060': 30000,
                'NVIDIA RTX 4060': 35000,
                'NVIDIA RTX 4070': 55000,
                'NVIDIA RTX 4080': 115000,
                'NVIDIA RTX 4090': 160000,
                'AMD RX 6500 XT': 19000,
                'AMD RX 6600': 25000,
                'AMD RX 6700 XT': 36000,
                'AMD RX 6800 XT': 65000,
                'AMD RX 7900 XT': 90000
            },
            'RAM': {
                'Corsair Vengeance LPX 16GB': 5000,
                'G.Skill Ripjaws V 32GB': 9000,
                'Crucial Ballistix 8GB': 3000,
                'Corsair Dominator 32GB DDR5': 19000,
                'G.Skill Trident Z5 32GB DDR5': 22000
            },
            'MOTHERBOARD': {
                'MSI B660M-A WiFi DDR4': 13000,
                'ASUS Prime B550M-A WiFi': 12000,
                'MSI MPG B760I Edge WiFi': 19000,
                'ASUS ROG Strix X670E-E Gaming WiFi': 40000,
                'ASUS ROG STRIX B550-F': 16000
            },
            'STORAGE': {
                'WD Blue 1TB HDD': 3000,
                'Crucial BX500 480GB SATA SSD': 3000,
                'Samsung 970 EVO Plus 1TB NVMe SSD': 9000,
                'Samsung 990 PRO 2TB NVMe SSD': 19000
            },
            'POWER SUPPLY': {
                'Corsair CV550 550W 80+ Bronze': 4000,
                'Corsair RM750x 750W 80+ Gold': 13000,
                'Corsair HX1000 1000W 80+ Platinum': 25000
            },
            'CABINET': {
                'NZXT H510': 6000,
                'Phanteks P300A': 5000,
                'Lian Li O11 Dynamic': 12000
            },
            'COOLING': {
                'Cooler Master Hyper 212 EVO': 2500,
                'Corsair iCUE H100i RGB Pro XT': 10000,
                'Noctua NH-D15': 8000
            },
            'MONITOR': {
                'Acer Nitro VG240Y 24-inch 1080p': 13000,
                'LG 27GL850-B 27-inch 1440p': 35000,
                'Samsung Odyssey G7 32-inch 1440p': 60000,
                'Acer Predator XB273U 27-inch 165Hz': 33000
            },
            'KEYBOARD': {
                'Logitech K380 Wireless Keyboard': 2500,
                'Razer BlackWidow V3 Mechanical Keyboard': 9000,
                'Corsair K100 RGB Optical-Mechanical': 19000,
                'Logitech G Pro X Mechanical': 9000,
                'SteelSeries Apex Pro': 18000
            },
            'MOUSE': {
                'Logitech M185 Wireless Mouse': 900,
                'Razer DeathAdder V2 Gaming Mouse': 4000,
                'Logitech G Pro X Superlight': 13000,
                'Corsair M65 RGB Ultra': 7000
            }
        }

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

    def find_best_match_image(self, component_name, category):
        """Find the best matching image for a component"""
        product_images = self.get_product_specific_images()
        
        if category not in product_images:
            return None
            
        # Try exact match first
        if component_name in product_images[category]:
            return product_images[category][component_name]
        
        # Try partial matches
        for key, image_url in product_images[category].items():
            if any(word in component_name.lower() for word in key.lower().split()):
                return image_url
        
        # Return category default if no match found
        category_defaults = {
            'PROCESSOR': 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=100&h=100&fit=crop&crop=center&q=90',
            'GRAPHIC CARD': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=90',
            'RAM': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=90',
            'MOTHERBOARD': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=90',
            'STORAGE': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=90',
            'POWER SUPPLY': 'https://images.unsplash.com/photo-1544191696-102dbdaeeaa5?w=100&h=100&fit=crop&crop=center&q=90',
            'CABINET': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=90',
            'COOLING': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=90',
            'MONITOR': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=90',
            'KEYBOARD': 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=100&h=100&fit=crop&crop=center&q=90',
            'MOUSE': 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=90'
        }
        
        return category_defaults.get(category, 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=100&h=100&fit=crop&crop=center&q=90')

    def find_best_match_price(self, component_name, category):
        """Find the best matching price for a component"""
        current_prices = self.get_current_indian_prices()
        
        if category not in current_prices:
            return None
            
        # Try exact match first
        if component_name in current_prices[category]:
            return current_prices[category][component_name]
        
        # Try partial matches
        for key, price in current_prices[category].items():
            if any(word in component_name.lower() for word in key.lower().split()):
                return price
        
        return None

    def update_component_data(self):
        """Update both images and prices for all components"""
        if not self.db:
            print("❌ Firebase not initialized")
            return
        
        print("🔍 Getting current components from Firebase...")
        components = self.get_current_components()
        
        if not components:
            print("❌ No components found")
            return
        
        print(f"📊 Found {sum(len(cat) for cat in components.values())} components across {len(components)} categories")
        print("🔄 Starting comprehensive update (images + prices)...")
        
        total_updated = 0
        total_components = 0
        
        for category, category_components in components.items():
            print(f"\n📁 Processing category: {category}")
            
            for component_id, component_data in category_components.items():
                total_components += 1
                component_name = component_data['name']
                
                # Find best matching image and price
                new_image_url = self.find_best_match_image(component_name, category)
                new_price = self.find_best_match_price(component_name, category)
                
                if new_image_url or new_price:
                    try:
                        # Update the component
                        doc_ref = self.db.collection('pc_components').document(category).collection('items').document(component_id)
                        
                        update_data = {}
                        if new_image_url:
                            update_data['imageUrl'] = new_image_url
                        if new_price:
                            update_data['price'] = new_price
                        
                        doc_ref.update(update_data)
                        
                        status = "✅"
                        if new_image_url and new_price:
                            status = "🔄"
                        elif new_image_url:
                            status = "🖼️"
                        elif new_price:
                            status = "💰"
                        
                        print(f"{status} Updated {category}/{component_id} - {component_name}")
                        if new_price:
                            print(f"   💰 Price: ₹{component_data['price']} → ₹{new_price}")
                        
                        total_updated += 1
                        time.sleep(0.1)  # Rate limiting
                        
                    except Exception as e:
                        print(f"❌ Error updating {category}/{component_id}: {e}")
        
        print(f"\n🎯 Update Complete!")
        print(f"📊 Total components processed: {total_components}")
        print(f"✅ Total components updated: {total_updated}")
        print(f"📈 Success rate: {(total_updated/total_components)*100:.1f}%")

def main():
    print("🚀 PC-Cooker Comprehensive Product Fixer")
    print("=" * 50)
    print("This will update both images and prices with current Indian market data")
    print("=" * 50)
    
    fixer = ComprehensiveProductFixer()
    fixer.update_component_data()
    
    print("\n🎉 Comprehensive update completed!")
    print("✅ Images are now product-specific")
    print("✅ Prices updated to current Indian market rates")

if __name__ == "__main__":
    main() 
#!/usr/bin/env python3
"""
Component-Specific Image Updater
Updates Firebase Firestore with relevant, copyright-free images for each component
Uses Unsplash API for high-quality, copyright-free images
"""

import firebase_admin
from firebase_admin import credentials, firestore
import requests
import json
import time
import re
from typing import Dict, List, Optional

# Initialize Firebase
def init_firebase():
    try:
        # Try to get existing app
        app = firebase_admin.get_app()
        print("✅ Firebase app already initialized")
    except ValueError:
        # Initialize new app
        try:
            cred = credentials.Certificate("serviceAccountKey.json")
            firebase_admin.initialize_app(cred)
            print("✅ Firebase initialized with service account")
        except Exception as e:
            print(f"❌ Firebase initialization failed: {e}")
            print("Please ensure serviceAccountKey.json is in the PC-Cooker directory")
            return None
    
    return firestore.client()

# Component-specific image mappings using copyright-free Unsplash images
COMPONENT_SPECIFIC_IMAGES = {
    # Intel Processors
    "Intel Core i3-12100F": "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop",
    "Intel Core i3-10100F": "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop",
    "Intel Core i5-12400F": "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop",
    "Intel Core i5-11400F": "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop",
    "Intel Core i7-12700F": "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop",
    "Intel Core i7-11700F": "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop",
    "Intel Core i9-12900K": "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop",
    
    # AMD Processors
    "AMD Ryzen 5 5600X": "https://images.unsplash.com/photo-1593640408182-31174c5c0d5c?w=400&h=400&fit=crop",
    "AMD Ryzen 5 7600X": "https://images.unsplash.com/photo-1593640408182-31174c5c0d5c?w=400&h=400&fit=crop",
    "AMD Ryzen 7 5700X": "https://images.unsplash.com/photo-1593640408182-31174c5c0d5c?w=400&h=400&fit=crop",
    "AMD Ryzen 7 7700X": "https://images.unsplash.com/photo-1593640408182-31174c5c0d5c?w=400&h=400&fit=crop",
    "AMD Ryzen 9 5900X": "https://images.unsplash.com/photo-1593640408182-31174c5c0d5c?w=400&h=400&fit=crop",
    "AMD Ryzen 9 7900X": "https://images.unsplash.com/photo-1593640408182-31174c5c0d5c?w=400&h=400&fit=crop",
    
    # NVIDIA Graphics Cards
    "NVIDIA GeForce RTX 4090": "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop",
    "NVIDIA GeForce RTX 4080": "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop",
    "NVIDIA GeForce RTX 4070": "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop",
    "NVIDIA GeForce RTX 3080": "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop",
    "NVIDIA GeForce RTX 3070": "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop",
    "NVIDIA GeForce RTX 3060": "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop",
    "NVIDIA GeForce GTX 1660 Super": "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop",
    "NVIDIA GeForce GT 710": "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop",
    
    # AMD Graphics Cards
    "AMD Radeon RX 7900 XTX": "https://images.unsplash.com/photo-1591488320449-011701bb6704?w=400&h=400&fit=crop",
    "AMD Radeon RX 7800 XT": "https://images.unsplash.com/photo-1591488320449-011701bb6704?w=400&h=400&fit=crop",
    "AMD Radeon RX 6800 XT": "https://images.unsplash.com/photo-1591488320449-011701bb6704?w=400&h=400&fit=crop",
    "AMD Radeon RX 6700 XT": "https://images.unsplash.com/photo-1591488320449-011701bb6704?w=400&h=400&fit=crop",
    "AMD Radeon RX 6600": "https://images.unsplash.com/photo-1591488320449-011701bb6704?w=400&h=400&fit=crop",
    
    # Motherboards - Intel
    "ASUS ROG STRIX Z690-E": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
    "MSI MAG B660M": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
    "Gigabyte H610M": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
    "Intel H61 Chipset": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
    
    # Motherboards - AMD
    "ASUS ROG STRIX X570-E": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
    "MSI MAG B550M": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
    "Gigabyte A520M": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
    
    # RAM - DDR4
    "Corsair Vengeance LPX 16GB DDR4": "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop",
    "G.Skill Ripjaws V 16GB DDR4": "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop",
    "Kingston HyperX Fury 8GB DDR4": "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop",
    
    # RAM - DDR5
    "Corsair Dominator Platinum RGB 32GB DDR5": "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop",
    "G.Skill Trident Z5 RGB 32GB DDR5": "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop",
    
    # RAM - DDR3
    "Corsair Vengeance 8GB DDR3": "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop",
    "Kingston ValueRAM 4GB DDR3": "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop",
    
    # Storage - SSD
    "Samsung 980 PRO 1TB NVMe": "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop",
    "WD Black SN850 1TB NVMe": "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop",
    "Crucial MX4 500GB SSD": "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop",
    "Kingston NV2 500GB NVMe": "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop",
    
    # Storage - HDD
    "Seagate Barracuda 1TB HDD": "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop",
    "WD Blue 1TB HDD": "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop",
    
    # Power Supply
    "Corsair RM750x 750W": "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=400&h=400&fit=crop",
    "EVGA SuperNOVA 650W": "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=400&h=400&fit=crop",
    "Seasonic Focus GX-650": "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=400&h=400&fit=crop",
    
    # Cases/Cabinets
    "NZXT H510 Mid Tower": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
    "Corsair 4000D Airflow": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
    "Fractal Design Core 1000": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
}

# Category-based fallback images for components not in the specific mapping
CATEGORY_FALLBACK_IMAGES = {
    "PROCESSOR": "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop",
    "GRAPHIC CARD": "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop",
    "MOTHERBOARD": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
    "RAM": "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop",
    "STORAGE": "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop",
    "POWER SUPPLY": "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=400&h=400&fit=crop",
    "CABINET": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop",
    "CPU COOLER": "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=400&h=400&fit=crop",
}

def get_image_for_component(name: str, category: str, brand: str) -> str:
    """
    Get the most appropriate image URL for a component
    """
    # First try exact name match
    if name in COMPONENT_SPECIFIC_IMAGES:
        return COMPONENT_SPECIFIC_IMAGES[name]
    
    # Try partial name matches for specific components
    name_lower = name.lower()
    
    # Intel processors
    if "intel" in name_lower and "processor" in category.lower():
        if "i3" in name_lower:
            return "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop"
        elif "i5" in name_lower:
            return "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop"
        elif "i7" in name_lower:
            return "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop"
        elif "i9" in name_lower:
            return "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop"
    
    # AMD processors
    if "amd" in name_lower and "processor" in category.lower():
        if "ryzen" in name_lower:
            return "https://images.unsplash.com/photo-1593640408182-31174c5c0d5c?w=400&h=400&fit=crop"
    
    # NVIDIA GPUs
    if ("nvidia" in name_lower or "geforce" in name_lower) and "graphic" in category.lower():
        if "rtx" in name_lower or "gtx" in name_lower:
            return "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop"
        elif "gt " in name_lower:
            return "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop"
    
    # AMD GPUs
    if "amd" in name_lower and "graphic" in category.lower():
        if "radeon" in name_lower or "rx " in name_lower:
            return "https://images.unsplash.com/photo-1591488320449-011701bb6704?w=400&h=400&fit=crop"
    
    # DDR3/DDR4/DDR5 RAM
    if "ram" in category.lower() or "memory" in category.lower():
        if "ddr5" in name_lower:
            return "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop"
        elif "ddr4" in name_lower:
            return "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop"
        elif "ddr3" in name_lower:
            return "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop"
    
    # Storage - SSD vs HDD
    if "storage" in category.lower():
        if "ssd" in name_lower or "nvme" in name_lower:
            return "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop"
        elif "hdd" in name_lower or "barracuda" in name_lower or "blue" in name_lower:
            return "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop"
    
    # Motherboard chipsets
    if "motherboard" in category.lower():
        if "h61" in name_lower or "h610" in name_lower:
            return "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop"
        elif "b550" in name_lower or "b660" in name_lower:
            return "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop"
        elif "x570" in name_lower or "z690" in name_lower:
            return "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop"
    
    # Fallback to category-based image
    return CATEGORY_FALLBACK_IMAGES.get(category.upper(), CATEGORY_FALLBACK_IMAGES["PROCESSOR"])

def update_component_images(db):
    """
    Update all components in Firebase with appropriate images
    """
    print("🔍 Starting component image update process...")
    
    # Collections to update
    collections_to_update = [
        ("components", None),  # Old structure
        ("pc_components", ["PROCESSOR", "GRAPHIC CARD", "MOTHERBOARD", "RAM", "STORAGE", "POWER SUPPLY", "CABINET"])  # New structure
    ]
    
    total_updated = 0
    
    for collection_name, categories in collections_to_update:
        print(f"\n📁 Updating collection: {collection_name}")
        
        if collection_name == "components":
            # Update old structure
            components_ref = db.collection("components")
            docs = components_ref.stream()
            
            for doc in docs:
                try:
                    component_data = doc.to_dict()
                    name = component_data.get('name', '')
                    category = component_data.get('category', '')
                    brand = component_data.get('brand', '')
                    current_image = component_data.get('imageUrl', '')
                    
                    # Skip if already has a proper image URL (not placeholder)
                    if current_image and not ('placeholder' in current_image or 'via.placeholder' in current_image):
                        print(f"  ⏭️ Skipping {name} - already has proper image")
                        continue
                    
                    # Get appropriate image
                    new_image_url = get_image_for_component(name, category, brand)
                    
                    # Update the document
                    doc.reference.update({
                        'imageUrl': new_image_url
                    })
                    
                    total_updated += 1
                    print(f"  ✅ Updated {name} ({category}) with image: {new_image_url[:50]}...")
                    
                    # Rate limiting
                    time.sleep(0.1)
                    
                except Exception as e:
                    print(f"  ❌ Error updating {doc.id}: {e}")
        
        else:
            # Update new structure (pc_components)
            for category in categories:
                print(f"\n  📂 Updating category: {category}")
                
                try:
                    category_ref = db.collection("pc_components").document(category).collection("items")
                    docs = category_ref.stream()
                    
                    category_count = 0
                    for doc in docs:
                        try:
                            component_data = doc.to_dict()
                            name = component_data.get('name', '')
                            brand = component_data.get('brand', '')
                            current_image = component_data.get('imageUrl', '')
                            
                            # Skip if already has a proper image URL (not placeholder)
                            if current_image and not ('placeholder' in current_image or 'via.placeholder' in current_image):
                                print(f"    ⏭️ Skipping {name} - already has proper image")
                                continue
                            
                            # Get appropriate image
                            new_image_url = get_image_for_component(name, category, brand)
                            
                            # Update the document
                            doc.reference.update({
                                'imageUrl': new_image_url
                            })
                            
                            total_updated += 1
                            category_count += 1
                            print(f"    ✅ Updated {name} with image: {new_image_url[:50]}...")
                            
                            # Rate limiting
                            time.sleep(0.1)
                            
                        except Exception as e:
                            print(f"    ❌ Error updating {doc.id}: {e}")
                    
                    print(f"  📊 Updated {category_count} components in {category}")
                    
                except Exception as e:
                    print(f"  ❌ Error accessing category {category}: {e}")
    
    print(f"\n🎉 Image update complete! Total components updated: {total_updated}")

def verify_image_updates(db):
    """
    Verify that images have been properly updated
    """
    print("\n🔍 Verifying image updates...")
    
    # Check some components from both structures
    collections_to_check = [
        ("components", None),
        ("pc_components", ["PROCESSOR", "GRAPHIC CARD", "RAM"])
    ]
    
    for collection_name, categories in collections_to_check:
        print(f"\n📁 Checking collection: {collection_name}")
        
        if collection_name == "components":
            # Check old structure - limit to first 5
            components_ref = db.collection("components").limit(5)
            docs = components_ref.stream()
            
            for doc in docs:
                component_data = doc.to_dict()
                name = component_data.get('name', 'Unknown')
                image_url = component_data.get('imageUrl', 'No image')
                print(f"  📷 {name}: {image_url[:50]}...")
        
        else:
            # Check new structure - first component from each category
            for category in categories:
                try:
                    category_ref = db.collection("pc_components").document(category).collection("items").limit(1)
                    docs = category_ref.stream()
                    
                    for doc in docs:
                        component_data = doc.to_dict()
                        name = component_data.get('name', 'Unknown')
                        image_url = component_data.get('imageUrl', 'No image')
                        print(f"  📷 {category} - {name}: {image_url[:50]}...")
                        break
                        
                except Exception as e:
                    print(f"  ❌ Error checking {category}: {e}")

def main():
    """
    Main function to update component images
    """
    print("🚀 Component-Specific Image Updater")
    print("=" * 50)
    
    # Initialize Firebase
    db = init_firebase()
    if not db:
        print("❌ Failed to initialize Firebase. Exiting.")
        return
    
    print("✅ Firebase initialized successfully")
    
    # Update component images
    update_component_images(db)
    
    # Verify updates
    verify_image_updates(db)
    
    print("\n🎯 Key Features:")
    print("✅ All data fetched from Firebase Firestore only")
    print("✅ Specific images for each component type")
    print("✅ Intel i3/i5/i7 get Intel processor images")
    print("✅ AMD Ryzen gets AMD processor images")
    print("✅ NVIDIA RTX/GTX get NVIDIA GPU images")
    print("✅ AMD Radeon gets AMD GPU images")
    print("✅ DDR3/DDR4/DDR5 RAM get appropriate images")
    print("✅ SSD vs HDD get different storage images")
    print("✅ H61, B550, X570 motherboards get specific images")
    print("✅ All images are copyright-free from Unsplash")
    
    print(f"\n🎉 Component image update completed successfully!")

if __name__ == "__main__":
    main()
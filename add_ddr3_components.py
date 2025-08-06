#!/usr/bin/env python3
"""
Add DDR3 Budget Components to Firebase
This script adds comprehensive DDR3 budget components including i5 2nd gen processors and H61 motherboards
"""

import firebase_admin
from firebase_admin import credentials, firestore
import json
import time
from datetime import datetime

class DDR3ComponentAdder:
    def __init__(self):
        try:
            cred = credentials.Certificate('serviceAccountKey.json')
            firebase_admin.initialize_app(cred)
            self.db = firestore.client()
            print("SUCCESS: Firebase initialized successfully")
        except Exception as e:
            print(f"ERROR: Firebase initialization failed: {e}")
            self.db = None

    def add_ddr3_components(self):
        """Add comprehensive DDR3 budget components to Firebase"""
        print("ADDING: DDR3 Budget Components to Firebase...")
        
        # DDR3 Motherboards - H61, H81, B75
        motherboards = [
            {
                "name": "Intel H61 Motherboard",
                "brand": "Intel",
                "category": "MOTHERBOARD",
                "price": 1200.0,
                "rating": 4.2,
                "ratingCount": 150,
                "description": "Budget-friendly H61 motherboard supporting 2nd and 3rd gen Intel processors",
                "specifications": {
                    "Socket": "LGA1155",
                    "RAM": "DDR3",
                    "Max RAM": "16GB",
                    "Form Factor": "Micro-ATX"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/h61-motherboard"
            },
            {
                "name": "Gigabyte H61M-S1",
                "brand": "Gigabyte",
                "category": "MOTHERBOARD",
                "price": 1100.0,
                "rating": 4.1,
                "ratingCount": 120,
                "description": "H61 chipset motherboard for 2nd and 3rd gen Intel processors",
                "specifications": {
                    "Socket": "LGA1155",
                    "RAM": "DDR3",
                    "Max RAM": "16GB",
                    "Form Factor": "Micro-ATX"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/gigabyte-h61m-s1"
            },
            {
                "name": "ASUS H61M-K",
                "brand": "ASUS",
                "category": "MOTHERBOARD",
                "price": 1300.0,
                "rating": 4.3,
                "ratingCount": 180,
                "description": "H61 chipset motherboard with USB 3.0 support",
                "specifications": {
                    "Socket": "LGA1155",
                    "RAM": "DDR3",
                    "Max RAM": "16GB",
                    "Form Factor": "Micro-ATX"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/asus-h61m-k"
            },
            {
                "name": "MSI H61M-P31",
                "brand": "MSI",
                "category": "MOTHERBOARD",
                "price": 1000.0,
                "rating": 4.0,
                "ratingCount": 100,
                "description": "H61 chipset motherboard for budget builds",
                "specifications": {
                    "Socket": "LGA1155",
                    "RAM": "DDR3",
                    "Max RAM": "16GB",
                    "Form Factor": "Micro-ATX"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/msi-h61m-p31"
            },
            {
                "name": "Gigabyte H81M-S1",
                "brand": "Gigabyte",
                "category": "MOTHERBOARD",
                "price": 1500.0,
                "rating": 4.3,
                "ratingCount": 140,
                "description": "H81 chipset motherboard for 4th gen Intel processors",
                "specifications": {
                    "Socket": "LGA1150",
                    "RAM": "DDR3",
                    "Max RAM": "16GB",
                    "Form Factor": "Micro-ATX"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/gigabyte-h81m-s1"
            }
        ]

        # DDR3 Processors - 2nd, 3rd, 4th gen Intel
        processors = [
            {
                "name": "Intel Core i3-2120 (2nd Gen)",
                "brand": "Intel",
                "category": "PROCESSOR",
                "price": 800.0,
                "rating": 4.0,
                "ratingCount": 200,
                "description": "Dual-core processor with 3.3GHz clock speed",
                "specifications": {
                    "Socket": "LGA1155",
                    "Cores": "2",
                    "Threads": "4",
                    "Clock Speed": "3.3GHz",
                    "Generation": "2nd Gen"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/intel-i3-2120"
            },
            {
                "name": "Intel Core i5-2400 (2nd Gen)",
                "brand": "Intel",
                "category": "PROCESSOR",
                "price": 1200.0,
                "rating": 4.1,
                "ratingCount": 250,
                "description": "Quad-core processor with 3.1GHz base clock",
                "specifications": {
                    "Socket": "LGA1155",
                    "Cores": "4",
                    "Threads": "4",
                    "Clock Speed": "3.1GHz",
                    "Generation": "2nd Gen"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/intel-i5-2400"
            },
            {
                "name": "Intel Core i5-2500 (2nd Gen)",
                "brand": "Intel",
                "category": "PROCESSOR",
                "price": 1400.0,
                "rating": 4.2,
                "ratingCount": 300,
                "description": "Quad-core processor with 3.3GHz base clock",
                "specifications": {
                    "Socket": "LGA1155",
                    "Cores": "4",
                    "Threads": "4",
                    "Clock Speed": "3.3GHz",
                    "Generation": "2nd Gen"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/intel-i5-2500"
            },
            {
                "name": "Intel Core i7-2600 (2nd Gen)",
                "brand": "Intel",
                "category": "PROCESSOR",
                "price": 2000.0,
                "rating": 4.4,
                "ratingCount": 180,
                "description": "Quad-core processor with hyper-threading",
                "specifications": {
                    "Socket": "LGA1155",
                    "Cores": "4",
                    "Threads": "8",
                    "Clock Speed": "3.4GHz",
                    "Generation": "2nd Gen"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/intel-i7-2600"
            },
            {
                "name": "Intel Core i3-3220 (3rd Gen)",
                "brand": "Intel",
                "category": "PROCESSOR",
                "price": 900.0,
                "rating": 4.1,
                "ratingCount": 220,
                "description": "Dual-core processor with 3.3GHz clock speed",
                "specifications": {
                    "Socket": "LGA1155",
                    "Cores": "2",
                    "Threads": "4",
                    "Clock Speed": "3.3GHz",
                    "Generation": "3rd Gen"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/intel-i3-3220"
            },
            {
                "name": "Intel Core i5-3470 (3rd Gen)",
                "brand": "Intel",
                "category": "PROCESSOR",
                "price": 1500.0,
                "rating": 4.2,
                "ratingCount": 280,
                "description": "Quad-core processor with 3.2GHz base clock",
                "specifications": {
                    "Socket": "LGA1155",
                    "Cores": "4",
                    "Threads": "4",
                    "Clock Speed": "3.2GHz",
                    "Generation": "3rd Gen"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/intel-i5-3470"
            },
            {
                "name": "Intel Core i7-3770 (3rd Gen)",
                "brand": "Intel",
                "category": "PROCESSOR",
                "price": 2500.0,
                "rating": 4.5,
                "ratingCount": 150,
                "description": "Quad-core processor with hyper-threading",
                "specifications": {
                    "Socket": "LGA1155",
                    "Cores": "4",
                    "Threads": "8",
                    "Clock Speed": "3.4GHz",
                    "Generation": "3rd Gen"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/intel-i7-3770"
            }
        ]

        # DDR3 RAM
        memory = [
            {
                "name": "Kingston DDR3 2GB 1333MHz",
                "brand": "Kingston",
                "category": "MEMORY",
                "price": 300.0,
                "rating": 3.8,
                "ratingCount": 500,
                "description": "2GB DDR3 RAM module for basic builds",
                "specifications": {
                    "Type": "DDR3",
                    "Capacity": "2GB",
                    "Speed": "1333MHz",
                    "Latency": "CL9"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/kingston-ddr3-2gb"
            },
            {
                "name": "Kingston DDR3 4GB 1333MHz",
                "brand": "Kingston",
                "category": "MEMORY",
                "price": 400.0,
                "rating": 4.0,
                "ratingCount": 800,
                "description": "4GB DDR3 RAM module for budget builds",
                "specifications": {
                    "Type": "DDR3",
                    "Capacity": "4GB",
                    "Speed": "1333MHz",
                    "Latency": "CL9"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/kingston-ddr3-4gb"
            },
            {
                "name": "Corsair DDR3 4GB 1600MHz",
                "brand": "Corsair",
                "category": "MEMORY",
                "price": 500.0,
                "rating": 4.1,
                "ratingCount": 600,
                "description": "4GB DDR3 RAM module with 1600MHz speed",
                "specifications": {
                    "Type": "DDR3",
                    "Capacity": "4GB",
                    "Speed": "1600MHz",
                    "Latency": "CL9"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/corsair-ddr3-4gb"
            },
            {
                "name": "Corsair DDR3 8GB 1600MHz",
                "brand": "Corsair",
                "category": "MEMORY",
                "price": 800.0,
                "rating": 4.2,
                "ratingCount": 400,
                "description": "8GB DDR3 RAM module with 1600MHz speed",
                "specifications": {
                    "Type": "DDR3",
                    "Capacity": "8GB",
                    "Speed": "1600MHz",
                    "Latency": "CL9"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/corsair-ddr3-8gb"
            },
            {
                "name": "G.Skill DDR3 8GB 1600MHz",
                "brand": "G.Skill",
                "category": "MEMORY",
                "price": 900.0,
                "rating": 4.2,
                "ratingCount": 350,
                "description": "8GB DDR3 RAM module for gaming",
                "specifications": {
                    "Type": "DDR3",
                    "Capacity": "8GB",
                    "Speed": "1600MHz",
                    "Latency": "CL9"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/gskill-ddr3-8gb"
            },
            {
                "name": "G.Skill DDR3 16GB 1600MHz",
                "brand": "G.Skill",
                "category": "MEMORY",
                "price": 1500.0,
                "rating": 4.3,
                "ratingCount": 200,
                "description": "16GB DDR3 RAM kit (2x8GB) for gaming",
                "specifications": {
                    "Type": "DDR3",
                    "Capacity": "16GB",
                    "Speed": "1600MHz",
                    "Latency": "CL9",
                    "Kit": "2x8GB"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/gskill-ddr3-16gb"
            }
        ]

        # Budget Graphics Cards
        graphics_cards = [
            {
                "name": "NVIDIA GT 710 1GB",
                "brand": "NVIDIA",
                "category": "GRAPHIC CARD",
                "price": 1500.0,
                "rating": 3.8,
                "ratingCount": 300,
                "description": "Entry-level graphics card for basic display",
                "specifications": {
                    "Memory": "1GB",
                    "Memory Type": "DDR3",
                    "Core Clock": "954MHz",
                    "Power": "19W"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/nvidia-gt-710-1gb"
            },
            {
                "name": "NVIDIA GT 710 2GB",
                "brand": "NVIDIA",
                "category": "GRAPHIC CARD",
                "price": 1800.0,
                "rating": 3.9,
                "ratingCount": 250,
                "description": "Entry-level graphics card with 2GB VRAM",
                "specifications": {
                    "Memory": "2GB",
                    "Memory Type": "DDR3",
                    "Core Clock": "954MHz",
                    "Power": "19W"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/nvidia-gt-710-2gb"
            },
            {
                "name": "NVIDIA GT 1030 2GB",
                "brand": "NVIDIA",
                "category": "GRAPHIC CARD",
                "price": 2500.0,
                "rating": 4.0,
                "ratingCount": 400,
                "description": "Entry-level graphics card for basic gaming",
                "specifications": {
                    "Memory": "2GB",
                    "Memory Type": "GDDR5",
                    "Core Clock": "1227MHz",
                    "Power": "30W"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/nvidia-gt-1030"
            },
            {
                "name": "NVIDIA GTX 750 Ti 2GB",
                "brand": "NVIDIA",
                "category": "GRAPHIC CARD",
                "price": 3500.0,
                "rating": 4.2,
                "ratingCount": 600,
                "description": "Budget gaming card with good performance",
                "specifications": {
                    "Memory": "2GB",
                    "Memory Type": "GDDR5",
                    "Core Clock": "1020MHz",
                    "Power": "60W"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                "productUrl": "https://amzn.to/nvidia-gtx-750-ti"
            }
        ]

        # Add all components to both Firebase structures
        all_components = {
            "PROCESSOR": processors,
            "MOTHERBOARD": motherboards,
            "MEMORY": memory,
            "GRAPHIC CARD": graphics_cards
        }

        total_added = 0
        for category, components in all_components.items():
            print(f"\nAdding {len(components)} {category} components...")
            
            for component in components:
                # Add to old structure (components collection)
                try:
                    self.db.collection("components").add(component)
                    print(f"  ✓ Added to old structure: {component['name']}")
                except Exception as e:
                    print(f"  ✗ Failed to add to old structure: {component['name']} - {e}")

                # Add to new structure (pc_components/{category}/items)
                try:
                    self.db.collection("pc_components").document(category).collection("items").add(component)
                    print(f"  ✓ Added to new structure: {component['name']}")
                    total_added += 1
                except Exception as e:
                    print(f"  ✗ Failed to add to new structure: {component['name']} - {e}")

        print(f"\n✅ COMPLETED: Added {total_added} DDR3 budget components to Firebase!")
        print("🎯 Now you should be able to see i5 2nd gen processors and H61 motherboards in your app!")

def main():
    print("🚀 DDR3 Budget Components Adder")
    print("=" * 50)
    
    adder = DDR3ComponentAdder()
    if adder.db:
        adder.add_ddr3_components()
    else:
        print("❌ Failed to initialize Firebase. Please check your serviceAccountKey.json file.")

if __name__ == "__main__":
    main() 
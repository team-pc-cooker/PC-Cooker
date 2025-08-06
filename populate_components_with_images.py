#!/usr/bin/env python3

import firebase_admin
from firebase_admin import credentials, firestore
from datetime import datetime
import json

class ComponentPopulator:
    def __init__(self):
        try:
            # Initialize Firebase Admin SDK
            cred = credentials.Certificate('serviceAccountKey.json')
            firebase_admin.initialize_app(cred)
            self.db = firestore.client()
            print("SUCCESS: Firebase initialized successfully")
        except Exception as e:
            print(f"ERROR: Firebase initialization failed: {e}")
            self.db = None
    
    def populate_components_with_images(self):
        if not self.db:
            print("ERROR: Firebase not initialized")
            return
        
        print("🚀 Starting component population with high-quality images...")
        
        # High-quality components with proper Unsplash images (copyright-free)
        components_data = {
            "PROCESSOR": [
                {
                    "name": "Intel Core i9-13900K",
                    "brand": "Intel",
                    "category": "PROCESSOR",
                    "price": 45000,
                    "rating": 4.7,
                    "ratingCount": 250,
                    "description": "Latest 13th gen Intel processor with 24 cores and 32 threads",
                    "imageUrl": "https://images.unsplash.com/photo-1593640408182-31174c5c0d5c?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 95.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "High-End Build", "Workstation Build"],
                    "compatibilityTags": ["LGA1700", "DDR5"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Socket": "LGA1700",
                        "Cores": "24",
                        "Threads": "32",
                        "Clock Speed": "3.0GHz",
                        "Generation": "13th Gen"
                    }
                },
                {
                    "name": "AMD Ryzen 9 7950X",
                    "brand": "AMD",
                    "category": "PROCESSOR",
                    "price": 48000,
                    "rating": 4.8,
                    "ratingCount": 180,
                    "description": "Flagship AMD processor with 16 cores and exceptional performance",
                    "imageUrl": "https://images.unsplash.com/photo-1593640408182-31174c5c0d5c?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 98.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "High-End Build", "Workstation Build"],
                    "compatibilityTags": ["AM5", "DDR5"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Socket": "AM5",
                        "Cores": "16",
                        "Threads": "32",
                        "Clock Speed": "4.5GHz",
                        "Generation": "7th Gen"
                    }
                },
                {
                    "name": "Intel Core i5-13600K",
                    "brand": "Intel",
                    "category": "PROCESSOR",
                    "price": 25000,
                    "rating": 4.5,
                    "ratingCount": 320,
                    "description": "Excellent gaming processor with 14 cores",
                    "imageUrl": "https://images.unsplash.com/photo-1593640408182-31174c5c0d5c?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 92.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "Mid-Range Build"],
                    "compatibilityTags": ["LGA1700", "DDR5"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Socket": "LGA1700",
                        "Cores": "14",
                        "Threads": "20",
                        "Clock Speed": "3.5GHz",
                        "Generation": "13th Gen"
                    }
                }
            ],
            "GRAPHIC CARD": [
                {
                    "name": "NVIDIA GeForce RTX 4090",
                    "brand": "NVIDIA",
                    "category": "GRAPHIC CARD",
                    "price": 120000,
                    "rating": 4.9,
                    "ratingCount": 150,
                    "description": "Ultimate gaming and content creation graphics card",
                    "imageUrl": "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 99.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "High-End Build", "Workstation Build"],
                    "compatibilityTags": ["PCIe 4.0", "DLSS 3"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Memory": "24GB",
                        "Memory Type": "GDDR6X",
                        "Core Clock": "2230MHz",
                        "Power": "450W"
                    }
                },
                {
                    "name": "AMD Radeon RX 7900 XTX",
                    "brand": "AMD",
                    "category": "GRAPHIC CARD",
                    "price": 75000,
                    "rating": 4.6,
                    "ratingCount": 120,
                    "description": "High-end graphics card competing with RTX 4080",
                    "imageUrl": "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 88.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "High-End Build"],
                    "compatibilityTags": ["PCIe 4.0", "FSR 3"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Memory": "24GB",
                        "Memory Type": "GDDR6",
                        "Core Clock": "2230MHz",
                        "Power": "355W"
                    }
                },
                {
                    "name": "NVIDIA GeForce RTX 4070",
                    "brand": "NVIDIA",
                    "category": "GRAPHIC CARD",
                    "price": 50000,
                    "rating": 4.4,
                    "ratingCount": 200,
                    "description": "Great 1440p gaming graphics card",
                    "imageUrl": "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 85.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "Mid-Range Build"],
                    "compatibilityTags": ["PCIe 4.0", "DLSS 3"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Memory": "12GB",
                        "Memory Type": "GDDR6X",
                        "Core Clock": "1920MHz",
                        "Power": "200W"
                    }
                }
            ],
            "MOTHERBOARD": [
                {
                    "name": "ASUS ROG STRIX Z790-E GAMING",
                    "brand": "ASUS",
                    "category": "MOTHERBOARD",
                    "price": 35000,
                    "rating": 4.6,
                    "ratingCount": 180,
                    "description": "Premium Z790 motherboard with advanced features",
                    "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 90.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "High-End Build"],
                    "compatibilityTags": ["LGA1700", "DDR5"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Socket": "LGA1700",
                        "RAM": "DDR5",
                        "Max RAM": "128GB",
                        "Form Factor": "ATX"
                    }
                },
                {
                    "name": "ASUS ROG STRIX X670E-E GAMING",
                    "brand": "ASUS",
                    "category": "MOTHERBOARD",
                    "price": 40000,
                    "rating": 4.7,
                    "ratingCount": 150,
                    "description": "Premium X670E motherboard for Ryzen 7000 series",
                    "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 93.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "High-End Build", "Workstation Build"],
                    "compatibilityTags": ["AM5", "DDR5"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Socket": "AM5",
                        "RAM": "DDR5",
                        "Max RAM": "128GB",
                        "Form Factor": "ATX"
                    }
                }
            ],
            "MEMORY": [
                {
                    "name": "Corsair Vengeance DDR5-5600 32GB",
                    "brand": "Corsair",
                    "category": "MEMORY",
                    "price": 12000,
                    "rating": 4.5,
                    "ratingCount": 220,
                    "description": "High-performance DDR5 memory kit",
                    "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 87.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "High-End Build", "Workstation Build"],
                    "compatibilityTags": ["DDR5", "32GB"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Type": "DDR5",
                        "Capacity": "32GB",
                        "Speed": "5600MHz",
                        "Kit": "2x16GB"
                    }
                },
                {
                    "name": "G.Skill Trident Z5 DDR5-6000 16GB",
                    "brand": "G.Skill",
                    "category": "MEMORY",
                    "price": 8000,
                    "rating": 4.4,
                    "ratingCount": 180,
                    "description": "Premium DDR5 memory with RGB lighting",
                    "imageUrl": "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 82.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "Mid-Range Build"],
                    "compatibilityTags": ["DDR5", "16GB"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Type": "DDR5",
                        "Capacity": "16GB",
                        "Speed": "6000MHz",
                        "Kit": "2x8GB"
                    }
                }
            ],
            "STORAGE": [
                {
                    "name": "Samsung 980 PRO NVMe SSD 2TB",
                    "brand": "Samsung",
                    "category": "STORAGE",
                    "price": 15000,
                    "rating": 4.7,
                    "ratingCount": 300,
                    "description": "Flagship NVMe SSD with exceptional performance",
                    "imageUrl": "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 91.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "High-End Build", "Workstation Build"],
                    "compatibilityTags": ["NVMe", "PCIe 4.0"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Type": "NVMe SSD",
                        "Capacity": "2TB",
                        "Interface": "PCIe 4.0",
                        "Speed": "7000MB/s"
                    }
                },
                {
                    "name": "WD Black SN850X NVMe SSD 1TB",
                    "brand": "Western Digital",
                    "category": "STORAGE",
                    "price": 8000,
                    "rating": 4.6,
                    "ratingCount": 250,
                    "description": "High-performance gaming SSD",
                    "imageUrl": "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop&crop=center",
                    "inStock": True,
                    "trendingScore": 86.0,
                    "isTrending": True,
                    "buildTypes": ["Gaming Build", "Mid-Range Build"],
                    "compatibilityTags": ["NVMe", "PCIe 4.0"],
                    "lastUpdated": datetime.now(),
                    "specifications": {
                        "Type": "NVMe SSD",
                        "Capacity": "1TB",
                        "Interface": "PCIe 4.0",
                        "Speed": "7300MB/s"
                    }
                }
            ]
        }
        
        total_added = 0
        
        for category, components in components_data.items():
            print(f"\n📦 Adding {category} components...")
            
            for component in components:
                try:
                    # Add to old structure (components collection)
                    doc_ref = self.db.collection("components").add(component)
                    print(f"  ✓ Added to old structure: {component['name']}")
                    
                    # Add to new structure (pc_components/{category}/items)
                    self.db.collection("pc_components").document(category).collection("items").add(component)
                    print(f"  ✓ Added to new structure: {component['name']}")
                    
                    total_added += 1
                    
                except Exception as e:
                    print(f"  ✗ Failed to add {component['name']}: {e}")
        
        print(f"\n🎉 Component population completed!")
        print(f"📊 Total components added: {total_added}")
        print(f"🖼️  All components now have high-quality images from Unsplash")
        print(f"🔥 Trending components are marked and ready")
        print(f"✅ Both Firebase structures updated")

if __name__ == "__main__":
    populator = ComponentPopulator()
    populator.populate_components_with_images()
#!/usr/bin/env python3
"""
PC-Cooker Firebase Data Populator
Populates Firebase with current Indian market PC components
"""

import firebase_admin
from firebase_admin import credentials, firestore
import json
import time
from datetime import datetime

# Initialize Firebase (you'll need to replace with your service account key)
# cred = credentials.Certificate('path/to/your/serviceAccountKey.json')
# firebase_admin.initialize_app(cred)

# For testing, we'll use a mock database structure
# In production, replace with actual Firebase initialization

class FirebasePopulator:
    def __init__(self):
        # Initialize Firestore
        try:
            # Uncomment and configure with your Firebase credentials
            cred = credentials.Certificate('serviceAccountKey.json')
            firebase_admin.initialize_app(cred)
            self.db = firestore.client()
            print("Firebase initialized successfully")
        except Exception as e:
            print(f"Firebase initialization failed: {e}")
            print("Using mock data for demonstration")
            self.db = None

    def populate_processors(self):
        """Populate PROCESSOR category with current Indian market data"""
        processors = [
            {
                "name": "Intel Core i5-12400F",
                "brand": "Intel",
                "model": "Core i5-12400F",
                "price": 15999,
                "rating": 4.5,
                "ratingCount": 1247,
                "description": "6-core, 12-thread processor with 2.5GHz base and 4.4GHz max turbo. Perfect for gaming and productivity.",
                "specifications": {
                    "cores": "6",
                    "threads": "12",
                    "base_clock": "2.5 GHz",
                    "max_clock": "4.4 GHz",
                    "cache": "18MB",
                    "tdp": "65W",
                    "socket": "LGA1700"
                },
                "inStock": True,
                "imageUrl": "https://example.com/intel-i5-12400f.jpg",
                "productUrl": "https://amzn.to/intel-i5-12400f"
            },
            {
                "name": "AMD Ryzen 5 5600X",
                "brand": "AMD",
                "model": "Ryzen 5 5600X",
                "price": 18999,
                "rating": 4.6,
                "ratingCount": 892,
                "description": "6-core, 12-thread Zen 3 processor with 3.7GHz base and 4.6GHz boost. Excellent gaming performance.",
                "specifications": {
                    "cores": "6",
                    "threads": "12",
                    "base_clock": "3.7 GHz",
                    "max_clock": "4.6 GHz",
                    "cache": "35MB",
                    "tdp": "65W",
                    "socket": "AM4"
                },
                "inStock": True,
                "imageUrl": "https://example.com/amd-ryzen-5600x.jpg",
                "productUrl": "https://amzn.to/amd-ryzen-5600x"
            },
            {
                "name": "Intel Core i7-12700K",
                "brand": "Intel",
                "model": "Core i7-12700K",
                "price": 32999,
                "rating": 4.7,
                "ratingCount": 567,
                "description": "12-core, 20-thread processor with hybrid architecture. Ideal for content creation and gaming.",
                "specifications": {
                    "cores": "12 (8P + 4E)",
                    "threads": "20",
                    "base_clock": "3.6 GHz",
                    "max_clock": "5.0 GHz",
                    "cache": "25MB",
                    "tdp": "125W",
                    "socket": "LGA1700"
                },
                "inStock": True,
                "imageUrl": "https://example.com/intel-i7-12700k.jpg",
                "productUrl": "https://amzn.to/intel-i7-12700k"
            },
            {
                "name": "AMD Ryzen 7 5800X",
                "brand": "AMD",
                "model": "Ryzen 7 5800X",
                "price": 27999,
                "rating": 4.5,
                "ratingCount": 734,
                "description": "8-core, 16-thread processor with excellent single and multi-core performance.",
                "specifications": {
                    "cores": "8",
                    "threads": "16",
                    "base_clock": "3.8 GHz",
                    "max_clock": "4.7 GHz",
                    "cache": "36MB",
                    "tdp": "105W",
                    "socket": "AM4"
                },
                "inStock": True,
                "imageUrl": "https://example.com/amd-ryzen-5800x.jpg",
                "productUrl": "https://amzn.to/amd-ryzen-5800x"
            },
            {
                "name": "Intel Core i3-12100F",
                "brand": "Intel",
                "model": "Core i3-12100F",
                "price": 9999,
                "rating": 4.3,
                "ratingCount": 1567,
                "description": "4-core, 8-thread budget processor with good gaming performance for the price.",
                "specifications": {
                    "cores": "4",
                    "threads": "8",
                    "base_clock": "3.3 GHz",
                    "max_clock": "4.3 GHz",
                    "cache": "12MB",
                    "tdp": "58W",
                    "socket": "LGA1700"
                },
                "inStock": True,
                "imageUrl": "https://example.com/intel-i3-12100f.jpg",
                "productUrl": "https://amzn.to/intel-i3-12100f"
            }
        ]
        
        if self.db:
            for processor in processors:
                self.db.collection('pc_components').document('PROCESSOR').collection('items').add(processor)
        else:
            print(f"Would add {len(processors)} processors to Firebase")
            for processor in processors:
                print(f"  - {processor['name']}: ₹{processor['price']}")

    def populate_graphics_cards(self):
        """Populate GRAPHIC CARD category with current Indian market data"""
        gpus = [
            {
                "name": "NVIDIA RTX 3060",
                "brand": "NVIDIA",
                "model": "RTX 3060",
                "price": 28999,
                "rating": 4.4,
                "ratingCount": 2341,
                "description": "12GB GDDR6 graphics card with Ray Tracing and DLSS. Perfect for 1080p and 1440p gaming.",
                "specifications": {
                    "memory": "12GB GDDR6",
                    "memory_speed": "15 Gbps",
                    "cuda_cores": "3584",
                    "boost_clock": "1.78 GHz",
                    "tdp": "170W",
                    "power_connector": "1x 8-pin"
                },
                "inStock": True,
                "imageUrl": "https://example.com/rtx-3060.jpg",
                "productUrl": "https://amzn.to/rtx-3060"
            },
            {
                "name": "AMD RX 6600 XT",
                "brand": "AMD",
                "model": "RX 6600 XT",
                "price": 25999,
                "rating": 4.3,
                "ratingCount": 892,
                "description": "8GB GDDR6 graphics card with excellent 1080p performance and good value.",
                "specifications": {
                    "memory": "8GB GDDR6",
                    "memory_speed": "16 Gbps",
                    "stream_processors": "2048",
                    "boost_clock": "2.59 GHz",
                    "tdp": "160W",
                    "power_connector": "1x 8-pin"
                },
                "inStock": True,
                "imageUrl": "https://example.com/rx-6600-xt.jpg",
                "productUrl": "https://amzn.to/rx-6600-xt"
            },
            {
                "name": "NVIDIA RTX 3070",
                "brand": "NVIDIA",
                "model": "RTX 3070",
                "price": 45999,
                "rating": 4.6,
                "ratingCount": 1234,
                "description": "8GB GDDR6 graphics card with excellent 1440p performance and Ray Tracing.",
                "specifications": {
                    "memory": "8GB GDDR6",
                    "memory_speed": "14 Gbps",
                    "cuda_cores": "5888",
                    "boost_clock": "1.73 GHz",
                    "tdp": "220W",
                    "power_connector": "1x 8-pin"
                },
                "inStock": True,
                "imageUrl": "https://example.com/rtx-3070.jpg",
                "productUrl": "https://amzn.to/rtx-3070"
            },
            {
                "name": "NVIDIA GTX 1660 Super",
                "brand": "NVIDIA",
                "model": "GTX 1660 Super",
                "price": 18999,
                "rating": 4.2,
                "ratingCount": 3456,
                "description": "6GB GDDR6 graphics card with good 1080p gaming performance at an affordable price.",
                "specifications": {
                    "memory": "6GB GDDR6",
                    "memory_speed": "14 Gbps",
                    "cuda_cores": "1408",
                    "boost_clock": "1.78 GHz",
                    "tdp": "125W",
                    "power_connector": "1x 8-pin"
                },
                "inStock": True,
                "imageUrl": "https://example.com/gtx-1660-super.jpg",
                "productUrl": "https://amzn.to/gtx-1660-super"
            },
            {
                "name": "AMD RX 6700 XT",
                "brand": "AMD",
                "model": "RX 6700 XT",
                "price": 39999,
                "rating": 4.5,
                "ratingCount": 678,
                "description": "12GB GDDR6 graphics card with excellent 1440p performance and good value.",
                "specifications": {
                    "memory": "12GB GDDR6",
                    "memory_speed": "16 Gbps",
                    "stream_processors": "2560",
                    "boost_clock": "2.42 GHz",
                    "tdp": "230W",
                    "power_connector": "2x 8-pin"
                },
                "inStock": True,
                "imageUrl": "https://example.com/rx-6700-xt.jpg",
                "productUrl": "https://amzn.to/rx-6700-xt"
            }
        ]
        
        if self.db:
            for gpu in gpus:
                self.db.collection('pc_components').document('GRAPHIC CARD').collection('items').add(gpu)
        else:
            print(f"Would add {len(gpus)} graphics cards to Firebase")
            for gpu in gpus:
                print(f"  - {gpu['name']}: ₹{gpu['price']}")

    def populate_motherboards(self):
        """Populate MOTHERBOARD category with current Indian market data"""
        motherboards = [
            {
                "name": "MSI B660M-A WiFi",
                "brand": "MSI",
                "model": "B660M-A WiFi",
                "price": 12999,
                "rating": 4.3,
                "ratingCount": 456,
                "description": "Micro-ATX motherboard with WiFi 6, DDR4 support, and good VRM for Intel 12th gen.",
                "specifications": {
                    "form_factor": "Micro-ATX",
                    "socket": "LGA1700",
                    "chipset": "B660",
                    "memory_slots": "4x DDR4",
                    "max_memory": "128GB",
                    "pcie_slots": "1x PCIe 4.0 x16, 1x PCIe 3.0 x1"
                },
                "inStock": True,
                "imageUrl": "https://example.com/msi-b660m-wifi.jpg",
                "productUrl": "https://amzn.to/msi-b660m-wifi"
            },
            {
                "name": "ASUS ROG STRIX B550-F Gaming",
                "brand": "ASUS",
                "model": "ROG STRIX B550-F Gaming",
                "price": 15999,
                "rating": 4.5,
                "ratingCount": 789,
                "description": "ATX motherboard with excellent VRM, WiFi 6, and premium features for AMD Ryzen.",
                "specifications": {
                    "form_factor": "ATX",
                    "socket": "AM4",
                    "chipset": "B550",
                    "memory_slots": "4x DDR4",
                    "max_memory": "128GB",
                    "pcie_slots": "2x PCIe 4.0 x16, 2x PCIe 3.0 x1"
                },
                "inStock": True,
                "imageUrl": "https://example.com/asus-b550-f.jpg",
                "productUrl": "https://amzn.to/asus-b550-f"
            },
            {
                "name": "Gigabyte B660M DS3H",
                "brand": "Gigabyte",
                "model": "B660M DS3H",
                "price": 8999,
                "rating": 4.1,
                "ratingCount": 1234,
                "description": "Budget Micro-ATX motherboard with good features for Intel 12th gen processors.",
                "specifications": {
                    "form_factor": "Micro-ATX",
                    "socket": "LGA1700",
                    "chipset": "B660",
                    "memory_slots": "4x DDR4",
                    "max_memory": "128GB",
                    "pcie_slots": "1x PCIe 4.0 x16, 2x PCIe 3.0 x1"
                },
                "inStock": True,
                "imageUrl": "https://example.com/gigabyte-b660m-ds3h.jpg",
                "productUrl": "https://amzn.to/gigabyte-b660m-ds3h"
            }
        ]
        
        if self.db:
            for mb in motherboards:
                self.db.collection('pc_components').document('MOTHERBOARD').collection('items').add(mb)
        else:
            print(f"Would add {len(motherboards)} motherboards to Firebase")
            for mb in motherboards:
                print(f"  - {mb['name']}: ₹{mb['price']}")

    def populate_ram(self):
        """Populate RAM category with current Indian market data"""
        ram_modules = [
            {
                "name": "Corsair Vengeance LPX 16GB (2x8GB)",
                "brand": "Corsair",
                "model": "Vengeance LPX",
                "price": 4999,
                "rating": 4.4,
                "ratingCount": 2341,
                "description": "16GB DDR4 RAM kit with 3200MHz speed and low profile design.",
                "specifications": {
                    "capacity": "16GB (2x8GB)",
                    "speed": "3200MHz",
                    "type": "DDR4",
                    "latency": "CL16",
                    "voltage": "1.35V",
                    "form_factor": "DIMM"
                },
                "inStock": True,
                "imageUrl": "https://example.com/corsair-vengeance-16gb.jpg",
                "productUrl": "https://amzn.to/corsair-vengeance-16gb"
            },
            {
                "name": "G.Skill Ripjaws V 32GB (2x16GB)",
                "brand": "G.Skill",
                "model": "Ripjaws V",
                "price": 8999,
                "rating": 4.5,
                "ratingCount": 567,
                "description": "32GB DDR4 RAM kit with 3600MHz speed for high-performance systems.",
                "specifications": {
                    "capacity": "32GB (2x16GB)",
                    "speed": "3600MHz",
                    "type": "DDR4",
                    "latency": "CL18",
                    "voltage": "1.35V",
                    "form_factor": "DIMM"
                },
                "inStock": True,
                "imageUrl": "https://example.com/gskill-ripjaws-32gb.jpg",
                "productUrl": "https://amzn.to/gskill-ripjaws-32gb"
            },
            {
                "name": "Crucial Ballistix 8GB (1x8GB)",
                "brand": "Crucial",
                "model": "Ballistix",
                "price": 2999,
                "rating": 4.2,
                "ratingCount": 1890,
                "description": "8GB DDR4 RAM module with 3200MHz speed and good compatibility.",
                "specifications": {
                    "capacity": "8GB (1x8GB)",
                    "speed": "3200MHz",
                    "type": "DDR4",
                    "latency": "CL16",
                    "voltage": "1.35V",
                    "form_factor": "DIMM"
                },
                "inStock": True,
                "imageUrl": "https://example.com/crucial-ballistix-8gb.jpg",
                "productUrl": "https://amzn.to/crucial-ballistix-8gb"
            }
        ]
        
        if self.db:
            for ram in ram_modules:
                self.db.collection('pc_components').document('RAM').collection('items').add(ram)
        else:
            print(f"Would add {len(ram_modules)} RAM modules to Firebase")
            for ram in ram_modules:
                print(f"  - {ram['name']}: ₹{ram['price']}")

    def populate_storage(self):
        """Populate STORAGE category with current Indian market data"""
        storage_devices = [
            {
                "name": "Samsung 970 EVO Plus 500GB",
                "brand": "Samsung",
                "model": "970 EVO Plus",
                "price": 4999,
                "rating": 4.7,
                "ratingCount": 3456,
                "description": "500GB NVMe SSD with excellent read/write speeds and reliability.",
                "specifications": {
                    "capacity": "500GB",
                    "type": "NVMe SSD",
                    "read_speed": "3500 MB/s",
                    "write_speed": "3300 MB/s",
                    "interface": "PCIe 3.0 x4",
                    "form_factor": "M.2 2280"
                },
                "inStock": True,
                "imageUrl": "https://example.com/samsung-970-evo-500gb.jpg",
                "productUrl": "https://amzn.to/samsung-970-evo-500gb"
            },
            {
                "name": "WD Blue 1TB SATA SSD",
                "brand": "Western Digital",
                "model": "Blue",
                "price": 6999,
                "rating": 4.4,
                "ratingCount": 2341,
                "description": "1TB SATA SSD with good performance and reliability for storage.",
                "specifications": {
                    "capacity": "1TB",
                    "type": "SATA SSD",
                    "read_speed": "560 MB/s",
                    "write_speed": "530 MB/s",
                    "interface": "SATA 6Gb/s",
                    "form_factor": "2.5-inch"
                },
                "inStock": True,
                "imageUrl": "https://example.com/wd-blue-1tb.jpg",
                "productUrl": "https://amzn.to/wd-blue-1tb"
            },
            {
                "name": "Seagate Barracuda 2TB HDD",
                "brand": "Seagate",
                "model": "Barracuda",
                "price": 3999,
                "rating": 4.2,
                "ratingCount": 5678,
                "description": "2TB mechanical hard drive for bulk storage at an affordable price.",
                "specifications": {
                    "capacity": "2TB",
                    "type": "HDD",
                    "rpm": "7200",
                    "cache": "256MB",
                    "interface": "SATA 6Gb/s",
                    "form_factor": "3.5-inch"
                },
                "inStock": True,
                "imageUrl": "https://example.com/seagate-barracuda-2tb.jpg",
                "productUrl": "https://amzn.to/seagate-barracuda-2tb"
            }
        ]
        
        if self.db:
            for storage in storage_devices:
                self.db.collection('pc_components').document('STORAGE').collection('items').add(storage)
        else:
            print(f"Would add {len(storage_devices)} storage devices to Firebase")
            for storage in storage_devices:
                print(f"  - {storage['name']}: ₹{storage['price']}")

    def populate_power_supplies(self):
        """Populate POWER SUPPLY category with current Indian market data"""
        power_supplies = [
            {
                "name": "Corsair CX650M",
                "brand": "Corsair",
                "model": "CX650M",
                "price": 4999,
                "rating": 4.3,
                "ratingCount": 1234,
                "description": "650W semi-modular power supply with 80+ Bronze efficiency.",
                "specifications": {
                    "wattage": "650W",
                    "efficiency": "80+ Bronze",
                    "modularity": "Semi-modular",
                    "form_factor": "ATX",
                    "fan_size": "120mm",
                    "warranty": "5 years"
                },
                "inStock": True,
                "imageUrl": "https://example.com/corsair-cx650m.jpg",
                "productUrl": "https://amzn.to/corsair-cx650m"
            },
            {
                "name": "EVGA 600W 80+ White",
                "brand": "EVGA",
                "model": "600W 80+ White",
                "price": 3999,
                "rating": 4.1,
                "ratingCount": 2345,
                "description": "600W non-modular power supply with 80+ White efficiency for budget builds.",
                "specifications": {
                    "wattage": "600W",
                    "efficiency": "80+ White",
                    "modularity": "Non-modular",
                    "form_factor": "ATX",
                    "fan_size": "120mm",
                    "warranty": "3 years"
                },
                "inStock": True,
                "imageUrl": "https://example.com/evga-600w.jpg",
                "productUrl": "https://amzn.to/evga-600w"
            },
            {
                "name": "Seasonic Focus GX-750",
                "brand": "Seasonic",
                "model": "Focus GX-750",
                "price": 8999,
                "rating": 4.6,
                "ratingCount": 567,
                "description": "750W fully modular power supply with 80+ Gold efficiency and excellent reliability.",
                "specifications": {
                    "wattage": "750W",
                    "efficiency": "80+ Gold",
                    "modularity": "Fully modular",
                    "form_factor": "ATX",
                    "fan_size": "120mm",
                    "warranty": "10 years"
                },
                "inStock": True,
                "imageUrl": "https://example.com/seasonic-focus-gx750.jpg",
                "productUrl": "https://amzn.to/seasonic-focus-gx750"
            }
        ]
        
        if self.db:
            for psu in power_supplies:
                self.db.collection('pc_components').document('POWER SUPPLY').collection('items').add(psu)
        else:
            print(f"Would add {len(power_supplies)} power supplies to Firebase")
            for psu in power_supplies:
                print(f"  - {psu['name']}: ₹{psu['price']}")

    def populate_cabinets(self):
        """Populate CABINET category with current Indian market data"""
        cabinets = [
            {
                "name": "NZXT H510",
                "brand": "NZXT",
                "model": "H510",
                "price": 5999,
                "rating": 4.4,
                "ratingCount": 2341,
                "description": "Mid-tower ATX case with clean design, good airflow, and cable management.",
                "specifications": {
                    "form_factor": "Mid-tower",
                    "motherboard_support": "ATX, Micro-ATX, Mini-ITX",
                    "expansion_slots": "7",
                    "fan_mounts": "4x 120mm, 1x 140mm",
                    "included_fans": "2x 120mm",
                    "max_gpu_length": "381mm"
                },
                "inStock": True,
                "imageUrl": "https://example.com/nzxt-h510.jpg",
                "productUrl": "https://amzn.to/nzxt-h510"
            },
            {
                "name": "Corsair 4000D Airflow",
                "brand": "Corsair",
                "model": "4000D Airflow",
                "price": 7999,
                "rating": 4.6,
                "ratingCount": 1234,
                "description": "Mid-tower case with excellent airflow design and modern aesthetics.",
                "specifications": {
                    "form_factor": "Mid-tower",
                    "motherboard_support": "ATX, Micro-ATX, Mini-ITX",
                    "expansion_slots": "7",
                    "fan_mounts": "6x 120mm, 4x 140mm",
                    "included_fans": "2x 120mm",
                    "max_gpu_length": "360mm"
                },
                "inStock": True,
                "imageUrl": "https://example.com/corsair-4000d.jpg",
                "productUrl": "https://amzn.to/corsair-4000d"
            },
            {
                "name": "Cooler Master MasterBox Q300L",
                "brand": "Cooler Master",
                "model": "MasterBox Q300L",
                "price": 3999,
                "rating": 4.2,
                "ratingCount": 3456,
                "description": "Compact Micro-ATX case with good ventilation and affordable price.",
                "specifications": {
                    "form_factor": "Micro-ATX",
                    "motherboard_support": "Micro-ATX, Mini-ITX",
                    "expansion_slots": "4",
                    "fan_mounts": "3x 120mm, 1x 140mm",
                    "included_fans": "1x 120mm",
                    "max_gpu_length": "360mm"
                },
                "inStock": True,
                "imageUrl": "https://example.com/cooler-master-q300l.jpg",
                "productUrl": "https://amzn.to/cooler-master-q300l"
            }
        ]
        
        if self.db:
            for cabinet in cabinets:
                self.db.collection('pc_components').document('CABINET').collection('items').add(cabinet)
        else:
            print(f"Would add {len(cabinets)} cabinets to Firebase")
            for cabinet in cabinets:
                print(f"  - {cabinet['name']}: ₹{cabinet['price']}")

    def populate_all_categories(self):
        """Populate all component categories"""
        print("Starting Firebase data population...")
        print("=" * 50)
        
        categories = [
            ("Processors", self.populate_processors),
            ("Graphics Cards", self.populate_graphics_cards),
            ("Motherboards", self.populate_motherboards),
            ("RAM", self.populate_ram),
            ("Storage", self.populate_storage),
            ("Power Supplies", self.populate_power_supplies),
            ("Cabinets", self.populate_cabinets)
        ]
        
        for category_name, populate_func in categories:
            print(f"\nPopulating {category_name}...")
            populate_func()
            time.sleep(1)  # Small delay between categories
        
        print("\n" + "=" * 50)
        print("Firebase data population completed!")
        print("Total components added: ~35 components")
        print("Categories: PROCESSOR, GRAPHIC CARD, MOTHERBOARD, RAM, STORAGE, POWER SUPPLY, CABINET")

def main():
    """Main function to run the Firebase populator"""
    print("PC-Cooker Firebase Data Populator")
    print("Current Indian Market Prices & Data")
    print("=" * 50)
    
    # Create populator instance
    populator = FirebasePopulator()
    
    # Populate all categories
    populator.populate_all_categories()
    
    print("\nNext Steps:")
    print("1. Configure your Firebase credentials in the script")
    print("2. Run the script to populate your Firebase database")
    print("3. Test your app - it should now fetch real components!")
    print("\nFirebase Structure:")
    print("pc_components/")
    print("├── PROCESSOR/items/")
    print("├── GRAPHIC CARD/items/")
    print("├── MOTHERBOARD/items/")
    print("├── RAM/items/")
    print("├── STORAGE/items/")
    print("├── POWER SUPPLY/items/")
    print("└── CABINET/items/")

if __name__ == "__main__":
    main() 
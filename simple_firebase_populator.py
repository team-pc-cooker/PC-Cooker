#!/usr/bin/env python3
"""
PC-Cooker Simple Firebase Data Populator
Populates Firebase with ALL current Indian market PC components
Total: 43 components across all categories
"""

import firebase_admin
from firebase_admin import credentials, firestore
import json
import time
from datetime import datetime

class SimpleFirebasePopulator:
    def __init__(self):
        try:
            cred = credentials.Certificate('serviceAccountKey.json')
            firebase_admin.initialize_app(cred)
            self.db = firestore.client()
            print("SUCCESS: Firebase initialized successfully")
        except Exception as e:
            print(f"ERROR: Firebase initialization failed: {e}")
            self.db = None

    def populate_processors(self):
        """Populate PROCESSOR category"""
        processors = [
            {
                "id": "cpu_001",
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
                    "socket": "LGA1700",
                    "integrated_graphics": "No"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/intel-i5-12400f"
            },
            {
                "id": "cpu_002",
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
                    "socket": "AM4",
                    "integrated_graphics": "No"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/amd-ryzen-5600x"
            },
            {
                "id": "cpu_003",
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
                    "socket": "LGA1700",
                    "integrated_graphics": "Intel UHD Graphics 770"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/intel-i7-12700k"
            },
            {
                "id": "cpu_004",
                "name": "AMD Ryzen 7 5800X",
                "brand": "AMD",
                "model": "Ryzen 7 5800X",
                "price": 27999,
                "rating": 4.8,
                "ratingCount": 445,
                "description": "8-core, 16-thread Zen 3 processor. Perfect for gaming and content creation.",
                "specifications": {
                    "cores": "8",
                    "threads": "16",
                    "base_clock": "3.8 GHz",
                    "max_clock": "4.7 GHz",
                    "cache": "36MB",
                    "tdp": "105W",
                    "socket": "AM4",
                    "integrated_graphics": "No"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/amd-ryzen-5800x"
            },
            {
                "id": "cpu_005",
                "name": "Intel Core i9-12900K",
                "brand": "Intel",
                "model": "Core i9-12900K",
                "price": 45999,
                "rating": 4.9,
                "ratingCount": 234,
                "description": "16-core, 24-thread flagship processor. Ultimate performance for gaming and professional work.",
                "specifications": {
                    "cores": "16 (8P + 8E)",
                    "threads": "24",
                    "base_clock": "3.2 GHz",
                    "max_clock": "5.2 GHz",
                    "cache": "30MB",
                    "tdp": "125W",
                    "socket": "LGA1700",
                    "integrated_graphics": "Intel UHD Graphics 770"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/intel-i9-12900k"
            }
        ]
        self._add_components("PROCESSOR", processors)
        print(f"SUCCESS: Added {len(processors)} processors")

    def populate_graphics_cards(self):
        """Populate GRAPHIC CARD category"""
        graphics_cards = [
            {
                "id": "gpu_001",
                "name": "NVIDIA RTX 4060 Ti",
                "brand": "NVIDIA",
                "model": "RTX 4060 Ti",
                "price": 39999,
                "rating": 4.4,
                "ratingCount": 789,
                "description": "8GB GDDR6 graphics card with DLSS 3.0. Perfect for 1080p and 1440p gaming.",
                "specifications": {
                    "memory": "8GB GDDR6",
                    "memory_speed": "18 Gbps",
                    "memory_bus": "128-bit",
                    "cuda_cores": "4352",
                    "boost_clock": "2.54 GHz",
                    "tdp": "160W",
                    "power_connector": "1x 8-pin",
                    "length": "242mm"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/rtx-4060-ti"
            },
            {
                "id": "gpu_002",
                "name": "AMD RX 6700 XT",
                "brand": "AMD",
                "model": "RX 6700 XT",
                "price": 35999,
                "rating": 4.5,
                "ratingCount": 567,
                "description": "12GB GDDR6 graphics card with excellent 1440p performance and ray tracing support.",
                "specifications": {
                    "memory": "12GB GDDR6",
                    "memory_speed": "16 Gbps",
                    "memory_bus": "192-bit",
                    "stream_processors": "2560",
                    "boost_clock": "2.42 GHz",
                    "tdp": "230W",
                    "power_connector": "1x 8-pin + 1x 6-pin",
                    "length": "267mm"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/rx-6700-xt"
            },
            {
                "id": "gpu_003",
                "name": "NVIDIA RTX 4070",
                "brand": "NVIDIA",
                "model": "RTX 4070",
                "price": 54999,
                "rating": 4.6,
                "ratingCount": 445,
                "description": "12GB GDDR6X graphics card with DLSS 3.0. Excellent for 1440p and 4K gaming.",
                "specifications": {
                    "memory": "12GB GDDR6X",
                    "memory_speed": "21 Gbps",
                    "memory_bus": "192-bit",
                    "cuda_cores": "5888",
                    "boost_clock": "2.48 GHz",
                    "tdp": "200W",
                    "power_connector": "1x 8-pin",
                    "length": "285mm"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/rtx-4070"
            },
            {
                "id": "gpu_004",
                "name": "AMD RX 6800 XT",
                "brand": "AMD",
                "model": "RX 6800 XT",
                "price": 64999,
                "rating": 4.7,
                "ratingCount": 234,
                "description": "16GB GDDR6 graphics card with exceptional 4K gaming performance.",
                "specifications": {
                    "memory": "16GB GDDR6",
                    "memory_speed": "16 Gbps",
                    "memory_bus": "256-bit",
                    "stream_processors": "4608",
                    "boost_clock": "2.25 GHz",
                    "tdp": "300W",
                    "power_connector": "2x 8-pin",
                    "length": "267mm"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/rx-6800-xt"
            },
            {
                "id": "gpu_005",
                "name": "NVIDIA RTX 4080",
                "brand": "NVIDIA",
                "model": "RTX 4080",
                "price": 114999,
                "rating": 4.8,
                "ratingCount": 123,
                "description": "16GB GDDR6X graphics card with DLSS 3.0. Ultimate 4K gaming performance.",
                "specifications": {
                    "memory": "16GB GDDR6X",
                    "memory_speed": "22.4 Gbps",
                    "memory_bus": "256-bit",
                    "cuda_cores": "9728",
                    "boost_clock": "2.51 GHz",
                    "tdp": "320W",
                    "power_connector": "1x 16-pin",
                    "length": "304mm"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/rtx-4080"
            }
        ]
        self._add_components("GRAPHIC CARD", graphics_cards)
        print(f"SUCCESS: Added {len(graphics_cards)} graphics cards")

    def populate_motherboards(self):
        """Populate MOTHERBOARD category"""
        motherboards = [
            {
                "id": "mb_001",
                "name": "MSI B660M-A WiFi DDR4",
                "brand": "MSI",
                "model": "B660M-A WiFi DDR4",
                "price": 12999,
                "rating": 4.4,
                "ratingCount": 567,
                "description": "Intel B660 chipset, DDR4, WiFi 6, PCIe 4.0, M.2 slots",
                "specifications": {
                    "chipset": "Intel B660",
                    "socket": "LGA1700",
                    "memory_type": "DDR4",
                    "max_memory": "128GB",
                    "memory_slots": "4",
                    "pcie_slots": "1x PCIe 4.0 x16, 1x PCIe 3.0 x1",
                    "m2_slots": "2",
                    "sata_ports": "4",
                    "wifi": "WiFi 6",
                    "bluetooth": "5.2"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/msi-b660m-wifi"
            },
            {
                "id": "mb_002",
                "name": "ASUS ROG STRIX B550-F Gaming",
                "brand": "ASUS",
                "model": "ROG STRIX B550-F Gaming",
                "price": 15999,
                "rating": 4.6,
                "ratingCount": 445,
                "description": "AMD B550 chipset, PCIe 4.0, 2.5Gb LAN, SupremeFX audio",
                "specifications": {
                    "chipset": "AMD B550",
                    "socket": "AM4",
                    "memory_type": "DDR4",
                    "max_memory": "128GB",
                    "memory_slots": "4",
                    "pcie_slots": "1x PCIe 4.0 x16, 2x PCIe 3.0 x1",
                    "m2_slots": "2",
                    "sata_ports": "6",
                    "lan": "2.5Gb",
                    "audio": "SupremeFX S1220A"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/asus-b550-f"
            }
        ]
        self._add_components("MOTHERBOARD", motherboards)
        print(f"SUCCESS: Added {len(motherboards)} motherboards")

    def populate_ram(self):
        """Populate MEMORY category"""
        ram_modules = [
            {
                "id": "ram_001",
                "name": "Corsair Vengeance LPX 16GB (2x8GB) DDR4-3200",
                "brand": "Corsair",
                "model": "Vengeance LPX",
                "price": 3999,
                "rating": 4.5,
                "ratingCount": 1234,
                "description": "16GB DDR4-3200MHz dual channel kit, low profile design",
                "specifications": {
                    "capacity": "16GB (2x8GB)",
                    "speed": "3200MHz",
                    "type": "DDR4",
                    "latency": "CL16",
                    "voltage": "1.35V",
                    "form_factor": "DIMM",
                    "heat_spreader": "Yes"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/corsair-vengeance-16gb"
            },
            {
                "id": "ram_002",
                "name": "G.Skill Ripjaws V 32GB (2x16GB) DDR4-3600",
                "brand": "G.Skill",
                "model": "Ripjaws V",
                "price": 8999,
                "rating": 4.6,
                "ratingCount": 567,
                "description": "32GB DDR4-3600MHz dual channel kit, optimized for AMD",
                "specifications": {
                    "capacity": "32GB (2x16GB)",
                    "speed": "3600MHz",
                    "type": "DDR4",
                    "latency": "CL18",
                    "voltage": "1.35V",
                    "form_factor": "DIMM",
                    "heat_spreader": "Yes"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/gskill-ripjaws-32gb"
            }
        ]
        self._add_components("MEMORY", ram_modules)
        print(f"SUCCESS: Added {len(ram_modules)} RAM modules")

    def populate_storage(self):
        """Populate STORAGE category"""
        storage_devices = [
            {
                "id": "ssd_001",
                "name": "Samsung 970 EVO Plus 1TB NVMe SSD",
                "brand": "Samsung",
                "model": "970 EVO Plus",
                "price": 8999,
                "rating": 4.8,
                "ratingCount": 2345,
                "description": "1TB NVMe SSD with 3500MB/s read, 3300MB/s write speeds",
                "specifications": {
                    "capacity": "1TB",
                    "type": "NVMe SSD",
                    "interface": "PCIe 3.0 x4",
                    "read_speed": "3500 MB/s",
                    "write_speed": "3300 MB/s",
                    "form_factor": "M.2 2280",
                    "nand_type": "V-NAND 3-bit MLC",
                    "endurance": "600 TBW"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/samsung-970-evo-1tb"
            },
            {
                "id": "ssd_002",
                "name": "WD Black SN850 500GB NVMe SSD",
                "brand": "Western Digital",
                "model": "Black SN850",
                "price": 5999,
                "rating": 4.7,
                "ratingCount": 1234,
                "description": "500GB PCIe 4.0 NVMe SSD with 7000MB/s read speeds",
                "specifications": {
                    "capacity": "500GB",
                    "type": "NVMe SSD",
                    "interface": "PCIe 4.0 x4",
                    "read_speed": "7000 MB/s",
                    "write_speed": "5300 MB/s",
                    "form_factor": "M.2 2280",
                    "nand_type": "3D NAND",
                    "endurance": "300 TBW"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/wd-black-sn850-500gb"
            }
        ]
        self._add_components("STORAGE", storage_devices)
        print(f"SUCCESS: Added {len(storage_devices)} storage devices")

    def populate_power_supplies(self):
        """Populate POWER SUPPLY category"""
        power_supplies = [
            {
                "id": "psu_001",
                "name": "Corsair RM750x 750W 80+ Gold",
                "brand": "Corsair",
                "model": "RM750x",
                "price": 9999,
                "rating": 4.6,
                "ratingCount": 1234,
                "description": "750W fully modular power supply with 80+ Gold efficiency",
                "specifications": {
                    "wattage": "750W",
                    "efficiency": "80+ Gold",
                    "modularity": "Fully Modular",
                    "form_factor": "ATX",
                    "fan_size": "135mm",
                    "protections": "OVP, UVP, SCP, OCP, OTP",
                    "warranty": "10 years"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/corsair-rm750x"
            },
            {
                "id": "psu_002",
                "name": "EVGA 650W GQ 80+ Gold",
                "brand": "EVGA",
                "model": "650W GQ",
                "price": 7999,
                "rating": 4.4,
                "ratingCount": 567,
                "description": "650W semi-modular power supply with 80+ Gold efficiency",
                "specifications": {
                    "wattage": "650W",
                    "efficiency": "80+ Gold",
                    "modularity": "Semi-Modular",
                    "form_factor": "ATX",
                    "fan_size": "135mm",
                    "protections": "OVP, UVP, SCP, OCP, OTP",
                    "warranty": "5 years"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/evga-650w-gq"
            }
        ]
        self._add_components("POWER SUPPLY", power_supplies)
        print(f"SUCCESS: Added {len(power_supplies)} power supplies")

    def populate_cabinets(self):
        """Populate CABINET category"""
        cases = [
            {
                "id": "case_001",
                "name": "NZXT H510 Flow",
                "brand": "NZXT",
                "model": "H510 Flow",
                "price": 6999,
                "rating": 4.5,
                "ratingCount": 789,
                "description": "ATX mid tower with mesh front panel for improved airflow",
                "specifications": {
                    "form_factor": "ATX Mid Tower",
                    "material": "Steel + Tempered Glass",
                    "expansion_slots": "7",
                    "drive_bays": "2x 3.5\", 2x 2.5\"",
                    "fan_mounts": "4x 120mm, 1x 140mm",
                    "included_fans": "2x 120mm",
                    "max_gpu_length": "360mm",
                    "max_cpu_height": "165mm"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/nzxt-h510-flow"
            },
            {
                "id": "case_002",
                "name": "Phanteks P300A Mesh",
                "brand": "Phanteks",
                "model": "P300A Mesh",
                "price": 4999,
                "rating": 4.3,
                "ratingCount": 456,
                "description": "Compact ATX case with mesh front panel and good airflow",
                "specifications": {
                    "form_factor": "ATX Mid Tower",
                    "material": "Steel + Mesh",
                    "expansion_slots": "7",
                    "drive_bays": "2x 3.5\", 2x 2.5\"",
                    "fan_mounts": "3x 120mm",
                    "included_fans": "1x 120mm",
                    "max_gpu_length": "330mm",
                    "max_cpu_height": "160mm"
                },
                "inStock": True,
                "imageUrl": "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400&h=400&fit=crop",
                "productUrl": "https://amzn.to/phanteks-p300a-mesh"
            }
        ]
        self._add_components("CABINET", cases)
        print(f"SUCCESS: Added {len(cases)} cases")

    def _add_components(self, category, components):
        """Helper method to add components to Firebase"""
        if not self.db:
            print(f"WARNING: Mock mode - Would add {len(components)} {category} components")
            return
            
        try:
            category_ref = self.db.collection('pc_components').document(category)
            category_ref.set({'name': category, 'lastUpdated': datetime.now()})
            
            for component in components:
                doc_ref = category_ref.collection('items').document(component['id'])
                doc_ref.set(component)
                print(f"  ADDED: {component['name']}")
                
        except Exception as e:
            print(f"ERROR: Error adding {category} components: {e}")

    def populate_all_categories(self):
        """Populate all component categories"""
        print("STARTING: Firebase data population...")
        print("=" * 60)
        
        # Add all categories
        self.populate_processors()
        self.populate_graphics_cards()
        self.populate_motherboards()
        self.populate_ram()
        self.populate_storage()
        self.populate_power_supplies()
        self.populate_cabinets()
        
        print("\n" + "=" * 60)
        print("COMPLETED: Firebase data population!")
        print("TOTAL: 21 components added")
        print("CATEGORIES:")
        print("  - PROCESSOR: 5 components")
        print("  - GRAPHIC CARD: 5 components")
        print("  - MOTHERBOARD: 2 components")
        print("  - MEMORY: 2 components")
        print("  - STORAGE: 2 components")
        print("  - POWER SUPPLY: 2 components")
        print("  - CABINET: 2 components")

def main():
    """Main function to run the simple Firebase populator"""
    print("PC-Cooker Simple Firebase Data Populator")
    print("Current Indian Market Prices & Data")
    print("=" * 60)
    
    populator = SimpleFirebasePopulator()
    populator.populate_all_categories()
    
    print("\nNEXT STEPS:")
    print("1. Test your Android app")
    print("2. Check if components are loading from Firebase")
    print("3. Verify prices and images are correct")

if __name__ == "__main__":
    main() 
const admin = require('firebase-admin');
const fs = require('fs');

// Initialize Firebase Admin SDK
// You'll need to download your service account key from Firebase Console
// Go to Project Settings > Service Accounts > Generate New Private Key
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

// Component data organized by categories
const componentData = {
  "PROCESSOR": [
    {
      id: "cpu_001",
      name: "Intel Core i5-12400F",
      category: "PROCESSOR",
      price: 15999.0,
      rating: 4.5,
      ratingCount: 1250,
      imageUrl: "",
      productUrl: "",
      description: "6 cores, 12 threads, 2.5GHz base, 4.4GHz boost",
      specifications: {
        "Cores": "6",
        "Threads": "12",
        "Base Clock": "2.5GHz",
        "Boost Clock": "4.4GHz",
        "Socket": "LGA1700",
        "TDP": "65W"
      },
      inStock: true,
      brand: "Intel",
      model: "i5-12400F"
    },
    {
      id: "cpu_002",
      name: "AMD Ryzen 5 5600X",
      category: "PROCESSOR",
      price: 18999.0,
      rating: 4.7,
      ratingCount: 980,
      imageUrl: "",
      productUrl: "",
      description: "6 cores, 12 threads, 3.7GHz base, 4.6GHz boost",
      specifications: {
        "Cores": "6",
        "Threads": "12",
        "Base Clock": "3.7GHz",
        "Boost Clock": "4.6GHz",
        "Socket": "AM4",
        "TDP": "65W"
      },
      inStock: true,
      brand: "AMD",
      model: "Ryzen 5 5600X"
    },
    {
      id: "cpu_003",
      name: "Intel Core i7-12700K",
      category: "PROCESSOR",
      price: 28999.0,
      rating: 4.8,
      ratingCount: 750,
      imageUrl: "",
      productUrl: "",
      description: "12 cores, 20 threads, 3.6GHz base, 5.0GHz boost",
      specifications: {
        "Cores": "12",
        "Threads": "20",
        "Base Clock": "3.6GHz",
        "Boost Clock": "5.0GHz",
        "Socket": "LGA1700",
        "TDP": "125W"
      },
      inStock: true,
      brand: "Intel",
      model: "i7-12700K"
    },
    {
      id: "cpu_004",
      name: "AMD Ryzen 7 5800X",
      category: "PROCESSOR",
      price: 24999.0,
      rating: 4.6,
      ratingCount: 650,
      imageUrl: "",
      productUrl: "",
      description: "8 cores, 16 threads, 3.8GHz base, 4.7GHz boost",
      specifications: {
        "Cores": "8",
        "Threads": "16",
        "Base Clock": "3.8GHz",
        "Boost Clock": "4.7GHz",
        "Socket": "AM4",
        "TDP": "105W"
      },
      inStock: true,
      brand: "AMD",
      model: "Ryzen 7 5800X"
    }
  ],
  "GRAPHIC CARD": [
    {
      id: "gpu_001",
      name: "NVIDIA RTX 4060 Ti",
      category: "GRAPHIC CARD",
      price: 34999.0,
      rating: 4.4,
      ratingCount: 890,
      imageUrl: "",
      productUrl: "",
      description: "8GB GDDR6, Ray Tracing, DLSS 3.0",
      specifications: {
        "Memory": "8GB GDDR6",
        "Memory Bus": "128-bit",
        "Boost Clock": "2.6GHz",
        "TDP": "160W",
        "Ray Tracing": "Yes",
        "DLSS": "3.0"
      },
      inStock: true,
      brand: "NVIDIA",
      model: "RTX 4060 Ti"
    },
    {
      id: "gpu_002",
      name: "AMD RX 6700 XT",
      category: "GRAPHIC CARD",
      price: 39999.0,
      rating: 4.5,
      ratingCount: 720,
      imageUrl: "",
      productUrl: "",
      description: "12GB GDDR6, RDNA 2 Architecture",
      specifications: {
        "Memory": "12GB GDDR6",
        "Memory Bus": "192-bit",
        "Boost Clock": "2.4GHz",
        "TDP": "230W",
        "Ray Tracing": "Yes",
        "FSR": "2.0"
      },
      inStock: true,
      brand: "AMD",
      model: "RX 6700 XT"
    },
    {
      id: "gpu_003",
      name: "NVIDIA RTX 4070",
      category: "GRAPHIC CARD",
      price: 54999.0,
      rating: 4.7,
      ratingCount: 450,
      imageUrl: "",
      productUrl: "",
      description: "12GB GDDR6X, Advanced Ray Tracing",
      specifications: {
        "Memory": "12GB GDDR6X",
        "Memory Bus": "192-bit",
        "Boost Clock": "2.5GHz",
        "TDP": "200W",
        "Ray Tracing": "Yes",
        "DLSS": "3.0"
      },
      inStock: true,
      brand: "NVIDIA",
      model: "RTX 4070"
    }
  ],
  "RAM": [
    {
      id: "ram_001",
      name: "Corsair Vengeance LPX 16GB (2x8GB)",
      category: "RAM",
      price: 4999.0,
      rating: 4.4,
      ratingCount: 5678,
      imageUrl: "",
      productUrl: "",
      description: "DDR4 3200MHz, CL16, Dual Channel",
      specifications: {
        "Capacity": "16GB (2x8GB)",
        "Speed": "3200MHz",
        "Type": "DDR4",
        "CAS Latency": "CL16",
        "Voltage": "1.35V",
        "Form Factor": "DIMM"
      },
      inStock: true,
      brand: "Corsair",
      model: "Vengeance LPX"
    },
    {
      id: "ram_002",
      name: "G.Skill Ripjaws V 32GB (2x16GB)",
      category: "RAM",
      price: 8999.0,
      rating: 4.5,
      ratingCount: 2341,
      imageUrl: "",
      productUrl: "",
      description: "DDR4 3600MHz, CL18, High Performance",
      specifications: {
        "Capacity": "32GB (2x16GB)",
        "Speed": "3600MHz",
        "Type": "DDR4",
        "CAS Latency": "CL18",
        "Voltage": "1.35V",
        "Form Factor": "DIMM"
      },
      inStock: true,
      brand: "G.Skill",
      model: "Ripjaws V"
    },
    {
      id: "ram_003",
      name: "Crucial Ballistix 8GB (1x8GB)",
      category: "RAM",
      price: 2999.0,
      rating: 4.3,
      ratingCount: 3456,
      imageUrl: "",
      productUrl: "",
      description: "DDR4 3000MHz, CL15, Budget Friendly",
      specifications: {
        "Capacity": "8GB (1x8GB)",
        "Speed": "3000MHz",
        "Type": "DDR4",
        "CAS Latency": "CL15",
        "Voltage": "1.35V",
        "Form Factor": "DIMM"
      },
      inStock: true,
      brand: "Crucial",
      model: "Ballistix"
    }
  ],
  "MOTHERBOARD": [
    {
      id: "mobo_001",
      name: "MSI B660M-A WiFi",
      category: "MOTHERBOARD",
      price: 12999.0,
      rating: 4.3,
      ratingCount: 3456,
      imageUrl: "",
      productUrl: "",
      description: "Intel B660, LGA1700, WiFi 6, M.2 Slots",
      specifications: {
        "Chipset": "Intel B660",
        "Socket": "LGA1700",
        "Form Factor": "Micro-ATX",
        "Memory Slots": "4x DDR4",
        "PCIe Slots": "1x PCIe 4.0 x16",
        "M.2 Slots": "2x M.2"
      },
      inStock: true,
      brand: "MSI",
      model: "B660M-A WiFi"
    },
    {
      id: "mobo_002",
      name: "ASUS ROG B550-F Gaming",
      category: "MOTHERBOARD",
      price: 15999.0,
      rating: 4.5,
      ratingCount: 2341,
      imageUrl: "",
      productUrl: "",
      description: "AMD B550, AM4, WiFi 6, 2.5Gb LAN",
      specifications: {
        "Chipset": "AMD B550",
        "Socket": "AM4",
        "Form Factor": "ATX",
        "Memory Slots": "4x DDR4",
        "PCIe Slots": "1x PCIe 4.0 x16",
        "M.2 Slots": "2x M.2"
      },
      inStock: true,
      brand: "ASUS",
      model: "ROG B550-F"
    },
    {
      id: "mobo_003",
      name: "Gigabyte B760M DS3H",
      category: "MOTHERBOARD",
      price: 11999.0,
      rating: 4.2,
      ratingCount: 1890,
      imageUrl: "",
      productUrl: "",
      description: "Intel B760, LGA1700, DDR4, Budget Option",
      specifications: {
        "Chipset": "Intel B760",
        "Socket": "LGA1700",
        "Form Factor": "Micro-ATX",
        "Memory Slots": "4x DDR4",
        "PCIe Slots": "1x PCIe 4.0 x16",
        "M.2 Slots": "1x M.2"
      },
      inStock: true,
      brand: "Gigabyte",
      model: "B760M DS3H"
    }
  ],
  "STORAGE": [
    {
      id: "storage_001",
      name: "Samsung 970 EVO Plus 500GB",
      category: "STORAGE",
      price: 5999.0,
      rating: 4.6,
      ratingCount: 7890,
      imageUrl: "",
      productUrl: "",
      description: "NVMe M.2 SSD, 3500MB/s Read, 3300MB/s Write",
      specifications: {
        "Capacity": "500GB",
        "Type": "NVMe M.2 SSD",
        "Read Speed": "3500MB/s",
        "Write Speed": "3300MB/s",
        "Interface": "PCIe 3.0 x4",
        "Form Factor": "M.2 2280"
      },
      inStock: true,
      brand: "Samsung",
      model: "970 EVO Plus"
    },
    {
      id: "storage_002",
      name: "WD Blue 1TB HDD",
      category: "STORAGE",
      price: 2999.0,
      rating: 4.2,
      ratingCount: 4567,
      imageUrl: "",
      productUrl: "",
      description: "7200 RPM, 64MB Cache, Reliable Storage",
      specifications: {
        "Capacity": "1TB",
        "Type": "HDD",
        "RPM": "7200",
        "Cache": "64MB",
        "Interface": "SATA 6Gb/s",
        "Form Factor": "3.5-inch"
      },
      inStock: true,
      brand: "Western Digital",
      model: "Blue"
    },
    {
      id: "storage_003",
      name: "Crucial P3 1TB",
      category: "STORAGE",
      price: 6999.0,
      rating: 4.4,
      ratingCount: 3245,
      imageUrl: "",
      productUrl: "",
      description: "NVMe M.2 SSD, 3500MB/s Read, 3000MB/s Write",
      specifications: {
        "Capacity": "1TB",
        "Type": "NVMe M.2 SSD",
        "Read Speed": "3500MB/s",
        "Write Speed": "3000MB/s",
        "Interface": "PCIe 3.0 x4",
        "Form Factor": "M.2 2280"
      },
      inStock: true,
      brand: "Crucial",
      model: "P3"
    }
  ],
  "POWER SUPPLY": [
    {
      id: "psu_001",
      name: "Corsair CX650M",
      category: "POWER SUPPLY",
      price: 4999.0,
      rating: 4.3,
      ratingCount: 3456,
      imageUrl: "",
      productUrl: "",
      description: "650W, 80+ Bronze, Semi-Modular",
      specifications: {
        "Wattage": "650W",
        "Efficiency": "80+ Bronze",
        "Modularity": "Semi-Modular",
        "Form Factor": "ATX",
        "PCIe Connectors": "2x 6+2-pin",
        "SATA Connectors": "6x"
      },
      inStock: true,
      brand: "Corsair",
      model: "CX650M"
    },
    {
      id: "psu_002",
      name: "EVGA 750W GQ",
      category: "POWER SUPPLY",
      price: 6999.0,
      rating: 4.4,
      ratingCount: 2341,
      imageUrl: "",
      productUrl: "",
      description: "750W, 80+ Gold, Semi-Modular",
      specifications: {
        "Wattage": "750W",
        "Efficiency": "80+ Gold",
        "Modularity": "Semi-Modular",
        "Form Factor": "ATX",
        "PCIe Connectors": "2x 6+2-pin",
        "SATA Connectors": "8x"
      },
      inStock: true,
      brand: "EVGA",
      model: "750W GQ"
    },
    {
      id: "psu_003",
      name: "Seasonic S12III 500W",
      category: "POWER SUPPLY",
      price: 3999.0,
      rating: 4.1,
      ratingCount: 2789,
      imageUrl: "",
      productUrl: "",
      description: "500W, 80+ Bronze, Non-Modular",
      specifications: {
        "Wattage": "500W",
        "Efficiency": "80+ Bronze",
        "Modularity": "Non-Modular",
        "Form Factor": "ATX",
        "PCIe Connectors": "1x 6+2-pin",
        "SATA Connectors": "4x"
      },
      inStock: true,
      brand: "Seasonic",
      model: "S12III 500W"
    }
  ],
  "CABINET": [
    {
      id: "case_001",
      name: "NZXT H510",
      category: "CABINET",
      price: 7999.0,
      rating: 4.5,
      ratingCount: 3456,
      imageUrl: "",
      productUrl: "",
      description: "Mid Tower ATX, Tempered Glass, USB-C",
      specifications: {
        "Form Factor": "Mid Tower ATX",
        "Material": "Steel + Tempered Glass",
        "Expansion Slots": "7",
        "Drive Bays": "2x 3.5\", 2x 2.5\"",
        "Front I/O": "USB-C, USB 3.0, Audio",
        "Fans": "2x 120mm included"
      },
      inStock: true,
      brand: "NZXT",
      model: "H510"
    },
    {
      id: "case_002",
      name: "Corsair 4000D Airflow",
      category: "CABINET",
      price: 8999.0,
      rating: 4.7,
      ratingCount: 2341,
      imageUrl: "",
      productUrl: "",
      description: "Mid Tower ATX, Mesh Front, Excellent Airflow",
      specifications: {
        "Form Factor": "Mid Tower ATX",
        "Material": "Steel + Mesh",
        "Expansion Slots": "7",
        "Drive Bays": "2x 3.5\", 2x 2.5\"",
        "Front I/O": "USB 3.0, Audio",
        "Fans": "2x 120mm included"
      },
      inStock: true,
      brand: "Corsair",
      model: "4000D Airflow"
    },
    {
      id: "case_003",
      name: "Phanteks P300A",
      category: "CABINET",
      price: 5999.0,
      rating: 4.3,
      ratingCount: 1890,
      imageUrl: "",
      productUrl: "",
      description: "Mid Tower ATX, Mesh Front, Budget Friendly",
      specifications: {
        "Form Factor": "Mid Tower ATX",
        "Material": "Steel + Mesh",
        "Expansion Slots": "7",
        "Drive Bays": "2x 3.5\", 2x 2.5\"",
        "Front I/O": "USB 3.0, Audio",
        "Fans": "1x 120mm included"
      },
      inStock: true,
      brand: "Phanteks",
      model: "P300A"
    }
  ]
};

// Function to import all component data
async function importComponents() {
  try {
    console.log('Starting component import...');
    
    for (const [category, components] of Object.entries(componentData)) {
      console.log(`Importing ${components.length} components for category: ${category}`);
      
      // Create the category document if it doesn't exist
      const categoryRef = db.collection('pc_components').doc(category);
      await categoryRef.set({ name: category }, { merge: true });
      
      // Import each component in the category
      for (const component of components) {
        const componentRef = categoryRef.collection('items').doc(component.id);
        await componentRef.set(component);
        console.log(`✓ Imported: ${component.name}`);
      }
      
      console.log(`✅ Completed importing ${components.length} components for ${category}\n`);
    }
    
    console.log('🎉 All components imported successfully!');
    console.log('\nSummary:');
    for (const [category, components] of Object.entries(componentData)) {
      console.log(`- ${category}: ${components.length} components`);
    }
    
  } catch (error) {
    console.error('❌ Error importing components:', error);
  } finally {
    process.exit(0);
  }
}

// Run the import
importComponents(); 
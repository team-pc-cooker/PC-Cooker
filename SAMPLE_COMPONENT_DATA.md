# Sample Component Data for PC Cooker

## Firebase Data Structure

### Collection: `pc_components`

#### Document: `PROCESSOR`
Collection: `items`

```json
{
  "id": "cpu_1",
  "name": "Intel Core i5-12400F",
  "category": "PROCESSOR",
  "brand": "Intel",
  "model": "Core i5-12400F",
  "price": 15500,
  "rating": 4.5,
  "ratingCount": 1250,
  "description": "6 cores, 12 threads, 2.5GHz base, 4.4GHz boost",
  "imageUrl": "https://via.placeholder.com/300x300?text=Intel+i5-12400F",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "cores": 6,
    "threads": 12,
    "baseSpeed": "2.5GHz",
    "boostSpeed": "4.4GHz",
    "socket": "LGA1700",
    "tdp": "65W"
  }
}
```

```json
{
  "id": "cpu_2",
  "name": "AMD Ryzen 5 5600X",
  "category": "PROCESSOR",
  "brand": "AMD",
  "model": "Ryzen 5 5600X",
  "price": 18500,
  "rating": 4.6,
  "ratingCount": 980,
  "description": "6 cores, 12 threads, 3.7GHz base, 4.6GHz boost",
  "imageUrl": "https://via.placeholder.com/300x300?text=AMD+Ryzen+5+5600X",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "cores": 6,
    "threads": 12,
    "baseSpeed": "3.7GHz",
    "boostSpeed": "4.6GHz",
    "socket": "AM4",
    "tdp": "65W"
  }
}
```

```json
{
  "id": "cpu_3",
  "name": "Intel Core i7-12700F",
  "category": "PROCESSOR",
  "brand": "Intel",
  "model": "Core i7-12700F",
  "price": 25000,
  "rating": 4.7,
  "ratingCount": 650,
  "description": "12 cores, 20 threads, 2.1GHz base, 4.9GHz boost",
  "imageUrl": "https://via.placeholder.com/300x300?text=Intel+i7-12700F",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "cores": 12,
    "threads": 20,
    "baseSpeed": "2.1GHz",
    "boostSpeed": "4.9GHz",
    "socket": "LGA1700",
    "tdp": "65W"
  }
}
```

#### Document: `GRAPHIC CARD`
Collection: `items`

```json
{
  "id": "gpu_1",
  "name": "NVIDIA RTX 4060 Ti",
  "category": "GRAPHIC CARD",
  "brand": "NVIDIA",
  "model": "RTX 4060 Ti",
  "price": 35000,
  "rating": 4.3,
  "ratingCount": 890,
  "description": "8GB GDDR6, Ray Tracing, DLSS 3.0",
  "imageUrl": "https://via.placeholder.com/300x300?text=RTX+4060+Ti",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "memory": "8GB GDDR6",
    "memoryBus": "128-bit",
    "boostClock": "2535 MHz",
    "tdp": "160W"
  }
}
```

```json
{
  "id": "gpu_2",
  "name": "AMD RX 6700 XT",
  "category": "GRAPHIC CARD",
  "brand": "AMD",
  "model": "RX 6700 XT",
  "price": 38000,
  "rating": 4.4,
  "ratingCount": 720,
  "description": "12GB GDDR6, RDNA 2 Architecture",
  "imageUrl": "https://via.placeholder.com/300x300?text=RX+6700+XT",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "memory": "12GB GDDR6",
    "memoryBus": "192-bit",
    "boostClock": "2424 MHz",
    "tdp": "230W"
  }
}
```

#### Document: `RAM`
Collection: `items`

```json
{
  "id": "ram_1",
  "name": "Corsair Vengeance LPX 16GB",
  "category": "RAM",
  "brand": "Corsair",
  "model": "Vengeance LPX",
  "price": 4500,
  "rating": 4.6,
  "ratingCount": 2100,
  "description": "16GB (2x8GB) DDR4 3200MHz",
  "imageUrl": "https://via.placeholder.com/300x300?text=Corsair+16GB",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "capacity": "16GB",
    "type": "DDR4",
    "speed": "3200MHz",
    "modules": "2x8GB"
  }
}
```

```json
{
  "id": "ram_2",
  "name": "G.Skill Ripjaws V 32GB",
  "category": "RAM",
  "brand": "G.Skill",
  "model": "Ripjaws V",
  "price": 8500,
  "rating": 4.5,
  "ratingCount": 450,
  "description": "32GB (2x16GB) DDR4 3600MHz",
  "imageUrl": "https://via.placeholder.com/300x300?text=G.Skill+32GB",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "capacity": "32GB",
    "type": "DDR4",
    "speed": "3600MHz",
    "modules": "2x16GB"
  }
}
```

#### Document: `MOTHERBOARD`
Collection: `items`

```json
{
  "id": "mobo_1",
  "name": "MSI B660M-A WiFi",
  "category": "MOTHERBOARD",
  "brand": "MSI",
  "model": "B660M-A WiFi",
  "price": 12000,
  "rating": 4.4,
  "ratingCount": 650,
  "description": "Intel B660, LGA1700, WiFi 6E",
  "imageUrl": "https://via.placeholder.com/300x300?text=MSI+B660M",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "chipset": "Intel B660",
    "socket": "LGA1700",
    "formFactor": "Micro-ATX",
    "wifi": true
  }
}
```

```json
{
  "id": "mobo_2",
  "name": "ASUS ROG B660-F",
  "category": "MOTHERBOARD",
  "brand": "ASUS",
  "model": "ROG B660-F",
  "price": 15000,
  "rating": 4.5,
  "ratingCount": 420,
  "description": "Intel B660, LGA1700, ATX",
  "imageUrl": "https://via.placeholder.com/300x300?text=ASUS+ROG+B660",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "chipset": "Intel B660",
    "socket": "LGA1700",
    "formFactor": "ATX",
    "wifi": false
  }
}
```

#### Document: `STORAGE`
Collection: `items`

```json
{
  "id": "ssd_1",
  "name": "Samsung 970 EVO Plus 500GB",
  "category": "STORAGE",
  "brand": "Samsung",
  "model": "970 EVO Plus",
  "price": 5500,
  "rating": 4.7,
  "ratingCount": 3200,
  "description": "500GB NVMe M.2 SSD",
  "imageUrl": "https://via.placeholder.com/300x300?text=Samsung+970+EVO",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "capacity": "500GB",
    "type": "NVMe M.2",
    "readSpeed": "3500 MB/s",
    "writeSpeed": "3300 MB/s"
  }
}
```

```json
{
  "id": "ssd_2",
  "name": "WD Black SN750 1TB",
  "category": "STORAGE",
  "brand": "Western Digital",
  "model": "Black SN750",
  "price": 9500,
  "rating": 4.6,
  "ratingCount": 1800,
  "description": "1TB NVMe M.2 SSD",
  "imageUrl": "https://via.placeholder.com/300x300?text=WD+Black+SN750",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "capacity": "1TB",
    "type": "NVMe M.2",
    "readSpeed": "3430 MB/s",
    "writeSpeed": "3000 MB/s"
  }
}
```

#### Document: `POWER SUPPLY`
Collection: `items`

```json
{
  "id": "psu_1",
  "name": "Corsair RM650x",
  "category": "POWER SUPPLY",
  "brand": "Corsair",
  "model": "RM650x",
  "price": 8500,
  "rating": 4.5,
  "ratingCount": 1100,
  "description": "650W 80+ Gold Fully Modular",
  "imageUrl": "https://via.placeholder.com/300x300?text=Corsair+RM650x",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "wattage": "650W",
    "efficiency": "80+ Gold",
    "modular": "Fully Modular",
    "formFactor": "ATX"
  }
}
```

```json
{
  "id": "psu_2",
  "name": "EVGA 600W 80+",
  "category": "POWER SUPPLY",
  "brand": "EVGA",
  "model": "600W 80+",
  "price": 5500,
  "rating": 4.3,
  "ratingCount": 850,
  "description": "600W 80+ Certified",
  "imageUrl": "https://via.placeholder.com/300x300?text=EVGA+600W",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "wattage": "600W",
    "efficiency": "80+",
    "modular": "Non-Modular",
    "formFactor": "ATX"
  }
}
```

#### Document: `CABINET`
Collection: `items`

```json
{
  "id": "case_1",
  "name": "NZXT H510",
  "category": "CABINET",
  "brand": "NZXT",
  "model": "H510",
  "price": 6500,
  "rating": 4.3,
  "ratingCount": 850,
  "description": "Mid Tower ATX Case",
  "imageUrl": "https://via.placeholder.com/300x300?text=NZXT+H510",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "formFactor": "Mid Tower",
    "motherboardSupport": "ATX, Micro-ATX, Mini-ITX",
    "expansionSlots": 7,
    "fans": "2x 120mm included"
  }
}
```

```json
{
  "id": "case_2",
  "name": "Phanteks P300A",
  "category": "CABINET",
  "brand": "Phanteks",
  "model": "P300A",
  "price": 5500,
  "rating": 4.4,
  "ratingCount": 620,
  "description": "Mid Tower ATX Case",
  "imageUrl": "https://via.placeholder.com/300x300?text=Phanteks+P300A",
  "amazonUrl": "https://amazon.in/dp/example",
  "inStock": true,
  "specifications": {
    "formFactor": "Mid Tower",
    "motherboardSupport": "ATX, Micro-ATX, Mini-ITX",
    "expansionSlots": 7,
    "fans": "1x 120mm included"
  }
}
```

## Price Update Guidelines

### Weekly Price Check Sources:
1. **Amazon.in** - Check current prices
2. **Flipkart.com** - Compare prices
3. **PrimeABGB** - Indian retailer
4. **Vedant Computers** - Indian retailer
5. **MD Computers** - Indian retailer

### Price Update Strategy:
- **High-demand items**: Check weekly
- **Standard components**: Check monthly
- **Low-demand items**: Check quarterly

### Markup Strategy:
- **Processors**: 5-8% markup
- **Graphics Cards**: 3-5% markup
- **RAM/Storage**: 8-12% markup
- **Motherboards**: 10-15% markup
- **Power Supplies**: 12-18% markup
- **Cases**: 15-20% markup

This data structure provides a solid foundation for your PC Cooker app with realistic market prices and comprehensive component information. 
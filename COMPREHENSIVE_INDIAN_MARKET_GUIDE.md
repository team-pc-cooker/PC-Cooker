# PC-Cooker Comprehensive Indian Market Data Population Guide

## 🎯 Overview

This guide will help you populate your Firebase database with **60+ current Indian market PC components** across all categories with real images, current prices, and comprehensive specifications.

## 📊 What You'll Get

### Component Categories & Counts
- **PROCESSOR**: 12 components (Intel & AMD - Low to High range)
- **GRAPHIC CARD**: 12 components (NVIDIA & AMD - Low to High range)
- **RAM**: 6 components (DDR4 & DDR5 - Low to High range)
- **MOTHERBOARD**: 4 components (Intel & AMD - Low to High range)
- **STORAGE**: 4 components (SSDs & HDDs - Low to High range)
- **POWER SUPPLY**: 3 components (550W-1000W - Low to High range)
- **CABINET**: 3 components (ATX cases - Low to High range)
- **COOLING**: 3 components (Air & Liquid - Low to High range)
- **MONITOR**: 3 components (1080p to 1440p - Low to High range)
- **KEYBOARD**: 3 components (Membrane to Mechanical - Low to High range)
- **MOUSE**: 3 components (Basic to Gaming - Low to High range)

**Total**: 60+ components across 11 categories

### Features
- ✅ Current Indian market prices (as of 2024)
- ✅ Non-copyright images (100x100 size from Unsplash)
- ✅ Comprehensive specifications for each component
- ✅ User ratings and review counts
- ✅ Stock availability status
- ✅ Product URLs for reference
- ✅ Price ranges: Low, Mid, and High for each category

## 🚀 Quick Start

### Step 1: Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing one
3. Enable Firestore Database
4. Go to Project Settings > Service Accounts
5. Click "Generate New Private Key"
6. Save the JSON file as `serviceAccountKey.json` in the PC-Cooker directory

### Step 2: Install Dependencies
```bash
pip install firebase-admin
```

### Step 3: Run the Comprehensive Population Script
```bash
python run_comprehensive_population.py
```

## 📁 File Structure

```
PC-Cooker/
├── run_comprehensive_population.py          # Master script (run this)
├── comprehensive_indian_market_populator.py # Complete populator
├── serviceAccountKey.json                   # Firebase credentials
└── COMPREHENSIVE_INDIAN_MARKET_GUIDE.md    # This guide
```

## 📊 Component Details

### Processors (12 components)
**Low Range (₹5,000 - ₹15,000):**
- Intel Core i3-12100F (₹8,999)
- AMD Ryzen 3 4100 (₹7,499)
- Intel Core i5-12400F (₹15,999)

**Mid Range (₹15,000 - ₹35,000):**
- AMD Ryzen 5 5600X (₹18,999)
- Intel Core i5-13400F (₹21,999)
- AMD Ryzen 5 7600X (₹24,999)

**High Range (₹35,000+):**
- Intel Core i7-12700K (₹32,999)
- AMD Ryzen 7 5800X (₹27,999)
- Intel Core i9-12900K (₹45,999)
- AMD Ryzen 9 5900X (₹38,999)
- Intel Core i7-13700K (₹37,999)
- AMD Ryzen 7 7700X (₹32,999)

### Graphics Cards (12 components)
**Low Range (₹10,000 - ₹25,000):**
- NVIDIA GTX 1650 (₹14,999)
- AMD RX 6500 XT (₹18,999)
- NVIDIA GTX 1660 Super (₹19,999)

**Mid Range (₹25,000 - ₹60,000):**
- NVIDIA RTX 3060 (₹29,999)
- AMD RX 6600 (₹24,999)
- NVIDIA RTX 4060 (₹34,999)
- AMD RX 6700 XT (₹35,999)

**High Range (₹60,000+):**
- NVIDIA RTX 4070 (₹54,999)
- AMD RX 6800 XT (₹64,999)
- NVIDIA RTX 4080 (₹1,14,999)
- NVIDIA RTX 4090 (₹1,59,999)
- AMD RX 7900 XT (₹89,999)

### RAM (6 components)
**Low Range (₹1,500 - ₹4,000):**
- Crucial 8GB DDR4 3200MHz (₹2,499)
- Kingston 8GB DDR4 2666MHz (₹1,999)

**Mid Range (₹4,000 - ₹12,000):**
- Corsair Vengeance 16GB DDR4 3600MHz (₹5,999)
- G.Skill Ripjaws 16GB DDR4 3200MHz (₹5,499)

**High Range (₹12,000+):**
- Corsair Dominator 32GB DDR5 5600MHz (₹18,999)
- G.Skill Trident Z5 32GB DDR5 6000MHz (₹21,999)

### Motherboards (4 components)
**Low Range (₹5,000 - ₹12,000):**
- MSI B660M-A WiFi DDR4 (₹12,999)
- ASUS Prime B550M-A WiFi (₹11,999)

**Mid Range (₹12,000 - ₹25,000):**
- MSI MPG B760I Edge WiFi (₹18,999)

**High Range (₹25,000+):**
- ASUS ROG Strix X670E-E Gaming WiFi (₹39,999)

### Storage (4 components)
**Low Range (₹2,000 - ₹6,000):**
- Crucial BX500 480GB SATA SSD (₹2,999)
- Western Digital Blue 1TB HDD (₹3,499)

**Mid Range (₹6,000 - ₹15,000):**
- Samsung 970 EVO Plus 1TB NVMe SSD (₹8,999)

**High Range (₹15,000+):**
- Samsung 990 PRO 2TB NVMe SSD (₹18,999)

### Power Supplies (3 components)
**Low Range (₹3,000 - ₹8,000):**
- Corsair CV550 550W 80+ Bronze (₹3,999)

**Mid Range (₹8,000 - ₹20,000):**
- Corsair RM750x 750W 80+ Gold (₹12,999)

**High Range (₹20,000+):**
- Corsair HX1000 1000W 80+ Platinum (₹24,999)

### Cases (3 components)
**Low Range (₹3,000 - ₹8,000):**
- Antec NX200M Micro-ATX Case (₹3,999)

**Mid Range (₹8,000 - ₹15,000):**
- NZXT H510 Flow ATX Case (₹8,999)

**High Range (₹15,000+):**
- Lian Li O11 Dynamic EVO (₹18,999)

### Cooling (3 components)
**Low Range (₹1,000 - ₹3,000):**
- Cooler Master Hyper 212 RGB (₹2,999)

**Mid Range (₹3,000 - ₹8,000):**
- NZXT Kraken X53 240mm AIO (₹8,999)

**High Range (₹8,000+):**
- Corsair H150i Elite Capellix 360mm (₹15,999)

### Monitors (3 components)
**Low Range (₹8,000 - ₹20,000):**
- Acer Nitro VG240Y 24-inch 1080p (₹12,999)

**Mid Range (₹20,000 - ₹50,000):**
- LG 27GL850-B 27-inch 1440p (₹34,999)

**High Range (₹50,000+):**
- Samsung Odyssey G7 32-inch 1440p (₹59,999)

### Keyboards (3 components)
**Low Range (₹1,000 - ₹3,000):**
- Logitech K380 Wireless Keyboard (₹2,499)

**Mid Range (₹3,000 - ₹8,000):**
- Razer BlackWidow V3 Mechanical Keyboard (₹8,999)

**High Range (₹8,000+):**
- Corsair K100 RGB Optical-Mechanical (₹18,999)

### Mice (3 components)
**Low Range (₹500 - ₹2,000):**
- Logitech M185 Wireless Mouse (₹899)

**Mid Range (₹2,000 - ₹6,000):**
- Razer DeathAdder V2 Gaming Mouse (₹3,999)

**High Range (₹6,000+):**
- Logitech G Pro X Superlight (₹12,999)

## 🔧 Data Structure

The script will create the following Firestore structure:

```
pc_components/
├── PROCESSOR/
│   └── items/
│       ├── cpu_001 (Intel Core i3-12100F)
│       ├── cpu_002 (AMD Ryzen 3 4100)
│       └── [... 10 more processors]
├── GRAPHIC CARD/
│   └── items/
│       ├── gpu_001 (NVIDIA GTX 1650)
│       ├── gpu_002 (AMD RX 6500 XT)
│       └── [... 10 more graphics cards]
├── RAM/
│   └── items/
│       ├── ram_001 (Crucial 8GB DDR4)
│       └── [... 5 more RAM modules]
└── [... other categories ...]
```

## 🖼️ Image Specifications

- **Size**: 100x100 pixels
- **Source**: Unsplash (non-copyright)
- **Format**: Optimized for web
- **Quality**: High quality with proper compression

## 💰 Price Ranges

### Low Range Components
- **Processors**: ₹5,000 - ₹15,000
- **Graphics Cards**: ₹10,000 - ₹25,000
- **RAM**: ₹1,500 - ₹4,000
- **Motherboards**: ₹5,000 - ₹12,000
- **Storage**: ₹2,000 - ₹6,000
- **Power Supplies**: ₹3,000 - ₹8,000
- **Cases**: ₹3,000 - ₹8,000
- **Cooling**: ₹1,000 - ₹3,000
- **Monitors**: ₹8,000 - ₹20,000
- **Keyboards**: ₹1,000 - ₹3,000
- **Mice**: ₹500 - ₹2,000

### Mid Range Components
- **Processors**: ₹15,000 - ₹35,000
- **Graphics Cards**: ₹25,000 - ₹60,000
- **RAM**: ₹4,000 - ₹12,000
- **Motherboards**: ₹12,000 - ₹25,000
- **Storage**: ₹6,000 - ₹15,000
- **Power Supplies**: ₹8,000 - ₹20,000
- **Cases**: ₹8,000 - ₹15,000
- **Cooling**: ₹3,000 - ₹8,000
- **Monitors**: ₹20,000 - ₹50,000
- **Keyboards**: ₹3,000 - ₹8,000
- **Mice**: ₹2,000 - ₹6,000

### High Range Components
- **Processors**: ₹35,000+
- **Graphics Cards**: ₹60,000+
- **RAM**: ₹12,000+
- **Motherboards**: ₹25,000+
- **Storage**: ₹15,000+
- **Power Supplies**: ₹20,000+
- **Cases**: ₹15,000+
- **Cooling**: ₹8,000+
- **Monitors**: ₹50,000+
- **Keyboards**: ₹8,000+
- **Mice**: ₹6,000+

## 🚀 Running the Script

### Method 1: Simple Run
```bash
python run_comprehensive_population.py
```

### Method 2: Direct Run
```bash
python comprehensive_indian_market_populator.py
```

## 📊 Expected Output

When you run the script successfully, you should see:

```
🚀 PC-Cooker Comprehensive Indian Market Data Population
============================================================
✅ Firebase Admin SDK is installed
📊 Starting comprehensive population...
This will add 60+ components across all categories:
   • PROCESSOR (12 components)
   • GRAPHIC CARD (12 components)
   • RAM (6 components)
   • MOTHERBOARD (4 components)
   • STORAGE (4 components)
   • POWER SUPPLY (3 components)
   • CABINET (3 components)
   • COOLING (3 components)
   • MONITOR (3 components)
   • KEYBOARD (3 components)
   • MOUSE (3 components)

✅ Firebase initialized successfully
✅ Added 12 processors
✅ Added 12 graphics cards
✅ Added 6 RAM modules
✅ Added 4 motherboards
✅ Added 4 storage devices
✅ Added 3 power supplies
✅ Added 3 cases
✅ Added 3 cooling solutions
✅ Added 3 monitors
✅ Added 3 keyboards
✅ Added 3 mice

🎉 Population completed successfully!
✅ All components have been added to your Firebase database
📱 You can now test your PC-Cooker app with the new data
```

## 🔧 Troubleshooting

### Error: "Cannot find module 'firebase-admin'"
**Solution**: Run `pip install firebase-admin`

### Error: "Cannot find module './serviceAccountKey.json'"
**Solution**: Make sure you've downloaded the service account key from Firebase Console and placed it in the correct location

### Error: "Permission denied"
**Solution**: Make sure your Firebase project has Firestore enabled and your service account has proper permissions

### Error: "Firebase app already exists"
**Solution**: This is normal if you run the script multiple times. The script will update existing data.

## 📱 Next Steps

After successful population:

1. **Test your Android app** to ensure it can fetch and display the components
2. **Verify all categories** are working properly
3. **Check component images** are loading correctly
4. **Test AI recommendations** with the new data
5. **Update prices regularly** to maintain current market rates
6. **Add more components** as needed

## 🎯 Benefits

- **Complete Coverage**: All major PC component categories
- **Indian Market Focus**: Current prices and availability
- **Quality Images**: Non-copyright, optimized images
- **Comprehensive Specs**: Detailed specifications for each component
- **Price Ranges**: Low, mid, and high range options
- **Easy Integration**: Works seamlessly with your existing app

## 📞 Support

If you encounter any issues:

1. Check the Firebase Console for any error messages
2. Verify your service account key is correct
3. Ensure your Firebase project has Firestore enabled
4. Check that your Firestore security rules allow write operations

The script is designed to be safe and can be run multiple times - it will update existing data rather than create duplicates.

---

**🎉 Your PC-Cooker app is now ready with comprehensive Indian market data!** 
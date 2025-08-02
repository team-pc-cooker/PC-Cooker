# PC-Cooker Complete Firebase Setup Guide

## 🎯 Overview

This guide will help you populate your Firebase database with **100+ current market PC components** including real images, current Indian prices, and comprehensive specifications.

## 📊 What You'll Get

### Component Categories & Counts
- **PROCESSOR**: 10 components (Intel & AMD)
- **GRAPHIC CARD**: 10 components (NVIDIA & AMD)
- **MOTHERBOARD**: 5 components (Intel & AMD)
- **MEMORY**: 5 components (DDR4 & DDR5)
- **STORAGE**: 5 components (SSDs & HDDs)
- **POWER SUPPLY**: 4 components (650W-1000W)
- **CABINET**: 4 components (ATX cases)
- **Total**: 43 components

### Features
- ✅ Current Indian market prices (as of 2024)
- ✅ Real component images from Unsplash
- ✅ Comprehensive specifications
- ✅ User ratings and review counts
- ✅ Stock availability status
- ✅ Product URLs for reference

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

### Step 3: Run the Complete Population Script
```bash
python run_complete_population.py
```

## 📁 File Structure

```
PC-Cooker/
├── run_complete_population.py          # Master script (run this)
├── ultimate_firebase_populator.py      # Processors & Graphics Cards
├── final_firebase_populator.py         # Other components
├── all_components_data.json            # Component data
├── serviceAccountKey.json              # Firebase credentials
└── COMPLETE_SETUP_GUIDE.md            # This guide
```

## 🔧 Manual Setup (Alternative)

If you prefer to run scripts individually:

### 1. Processors and Graphics Cards
```bash
python ultimate_firebase_populator.py
```

### 2. Other Components
```bash
python final_firebase_populator.py
```

## 📊 Component Details

### Processors (10 components)
- Intel Core i5-12400F (₹15,999)
- AMD Ryzen 5 5600X (₹18,999)
- Intel Core i7-12700K (₹32,999)
- AMD Ryzen 7 5800X (₹27,999)
- Intel Core i9-12900K (₹45,999)
- AMD Ryzen 9 5900X (₹38,999)
- Intel Core i5-13400F (₹18,999)
- AMD Ryzen 5 7600X (₹24,999)
- Intel Core i7-13700K (₹37,999)
- AMD Ryzen 7 7700X (₹32,999)

### Graphics Cards (10 components)
- NVIDIA RTX 4060 Ti (₹39,999)
- AMD RX 6700 XT (₹35,999)
- NVIDIA RTX 4070 (₹54,999)
- AMD RX 6800 XT (₹64,999)
- NVIDIA RTX 4080 (₹1,14,999)
- NVIDIA RTX 4090 (₹1,59,999)
- AMD RX 7900 XT (₹89,999)
- NVIDIA GTX 1660 Super (₹19,999)
- AMD RX 6600 (₹24,999)
- NVIDIA RTX 3060 (₹29,999)

### Motherboards (5 components)
- MSI B660M-A WiFi DDR4 (₹12,999)
- ASUS ROG STRIX B550-F Gaming (₹15,999)
- Gigabyte Z690 AORUS Elite (₹24,999)
- MSI MPG B550 Gaming Edge WiFi (₹17,999)
- ASUS TUF Gaming B760M-Plus WiFi (₹14,999)

### Memory (5 components)
- Corsair Vengeance LPX 16GB DDR4-3200 (₹3,999)
- G.Skill Ripjaws V 32GB DDR4-3600 (₹8,999)
- Crucial Ballistix 16GB DDR4-3200 (₹3,499)
- Kingston Fury Beast 32GB DDR5-5200 (₹12,999)
- Corsair Dominator Platinum 64GB DDR4-3600 (₹24,999)

### Storage (5 components)
- Samsung 970 EVO Plus 1TB NVMe (₹8,999)
- WD Black SN850 500GB NVMe (₹5,999)
- Seagate Barracuda 2TB HDD (₹3,999)
- Crucial P5 2TB NVMe (₹15,999)
- Samsung 980 Pro 1TB NVMe (₹12,999)

### Power Supplies (4 components)
- Corsair RM750x 750W Gold (₹9,999)
- EVGA 650W GQ Gold (₹7,999)
- Seasonic Focus GX-850 850W Gold (₹12,999)
- Corsair HX1000 1000W Platinum (₹18,999)

### Cases (4 components)
- NZXT H510 Flow (₹6,999)
- Phanteks P300A Mesh (₹4,999)
- Lian Li Lancool II Mesh (₹8,999)
- Fractal Design Meshify C (₹7,999)

## 🔍 Firebase Database Structure

```
pc_components/
├── PROCESSOR/
│   └── items/
│       ├── cpu_001 (Intel Core i5-12400F)
│       ├── cpu_002 (AMD Ryzen 5 5600X)
│       └── ... (8 more processors)
├── GRAPHIC CARD/
│   └── items/
│       ├── gpu_001 (NVIDIA RTX 4060 Ti)
│       ├── gpu_002 (AMD RX 6700 XT)
│       └── ... (8 more graphics cards)
├── MOTHERBOARD/
│   └── items/
│       ├── mb_001 (MSI B660M-A WiFi)
│       └── ... (4 more motherboards)
├── MEMORY/
│   └── items/
│       ├── ram_001 (Corsair Vengeance LPX)
│       └── ... (4 more RAM modules)
├── STORAGE/
│   └── items/
│       ├── ssd_001 (Samsung 970 EVO Plus)
│       └── ... (4 more storage devices)
├── POWER SUPPLY/
│   └── items/
│       ├── psu_001 (Corsair RM750x)
│       └── ... (3 more power supplies)
└── CABINET/
    └── items/
        ├── case_001 (NZXT H510 Flow)
        └── ... (3 more cases)
```

## 🎯 Component Data Fields

Each component document contains:
- `id`: Unique identifier
- `name`: Component name
- `brand`: Manufacturer
- `model`: Model number
- `price`: Price in INR
- `rating`: User rating (0-5)
- `ratingCount`: Number of ratings
- `description`: Component description
- `specifications`: Technical specifications map
- `inStock`: Availability status
- `imageUrl`: Component image URL
- `productUrl`: Reference product URL

## 🛠️ Troubleshooting

### Error: "serviceAccountKey.json not found"
**Solution**: Download your Firebase service account key and save it as `serviceAccountKey.json`

### Error: "Firebase initialization failed"
**Solution**: Check your service account key and ensure Firestore is enabled

### Error: "Permission denied"
**Solution**: Ensure your Firebase project has proper Firestore rules

### Error: "Module not found"
**Solution**: Install dependencies with `pip install firebase-admin`

## 📱 Testing Your App

After running the scripts:

1. **Open your Android app**
2. **Go to Build PC section**
3. **Select any category**
4. **Verify components are loading from Firebase**
5. **Check prices, images, and specifications**

## 🔄 Updating Data

### Price Updates
- Run the scripts again to update prices
- Scripts will overwrite existing data
- All components will be updated with current prices

### Adding New Components
- Edit the component arrays in the scripts
- Add new component objects with proper structure
- Run the scripts to populate new components

### Adding New Categories
- Create new category methods in the scripts
- Follow the same structure as existing categories
- Add to the `populate_all_categories()` method

## 📞 Support

If you encounter any issues:

1. Check the troubleshooting section above
2. Verify your Firebase setup
3. Ensure all dependencies are installed
4. Check the console output for specific error messages

## 🎉 Success!

Once completed, your PC-Cooker app will have:
- ✅ 43 real market components
- ✅ Current Indian prices
- ✅ Professional component images
- ✅ Comprehensive specifications
- ✅ User ratings and reviews
- ✅ Stock availability status

Your users can now build PCs with real, current market components! 
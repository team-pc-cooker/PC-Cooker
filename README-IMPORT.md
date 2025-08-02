# PC Cooker - Firestore Data Import Guide

This guide will help you import all PC component data into your Firestore database using the provided Node.js script.

## Prerequisites

1. **Node.js** installed on your computer
2. **Firebase Project** with Firestore enabled
3. **Firebase Service Account Key**

## Step-by-Step Instructions

### 1. Get Your Firebase Service Account Key

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to **Project Settings** (gear icon)
4. Click on **Service Accounts** tab
5. Click **Generate New Private Key**
6. Download the JSON file
7. Rename it to `serviceAccountKey.json`
8. Place it in the same folder as the script (`PC-Cooker/`)

### 2. Install Dependencies

Open terminal/command prompt in the `PC-Cooker/` folder and run:

```bash
npm install
```

### 3. Run the Import Script

```bash
npm run import
```

Or directly:

```bash
node firestore-import-script.js
```

### 4. Verify the Import

1. Go to Firebase Console → Firestore Database
2. You should see a collection called `pc_components`
3. Inside it, you'll find documents for each category:
   - PROCESSOR
   - GRAPHIC CARD
   - RAM
   - MOTHERBOARD
   - STORAGE
   - POWER SUPPLY
   - CABINET

4. Each category document will have a subcollection called `items` containing all the components.

## Expected Output

When you run the script successfully, you should see:

```
Starting component import...
Importing 4 components for category: PROCESSOR
✓ Imported: Intel Core i5-12400F
✓ Imported: AMD Ryzen 5 5600X
✓ Imported: Intel Core i7-12700K
✓ Imported: AMD Ryzen 7 5800X
✅ Completed importing 4 components for PROCESSOR

Importing 3 components for category: GRAPHIC CARD
✓ Imported: NVIDIA RTX 4060 Ti
✓ Imported: AMD RX 6700 XT
✓ Imported: NVIDIA RTX 4070
✅ Completed importing 3 components for GRAPHIC CARD

[... continues for all categories ...]

🎉 All components imported successfully!

Summary:
- PROCESSOR: 4 components
- GRAPHIC CARD: 3 components
- RAM: 3 components
- MOTHERBOARD: 3 components
- STORAGE: 3 components
- POWER SUPPLY: 3 components
- CABINET: 3 components
```

## Data Structure

The script will create the following Firestore structure:

```
pc_components/
├── PROCESSOR/
│   └── items/
│       ├── cpu_001 (Intel Core i5-12400F)
│       ├── cpu_002 (AMD Ryzen 5 5600X)
│       ├── cpu_003 (Intel Core i7-12700K)
│       └── cpu_004 (AMD Ryzen 7 5800X)
├── GRAPHIC CARD/
│   └── items/
│       ├── gpu_001 (NVIDIA RTX 4060 Ti)
│       ├── gpu_002 (AMD RX 6700 XT)
│       └── gpu_003 (NVIDIA RTX 4070)
└── [... other categories ...]
```

## Component Data Fields

Each component document contains:

- `id`: Unique identifier
- `name`: Component name
- `category`: Component category
- `price`: Price in INR
- `rating`: User rating (0-5)
- `ratingCount`: Number of ratings
- `imageUrl`: Image URL (empty for now)
- `productUrl`: Product URL (empty for now)
- `description`: Component description
- `specifications`: Map of technical specifications
- `inStock`: Boolean indicating availability
- `brand`: Manufacturer brand
- `model`: Model number

## Troubleshooting

### Error: "Cannot find module 'firebase-admin'"
Solution: Run `npm install` to install dependencies

### Error: "Cannot find module './serviceAccountKey.json'"
Solution: Make sure you've downloaded the service account key from Firebase Console and placed it in the correct location

### Error: "Permission denied"
Solution: Make sure your Firebase project has Firestore enabled and your service account has proper permissions

### Error: "Firebase app already exists"
Solution: This is normal if you run the script multiple times. The script will update existing data.

## Current Market Data

The imported data includes current market prices (December 2024) for Indian market:

- **Processors**: Intel and AMD CPUs ranging from ₹15,999 to ₹28,999
- **Graphics Cards**: NVIDIA and AMD GPUs ranging from ₹34,999 to ₹54,999
- **RAM**: DDR4 modules ranging from ₹2,999 to ₹8,999
- **Motherboards**: Intel and AMD compatible boards ranging from ₹11,999 to ₹15,999
- **Storage**: SSDs and HDDs ranging from ₹2,999 to ₹6,999
- **Power Supplies**: 500W-750W units ranging from ₹3,999 to ₹6,999
- **Cases**: Mid-tower cases ranging from ₹5,999 to ₹8,999

## Next Steps

After successful import:

1. Test your Android app to ensure it can fetch and display the components
2. Add product images by updating the `imageUrl` field
3. Add product links by updating the `productUrl` field
4. Update prices regularly to maintain current market rates
5. Add more components as needed

## Support

If you encounter any issues:

1. Check the Firebase Console for any error messages
2. Verify your service account key is correct
3. Ensure your Firebase project has Firestore enabled
4. Check that your Firestore security rules allow write operations

The script is designed to be safe and can be run multiple times - it will update existing data rather than create duplicates. 
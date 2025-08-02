# Firebase Setup Guide for PC-Cooker App

## 🚀 Quick Setup

### 1. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project"
3. Name it "PC-Cooker" or your preferred name
4. Enable Google Analytics (optional)
5. Click "Create project"

### 2. Enable Firestore Database
1. In Firebase Console, go to "Firestore Database"
2. Click "Create database"
3. Choose "Start in test mode" (for development)
4. Select a location (choose closest to India)
5. Click "Done"

### 3. Get Service Account Key
1. Go to Project Settings (gear icon)
2. Click "Service accounts" tab
3. Click "Generate new private key"
4. Download the JSON file
5. Save it as `serviceAccountKey.json` in your project folder

### 4. Install Python Dependencies
```bash
pip install -r requirements.txt
```

### 5. Configure the Script
Edit `firebase_data_populator.py`:
```python
# Replace this line:
# cred = credentials.Certificate('path/to/your/serviceAccountKey.json')

# With your actual path:
cred = credentials.Certificate('serviceAccountKey.json')
```

### 6. Run the Script
```bash
python firebase_data_populator.py
```

## 📊 Data Structure

The script creates this Firebase structure:
```
pc_components/
├── PROCESSOR/
│   └── items/
│       ├── Intel Core i5-12400F
│       ├── AMD Ryzen 5 5600X
│       └── ...
├── GRAPHIC CARD/
│   └── items/
│       ├── NVIDIA RTX 3060
│       ├── AMD RX 6600 XT
│       └── ...
├── MOTHERBOARD/
│   └── items/
│       ├── MSI B660M-A WiFi
│       ├── ASUS ROG STRIX B550-F
│       └── ...
├── RAM/
│   └── items/
│       ├── Corsair Vengeance 16GB
│       ├── G.Skill Ripjaws V 32GB
│       └── ...
├── STORAGE/
│   └── items/
│       ├── Samsung 970 EVO Plus 500GB
│       ├── WD Blue 1TB SATA SSD
│       └── ...
├── POWER SUPPLY/
│   └── items/
│       ├── Corsair CX650M
│       ├── EVGA 600W 80+ White
│       └── ...
└── CABINET/
    └── items/
        ├── NZXT H510
        ├── Corsair 4000D Airflow
        └── ...
```

## 🔧 Component Data Fields

Each component includes:
- **name**: Product name
- **brand**: Manufacturer
- **model**: Model number
- **price**: Current Indian market price (₹)
- **rating**: User rating (1-5)
- **ratingCount**: Number of ratings
- **description**: Product description
- **specifications**: Technical specs (JSON object)
- **inStock**: Availability status
- **imageUrl**: Product image URL
- **productUrl**: Purchase link

## 💰 Current Indian Market Prices

### Processors (₹9,999 - ₹32,999)
- Intel Core i3-12100F: ₹9,999
- Intel Core i5-12400F: ₹15,999
- AMD Ryzen 5 5600X: ₹18,999
- AMD Ryzen 7 5800X: ₹27,999
- Intel Core i7-12700K: ₹32,999

### Graphics Cards (₹18,999 - ₹45,999)
- NVIDIA GTX 1660 Super: ₹18,999
- AMD RX 6600 XT: ₹25,999
- NVIDIA RTX 3060: ₹28,999
- AMD RX 6700 XT: ₹39,999
- NVIDIA RTX 3070: ₹45,999

### Motherboards (₹8,999 - ₹15,999)
- Gigabyte B660M DS3H: ₹8,999
- MSI B660M-A WiFi: ₹12,999
- ASUS ROG STRIX B550-F: ₹15,999

### RAM (₹2,999 - ₹8,999)
- Crucial Ballistix 8GB: ₹2,999
- Corsair Vengeance 16GB: ₹4,999
- G.Skill Ripjaws V 32GB: ₹8,999

### Storage (₹3,999 - ₹6,999)
- Seagate Barracuda 2TB HDD: ₹3,999
- Samsung 970 EVO Plus 500GB: ₹4,999
- WD Blue 1TB SATA SSD: ₹6,999

### Power Supplies (₹3,999 - ₹8,999)
- EVGA 600W 80+ White: ₹3,999
- Corsair CX650M: ₹4,999
- Seasonic Focus GX-750: ₹8,999

### Cabinets (₹3,999 - ₹7,999)
- Cooler Master Q300L: ₹3,999
- NZXT H510: ₹5,999
- Corsair 4000D Airflow: ₹7,999

## 🔒 Security Rules

Add these Firestore security rules:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /pc_components/{category}/items/{item} {
      allow read: if true;  // Anyone can read
      allow write: if false; // Only admin can write
    }
  }
}
```

## 🧪 Testing

After running the script:
1. Open your PC-Cooker app
2. Go to AI Assistant
3. Enter: "I need a gaming PC for ₹60000"
4. Click "Let's Build"
5. Verify components are loaded from Firebase

## 🔄 Updating Prices

To update prices:
1. Edit the prices in `firebase_data_populator.py`
2. Run the script again
3. It will update existing components

## 🆘 Troubleshooting

### Common Issues:
1. **Authentication Error**: Check service account key path
2. **Permission Denied**: Verify Firestore rules
3. **Network Error**: Check internet connection
4. **Import Error**: Install requirements: `pip install -r requirements.txt`

### Support:
- Check Firebase Console for errors
- Verify service account has proper permissions
- Ensure Firestore is enabled in your project

## 📱 App Integration

Your Android app will now:
- ✅ Fetch real components from Firebase
- ✅ Show current Indian market prices
- ✅ Display proper descriptions and ratings
- ✅ Work without crashes
- ✅ Provide accurate AI recommendations

The AI will now have real data to work with and provide much better recommendations! 
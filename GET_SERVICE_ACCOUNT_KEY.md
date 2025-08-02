# 🔑 How to Get Your Firebase Service Account Key

## Step-by-Step Instructions

### 1. Go to Firebase Console
- Open your browser and go to: https://console.firebase.google.com/
- Sign in with your Google account

### 2. Select Your Project
- Click on your PC Cooker project from the list
- If you don't see your project, make sure you're signed in with the correct Google account

### 3. Access Project Settings
- Click the **gear icon** (⚙️) in the top-left corner
- Select **"Project settings"**

### 4. Go to Service Accounts Tab
- In the Project settings page, click on the **"Service accounts"** tab
- You should see a section called "Firebase Admin SDK"

### 5. Generate Private Key
- Click the **"Generate new private key"** button
- A popup will appear asking for confirmation
- Click **"Generate key"**

### 6. Download the File
- Your browser will automatically download a JSON file
- The file will have a name like: `pccooker-xxxxx-firebase-adminsdk-xxxxx-xxxxxxxxxx.json`

### 7. Rename and Move the File
- Rename the downloaded file to: `serviceAccountKey.json`
- Move it to the `PC-Cooker` folder (same folder as `firestore-import-script.js`)

### 8. Verify File Location
Your folder structure should look like this:
```
PC-Cooker/
├── firestore-import-script.js
├── serviceAccountKey.json  ← This file should be here
├── package.json
└── README-IMPORT.md
```

## 🔒 Security Note
- Keep this file secure and don't share it publicly
- This file contains sensitive credentials for your Firebase project
- Add `serviceAccountKey.json` to your `.gitignore` file if you're using Git

## ✅ After Getting the File
Once you have the `serviceAccountKey.json` file in the correct location, you can run:

```bash
npm run import
```

## 🆘 If You Can't Find Your Project
1. Make sure you're signed in with the correct Google account
2. Check if you have multiple Firebase projects
3. Look for a project with a name related to "PC Cooker" or "pccooker"
4. If you can't find your project, you may need to create a new Firebase project first

## 🆘 If You Get Permission Errors
1. Make sure your Firebase project has Firestore enabled
2. Check that your service account has proper permissions
3. Verify that your Firestore security rules allow write operations

## 📞 Need Help?
If you're still having trouble:
1. Check the Firebase Console for any error messages
2. Make sure your Firebase project is properly set up
3. Verify that Firestore Database is enabled in your project 
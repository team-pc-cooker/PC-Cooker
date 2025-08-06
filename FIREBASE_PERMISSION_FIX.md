# 🔧 Firebase Permission Fix Guide

## 🚨 Current Issue
The app is experiencing "permission denied" errors when trying to access the `components` collection in Firebase Firestore. This is happening because the Firebase security rules are not properly configured.

## 🔍 Root Cause
The app is trying to read from the `components` collection, but the Firebase security rules only allow access to `pc_components` collection.

## ✅ Solution Steps

### Step 1: Update Firebase Security Rules

1. **Go to Firebase Console**
   - Visit: https://console.firebase.google.com/
   - Select your PC-Cooker project

2. **Navigate to Firestore Database**
   - Click on "Firestore Database" in the left sidebar
   - Click on the "Rules" tab

3. **Replace the current rules with these:**

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // ✅ Public read access for PC components (like processors, RAM, etc.)
    match /components/{componentId} {
      allow read: if true;  // Anyone can read components
      // Only allow write operations from authenticated users (for admin panel)
      allow write: if request.auth != null;
    }

    // ✅ Legacy support for pc_components collection
    match /pc_components/{category} {
      allow read: if true;
      // Only allow write operations from authenticated users (for admin panel)
      allow write: if request.auth != null;

      match /items/{itemId} {
        allow read: if true;
        // Only allow write operations from authenticated users (for admin panel)
        allow write: if request.auth != null;
      }
    }

    // ✅ User builds collection
    match /user_builds/{buildId} {
      allow read, write: if request.auth != null && request.auth.uid == resource.data.userId;
      allow create: if request.auth != null && request.auth.uid == request.resource.data.userId;
    }

    // ✅ Secure access to user-specific data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;

      match /addresses/{addressId} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }

      match /orders/{orderId} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }

      match /cart/{cartItemId} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }

    // ✅ Orders collection (if you store orders separately)
    match /orders/{orderId} {
      allow read, write: if request.auth != null && request.auth.uid == resource.data.userId;
      allow create: if request.auth != null && request.auth.uid == request.resource.data.userId;
    }

    // ✅ Cart collection (if you store cart separately)
    match /carts/{cartId} {
      allow read, write: if request.auth != null && request.auth.uid == resource.data.userId;
      allow create: if request.auth != null && request.auth.uid == request.resource.data.userId;
    }

    // ✅ Admin panel access (if you have admin users)
    match /admin/{document=**} {
      allow read, write: if request.auth != null && 
        (request.auth.token.admin == true || request.auth.uid in ['your-admin-uid-here']);
    }

    // ✅ Deny all other access by default
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

4. **Publish the rules**
   - Click "Publish" to save the new rules

### Step 2: Verify Firestore Database is Enabled

1. **Check if Firestore is enabled**
   - In Firebase Console, go to "Firestore Database"
   - If you see "Get started" button, click it
   - Choose "Start in test mode" for now (we'll secure it later)

### Step 3: Test the Connection

1. **Build and run the app**
   ```bash
   ./gradlew assembleDebug
   ```

2. **Test the features**
   - Go to "Search Components" - should work without permission errors
   - Go to "Build PC" - set a budget and click "Auto Build PC" - should work
   - Go to "AI Assistant" - should provide relevant recommendations

### Step 4: Add Sample Data (if needed)

If the database is empty, the app will automatically add sample components. If you want to add more data manually:

1. **Use the Firebase Console**
   - Go to Firestore Database
   - Click "Start collection" (if no collections exist)
   - Collection ID: `components`
   - Add documents with the following structure:

```json
{
  "name": "AMD Ryzen 5 5600G",
  "brand": "AMD",
  "category": "PROCESSOR",
  "description": "6-core processor with integrated graphics",
  "price": 15000,
  "rating": 4.5,
  "specifications": {
    "Socket": "AM4"
  }
}
```

## 🔍 Troubleshooting

### If you still get permission errors:

1. **Check the error message**
   - Look at the exact error in the app's toast messages
   - Check Android Studio's Logcat for detailed error logs

2. **Verify Firebase project**
   - Make sure you're using the correct Firebase project
   - Check that `google-services.json` matches your project

3. **Check network connectivity**
   - Ensure the device has internet access
   - Try on a different network if possible

4. **Clear app data**
   - Go to Android Settings > Apps > PC Cooker > Storage > Clear Data
   - This will clear any cached Firebase data

### Common Error Messages:

- **"Permission denied"**: Security rules are blocking access
- **"Missing or insufficient permissions"**: Rules need to be updated
- **"Collection not found"**: Database is empty or collection doesn't exist
- **"Network error"**: Internet connectivity issue

## 🎯 Expected Results

After following these steps:

✅ **Search Components** should work without permission errors
✅ **Auto Build PC** should automatically select components within budget
✅ **AI Assistant** should provide relevant recommendations
✅ No more "permission denied" or "missing" error messages

## 📞 Need Help?

If you're still having issues:

1. Check the Firebase Console for any error messages
2. Verify that your Firebase project has Firestore enabled
3. Make sure your service account has proper permissions
4. Check that the security rules are published and active

## 🔒 Security Note

The current rules allow public read access to components. For production, you might want to restrict this based on your requirements. The write operations are already restricted to authenticated users only. 
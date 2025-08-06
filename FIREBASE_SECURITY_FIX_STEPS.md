# 🔧 Firebase Security Rules - STEP BY STEP FIX

## 🚨 URGENT: You need to update your Firebase Console

The app is working correctly, but your Firebase Console still has the old security rules that are blocking access to the `components` collection.

## 📋 STEP-BY-STEP SOLUTION

### Step 1: Go to Firebase Console
1. Open your web browser
2. Go to: https://console.firebase.google.com/
3. **Sign in with your Google account**
4. **Select your PC-Cooker project**

### Step 2: Navigate to Firestore Database
1. In the left sidebar, click **"Firestore Database"**
2. Click on the **"Rules"** tab at the top
3. You should see the current security rules

### Step 3: Replace the Rules
1. **Delete all existing rules** in the editor
2. **Copy and paste** these exact rules:

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

### Step 4: Publish the Rules
1. Click the **"Publish"** button
2. Wait for the confirmation message: "Rules published successfully"
3. **This is crucial - the rules won't work until you publish them!**

### Step 5: Verify Firestore is Enabled
1. In the same Firestore Database section
2. If you see "Get started" button, click it
3. Choose **"Start in test mode"** for now
4. Click **"Enable"**

### Step 6: Test the App
1. **Build and run your app** again
2. Test these features:
   - **Search Components** - Should work without permission errors
   - **Auto Build PC** - Should work without permission errors
   - **AI Assistant** - Should provide recommendations

## 🔍 TROUBLESHOOTING

### If you still get permission errors:

1. **Check if rules are published:**
   - Go back to Firebase Console → Firestore Database → Rules
   - Make sure you see the new rules (not the old ones)
   - Check the timestamp at the bottom - should be recent

2. **Check if Firestore is enabled:**
   - In Firebase Console → Firestore Database
   - You should see your database, not a "Get started" button

3. **Check your Firebase project:**
   - Make sure you're in the correct Firebase project
   - The project name should match your `google-services.json`

4. **Clear app data:**
   - Go to Android Settings → Apps → PC Cooker → Storage → Clear Data
   - This clears any cached Firebase data

### Common Error Messages:
- **"Permission denied"** = Rules not updated in Firebase Console
- **"Missing or insufficient permissions"** = Rules not published
- **"Collection not found"** = Firestore not enabled
- **"Network error"** = Internet connectivity issue

## ⚡ QUICK TEST

After updating the rules, test this in your app:

1. **Search Components** - Should show components or add sample data
2. **Auto Build PC** - Set budget to 50k, should work without errors
3. **AI Assistant** - Ask "build a PC for 50k", should give recommendations

## 📞 STILL HAVING ISSUES?

If you're still getting permission errors after following these steps:

1. **Double-check the Firebase project** - Make sure you're in the right project
2. **Verify the rules are published** - Check the timestamp in Firebase Console
3. **Check internet connection** - Make sure your device has internet access
4. **Try on a different device/emulator** - Sometimes cache issues occur

## 🎯 EXPECTED RESULT

After following these steps:
✅ **No more "permission denied" errors**
✅ **Search Components works**
✅ **Auto Build PC works**
✅ **AI Assistant provides recommendations**
✅ **Clean, hassle-free experience**

**The key is updating the Firebase Console rules - the app code is already fixed!** 
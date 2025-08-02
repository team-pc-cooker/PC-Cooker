const admin = require('firebase-admin');

console.log('🔍 Testing Firebase connection...');

try {
  // Try to load the service account key
  const serviceAccount = require('./serviceAccountKey.json');
  console.log('✅ Service account key loaded successfully');
  
  // Initialize Firebase Admin SDK
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
  });
  
  console.log('✅ Firebase Admin SDK initialized');
  
  // Test Firestore connection
  const db = admin.firestore();
  
  // Try to read from Firestore
  db.collection('test').doc('test').get()
    .then(() => {
      console.log('✅ Firestore connection successful');
      console.log('🎉 All tests passed! You can now run: npm run import');
    })
    .catch((error) => {
      console.log('❌ Firestore connection failed:', error.message);
      console.log('💡 Make sure:');
      console.log('   - Your Firebase project has Firestore enabled');
      console.log('   - Your service account has proper permissions');
      console.log('   - Your Firestore security rules allow read operations');
    });
    
} catch (error) {
  console.log('❌ Error loading service account key:', error.message);
  console.log('💡 Make sure:');
  console.log('   - You have downloaded the service account key from Firebase Console');
  console.log('   - The file is named "serviceAccountKey.json"');
  console.log('   - The file is in the same folder as this script');
  console.log('   - Follow the instructions in GET_SERVICE_ACCOUNT_KEY.md');
} 
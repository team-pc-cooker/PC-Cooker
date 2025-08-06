import firebase_admin
from firebase_admin import credentials, firestore

def initialize_firebase():
    cred = credentials.Certificate("serviceAccountKey.json")
    firebase_admin.initialize_app(cred)
    return firestore.client()

def cleanup_old_processor_data(db):
    """Remove any old processor data from the incorrect Firebase structure"""
    try:
        # Check and clean the old direct PROCESSOR collection if it exists
        old_col = db.collection("PROCESSOR")
        docs = old_col.stream()
        
        deleted_count = 0
        for doc in docs:
            doc.reference.delete()
            deleted_count += 1
        
        if deleted_count > 0:
            print(f"🧹 Cleaned up {deleted_count} old processor items from incorrect structure.")
        else:
            print("✅ No old data found in incorrect structure.")
            
        return deleted_count
    except Exception as e:
        print(f"⚠️ Error during cleanup: {e}")
        return 0

if __name__ == "__main__":
    print("🧹 Starting cleanup of old processor data...")
    db = initialize_firebase()
    
    cleanup_old_processor_data(db)
    
    print("✨ Cleanup completed!")

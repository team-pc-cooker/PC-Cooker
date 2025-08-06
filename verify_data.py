import firebase_admin
from firebase_admin import credentials, firestore

def initialize_firebase():
    cred = credentials.Certificate("serviceAccountKey.json")
    firebase_admin.initialize_app(cred)
    return firestore.client()

def verify_processor_data(db):
    """Verify that processor data is correctly stored in Firebase"""
    try:
        # Check the correct Firebase structure that the app expects
        col = db.collection("pc_components").document("PROCESSOR").collection("items")
        docs = col.stream()
        
        processor_count = 0
        print("📋 Processors found in correct Firebase structure:")
        print("-" * 80)
        
        for doc in docs:
            processor_count += 1
            data = doc.to_dict()
            name = data.get('name', 'Unknown')
            price = data.get('price', 0)
            brand = data.get('brand', 'Unknown')
            rating = data.get('rating', 0)
            
            print(f"{processor_count:2d}. {name} - ₹{price:,} ({brand}) - {rating}⭐")
        
        print("-" * 80)
        print(f"✅ Total processors found: {processor_count}")
        
        if processor_count == 0:
            print("❌ No processors found! Please run upload_components.py")
        else:
            print("🎉 Processor data is correctly stored and ready for your app!")
            
        return processor_count
        
    except Exception as e:
        print(f"❌ Error verifying data: {e}")
        return 0

def check_old_structure(db):
    """Check if any data remains in the old incorrect structure"""
    try:
        old_col = db.collection("PROCESSOR")
        docs = list(old_col.stream())
        
        if len(docs) > 0:
            print(f"⚠️ Warning: {len(docs)} items still found in old structure!")
            print("   Run cleanup_old_data.py to remove them.")
        else:
            print("✅ No data found in old incorrect structure.")
            
    except Exception as e:
        print(f"⚠️ Error checking old structure: {e}")

if __name__ == "__main__":
    print("🔍 Verifying processor data in Firebase...")
    print()
    db = initialize_firebase()
    
    # Verify correct structure
    verify_processor_data(db)
    print()
    
    # Check old structure
    check_old_structure(db)
    print()
    print("✨ Verification completed!")

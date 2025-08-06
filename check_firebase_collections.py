import firebase_admin
from firebase_admin import credentials, firestore

class FirebaseCollectionChecker:
    def __init__(self):
        """Initialize Firebase connection"""
        try:
            # Initialize Firebase Admin SDK
            cred = credentials.Certificate('serviceAccountKey.json')
            firebase_admin.initialize_app(cred)
            self.db = firestore.client()
            print("✅ Firebase connection established successfully!")
        except Exception as e:
            print(f"❌ Error connecting to Firebase: {e}")
            return

    def list_all_collections(self):
        """List all collections in the database"""
        try:
            print("\n🔍 Checking all collections in Firebase...")
            
            # Get all collections
            collections = self.db.collections()
            
            print(f"\n📂 FOUND COLLECTIONS:")
            print("=" * 50)
            
            collection_count = 0
            for collection in collections:
                collection_count += 1
                collection_name = collection.id
                print(f"\n🔹 Collection: {collection_name}")
                
                # Count documents in this collection
                docs = collection.stream()
                doc_count = 0
                sample_docs = []
                
                for doc in docs:
                    doc_count += 1
                    if doc_count <= 3:  # Show first 3 documents as sample
                        data = doc.to_dict()
                        sample_docs.append({
                            'id': doc.id,
                            'data': data
                        })
                
                print(f"   📄 Documents: {doc_count}")
                
                if sample_docs:
                    print(f"   📋 Sample documents:")
                    for i, sample in enumerate(sample_docs, 1):
                        print(f"      {i}. ID: {sample['id']}")
                        for key, value in sample['data'].items():
                            if key == 'imageUrl':
                                print(f"         {key}: {str(value)[:50]}...")
                            else:
                                print(f"         {key}: {value}")
                        print()
            
            print(f"\n📊 SUMMARY:")
            print(f"   Total Collections: {collection_count}")
            
            if collection_count == 0:
                print("   ⚠️  No collections found in the database!")
                print("   💡 This might mean:")
                print("      - The database is empty")
                print("      - The service account doesn't have access")
                print("      - The project ID is incorrect")
            
            return collection_count
            
        except Exception as e:
            print(f"❌ Error listing collections: {e}")
            return 0

    def check_specific_collection(self, collection_name):
        """Check a specific collection in detail"""
        try:
            print(f"\n🔍 Checking collection: {collection_name}")
            print("=" * 50)
            
            collection_ref = self.db.collection(collection_name)
            docs = collection_ref.stream()
            
            doc_count = 0
            all_docs = []
            
            for doc in docs:
                doc_count += 1
                data = doc.to_dict()
                all_docs.append({
                    'id': doc.id,
                    'data': data
                })
            
            print(f"📄 Total documents: {doc_count}")
            
            if doc_count > 0:
                print(f"\n📋 ALL DOCUMENTS IN '{collection_name}':")
                print("-" * 50)
                
                for i, doc_info in enumerate(all_docs, 1):
                    print(f"\n{i}. ID: {doc_info['id']}")
                    for key, value in doc_info['data'].items():
                        if key == 'imageUrl':
                            print(f"   {key}: {str(value)[:80]}...")
                        else:
                            print(f"   {key}: {value}")
            
            return all_docs
            
        except Exception as e:
            print(f"❌ Error checking collection {collection_name}: {e}")
            return []

def main():
    print("🚀 Firebase Collection Checker")
    print("=" * 50)
    
    checker = FirebaseCollectionChecker()
    
    if not checker.db:
        print("❌ Failed to initialize Firebase connection")
        return
    
    # List all collections
    collection_count = checker.list_all_collections()
    
    if collection_count > 0:
        # Check common collection names
        common_collections = ['components', 'products', 'items', 'parts']
        
        for collection_name in common_collections:
            docs = checker.check_specific_collection(collection_name)
            if docs:
                print(f"\n✅ Found data in '{collection_name}' collection!")
                break

if __name__ == "__main__":
    main() 
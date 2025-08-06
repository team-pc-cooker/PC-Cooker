import firebase_admin
from firebase_admin import credentials, firestore

def main():
    print("🚀 Extracting all component names from Firebase...")
    
    # Initialize Firebase
    cred = credentials.Certificate('serviceAccountKey.json')
    firebase_admin.initialize_app(cred)
    db = firestore.client()
    
    # Component categories found in your database
    categories = ['CABINET', 'GRAPHIC CARD', 'MOTHERBOARD', 'POWER SUPPLY', 'RAM', 'STORAGE']
    
    all_components = []
    
    for category in categories:
        print(f"\n📂 {category}:")
        try:
            docs = db.collection(category).stream()
            for doc in docs:
                data = doc.to_dict()
                name = data.get('name', 'Unknown')
                price = data.get('price', 0)
                brand = data.get('brand', 'Unknown')
                print(f"  - {name} ({brand}) - ₹{price:,}")
                all_components.append({
                    'name': name,
                    'category': category,
                    'price': price,
                    'brand': brand
                })
        except Exception as e:
            print(f"  Error: {e}")
    
    print(f"\n📊 TOTAL COMPONENTS: {len(all_components)}")
    print(f"\n📝 COMPLETE LIST:")
    for i, comp in enumerate(all_components, 1):
        print(f"{i:3d}. {comp['name']} ({comp['category']})")

if __name__ == "__main__":
    main() 
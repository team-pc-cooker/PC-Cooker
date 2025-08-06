import firebase_admin
from firebase_admin import credentials, firestore

def main():
    print("🚀 Extracting ALL component names from ALL Firebase categories...")
    
    # Initialize Firebase
    cred = credentials.Certificate('serviceAccountKey.json')
    firebase_admin.initialize_app(cred)
    db = firestore.client()
    
    # Get ALL collections from Firebase
    print("\n🔍 Checking ALL collections in Firebase...")
    collections = db.collections()
    
    all_components = []
    component_categories = []
    
    for collection in collections:
        collection_name = collection.id
        print(f"\n📂 Checking collection: {collection_name}")
        
        # Skip non-component collections
        if collection_name in ['users', 'pc_components']:
            print(f"  ⏭️  Skipping non-component collection: {collection_name}")
            continue
            
        try:
            docs = collection.stream()
            category_components = []
            
            for doc in docs:
                data = doc.to_dict()
                name = data.get('name', 'Unknown')
                price = data.get('price', 0)
                brand = data.get('brand', 'Unknown')
                
                component_info = {
                    'name': name,
                    'category': collection_name,
                    'price': price,
                    'brand': brand,
                    'id': doc.id,
                    'imageUrl': data.get('imageUrl', ''),
                    'description': data.get('description', ''),
                    'rating': data.get('rating', 0),
                    'ratingCount': data.get('ratingCount', 0),
                    'useCases': data.get('useCases', [])
                }
                
                category_components.append(component_info)
                all_components.append(component_info)
                
                print(f"  - {name} ({brand}) - ₹{price:,}")
            
            if category_components:
                component_categories.append(collection_name)
                print(f"  ✅ Found {len(category_components)} components in {collection_name}")
            else:
                print(f"  ⚠️  No components found in {collection_name}")
                
        except Exception as e:
            print(f"  ❌ Error reading {collection_name}: {e}")
    
    # Display comprehensive results
    print(f"\n" + "="*80)
    print("📊 COMPLETE COMPONENT ANALYSIS")
    print("="*80)
    
    print(f"\n🎯 TOTAL STATISTICS:")
    print(f"   📦 Total Components: {len(all_components)}")
    print(f"   📂 Total Categories: {len(component_categories)}")
    
    print(f"\n📋 COMPONENT CATEGORIES FOUND:")
    for i, category in enumerate(component_categories, 1):
        category_count = len([c for c in all_components if c['category'] == category])
        print(f"   {i:2d}. {category}: {category_count} components")
    
    print(f"\n📝 DETAILED BREAKDOWN BY CATEGORY:")
    print("-" * 60)
    
    for category in component_categories:
        category_components = [c for c in all_components if c['category'] == category]
        print(f"\n🔹 {category} ({len(category_components)} components):")
        print("-" * 50)
        
        for i, component in enumerate(category_components, 1):
            print(f"  {i:2d}. {component['name']}")
            print(f"      Brand: {component['brand']}")
            print(f"      Price: ₹{component['price']:,}")
            print(f"      Rating: {component['rating']} ({component['ratingCount']} reviews)")
            if component['useCases']:
                print(f"      Use Cases: {', '.join(component['useCases'])}")
            print()
    
    print(f"\n" + "="*80)
    print("📝 COMPLETE COMPONENT NAME LIST")
    print("="*80)
    
    for i, component in enumerate(all_components, 1):
        print(f"{i:3d}. {component['name']} ({component['category']})")
    
    # Save to file
    try:
        with open('complete_component_list.txt', 'w', encoding='utf-8') as f:
            f.write("COMPLETE COMPONENT LIST FROM FIREBASE\n")
            f.write("=" * 50 + "\n\n")
            f.write(f"Total Components: {len(all_components)}\n")
            f.write(f"Total Categories: {len(component_categories)}\n\n")
            
            for category in component_categories:
                category_components = [c for c in all_components if c['category'] == category]
                f.write(f"\n{category} ({len(category_components)} components):\n")
                f.write("-" * 40 + "\n")
                for component in category_components:
                    f.write(f"- {component['name']} ({component['brand']}) - ₹{component['price']:,}\n")
                f.write("\n")
        
        print(f"\n💾 Complete component list saved to: complete_component_list.txt")
        
    except Exception as e:
        print(f"❌ Error saving file: {e}")

if __name__ == "__main__":
    main() 
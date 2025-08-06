import firebase_admin
from firebase_admin import credentials, firestore
import json

class ComponentNameExtractor:
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

    def get_all_component_names(self):
        """Extract all component names from Firestore"""
        try:
            print("\n🔍 Fetching all components from Firebase...")
            
            # Get all documents from the components collection
            components_ref = self.db.collection('components')
            docs = components_ref.stream()
            
            # Organize by category
            components_by_category = {}
            all_component_names = []
            
            for doc in docs:
                data = doc.to_dict()
                category = data.get('category', 'UNKNOWN')
                name = data.get('name', 'Unknown Component')
                price = data.get('price', 0)
                
                # Add to category list
                if category not in components_by_category:
                    components_by_category[category] = []
                
                components_by_category[category].append({
                    'name': name,
                    'price': price,
                    'id': doc.id
                })
                
                all_component_names.append(name)
            
            # Display results
            print(f"\n📊 TOTAL COMPONENTS FOUND: {len(all_component_names)}")
            print(f"📂 TOTAL CATEGORIES: {len(components_by_category)}")
            
            print("\n" + "="*60)
            print("📋 COMPLETE COMPONENT LIST BY CATEGORY")
            print("="*60)
            
            for category, components in components_by_category.items():
                print(f"\n🔹 {category.upper()} ({len(components)} components):")
                print("-" * 50)
                
                for i, component in enumerate(components, 1):
                    print(f"  {i:2d}. {component['name']} - ₹{component['price']:,}")
            
            print("\n" + "="*60)
            print("📝 ALL COMPONENT NAMES (FLAT LIST)")
            print("="*60)
            
            for i, name in enumerate(all_component_names, 1):
                print(f"{i:3d}. {name}")
            
            # Save to file for easy reference
            self.save_component_list(components_by_category, all_component_names)
            
            return components_by_category, all_component_names
            
        except Exception as e:
            print(f"❌ Error fetching components: {e}")
            return None, None

    def save_component_list(self, components_by_category, all_component_names):
        """Save component lists to files for easy reference"""
        try:
            # Save categorized list
            with open('component_names_by_category.json', 'w', encoding='utf-8') as f:
                json.dump(components_by_category, f, indent=2, ensure_ascii=False)
            
            # Save flat list
            with open('all_component_names.txt', 'w', encoding='utf-8') as f:
                f.write("ALL COMPONENT NAMES FROM FIREBASE\n")
                f.write("=" * 40 + "\n\n")
                for i, name in enumerate(all_component_names, 1):
                    f.write(f"{i:3d}. {name}\n")
            
            print(f"\n💾 Component lists saved to:")
            print(f"   📄 component_names_by_category.json")
            print(f"   📄 all_component_names.txt")
            
        except Exception as e:
            print(f"❌ Error saving files: {e}")

    def get_component_details(self, component_name):
        """Get detailed information about a specific component"""
        try:
            components_ref = self.db.collection('components')
            query = components_ref.where('name', '==', component_name)
            docs = query.stream()
            
            for doc in docs:
                data = doc.to_dict()
                print(f"\n📋 DETAILS FOR: {component_name}")
                print("-" * 40)
                print(f"ID: {doc.id}")
                print(f"Category: {data.get('category', 'N/A')}")
                print(f"Price: ₹{data.get('price', 0):,}")
                print(f"Image URL: {data.get('imageUrl', 'N/A')}")
                print(f"Specifications: {data.get('specifications', 'N/A')}")
                return data
            
            print(f"❌ Component '{component_name}' not found")
            return None
            
        except Exception as e:
            print(f"❌ Error fetching component details: {e}")
            return None

def main():
    print("🚀 PC-Cooker Component Name Extractor")
    print("=" * 50)
    
    extractor = ComponentNameExtractor()
    
    if not extractor.db:
        print("❌ Failed to initialize Firebase connection")
        return
    
    # Get all component names
    components_by_category, all_component_names = extractor.get_all_component_names()
    
    if components_by_category:
        print(f"\n✅ Successfully extracted {len(all_component_names)} component names!")
        print(f"📂 Organized into {len(components_by_category)} categories")
        
        # Show summary
        print(f"\n📊 SUMMARY:")
        for category, components in components_by_category.items():
            print(f"   {category}: {len(components)} components")
        
        print(f"\n🎯 You can now use these component names to:")
        print(f"   1. Create proper product-specific image mappings")
        print(f"   2. Update prices accurately")
        print(f"   3. Verify data consistency")
        
    else:
        print("❌ Failed to extract component names")

if __name__ == "__main__":
    main() 
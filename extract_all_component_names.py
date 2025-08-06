import firebase_admin
from firebase_admin import credentials, firestore
import json

class AllComponentNameExtractor:
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
        """Extract all component names from all category collections"""
        try:
            print("\n🔍 Fetching all components from all Firebase collections...")
            
            # Define all component categories based on what we found
            component_categories = [
                'CABINET', 'GRAPHIC CARD', 'MOTHERBOARD', 'POWER SUPPLY', 
                'RAM', 'STORAGE', 'PROCESSOR', 'COOLING', 'MONITOR', 
                'KEYBOARD', 'MOUSE'
            ]
            
            components_by_category = {}
            all_component_names = []
            total_components = 0
            
            for category in component_categories:
                try:
                    print(f"\n📂 Checking category: {category}")
                    
                    # Get documents from this category collection
                    collection_ref = self.db.collection(category)
                    docs = collection_ref.stream()
                    
                    category_components = []
                    category_count = 0
                    
                    for doc in docs:
                        category_count += 1
                        data = doc.to_dict()
                        name = data.get('name', 'Unknown Component')
                        price = data.get('price', 0)
                        brand = data.get('brand', 'Unknown Brand')
                        image_url = data.get('imageUrl', 'No Image')
                        
                        component_info = {
                            'id': doc.id,
                            'name': name,
                            'price': price,
                            'brand': brand,
                            'imageUrl': image_url,
                            'description': data.get('description', ''),
                            'rating': data.get('rating', 0),
                            'ratingCount': data.get('ratingCount', 0),
                            'useCases': data.get('useCases', [])
                        }
                        
                        category_components.append(component_info)
                        all_component_names.append(name)
                        total_components += 1
                    
                    if category_count > 0:
                        components_by_category[category] = category_components
                        print(f"   ✅ Found {category_count} components")
                    else:
                        print(f"   ⚠️  No components found")
                        
                except Exception as e:
                    print(f"   ❌ Error reading {category}: {e}")
                    continue
            
            # Display comprehensive results
            print(f"\n" + "="*80)
            print("📊 COMPREHENSIVE COMPONENT ANALYSIS")
            print("="*80)
            
            print(f"\n🎯 TOTAL STATISTICS:")
            print(f"   📦 Total Components: {total_components}")
            print(f"   📂 Total Categories: {len(components_by_category)}")
            
            print(f"\n📋 DETAILED BREAKDOWN BY CATEGORY:")
            print("-" * 60)
            
            for category, components in components_by_category.items():
                print(f"\n🔹 {category} ({len(components)} components):")
                print("-" * 50)
                
                for i, component in enumerate(components, 1):
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
            
            for i, name in enumerate(all_component_names, 1):
                print(f"{i:3d}. {name}")
            
            # Save detailed data
            self.save_comprehensive_data(components_by_category, all_component_names)
            
            return components_by_category, all_component_names
            
        except Exception as e:
            print(f"❌ Error fetching components: {e}")
            return None, None

    def save_comprehensive_data(self, components_by_category, all_component_names):
        """Save comprehensive component data to files"""
        try:
            # Save detailed JSON data
            with open('all_components_detailed.json', 'w', encoding='utf-8') as f:
                json.dump(components_by_category, f, indent=2, ensure_ascii=False)
            
            # Save simple name list
            with open('all_component_names_list.txt', 'w', encoding='utf-8') as f:
                f.write("ALL COMPONENT NAMES FROM FIREBASE\n")
                f.write("=" * 50 + "\n\n")
                for i, name in enumerate(all_component_names, 1):
                    f.write(f"{i:3d}. {name}\n")
            
            # Save category summary
            with open('component_categories_summary.txt', 'w', encoding='utf-8') as f:
                f.write("COMPONENT CATEGORIES SUMMARY\n")
                f.write("=" * 40 + "\n\n")
                for category, components in components_by_category.items():
                    f.write(f"{category}: {len(components)} components\n")
                    for component in components:
                        f.write(f"  - {component['name']} (₹{component['price']:,})\n")
                    f.write("\n")
            
            print(f"\n💾 Data saved to files:")
            print(f"   📄 all_components_detailed.json (Complete data)")
            print(f"   📄 all_component_names_list.txt (Simple name list)")
            print(f"   📄 component_categories_summary.txt (Category summary)")
            
        except Exception as e:
            print(f"❌ Error saving files: {e}")

    def get_component_by_name(self, component_name):
        """Find a specific component across all categories"""
        try:
            print(f"\n🔍 Searching for component: {component_name}")
            
            component_categories = [
                'CABINET', 'GRAPHIC CARD', 'MOTHERBOARD', 'POWER SUPPLY', 
                'RAM', 'STORAGE', 'PROCESSOR', 'COOLING', 'MONITOR', 
                'KEYBOARD', 'MOUSE'
            ]
            
            for category in component_categories:
                try:
                    collection_ref = self.db.collection(category)
                    query = collection_ref.where('name', '==', component_name)
                    docs = query.stream()
                    
                    for doc in docs:
                        data = doc.to_dict()
                        print(f"\n✅ FOUND in {category} collection:")
                        print(f"   ID: {doc.id}")
                        print(f"   Name: {data.get('name')}")
                        print(f"   Brand: {data.get('brand')}")
                        print(f"   Price: ₹{data.get('price', 0):,}")
                        print(f"   Image URL: {data.get('imageUrl', 'N/A')}")
                        print(f"   Description: {data.get('description', 'N/A')}")
                        return data, category
                        
                except Exception as e:
                    continue
            
            print(f"❌ Component '{component_name}' not found in any collection")
            return None, None
            
        except Exception as e:
            print(f"❌ Error searching for component: {e}")
            return None, None

def main():
    print("🚀 PC-Cooker All Component Name Extractor")
    print("=" * 60)
    
    extractor = AllComponentNameExtractor()
    
    if not extractor.db:
        print("❌ Failed to initialize Firebase connection")
        return
    
    # Get all component names
    components_by_category, all_component_names = extractor.get_all_component_names()
    
    if components_by_category:
        print(f"\n✅ SUCCESSFULLY EXTRACTED ALL COMPONENT DATA!")
        print(f"📊 Total Components: {len(all_component_names)}")
        print(f"📂 Total Categories: {len(components_by_category)}")
        
        print(f"\n🎯 CATEGORY BREAKDOWN:")
        for category, components in components_by_category.items():
            print(f"   {category}: {len(components)} components")
        
        print(f"\n💡 NEXT STEPS:")
        print(f"   1. Use the component names to create proper image mappings")
        print(f"   2. Update prices to current Indian market rates")
        print(f"   3. Verify all images are product-specific")
        print(f"   4. Test the app with the updated data")
        
    else:
        print("❌ Failed to extract component data")

if __name__ == "__main__":
    main() 
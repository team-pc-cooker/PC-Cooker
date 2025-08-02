import firebase_admin
from firebase_admin import credentials, firestore

def initialize_firebase():
    cred = credentials.Certificate("serviceAccountKey.json")
    firebase_admin.initialize_app(cred)
    return firestore.client()

def add_budget_friendly_components(db):
    components = {
        "PROCESSOR": [
            {"name": "Intel Core i3-2100", "price": 2500, "brand": "Intel", "description": "2nd Gen i3", "rating": 4, "ratingCount": 150},
            {"name": "Intel Core i5-2400", "price": 3000, "brand": "Intel", "description": "2nd Gen i5", "rating": 4.2, "ratingCount": 200},
        ],
        "GRAPHIC CARD": [
            {"name": "NVIDIA GeForce GT 710", "price": 2500, "brand": "NVIDIA", "description": "2GB Low-Profile GPU", "rating": 3.8, "ratingCount": 120},
        ],
        "MOTHERBOARD": [
            {"name": "H61 Chipset Board", "price": 2000, "brand": "Generic", "description": "Compatible with 2nd Gen Intel", "rating": 3.7, "ratingCount": 100},
        ],
        "RAM": [
            {"name": "4GB DDR3 1600MHz", "price": 1200, "brand": "Kingston", "description": "DDR3 RAM", "rating": 4, "ratingCount": 300},
        ],
        "STORAGE": [
            {"name": "WD Blue 500GB HDD", "price": 1500, "brand": "WD", "description": "500GB 7200RPM", "rating": 4.1, "ratingCount": 250},
        ],
        "CABINET": [
            {"name": "ANT Esports ICE-120AG", "price": 1500, "brand": "ANT Esports", "description": "Budget Gaming Case", "rating": 3.9, "ratingCount": 180},
        ],
        "POWER SUPPLY": [
            {"name": "450W Generic PSU", "price": 900, "brand": "Generic", "description": "Basic 450W PSU", "rating": 3.5, "ratingCount": 90},
        ]
    }
    
    for category, items in components.items():
        for item in items:
            db.collection("pc_components").document(category).collection("items").add(item)
            print(f"Added {category}: {item['name']}")

if __name__ == "__main__":
    db = initialize_firebase()
    add_budget_friendly_components(db)
    print("Firebase update complete.")


import firebase_admin
from firebase_admin import credentials, firestore

def initialize_firebase():
    cred = credentials.Certificate("serviceAccountKey.json")
    firebase_admin.initialize_app(cred)
    return firestore.client()

def get_all_processors():
    return [
        # Intel i3 models
        {"name": "Intel Core i3-7100", "price": 3000, "brand": "Intel",
         "description": "7th Gen, 2C/4T, 3.9 GHz, LGA1151", "rating": 4.0,
         "ratingCount": 36, "useCases": ["Budget", "Office"],
         "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/6/6e/Intel_Core_i3_cpu_socket.jpg"},
        {"name": "Intel Core i3-2120", "price": 1099, "brand": "Intel",
         "description": "2nd Gen, 2C/4T, 3.3 GHz, LGA1155", "rating": 3.9,
         "ratingCount": 198, "useCases": ["Budget", "Office"],
         "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/6/6e/Intel_Core_i3_cpu_socket.jpg"},
        {"name": "Intel Core i3-3220", "price": 1440, "brand": "Intel",
         "description": "3rd Gen, 2C/4T, 3.3 GHz, LGA1155", "rating": 3.9,
         "ratingCount": 25, "useCases": ["Budget", "Office"],
         "imageUrl": "https://turn0image4.jpg"},
        {"name": "Intel Core i3-4130T", "price": 4180, "brand": "Intel",
         "description": "4th Gen low‑power, 2C/4T, 2.9 GHz", "rating": 3.2,
         "ratingCount": 13, "useCases": ["Budget", "Office"],
         "imageUrl": "https://turn0image6.jpg"},
        {"name": "Intel Core i3-10100", "price": 6999, "brand": "Intel",
         "description": "10th Gen Comet Lake, 4C/8T, 3.6–4.3 GHz", "rating": 4.5,
         "ratingCount": 86, "useCases": ["Budget", "Light Gaming"],
         "imageUrl": "https://turn0image7.jpg"},
        {"name": "Intel Core i3-10100F", "price": 5590, "brand": "Intel",
         "description": "10 Gen, no‑gpu, 4C/8T up to 4.4 GHz", "rating": 4.4,
         "ratingCount": 434, "useCases": ["Budget", "Light Gaming"],
         "imageUrl": "https://turn0image1.jpg"},
        {"name": "Intel Core i3-10105F", "price": 6199, "brand": "Intel",
         "description": "10 Gen variant i3‑10100F boxed", "rating": 4.3,
         "ratingCount": 399, "useCases": ["Budget", "Light Gaming"],
         "imageUrl": "https://turn0image1.jpg"},
        {"name": "Intel Core i3-12100", "price": 7499, "brand": "Intel",
         "description": "12th Gen Alder Lake, 4C/8T, 4.3 GHz", "rating": 4.5,
         "ratingCount": 149, "useCases": ["Budget", "Light Gaming"],
         "imageUrl": "https://turn0image2.jpg"},
        {"name": "Intel Core i3-12100F", "price": 7019, "brand": "Intel",
         "description": "12 Gen boxed i3‑12100F 4C/8T", "rating": 4.7,
         "ratingCount": 1486, "useCases": ["Budget", "Light Gaming"],
         "imageUrl": "https://turn0image2.jpg"},
        {"name": "Intel Core i3-14100F", "price": 7995, "brand": "Intel",
         "description": "14th Gen latest budget i3, 4C/8T", "rating": 4.3,
         "ratingCount": 150, "useCases": ["Budget", "Light Gaming"],
         "imageUrl": "https://turn0image2.jpg"},

        # Intel i5 models
        {"name": "Intel Core i5-10400F", "price": 9879, "brand": "Intel",
         "description": "10th Gen, 6C/12T up to 4.3 GHz", "rating": 4.3,
         "ratingCount": 110, "useCases": ["Light Gaming", "Editing"],
         "imageUrl": "https://turn0image1.jpg"},
        {"name": "Intel Core i5-12400F", "price": 10999, "brand": "Intel",
         "description": "12th Gen, 6C/12T, great value for gaming", "rating": 4.5,
         "ratingCount": 200, "useCases": ["Light Gaming", "Editing"],
         "imageUrl": "https://turn0image2.jpg"},
        # AMD Ryzen 3 / Ryzen 5
        {"name": "AMD Athlon 3000G", "price": 3680, "brand": "AMD",
         "description": "Dual Core APU, Vega 3 graphics, AM4", "rating": 3.9,
         "ratingCount": 90, "useCases": ["Budget", "Office"],
         "imageUrl": "https://turn0image9.jpg"},
        {"name": "AMD Ryzen 3 3200G", "price": 5959, "brand": "AMD",
         "description": "Zen+ APU, 4C/4T Vega 8, AM4", "rating": 4.3,
         "ratingCount": 1282, "useCases": ["Budget", "Light Gaming"],
         "imageUrl": "https://turn0image9.jpg"},
        {"name": "AMD Ryzen 5 5500", "price": 10590, "brand": "AMD",
         "description": "Zen 3, 6C/12T, AM4", "rating": 4.6,
         "ratingCount": 63, "useCases": ["Medium Gaming", "Editing"],
         "imageUrl": "https://turn0image9.jpg"},
        {"name": "AMD Ryzen 5 5600G", "price": 11213, "brand": "AMD",
         "description": "Zen 3 APU, Vega 7 graphics, AM4", "rating": 4.5,
         "ratingCount": 1508, "useCases": ["Light Gaming", "Editing"],
         "imageUrl": "https://turn0image9.jpg"},
        {"name": "AMD Ryzen 5 5600", "price": 13000, "brand": "AMD",
         "description": "Zen 3 non‑APU 6C/12T, AM4", "rating": 4.6,
         "ratingCount": 150, "useCases": ["Medium Gaming", "Editing"],
         "imageUrl": "https://turn0image9.jpg"},

        # Extra filler entries: i3 older/used & Ryzen APU variants
        {"name": "Intel Core i3-9100", "price": 17900, "brand": "Intel",
         "description": "9th Gen 4C/4T, LGA1151", "rating": 4.4,
         "ratingCount": 79, "useCases": ["Basic Gaming"], "imageUrl": "https://turn0image1.jpg"},
        {"name": "Intel Core i3-6100", "price": 30000, "brand": "Intel",
         "description": "6th Gen 2C/4T, LGA1151", "rating": 4.1,
         "ratingCount": 27, "useCases": ["Legacy Builds"], "imageUrl": "https://turn0image6.jpg"},
        {"name": "Intel Core i3-3220T", "price": 2500, "brand": "Intel",
         "description": "3rd Gen Low‑power, 2.5 GHz, LGA1155", "rating": 3.8,
         "ratingCount": 25, "useCases": ["Budget"], "imageUrl": "https://turn0image4.jpg"},
    ]

def clear_existing_processors(db):
    """Remove all existing documents from the PROCESSOR collection"""
    col = db.collection("PROCESSOR")
    docs = col.stream()
    
    deleted_count = 0
    for doc in docs:
        doc.reference.delete()
        deleted_count += 1
    
    print(f"🗑️ Deleted {deleted_count} existing processor items.")
    return deleted_count

def upload_processors(db):
    """Upload new processor data to Firestore"""
    procs = get_all_processors()
    col = db.collection("PROCESSOR")
    
    for p in procs:
        col.add(p)
    
    print(f"✅ Uploaded {len(procs)} new processor items.")
    return len(procs)

if __name__ == "__main__":
    print("🚀 Starting processor data update...")
    db = initialize_firebase()
    
    # First, clear all existing processor data
    deleted_count = clear_existing_processors(db)
    
    # Then upload new processor data
    uploaded_count = upload_processors(db)
    
    print(f"\n📊 Summary:")
    print(f"   - Deleted: {deleted_count} old processors")
    print(f"   - Uploaded: {uploaded_count} new processors")
    print(f"✨ Processor data update completed successfully!")

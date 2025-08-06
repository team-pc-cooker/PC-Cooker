import firebase_admin
from firebase_admin import credentials, firestore

def initialize_firebase():
    cred = credentials.Certificate("serviceAccountKey.json")
    firebase_admin.initialize_app(cred)
    return firestore.client()

def get_all_processors():
    return [
        # Intel i3 Processors - Budget Range
        {"name": "Intel Core i3-10100F", "price": 5200, "brand": "Intel",
         "description": "10th Gen Comet Lake, 4C/8T, up to 4.3 GHz, No iGPU", "rating": 4.4,
         "ratingCount": 1200, "useCases": ["Budget Gaming", "Light Gaming"],
         "imageUrl": "https://m.media-amazon.com/images/I/51X8GV9YSOL._SL1200_.jpg"},
        {"name": "Intel Core i3-10100", "price": 6800, "brand": "Intel",
         "description": "10th Gen Comet Lake, 4C/8T, up to 4.3 GHz with UHD 630", "rating": 4.3,
         "ratingCount": 900, "useCases": ["Budget Gaming", "Office"],
         "imageUrl": "https://m.media-amazon.com/images/I/51d7UQJWDXL._SL1200_.jpg"},
        {"name": "Intel Core i3-10105F", "price": 5800, "brand": "Intel",
         "description": "10th Gen Comet Lake, 4C/8T, up to 4.4 GHz, No iGPU", "rating": 4.2,
         "ratingCount": 600, "useCases": ["Budget Gaming", "Entry Level"],
         "imageUrl": "https://m.media-amazon.com/images/I/41OoTKd9VqL._SL1024_.jpg"},
        {"name": "Intel Core i3-12100F", "price": 7500, "brand": "Intel",
         "description": "12th Gen Alder Lake, 4C/8T, up to 4.3 GHz, No iGPU", "rating": 4.7,
         "ratingCount": 1500, "useCases": ["Budget Gaming", "Light Gaming"],
         "imageUrl": "https://m.media-amazon.com/images/I/51bhNJRVXyL._SL1200_.jpg"},
        {"name": "Intel Core i3-12100", "price": 8500, "brand": "Intel",
         "description": "12th Gen Alder Lake, 4C/8T, up to 4.3 GHz with UHD 730", "rating": 4.5,
         "ratingCount": 800, "useCases": ["Budget Gaming", "Office"],
         "imageUrl": "https://m.media-amazon.com/images/I/51VwKDNR-QL._SL1200_.jpg"},
        {"name": "Intel Core i3-13100F", "price": 8800, "brand": "Intel",
         "description": "13th Gen Raptor Lake, 4C/8T, up to 4.5 GHz, No iGPU", "rating": 4.6,
         "ratingCount": 450, "useCases": ["Budget Gaming", "Light Gaming"],
         "imageUrl": "https://m.media-amazon.com/images/I/51kE4t8FVDL._SL1200_.jpg"},
        {"name": "Intel Core i3-14100F", "price": 9200, "brand": "Intel",
         "description": "14th Gen Raptor Lake Refresh, 4C/8T, up to 4.7 GHz", "rating": 4.5,
         "ratingCount": 300, "useCases": ["Budget Gaming", "Light Gaming"],
         "imageUrl": "https://m.media-amazon.com/images/I/51JZRJ4tR5L._SL1200_.jpg"},

        # Intel i5 Processors - Mid to High Range
        {"name": "Intel Core i5-10400F", "price": 9500, "brand": "Intel",
         "description": "10th Gen Comet Lake, 6C/12T, up to 4.3 GHz, No iGPU", "rating": 4.5,
         "ratingCount": 1800, "useCases": ["Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/51mJMUGNLIL._SL1200_.jpg"},
        {"name": "Intel Core i5-11400F", "price": 11200, "brand": "Intel",
         "description": "11th Gen Rocket Lake, 6C/12T, up to 4.4 GHz, No iGPU", "rating": 4.6,
         "ratingCount": 1500, "useCases": ["Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/51qJ8lCqCqL._SL1200_.jpg"},
        {"name": "Intel Core i5-12400F", "price": 12500, "brand": "Intel",
         "description": "12th Gen Alder Lake, 6C/12T, up to 4.4 GHz, No iGPU", "rating": 4.8,
         "ratingCount": 2500, "useCases": ["Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/51LkqeUYJeL._SL1200_.jpg"},
        {"name": "Intel Core i5-12400", "price": 14200, "brand": "Intel",
         "description": "12th Gen Alder Lake, 6C/12T, up to 4.4 GHz with UHD 730", "rating": 4.6,
         "ratingCount": 1800, "useCases": ["Gaming", "Productivity"],
         "imageUrl": "https://m.media-amazon.com/images/I/51IzYkfJPEL._SL1200_.jpg"},
        {"name": "Intel Core i5-13400F", "price": 15800, "brand": "Intel",
         "description": "13th Gen Raptor Lake, 10C/16T, up to 4.6 GHz, No iGPU", "rating": 4.9,
         "ratingCount": 1200, "useCases": ["Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/51l0SBKpWyL._SL1200_.jpg"},
        {"name": "Intel Core i5-13600KF", "price": 19500, "brand": "Intel",
         "description": "13th Gen Raptor Lake, 14C/20T, up to 5.1 GHz, Unlocked", "rating": 4.9,
         "ratingCount": 900, "useCases": ["High-End Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/51zHyBAjdOL._SL1200_.jpg"},
        {"name": "Intel Core i5-14400F", "price": 17500, "brand": "Intel",
         "description": "14th Gen Raptor Lake Refresh, 10C/16T, up to 4.7 GHz", "rating": 4.8,
         "ratingCount": 800, "useCases": ["Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/51Y7XFQHHJL._SL1200_.jpg"},

        # Intel i7 Processors - High Performance
        {"name": "Intel Core i7-12700F", "price": 24500, "brand": "Intel",
         "description": "12th Gen Alder Lake, 12C/20T, up to 4.9 GHz, No iGPU", "rating": 4.8,
         "ratingCount": 1200, "useCases": ["High-End Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/51ZoXx5oqIL._SL1200_.jpg"},
        {"name": "Intel Core i7-13700F", "price": 28500, "brand": "Intel",
         "description": "13th Gen Raptor Lake, 16C/24T, up to 5.2 GHz, No iGPU", "rating": 4.9,
         "ratingCount": 800, "useCases": ["High-End Gaming", "Professional Work"],
         "imageUrl": "https://m.media-amazon.com/images/I/51c9V0E8sBL._SL1200_.jpg"},

        # AMD Ryzen 3 Processors - Budget
        {"name": "AMD Ryzen 3 3100", "price": 6500, "brand": "AMD",
         "description": "Zen 2, 4C/8T, up to 3.9 GHz, AM4 Socket", "rating": 4.3,
         "ratingCount": 800, "useCases": ["Budget Gaming", "Office"],
         "imageUrl": "https://m.media-amazon.com/images/I/61nxSECe9HL._SL1500_.jpg"},
        {"name": "AMD Ryzen 3 3200G", "price": 7200, "brand": "AMD",
         "description": "Zen+ APU, 4C/4T, up to 4.0 GHz with Vega 8 Graphics", "rating": 4.3,
         "ratingCount": 1500, "useCases": ["Budget Gaming", "Office"],
         "imageUrl": "https://m.media-amazon.com/images/I/61oOJqzn52L._SL1500_.jpg"},
        {"name": "AMD Ryzen 3 4300G", "price": 8200, "brand": "AMD",
         "description": "Zen 2 APU, 4C/8T, up to 4.0 GHz with Radeon Graphics", "rating": 4.4,
         "ratingCount": 600, "useCases": ["Budget Gaming", "Productivity"],
         "imageUrl": "https://m.media-amazon.com/images/I/61kR4bPCHXL._SL1500_.jpg"},

        # AMD Ryzen 5 Processors - Mid to High Range
        {"name": "AMD Ryzen 5 3600", "price": 10500, "brand": "AMD",
         "description": "Zen 2, 6C/12T, up to 4.2 GHz, AM4 Socket", "rating": 4.7,
         "ratingCount": 3500, "useCases": ["Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/61vGQNUEsKL._SL1500_.jpg"},
        {"name": "AMD Ryzen 5 4600G", "price": 9800, "brand": "AMD",
         "description": "Zen 2 APU, 6C/12T, up to 4.2 GHz with Radeon Graphics", "rating": 4.5,
         "ratingCount": 800, "useCases": ["Budget Gaming", "Productivity"],
         "imageUrl": "https://m.media-amazon.com/images/I/61wxqKWHrJL._SL1500_.jpg"},
        {"name": "AMD Ryzen 5 5500", "price": 10200, "brand": "AMD",
         "description": "Zen 3, 6C/12T, up to 4.2 GHz, AM4 Socket", "rating": 4.5,
         "ratingCount": 900, "useCases": ["Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/61OoVKlM0wL._SL1500_.jpg"},
        {"name": "AMD Ryzen 5 5600", "price": 11800, "brand": "AMD",
         "description": "Zen 3 Architecture, 6C/12T, up to 4.4 GHz, AM4 Socket", "rating": 4.7,
         "ratingCount": 2200, "useCases": ["Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/61GHJmXHPVL._SL1500_.jpg"},
        {"name": "AMD Ryzen 5 5600G", "price": 12800, "brand": "AMD",
         "description": "Zen 3 APU, 6C/12T, up to 4.4 GHz with Radeon Graphics", "rating": 4.6,
         "ratingCount": 1800, "useCases": ["Budget Gaming", "Office"],
         "imageUrl": "https://m.media-amazon.com/images/I/61VRBm8RJOL._SL1500_.jpg"},
        {"name": "AMD Ryzen 5 5600X", "price": 13500, "brand": "AMD",
         "description": "Zen 3, 6C/12T, up to 4.6 GHz, Unlocked Multiplier", "rating": 4.8,
         "ratingCount": 3000, "useCases": ["Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/61vGQNUEsKL._SL1500_.jpg"},
        {"name": "AMD Ryzen 5 7600", "price": 19800, "brand": "AMD",
         "description": "Zen 4, 6C/12T, up to 5.1 GHz, AM5 Socket with DDR5", "rating": 4.7,
         "ratingCount": 900, "useCases": ["Gaming", "Future Proof"],
         "imageUrl": "https://m.media-amazon.com/images/I/61bBhKYXfYL._SL1500_.jpg"},
        {"name": "AMD Ryzen 5 7600X", "price": 22500, "brand": "AMD",
         "description": "Zen 4, 6C/12T, up to 5.3 GHz, AM5 with Unlocked Multiplier", "rating": 4.8,
         "ratingCount": 750, "useCases": ["High-End Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/61LQ7dPdKaL._SL1500_.jpg"},

        # AMD Ryzen 7 Processors - High Performance
        {"name": "AMD Ryzen 7 3700X", "price": 16500, "brand": "AMD",
         "description": "Zen 2, 8C/16T, up to 4.4 GHz, AM4 Socket", "rating": 4.8,
         "ratingCount": 2800, "useCases": ["Gaming", "Content Creation", "Streaming"],
         "imageUrl": "https://m.media-amazon.com/images/I/61HBxVgCaYL._SL1500_.jpg"},
        {"name": "AMD Ryzen 7 5700X", "price": 18500, "brand": "AMD",
         "description": "Zen 3, 8C/16T, up to 4.6 GHz, AM4 Socket", "rating": 4.9,
         "ratingCount": 1500, "useCases": ["Gaming", "Content Creation", "Streaming"],
         "imageUrl": "https://m.media-amazon.com/images/I/61EW6zYOxpL._SL1500_.jpg"},
        {"name": "AMD Ryzen 7 5800X", "price": 21500, "brand": "AMD",
         "description": "Zen 3, 8C/16T, up to 4.7 GHz, Unlocked Multiplier", "rating": 4.8,
         "ratingCount": 2200, "useCases": ["High-End Gaming", "Content Creation"],
         "imageUrl": "https://m.media-amazon.com/images/I/61u+fFOGzrL._SL1500_.jpg"},
        {"name": "AMD Ryzen 7 7700X", "price": 26500, "brand": "AMD",
         "description": "Zen 4, 8C/16T, up to 5.4 GHz, AM5 Socket with DDR5", "rating": 4.8,
         "ratingCount": 800, "useCases": ["High-End Gaming", "Professional Work"],
         "imageUrl": "https://m.media-amazon.com/images/I/61mZ9Y7CdyL._SL1500_.jpg"},

        # AMD Ryzen 9 Processors - Flagship
        {"name": "AMD Ryzen 9 5900X", "price": 32500, "brand": "AMD",
         "description": "Zen 3, 12C/24T, up to 4.8 GHz, AM4 Socket", "rating": 4.9,
         "ratingCount": 1800, "useCases": ["High-End Gaming", "Professional Work", "Streaming"],
         "imageUrl": "https://m.media-amazon.com/images/I/61u0WZMdlEL._SL1500_.jpg"},
        {"name": "AMD Ryzen 9 7900X", "price": 38500, "brand": "AMD",
         "description": "Zen 4, 12C/24T, up to 5.6 GHz, AM5 Socket with DDR5", "rating": 4.9,
         "ratingCount": 600, "useCases": ["Enthusiast Gaming", "Professional Work"],
         "imageUrl": "https://m.media-amazon.com/images/I/61FQtJNTzAL._SL1500_.jpg"},
    ]

def clear_existing_processors(db):
    """Remove all existing documents from the PROCESSOR collection"""
    # Clear from the correct Firebase structure that the app expects
    col = db.collection("pc_components").document("PROCESSOR").collection("items")
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
    # Upload to the correct Firebase structure that the app expects
    col = db.collection("pc_components").document("PROCESSOR").collection("items")
    
    for p in procs:
        # Add category field to match ComponentModel structure
        p["category"] = "PROCESSOR"
        p["inStock"] = True  # Set as in stock by default
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

package com.app.pccooker;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.app.pccooker.ComponentModel;
import java.util.Map;
import java.util.HashMap;

public class AddComponentActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private int totalComponents = 0;
    private int addedComponents = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_component);
        
        // Show progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding DDR3 Components");
        progressDialog.setMessage("Please wait while adding budget components to Firebase...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Add DDR3 budget components to both Firebase structures
        addDDR3BudgetComponents();
    }
    
    private void addDDR3BudgetComponents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        Log.d("AddComponent", "Starting to add DDR3 budget components...");
        
        // Count total components to add
        totalComponents = 0;
        addedComponents = 0;
        
        // DDR3 Motherboards - H61, H81, B75 (Budget-friendly)
        addComponent(db, "MOTHERBOARD", "Intel H61 Motherboard", "Intel", 
            "Budget-friendly H61 motherboard supporting 2nd and 3rd gen Intel processors", 
            1200.0, 4.2, 
            Map.of("Socket", "LGA1155", "RAM", "DDR3", "Max RAM", "16GB", "Form Factor", "Micro-ATX"));
            
        addComponent(db, "MOTHERBOARD", "Gigabyte H61M-S1", "Gigabyte", 
            "H61 chipset motherboard for 2nd and 3rd gen Intel processors", 
            1100.0, 4.1, 
            Map.of("Socket", "LGA1155", "RAM", "DDR3", "Max RAM", "16GB", "Form Factor", "Micro-ATX"));
            
        addComponent(db, "MOTHERBOARD", "ASUS H61M-K", "ASUS", 
            "H61 chipset motherboard with USB 3.0 support", 
            1300.0, 4.3, 
            Map.of("Socket", "LGA1155", "RAM", "DDR3", "Max RAM", "16GB", "Form Factor", "Micro-ATX"));
            
        addComponent(db, "MOTHERBOARD", "MSI H61M-P31", "MSI", 
            "H61 chipset motherboard for budget builds", 
            1000.0, 4.0, 
            Map.of("Socket", "LGA1155", "RAM", "DDR3", "Max RAM", "16GB", "Form Factor", "Micro-ATX"));
            
        addComponent(db, "MOTHERBOARD", "Gigabyte H81M-S1", "Gigabyte", 
            "H81 chipset motherboard for 4th gen Intel processors", 
            1500.0, 4.3, 
            Map.of("Socket", "LGA1150", "RAM", "DDR3", "Max RAM", "16GB", "Form Factor", "Micro-ATX"));
            
        addComponent(db, "MOTHERBOARD", "ASUS B75M-A", "ASUS", 
            "B75 chipset motherboard with USB 3.0 support", 
            1800.0, 4.4, 
            Map.of("Socket", "LGA1155", "RAM", "DDR3", "Max RAM", "16GB", "Form Factor", "Micro-ATX"));
            
        addComponent(db, "MOTHERBOARD", "MSI H61M-P20", "MSI", 
            "Ultra-budget H61 motherboard for basic builds", 
            800.0, 3.8, 
            Map.of("Socket", "LGA1155", "RAM", "DDR3", "Max RAM", "8GB", "Form Factor", "Micro-ATX"));
            
        addComponent(db, "MOTHERBOARD", "Gigabyte H61M-DS2", "Gigabyte", 
            "H61 motherboard with dual BIOS", 
            1400.0, 4.2, 
            Map.of("Socket", "LGA1155", "RAM", "DDR3", "Max RAM", "16GB", "Form Factor", "Micro-ATX"));
        
        // DDR3 Processors - 2nd, 3rd, 4th gen Intel (Budget-friendly)
        addComponent(db, "PROCESSOR", "Intel Core i3-2120 (2nd Gen)", "Intel", 
            "Dual-core processor with 3.3GHz clock speed", 
            800.0, 4.0, 
            Map.of("Socket", "LGA1155", "Cores", "2", "Threads", "4", "Clock Speed", "3.3GHz", "Generation", "2nd Gen"));
            
        addComponent(db, "PROCESSOR", "Intel Core i5-2400 (2nd Gen)", "Intel", 
            "Quad-core processor with 3.1GHz base clock", 
            1200.0, 4.1, 
            Map.of("Socket", "LGA1155", "Cores", "4", "Threads", "4", "Clock Speed", "3.1GHz", "Generation", "2nd Gen"));
            
        addComponent(db, "PROCESSOR", "Intel Core i5-2500 (2nd Gen)", "Intel", 
            "Quad-core processor with 3.3GHz base clock", 
            1400.0, 4.2, 
            Map.of("Socket", "LGA1155", "Cores", "4", "Threads", "4", "Clock Speed", "3.3GHz", "Generation", "2nd Gen"));
            
        addComponent(db, "PROCESSOR", "Intel Core i7-2600 (2nd Gen)", "Intel", 
            "Quad-core processor with hyper-threading", 
            2000.0, 4.4, 
            Map.of("Socket", "LGA1155", "Cores", "4", "Threads", "8", "Clock Speed", "3.4GHz", "Generation", "2nd Gen"));
            
        addComponent(db, "PROCESSOR", "Intel Core i3-3220 (3rd Gen)", "Intel", 
            "Dual-core processor with 3.3GHz clock speed", 
            900.0, 4.1, 
            Map.of("Socket", "LGA1155", "Cores", "2", "Threads", "4", "Clock Speed", "3.3GHz", "Generation", "3rd Gen"));
            
        addComponent(db, "PROCESSOR", "Intel Core i5-3470 (3rd Gen)", "Intel", 
            "Quad-core processor with 3.2GHz base clock", 
            1500.0, 4.2, 
            Map.of("Socket", "LGA1155", "Cores", "4", "Threads", "4", "Clock Speed", "3.2GHz", "Generation", "3rd Gen"));
            
        addComponent(db, "PROCESSOR", "Intel Core i7-3770 (3rd Gen)", "Intel", 
            "Quad-core processor with hyper-threading", 
            2500.0, 4.5, 
            Map.of("Socket", "LGA1155", "Cores", "4", "Threads", "8", "Clock Speed", "3.4GHz", "Generation", "3rd Gen"));
            
        addComponent(db, "PROCESSOR", "Intel Core i3-4130 (4th Gen)", "Intel", 
            "Dual-core processor with 3.4GHz clock speed", 
            1200.0, 4.1, 
            Map.of("Socket", "LGA1150", "Cores", "2", "Threads", "4", "Clock Speed", "3.4GHz", "Generation", "4th Gen"));
            
        addComponent(db, "PROCESSOR", "Intel Core i5-4440 (4th Gen)", "Intel", 
            "Quad-core processor with 3.1GHz base clock", 
            1800.0, 4.3, 
            Map.of("Socket", "LGA1150", "Cores", "4", "Threads", "4", "Clock Speed", "3.1GHz", "Generation", "4th Gen"));
            
        addComponent(db, "PROCESSOR", "Intel Core i5-4570 (4th Gen)", "Intel", 
            "Quad-core processor with 3.2GHz base clock", 
            2000.0, 4.4, 
            Map.of("Socket", "LGA1150", "Cores", "4", "Threads", "4", "Clock Speed", "3.2GHz", "Generation", "4th Gen"));
            
        addComponent(db, "PROCESSOR", "Intel Core i7-4770 (4th Gen)", "Intel", 
            "Quad-core processor with hyper-threading", 
            3000.0, 4.6, 
            Map.of("Socket", "LGA1150", "Cores", "4", "Threads", "8", "Clock Speed", "3.4GHz", "Generation", "4th Gen"));
        
        // DDR3 RAM - Various capacities and speeds
        addComponent(db, "MEMORY", "Kingston DDR3 2GB 1333MHz", "Kingston", 
            "2GB DDR3 RAM module for basic builds", 
            300.0, 3.8, 
            Map.of("Type", "DDR3", "Capacity", "2GB", "Speed", "1333MHz", "Latency", "CL9"));
            
        addComponent(db, "MEMORY", "Kingston DDR3 4GB 1333MHz", "Kingston", 
            "4GB DDR3 RAM module for budget builds", 
            400.0, 4.0, 
            Map.of("Type", "DDR3", "Capacity", "4GB", "Speed", "1333MHz", "Latency", "CL9"));
            
        addComponent(db, "MEMORY", "Corsair DDR3 4GB 1600MHz", "Corsair", 
            "4GB DDR3 RAM module with 1600MHz speed", 
            500.0, 4.1, 
            Map.of("Type", "DDR3", "Capacity", "4GB", "Speed", "1600MHz", "Latency", "CL9"));
            
        addComponent(db, "MEMORY", "Corsair DDR3 8GB 1600MHz", "Corsair", 
            "8GB DDR3 RAM module with 1600MHz speed", 
            800.0, 4.2, 
            Map.of("Type", "DDR3", "Capacity", "8GB", "Speed", "1600MHz", "Latency", "CL9"));
            
        addComponent(db, "MEMORY", "G.Skill DDR3 8GB 1600MHz", "G.Skill", 
            "8GB DDR3 RAM module for gaming", 
            900.0, 4.2, 
            Map.of("Type", "DDR3", "Capacity", "8GB", "Speed", "1600MHz", "Latency", "CL9"));
            
        addComponent(db, "MEMORY", "G.Skill DDR3 16GB 1600MHz", "G.Skill", 
            "16GB DDR3 RAM kit (2x8GB) for gaming", 
            1500.0, 4.3, 
            Map.of("Type", "DDR3", "Capacity", "16GB", "Speed", "1600MHz", "Latency", "CL9", "Kit", "2x8GB"));
            
        addComponent(db, "MEMORY", "Crucial DDR3 4GB 1333MHz", "Crucial", 
            "4GB DDR3 RAM module for basic builds", 
            350.0, 3.9, 
            Map.of("Type", "DDR3", "Capacity", "4GB", "Speed", "1333MHz", "Latency", "CL9"));
            
        addComponent(db, "MEMORY", "Crucial DDR3 8GB 1600MHz", "Crucial", 
            "8GB DDR3 RAM module for budget builds", 
            750.0, 4.1, 
            Map.of("Type", "DDR3", "Capacity", "8GB", "Speed", "1600MHz", "Latency", "CL9"));
        
        // Budget Graphics Cards
        addComponent(db, "GRAPHIC CARD", "NVIDIA GT 710 1GB", "NVIDIA", 
            "Entry-level graphics card for basic display", 
            1500.0, 3.8, 
            Map.of("Memory", "1GB", "Memory Type", "DDR3", "Core Clock", "954MHz", "Power", "19W"));
            
        addComponent(db, "GRAPHIC CARD", "NVIDIA GT 710 2GB", "NVIDIA", 
            "Entry-level graphics card with 2GB VRAM", 
            1800.0, 3.9, 
            Map.of("Memory", "2GB", "Memory Type", "DDR3", "Core Clock", "954MHz", "Power", "19W"));
            
        addComponent(db, "GRAPHIC CARD", "NVIDIA GT 1030 2GB", "NVIDIA", 
            "Entry-level graphics card for basic gaming", 
            2500.0, 4.0, 
            Map.of("Memory", "2GB", "Memory Type", "GDDR5", "Core Clock", "1227MHz", "Power", "30W"));
            
        addComponent(db, "GRAPHIC CARD", "NVIDIA GTX 750 Ti 2GB", "NVIDIA", 
            "Budget gaming card with good performance", 
            3500.0, 4.2, 
            Map.of("Memory", "2GB", "Memory Type", "GDDR5", "Core Clock", "1020MHz", "Power", "60W"));
            
        addComponent(db, "GRAPHIC CARD", "NVIDIA GTX 1060 3GB", "NVIDIA", 
            "Mid-range graphics card for 1080p gaming", 
            8000.0, 4.4, 
            Map.of("Memory", "3GB", "Memory Type", "GDDR5", "Core Clock", "1506MHz", "Power", "120W"));
            
        addComponent(db, "GRAPHIC CARD", "NVIDIA GTX 1650 4GB", "NVIDIA", 
            "Budget gaming card with 4GB VRAM", 
            12000.0, 4.5, 
            Map.of("Memory", "4GB", "Memory Type", "GDDR5", "Core Clock", "1485MHz", "Power", "75W"));
            
        addComponent(db, "GRAPHIC CARD", "AMD Radeon R7 240 2GB", "AMD", 
            "Entry-level AMD graphics card", 
            2000.0, 3.7, 
            Map.of("Memory", "2GB", "Memory Type", "DDR3", "Core Clock", "730MHz", "Power", "30W"));
            
        addComponent(db, "GRAPHIC CARD", "AMD Radeon R7 250 2GB", "AMD", 
            "Budget AMD graphics card for basic gaming", 
            2500.0, 3.9, 
            Map.of("Memory", "2GB", "Memory Type", "GDDR5", "Core Clock", "1000MHz", "Power", "65W"));
        
        // Budget Storage
        addComponent(db, "STORAGE", "Kingston A400 120GB SSD", "Kingston", 
            "120GB SATA SSD for fast boot times", 
            800.0, 4.1, 
            Map.of("Type", "SSD", "Capacity", "120GB", "Interface", "SATA III", "Read Speed", "500MB/s"));
            
        addComponent(db, "STORAGE", "Crucial BX500 240GB SSD", "Crucial", 
            "240GB SATA SSD with good performance", 
            1200.0, 4.2, 
            Map.of("Type", "SSD", "Capacity", "240GB", "Interface", "SATA III", "Read Speed", "540MB/s"));
            
        addComponent(db, "STORAGE", "Western Digital Blue 500GB HDD", "Western Digital", 
            "500GB HDD for mass storage", 
            1200.0, 4.0, 
            Map.of("Type", "HDD", "Capacity", "500GB", "Interface", "SATA III", "RPM", "7200"));
            
        addComponent(db, "STORAGE", "Seagate Barracuda 1TB HDD", "Seagate", 
            "1TB HDD for large storage needs", 
            2000.0, 4.1, 
            Map.of("Type", "HDD", "Capacity", "1TB", "Interface", "SATA III", "RPM", "7200"));
            
        addComponent(db, "STORAGE", "Western Digital Blue 320GB HDD", "Western Digital", 
            "320GB HDD for basic storage needs", 
            800.0, 3.8, 
            Map.of("Type", "HDD", "Capacity", "320GB", "Interface", "SATA III", "RPM", "7200"));
        
        // Budget Power Supplies
        addComponent(db, "POWER SUPPLY", "Corsair VS350 350W", "Corsair", 
            "350W power supply for basic builds", 
            1200.0, 4.0, 
            Map.of("Wattage", "350W", "Efficiency", "80+ White", "Modular", "No", "Connectors", "24-pin, 4+4-pin"));
            
        addComponent(db, "POWER SUPPLY", "Corsair VS450 450W", "Corsair", 
            "450W power supply for budget builds", 
            1500.0, 4.1, 
            Map.of("Wattage", "450W", "Efficiency", "80+ White", "Modular", "No", "Connectors", "24-pin, 4+4-pin"));
            
        addComponent(db, "POWER SUPPLY", "Cooler Master MWE 550W", "Cooler Master", 
            "550W power supply with 80+ Bronze efficiency", 
            2000.0, 4.2, 
            Map.of("Wattage", "550W", "Efficiency", "80+ Bronze", "Modular", "No", "Connectors", "24-pin, 8-pin"));
            
        addComponent(db, "POWER SUPPLY", "Antec VP450P 450W", "Antec", 
            "450W power supply for budget builds", 
            1400.0, 4.0, 
            Map.of("Wattage", "450W", "Efficiency", "80+ White", "Modular", "No", "Connectors", "24-pin, 4+4-pin"));
        
        // Budget Cases
        addComponent(db, "CABINET", "Antec NX100", "Antec", 
            "Budget-friendly micro-ATX case", 
            1500.0, 4.0, 
            Map.of("Form Factor", "Micro-ATX", "Fans", "1x120mm", "Material", "Steel", "Expansion Slots", "4"));
            
        addComponent(db, "CABINET", "Cooler Master Q300L", "Cooler Master", 
            "Compact micro-ATX case with good airflow", 
            2000.0, 4.2, 
            Map.of("Form Factor", "Micro-ATX", "Fans", "1x120mm", "Material", "Steel/Plastic", "Expansion Slots", "4"));
            
        addComponent(db, "CABINET", "Deepcool MATREXX 30", "Deepcool", 
            "Budget micro-ATX case with side panel", 
            1800.0, 4.1, 
            Map.of("Form Factor", "Micro-ATX", "Fans", "1x120mm", "Material", "Steel", "Expansion Slots", "4"));
            
        addComponent(db, "CABINET", "Zebronics Zeb-2000", "Zebronics", 
            "Ultra-budget micro-ATX case", 
            800.0, 3.5, 
            Map.of("Form Factor", "Micro-ATX", "Fans", "1x80mm", "Material", "Steel", "Expansion Slots", "3"));
        
        // CPU Coolers
        addComponent(db, "CPU COOLER", "Intel Stock Cooler", "Intel", 
            "Included CPU cooler with Intel processors", 
            0.0, 3.5, 
            Map.of("Type", "Air", "Fan Size", "92mm", "Noise Level", "Medium", "Compatibility", "LGA1155/LGA1150"));
            
        addComponent(db, "CPU COOLER", "Cooler Master Hyper 212", "Cooler Master", 
            "Popular air cooler with good performance", 
            1500.0, 4.3, 
            Map.of("Type", "Air", "Fan Size", "120mm", "Noise Level", "Low", "Compatibility", "Universal"));
            
        addComponent(db, "CPU COOLER", "Deepcool GAMMAXX 400", "Deepcool", 
            "Budget air cooler with good performance", 
            1200.0, 4.2, 
            Map.of("Type", "Air", "Fan Size", "120mm", "Noise Level", "Low", "Compatibility", "Universal"));
            
        addComponent(db, "CPU COOLER", "Antec A30", "Antec", 
            "Budget air cooler for basic builds", 
            800.0, 3.8, 
            Map.of("Type", "Air", "Fan Size", "92mm", "Noise Level", "Medium", "Compatibility", "LGA1155/LGA1150"));
        
        Log.d("AddComponent", "Total components to add: " + totalComponents);
    }
    
    private void addComponent(FirebaseFirestore db, String category, String name, String brand, 
                            String description, double price, double rating, Map<String, String> specifications) {
        totalComponents++;
        
        ComponentModel component = new ComponentModel();
        component.setName(name);
        component.setBrand(brand);
        component.setCategory(category);
        component.setDescription(description);
        component.setPrice(price);
        component.setRating(rating);
        component.setSpecifications(specifications);
        component.setInStock(true);
        component.setRatingCount(100); // Default rating count
        
        Log.d("AddComponent", "Adding component: " + name + " (Category: " + category + ")");
        
        // Add to old structure (components collection)
        db.collection("components")
            .add(component)
            .addOnSuccessListener(documentReference -> {
                Log.d("AddComponent", "Component added to old structure: " + name);
                checkCompletion();
            })
            .addOnFailureListener(e -> {
                Log.e("AddComponent", "Error adding component to old structure: " + name, e);
                checkCompletion();
            });
        
        // Add to new structure (pc_components/{category}/items)
        db.collection("pc_components")
            .document(category)
            .collection("items")
            .add(component)
            .addOnSuccessListener(documentReference -> {
                Log.d("AddComponent", "Component added to new structure: " + name);
                checkCompletion();
            })
            .addOnFailureListener(e -> {
                Log.e("AddComponent", "Error adding component to new structure: " + name, e);
                checkCompletion();
            });
    }
    
    private void checkCompletion() {
        addedComponents++;
        
        if (progressDialog != null) {
            progressDialog.setMessage("Added " + addedComponents + " of " + totalComponents + " components...");
        }
        
        if (addedComponents >= totalComponents * 2) { // *2 because we add to both structures
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            
            Log.d("AddComponent", "All components added successfully!");
            Toast.makeText(this, "DDR3 budget components added successfully to both Firebase structures!", Toast.LENGTH_LONG).show();
            
            // Finish the activity after adding components
            finish();
        }
    }
} 
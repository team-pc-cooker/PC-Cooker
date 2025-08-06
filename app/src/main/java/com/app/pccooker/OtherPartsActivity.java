package com.app.pccooker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

public class OtherPartsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OtherPartsAdapter adapter;
    private List<OtherPart> otherPartsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_parts);

        // Initialize views
        ImageView backButton = findViewById(R.id.backButton);
        TextView titleText = findViewById(R.id.titleText);
        recyclerView = findViewById(R.id.recyclerView);

        // Set up back button
        backButton.setOnClickListener(v -> finish());

        // Set up RecyclerView with grid layout
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        
        // Initialize data
        initializeOtherParts();
        
        // Set up adapter
        adapter = new OtherPartsAdapter(otherPartsList, this::onPartSelected);
        recyclerView.setAdapter(adapter);
    }

    private void initializeOtherParts() {
        otherPartsList = new ArrayList<>();
        
        // Core Components
        otherPartsList.add(new OtherPart("PC", "Desktop Computer", "₹25,000+", "PC", R.drawable.ic_pc));
        otherPartsList.add(new OtherPart("Laptop", "Portable Computer", "₹35,000+", "LAPTOP", R.drawable.ic_laptop));
        otherPartsList.add(new OtherPart("OS", "Operating System", "₹5,000+", "OS", R.drawable.ic_os));
        
        // Cooling & Storage
        otherPartsList.add(new OtherPart("CPU Cooler", "Processor Cooling", "₹2,000+", "COOLER", R.drawable.ic_cpu_cooler));
        otherPartsList.add(new OtherPart("Storage", "Hard Drives & SSDs", "₹3,000+", "STORAGE", R.drawable.ic_storage));
        otherPartsList.add(new OtherPart("Case Fans", "System Cooling", "₹500+", "FANS", R.drawable.ic_cpu_cooler));
        
        // Input Devices
        otherPartsList.add(new OtherPart("Keyboard", "Input Device", "₹1,500+", "KEYBOARD", R.drawable.ic_keyboard));
        otherPartsList.add(new OtherPart("Mouse", "Pointing Device", "₹800+", "MOUSE", R.drawable.ic_mouse));
        otherPartsList.add(new OtherPart("Game Controller", "Gaming Input", "₹2,500+", "CONTROLLER", R.drawable.ic_game_controller));
        
        // Display & Graphics
        otherPartsList.add(new OtherPart("GPU", "Graphics Card", "₹15,000+", "GPU", R.drawable.ic_gpu));
        otherPartsList.add(new OtherPart("Monitor", "Display Screen", "₹8,000+", "MONITOR", R.drawable.ic_monitor));
        otherPartsList.add(new OtherPart("Monitor Accessories", "Stands & Mounts", "₹2,000+", "MONITOR_ACC", R.drawable.ic_monitor));
        
        // Audio & Capture
        otherPartsList.add(new OtherPart("Speaker & Headset", "Audio Output", "₹1,500+", "AUDIO", R.drawable.ic_speaker_headset));
        otherPartsList.add(new OtherPart("Sound Card", "Audio Processing", "₹3,000+", "SOUND_CARD", R.drawable.ic_sound_card));
        otherPartsList.add(new OtherPart("Capture Card", "Video Capture", "₹5,000+", "CAPTURE", R.drawable.ic_capture_card));
        
        // Accessories & Lighting
        otherPartsList.add(new OtherPart("Cable Extension", "Power & Data Cables", "₹500+", "CABLES", R.drawable.ic_cable_extension));
        otherPartsList.add(new OtherPart("RGB Strips", "LED Lighting", "₹800+", "RGB", R.drawable.ic_rgb_strips));
        otherPartsList.add(new OtherPart("Cooler Accessories", "Thermal Paste & Mounts", "₹300+", "COOLER_ACC", R.drawable.ic_cpu_cooler));
        
        // Furniture & Network
        otherPartsList.add(new OtherPart("Gaming Chair", "Ergonomic Seating", "₹8,000+", "CHAIR", R.drawable.ic_gaming_chair));
        otherPartsList.add(new OtherPart("Gaming Desk", "Workstation", "₹12,000+", "DESK", R.drawable.ic_gaming_desk));
        otherPartsList.add(new OtherPart("Network", "WiFi & Ethernet", "₹1,000+", "NETWORK", R.drawable.ic_network));
    }

    private void onPartSelected(OtherPart part) {
        // Navigate to component selection for this category
        Intent intent = new Intent(this, ComponentListActivity.class);
        intent.putExtra("category", part.getCategory());
        intent.putExtra("categoryName", part.getName());
        startActivity(intent);
    }

    public static class OtherPart {
        private String name;
        private String description;
        private String price;
        private String category;
        private int iconResId;

        public OtherPart(String name, String description, String price, String category, int iconResId) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.category = category;
            this.iconResId = iconResId;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getPrice() { return price; }
        public String getCategory() { return category; }
        public int getIconResId() { return iconResId; }
    }
} 
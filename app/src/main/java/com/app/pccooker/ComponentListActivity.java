package com.app.pccooker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;
import android.view.View;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.ComponentModel;
import com.app.pccooker.ui.UiNotifier;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ComponentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ComponentAdapter adapter;
    private List<ComponentModel> componentList;
    private String category;
    private int budget;
    private String processorType;
    private String socketType;
    private String storageType;
    private String gpuType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_list);

        // Get parameters from intent
        category = getIntent().getStringExtra("category");
        budget = getIntent().getIntExtra("budget", 40000);
        processorType = getIntent().getStringExtra("processorType");
        socketType = getIntent().getStringExtra("socketType");
        storageType = getIntent().getStringExtra("storageType");
        gpuType = getIntent().getStringExtra("gpuType");

        // Set title
        TextView titleText = findViewById(R.id.componentListTitle);
        TextView subtitleText = findViewById(R.id.componentListSubtitle);
        
        if (titleText != null && category != null) {
            titleText.setText("Select " + category);
        }
        
        // Hide filter information for manual component selection
        if (subtitleText != null) {
            subtitleText.setVisibility(View.GONE);
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        componentList = new ArrayList<>();

        adapter = new ComponentAdapter(this, componentList, new ComponentAdapter.OnComponentClickListener() {
            @Override
            public void onComponentClick(ComponentModel component) {
                // Add selected component to cart
                CartManager.getInstance(ComponentListActivity.this).addToCart(component);
                // Component added to build (no toast shown)
                
                // Return to previous screen
                finish();
            }
        });

        recyclerView.setAdapter(adapter);

        loadComponentsFromFirebase();
    }

    private boolean isComponentCompatible(ComponentModel component) {
        // For manual component selection, show all components without filtering
        // Only check compatibility for display purposes, don't filter out
        return true;
    }

    private void loadComponentsFromFirebase() {
        if (category == null) {
            UiNotifier.showShort(this, "No category specified");
            return;
        }

        // Show loading message
        String loadingMessage = "Loading " + category + " components";
        UiNotifier.showShort(this, loadingMessage);

        Log.d("ComponentList", "Loading components for category: " + category);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // First try the new structure (pc_components/{category}/items)
        Log.d("ComponentList", "Trying new structure: pc_components/" + category + "/items");
        db.collection("pc_components")
            .document(category)
            .collection("items")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<ComponentModel> allComponents = new ArrayList<>();
                int totalComponents = 0;
                
                Log.d("ComponentList", "New structure query successful. Found " + queryDocumentSnapshots.size() + " documents");
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    totalComponents++;
                    ComponentModel component = document.toObject(ComponentModel.class);
                    if (component != null) {
                        component.setId(document.getId());
                        allComponents.add(component);
                        Log.d("ComponentList", "Added component (new structure): " + component.getName() + " (Brand: " + component.getBrand() + ", Price: " + component.getPrice() + ")");
                    } else {
                        Log.e("ComponentList", "Failed to parse component from document: " + document.getId());
                    }
                }
                
                Log.d("ComponentList", "Total components found in new structure: " + totalComponents);
                
                if (allComponents.isEmpty()) {
                    // If no components found in new structure, try old structure
                    Log.d("ComponentList", "No components in new structure, trying old structure...");
                    loadFromOldStructure();
                } else {
                    // Sort components by price (cheapest first)
                    allComponents.sort((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice()));
                    
                    componentList.clear();
                    componentList.addAll(allComponents);
                    adapter.notifyDataSetChanged();
                    
                    String successMessage = "Found " + allComponents.size() + " " + category + " components";
                    UiNotifier.showShort(this, successMessage);
                    Log.d("ComponentList", successMessage);
                }
            })
            .addOnFailureListener(e -> {
                Log.e("ComponentList", "Failed to load from new structure: " + e.getMessage());
                // If new structure fails, try old structure
                loadFromOldStructure();
            });
    }

    private void loadFromOldStructure() {
        Log.d("ComponentList", "Loading from old structure (components collection)...");
        
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ComponentList", "Querying old structure: components where category = " + category);
        
        db.collection("components")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<ComponentModel> allComponents = new ArrayList<>();
                int totalComponents = 0;
                
                Log.d("ComponentList", "Old structure query successful. Found " + queryDocumentSnapshots.size() + " documents");
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    totalComponents++;
                    ComponentModel component = document.toObject(ComponentModel.class);
                    if (component != null) {
                        component.setId(document.getId());
                        allComponents.add(component);
                        Log.d("ComponentList", "Added component (old structure): " + component.getName() + " (Brand: " + component.getBrand() + ", Price: " + component.getPrice() + ")");
                    } else {
                        Log.e("ComponentList", "Failed to parse component from document: " + document.getId());
                    }
                }
                
                Log.d("ComponentList", "Total components found in old structure: " + totalComponents);
                
                if (allComponents.isEmpty()) {
                    String noComponentsMessage = "No " + category + " components found. Please add components to the database.";
                    UiNotifier.showShort(this, noComponentsMessage);
                    Log.w("ComponentList", noComponentsMessage);
                } else {
                    // Sort components by price (cheapest first)
                    allComponents.sort((c1, c2) -> Double.compare(c1.getPrice(), c2.getPrice()));
                    
                    componentList.clear();
                    componentList.addAll(allComponents);
                    adapter.notifyDataSetChanged();
                    
                    String successMessage = "Found " + allComponents.size() + " " + category + " components";
                    UiNotifier.showShort(this, successMessage);
                    Log.d("ComponentList", successMessage);
                }
            })
            .addOnFailureListener(e -> {
                String errorMessage = "Failed to load " + category + " components: " + e.getMessage();
                UiNotifier.showShort(this, errorMessage);
                Log.e("ComponentList", errorMessage, e);
                
                // Check if it's a permission error
                if (e.getMessage() != null && e.getMessage().contains("permission")) {
                    UiNotifier.showShort(this, "Firebase permission error. Please check security rules.");
                }
            });
    }
}

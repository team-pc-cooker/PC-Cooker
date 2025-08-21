package com.app.pccooker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.adapters.TrendingComponentAdapter;
import com.app.pccooker.models.ComponentModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrendingComponentsActivity extends AppCompatActivity {
    
    private static final String TAG = "TrendingComponents";
    
    private RecyclerView trendingRecyclerView;
    private TrendingComponentAdapter adapter;
    private ProgressBar loadingProgress;
    private LinearLayout noResultsText;
    private ChipGroup categoryFilterGroup, buildTypeFilterGroup;
    private Button refreshButton;
    
    private FirebaseFirestore db;
    private List<ComponentModel> allTrendingComponents;
    private List<ComponentModel> filteredComponents;
    
    // Filter states
    private String selectedCategory = "ALL";
    private String selectedBuildType = "ALL";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_components);
        
        initializeViews();
        setupFirebase();
        setupRecyclerView();
        setupFilters();
        loadTrendingComponents();
    }
    
    private void initializeViews() {
        trendingRecyclerView = findViewById(R.id.trendingRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);
        noResultsText = findViewById(R.id.noResultsText);
        categoryFilterGroup = findViewById(R.id.categoryFilterGroup);
        buildTypeFilterGroup = findViewById(R.id.buildTypeFilterGroup);
        refreshButton = findViewById(R.id.refreshButton);
        
        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        // Refresh button
        refreshButton.setOnClickListener(v -> {
            refreshButton.setEnabled(false);
            loadTrendingComponents();
        });
    }
    
    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        allTrendingComponents = new ArrayList<>();
        filteredComponents = new ArrayList<>();
    }
    
    private void setupRecyclerView() {
        adapter = new TrendingComponentAdapter(this, filteredComponents, this::onComponentSelected);
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trendingRecyclerView.setAdapter(adapter);
    }
    
    private void setupFilters() {
        // Category filter
        categoryFilterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip selectedChip = findViewById(checkedIds.get(0));
                selectedCategory = selectedChip.getText().toString();
                Log.d(TAG, "Category filter changed: " + selectedCategory);
                applyFilters();
            }
        });
        
        // Build type filter
        buildTypeFilterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip selectedChip = findViewById(checkedIds.get(0));
                selectedBuildType = selectedChip.getText().toString();
                Log.d(TAG, "Build type filter changed: " + selectedBuildType);
                applyFilters();
            }
        });
        
        // Set default selections
        ((Chip) findViewById(R.id.chipAllCategories)).setChecked(true);
        ((Chip) findViewById(R.id.chipAllBuildTypes)).setChecked(true);
    }
    
    private void loadTrendingComponents() {
        showLoading(true);
        
        // Query trending components (trending score > 85)
        db.collection("components")
                .whereGreaterThan("trendingScore", 85)
                .orderBy("trendingScore", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allTrendingComponents.clear();
                    
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            ComponentModel component = document.toObject(ComponentModel.class);
                            component.setId(document.getId());
                            allTrendingComponents.add(component);
                            Log.d(TAG, "Loaded trending component: " + component.getName());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing component: " + e.getMessage());
                        }
                    }
                    
                    Log.d(TAG, "Loaded " + allTrendingComponents.size() + " trending components");
                    applyFilters();
                    showLoading(false);
                    refreshButton.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading trending components: " + e.getMessage());
                    Toast.makeText(this, "Failed to load trending components", Toast.LENGTH_SHORT).show();
                    showLoading(false);
                    refreshButton.setEnabled(true);
                });
    }
    
    private void applyFilters() {
        filteredComponents.clear();
        
        for (ComponentModel component : allTrendingComponents) {
            boolean matchesCategory = selectedCategory.equals("ALL") || 
                                    component.getCategory().equalsIgnoreCase(selectedCategory);
            
            boolean matchesBuildType = selectedBuildType.equals("ALL") || 
                                     componentMatchesBuildType(component, selectedBuildType);
            
            if (matchesCategory && matchesBuildType) {
                filteredComponents.add(component);
            }
        }
        
        Log.d(TAG, "Filtered components: " + filteredComponents.size() + "/" + allTrendingComponents.size());
        
        // Update UI
        adapter.notifyDataSetChanged();
        noResultsText.setVisibility(filteredComponents.isEmpty() ? View.VISIBLE : View.GONE);
        trendingRecyclerView.setVisibility(filteredComponents.isEmpty() ? View.GONE : View.VISIBLE);
    }
    
    private boolean componentMatchesBuildType(ComponentModel component, String buildType) {
        if (buildType.equals("ALL")) return true;
        
        // Check if component has build types
        List<String> buildTypes = component.getBuildTypes();
        if (buildTypes != null && !buildTypes.isEmpty()) {
            for (String type : buildTypes) {
                if (type.equalsIgnoreCase(buildType)) {
                    return true;
                }
            }
        }
        
        // Fallback logic based on price and category
        double price = component.getPrice();
        String category = component.getCategory();
        
        switch (buildType.toLowerCase()) {
            case "budget build":
                return price < 5000;
            case "gaming build":
                return (category.equalsIgnoreCase("GRAPHIC CARD") && price > 20000) ||
                       (category.equalsIgnoreCase("PROCESSOR") && price > 15000);
            case "office build":
                return price < 10000 && !category.equalsIgnoreCase("GRAPHIC CARD");
            case "workstation":
                return price > 15000 && (category.equalsIgnoreCase("PROCESSOR") || 
                                       category.equalsIgnoreCase("MEMORY"));
            default:
                return true;
        }
    }
    
    private void onComponentSelected(ComponentModel component) {
        Log.d(TAG, "Component selected: " + component.getName());
        
        // Navigate to component details or add to build
        Intent intent = new Intent(this, ComponentListActivity.class);
        intent.putExtra("category", component.getCategory());
        intent.putExtra("selectedComponent", component.getName());
        startActivity(intent);
    }
    
    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        trendingRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        noResultsText.setVisibility(View.GONE);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data if needed
        if (allTrendingComponents.isEmpty()) {
            loadTrendingComponents();
        }
    }
}
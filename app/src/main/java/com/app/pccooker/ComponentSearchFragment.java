package com.app.pccooker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.ComponentModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class ComponentSearchFragment extends Fragment {

    private static final String ARG_SEARCH_QUERY = "search_query";
    
    private EditText searchInput;
    private RecyclerView searchResultsRecyclerView;
    private ComponentAdapter searchAdapter;
    private List<ComponentModel> allComponents = new ArrayList<>();
    private List<ComponentModel> filteredComponents = new ArrayList<>();
    
    // Filter views
    private LinearLayout filterLayout;
    private Button filterButton;
    private Button clearFiltersButton;
    private SeekBar priceRangeSeekBar;
    private TextView priceRangeText;
    private EditText minPriceInput, maxPriceInput;
    private Button applyFiltersButton;
    
    // Filter states
    private String currentQuery = "";
    private double minPrice = 0;
    private double maxPrice = 100000;
    private String selectedBrand = "";
    private double minRating = 0;
    
    private FirebaseFirestore db;

    public static ComponentSearchFragment newInstance(String searchQuery) {
        ComponentSearchFragment fragment = new ComponentSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_QUERY, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_component_search, container, false);

            db = FirebaseFirestore.getInstance();
            
            initializeViews(view);
            setupListeners();
            
            if (getArguments() != null) {
                currentQuery = getArguments().getString(ARG_SEARCH_QUERY, "");
                if (searchInput != null) {
                    searchInput.setText(currentQuery);
                }
            }
            
            loadComponents();
            
            return view;
        } catch (Exception e) {
            Log.e("ComponentSearch", "Error in onCreateView", e);
            // Return a simple error view if inflation fails
            TextView errorView = new TextView(getContext());
            errorView.setText("Error loading component search. Please try again.");
            errorView.setPadding(50, 50, 50, 50);
            return errorView;
        }
    }

    private void initializeViews(View view) {
        try {
            if (view == null) {
                Log.e("ComponentSearch", "View is null in initializeViews");
                return;
            }

            searchInput = view.findViewById(R.id.searchInput);
            searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
            filterLayout = view.findViewById(R.id.filterLayout);
            filterButton = view.findViewById(R.id.filterButton);
            clearFiltersButton = view.findViewById(R.id.clearFiltersButton);
            priceRangeSeekBar = view.findViewById(R.id.priceRangeSeekBar);
            priceRangeText = view.findViewById(R.id.priceRangeText);
            minPriceInput = view.findViewById(R.id.minPriceInput);
            maxPriceInput = view.findViewById(R.id.maxPriceInput);
            applyFiltersButton = view.findViewById(R.id.applyFiltersButton);
            
            // Check if context is available
            if (getContext() == null) {
                Log.e("ComponentSearch", "Context is null in initializeViews");
                return;
            }
            
            // Setup RecyclerView with proper configuration
            if (searchResultsRecyclerView != null) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                searchResultsRecyclerView.setLayoutManager(layoutManager);
                
                // Add item decoration for proper spacing
                searchResultsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, 
                                             @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        outRect.top = 4;
                        outRect.bottom = 4;
                        outRect.left = 8;
                        outRect.right = 8;
                    }
                });
                
                // Enable nested scrolling for better performance
                searchResultsRecyclerView.setNestedScrollingEnabled(true);
                searchResultsRecyclerView.setHasFixedSize(false);
            } else {
                Log.e("ComponentSearch", "searchResultsRecyclerView is null");
            }
        } catch (Exception e) {
            Log.e("ComponentSearch", "Error in initializeViews", e);
        }
    }

    private void setupListeners() {
        try {
            // Real-time search with debouncing
            if (searchInput != null) {
                searchInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        currentQuery = s.toString().trim();
                        applyFilters();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            }
            
            // Filter button
            if (filterButton != null) {
                filterButton.setOnClickListener(v -> {
                    if (filterLayout != null) {
                        filterLayout.setVisibility(filterLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    }
                });
            }
            
            // Clear filters
            if (clearFiltersButton != null) {
                clearFiltersButton.setOnClickListener(v -> {
                    clearFilters();
                });
            }
            
            // Price range seekbar
            if (priceRangeSeekBar != null) {
                priceRangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        maxPrice = progress * 1000; // Convert to actual price
                        if (priceRangeText != null) {
                            priceRangeText.setText("Max Price: ₹" + String.format("%.0f", maxPrice));
                        }
                    }
                    
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });
            }
            
            // Apply filters
            if (applyFiltersButton != null) {
                applyFiltersButton.setOnClickListener(v -> {
                    try {
                        if (minPriceInput != null && maxPriceInput != null) {
                            String minPriceStr = minPriceInput.getText().toString();
                            String maxPriceStr = maxPriceInput.getText().toString();
                            
                            if (!minPriceStr.isEmpty()) {
                                minPrice = Double.parseDouble(minPriceStr);
                            }
                            if (!maxPriceStr.isEmpty()) {
                                maxPrice = Double.parseDouble(maxPriceStr);
                            }
                            
                            applyFilters();
                            if (filterLayout != null) {
                                filterLayout.setVisibility(View.GONE);
                            }
                        }
                    } catch (NumberFormatException e) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Please enter valid price values", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e("ComponentSearch", "Error in setupListeners", e);
        }
    }

    private void loadComponents() {
        try {
            if (getContext() == null) {
                Log.e("ComponentSearch", "Context is null in loadComponents");
                return;
            }

            Toast.makeText(getContext(), "Loading components...", Toast.LENGTH_SHORT).show();

            db.collection("components")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        try {
                            allComponents.clear();
                            if (queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(getContext(), "No components found. Adding sample data...", Toast.LENGTH_SHORT).show();
                                addSampleComponents();
                                return;
                            }

                            // Use a Map to deduplicate components by name and specifications
                            Map<String, ComponentModel> uniqueComponents = new HashMap<>();

                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                try {
                                    ComponentModel component = document.toObject(ComponentModel.class);
                                    if (component != null) {
                                        component.setId(document.getId());
                                        
                                        // Create a unique key based on name, category, and brand
                                        String uniqueKey = component.getName().toLowerCase() + "_" + 
                                                         component.getCategory().toLowerCase() + "_" + 
                                                         component.getBrand().toLowerCase();
                                        
                                        // If component with same key exists, merge them (keep the one with better rating/price)
                                        if (uniqueComponents.containsKey(uniqueKey)) {
                                            ComponentModel existing = uniqueComponents.get(uniqueKey);
                                            // Keep the one with better rating, or if same rating, keep the cheaper one
                                            if (component.getRating() > existing.getRating() || 
                                                (component.getRating() == existing.getRating() && component.getPrice() < existing.getPrice())) {
                                                uniqueComponents.put(uniqueKey, component);
                                            }
                                        } else {
                                            uniqueComponents.put(uniqueKey, component);
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e("ComponentSearch", "Error parsing component from document: " + document.getId(), e);
                                }
                            }

                            // Convert back to list
                            allComponents.addAll(uniqueComponents.values());

                            Toast.makeText(getContext(), "Loaded " + allComponents.size() + " unique components", Toast.LENGTH_SHORT).show();
                            applyFilters();
                        } catch (Exception e) {
                            Log.e("ComponentSearch", "Error processing components", e);
                            addSampleComponents();
                        }
                    })
                    .addOnFailureListener(e -> {
                        try {
                            // Only show error if it's not a permission issue (since search is working)
                            if (e.getMessage() != null && !e.getMessage().contains("permission")) {
                                String errorMessage = "Failed to load components: " + e.getMessage();
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                                }
                                Log.e("ComponentSearch", errorMessage, e);
                            }
                            
                            // Add sample data if loading fails
                            addSampleComponents();
                        } catch (Exception ex) {
                            Log.e("ComponentSearch", "Error in failure listener", ex);
                            addSampleComponents();
                        }
                    });
        } catch (Exception e) {
            Log.e("ComponentSearch", "Error in loadComponents", e);
            addSampleComponents();
        }
    }
    
    private void addSampleComponents() {
        Toast.makeText(getContext(), "Adding sample components...", Toast.LENGTH_SHORT).show();
        
        List<ComponentModel> sampleComponents = new ArrayList<>();
        
        // AMD Processors (with generation info)
        ComponentModel amd3 = new ComponentModel();
        amd3.setName("AMD Ryzen 3 3200G (3rd Generation)");
        amd3.setBrand("AMD");
        amd3.setCategory("PROCESSOR");
        amd3.setDescription("Quad-core processor with integrated graphics, perfect for budget builds");
        amd3.setPrice(8500);
        amd3.setRating(4.2);
        amd3.getSpecifications().put("Socket", "AM4");
        amd3.getSpecifications().put("Generation", "3rd Gen");
        sampleComponents.add(amd3);
        
        ComponentModel amd5 = new ComponentModel();
        amd5.setName("AMD Ryzen 5 5600G (5th Generation)");
        amd5.setBrand("AMD");
        amd5.setCategory("PROCESSOR");
        amd5.setDescription("6-core processor with integrated graphics, great for gaming and productivity");
        amd5.setPrice(15000);
        amd5.setRating(4.5);
        amd5.getSpecifications().put("Socket", "AM4");
        amd5.getSpecifications().put("Generation", "5th Gen");
        sampleComponents.add(amd5);
        
        ComponentModel amd7 = new ComponentModel();
        amd7.setName("AMD Ryzen 7 5800X (5th Generation)");
        amd7.setBrand("AMD");
        amd7.setCategory("PROCESSOR");
        amd7.setDescription("8-core high-performance processor for gaming and content creation");
        amd7.setPrice(28000);
        amd7.setRating(4.7);
        amd7.getSpecifications().put("Socket", "AM4");
        amd7.getSpecifications().put("Generation", "5th Gen");
        sampleComponents.add(amd7);
        
        // Intel Processors (with generation info and budget options)
        ComponentModel intel2gen = new ComponentModel();
        intel2gen.setName("Intel Core i5-2400 (2nd Generation)");
        intel2gen.setBrand("Intel");
        intel2gen.setCategory("PROCESSOR");
        intel2gen.setDescription("Quad-core budget processor, great for basic computing");
        intel2gen.setPrice(2500);
        intel2gen.setRating(3.8);
        intel2gen.getSpecifications().put("Socket", "LGA1155");
        intel2gen.getSpecifications().put("Generation", "2nd Gen");
        sampleComponents.add(intel2gen);
        
        ComponentModel intel3gen = new ComponentModel();
        intel3gen.setName("Intel Core i5-3470 (3rd Generation)");
        intel3gen.setBrand("Intel");
        intel3gen.setCategory("PROCESSOR");
        intel3gen.setDescription("Quad-core processor with good performance for budget builds");
        intel3gen.setPrice(3500);
        intel3gen.setRating(4.0);
        intel3gen.getSpecifications().put("Socket", "LGA1155");
        intel3gen.getSpecifications().put("Generation", "3rd Gen");
        sampleComponents.add(intel3gen);
        
        ComponentModel intel4gen = new ComponentModel();
        intel4gen.setName("Intel Core i3-4130 (4th Generation)");
        intel4gen.setBrand("Intel");
        intel4gen.setCategory("PROCESSOR");
        intel4gen.setDescription("Dual-core budget processor for basic tasks");
        intel4gen.setPrice(2000);
        intel4gen.setRating(3.5);
        intel4gen.getSpecifications().put("Socket", "LGA1150");
        intel4gen.getSpecifications().put("Generation", "4th Gen");
        sampleComponents.add(intel4gen);
        
        ComponentModel intel6gen = new ComponentModel();
        intel6gen.setName("Intel Core i5-6400 (6th Generation)");
        intel6gen.setBrand("Intel");
        intel6gen.setCategory("PROCESSOR");
        intel6gen.setDescription("Quad-core processor with good performance");
        intel6gen.setPrice(4500);
        intel6gen.setRating(4.1);
        intel6gen.getSpecifications().put("Socket", "LGA1151");
        intel6gen.getSpecifications().put("Generation", "6th Gen");
        sampleComponents.add(intel6gen);
        
        ComponentModel intel8gen = new ComponentModel();
        intel8gen.setName("Intel Core i3-8100 (8th Generation)");
        intel8gen.setBrand("Intel");
        intel8gen.setCategory("PROCESSOR");
        intel8gen.setDescription("Quad-core budget processor with good performance");
        intel8gen.setPrice(6000);
        intel8gen.setRating(4.2);
        intel8gen.getSpecifications().put("Socket", "LGA1151");
        intel8gen.getSpecifications().put("Generation", "8th Gen");
        sampleComponents.add(intel8gen);
        
        ComponentModel intel10gen = new ComponentModel();
        intel10gen.setName("Intel Core i5-10400F (10th Generation)");
        intel10gen.setBrand("Intel");
        intel10gen.setCategory("PROCESSOR");
        intel10gen.setDescription("6-core gaming processor with excellent performance");
        intel10gen.setPrice(12000);
        intel10gen.setRating(4.4);
        intel10gen.getSpecifications().put("Socket", "LGA1200");
        intel10gen.getSpecifications().put("Generation", "10th Gen");
        sampleComponents.add(intel10gen);
        
        ComponentModel intel3 = new ComponentModel();
        intel3.setName("Intel Core i3-12100F (12th Generation)");
        intel3.setBrand("Intel");
        intel3.setCategory("PROCESSOR");
        intel3.setDescription("Quad-core budget processor with excellent performance");
        intel3.setPrice(8000);
        intel3.setRating(4.0);
        intel3.getSpecifications().put("Socket", "LGA1700");
        intel3.getSpecifications().put("Generation", "12th Gen");
        sampleComponents.add(intel3);
        
        ComponentModel intel5 = new ComponentModel();
        intel5.setName("Intel Core i5-12400F (12th Generation)");
        intel5.setBrand("Intel");
        intel5.setCategory("PROCESSOR");
        intel5.setDescription("6-core gaming processor with excellent performance");
        intel5.setPrice(15000);
        intel5.setRating(4.3);
        intel5.getSpecifications().put("Socket", "LGA1700");
        intel5.getSpecifications().put("Generation", "12th Gen");
        sampleComponents.add(intel5);
        
        ComponentModel intel7 = new ComponentModel();
        intel7.setName("Intel Core i7-12700K (12th Generation)");
        intel7.setBrand("Intel");
        intel7.setCategory("PROCESSOR");
        intel7.setDescription("8-core high-end processor for gaming and content creation");
        intel7.setPrice(35000);
        intel7.setRating(4.6);
        intel7.getSpecifications().put("Socket", "LGA1700");
        intel7.getSpecifications().put("Generation", "12th Gen");
        sampleComponents.add(intel7);
        
        // Budget Motherboards (H61, H81, etc.)
        ComponentModel moboH61 = new ComponentModel();
        moboH61.setName("Gigabyte GA-H61M-S1 (H61 Chipset)");
        moboH61.setBrand("Gigabyte");
        moboH61.setCategory("MOTHERBOARD");
        moboH61.setDescription("Budget micro-ATX motherboard for 2nd/3rd gen Intel processors");
        moboH61.setPrice(2500);
        moboH61.setRating(3.9);
        moboH61.getSpecifications().put("Socket", "LGA1155");
        moboH61.getSpecifications().put("RAM", "DDR3");
        moboH61.getSpecifications().put("Chipset", "H61");
        sampleComponents.add(moboH61);
        
        ComponentModel moboH81 = new ComponentModel();
        moboH81.setName("ASUS H81M-K (H81 Chipset)");
        moboH81.setBrand("ASUS");
        moboH81.setCategory("MOTHERBOARD");
        moboH81.setDescription("Budget micro-ATX motherboard for 4th gen Intel processors");
        moboH81.setPrice(3000);
        moboH81.setRating(4.0);
        moboH81.getSpecifications().put("Socket", "LGA1150");
        moboH81.getSpecifications().put("RAM", "DDR3");
        moboH81.getSpecifications().put("Chipset", "H81");
        sampleComponents.add(moboH81);
        
        ComponentModel moboH110 = new ComponentModel();
        moboH110.setName("MSI H110M Pro-VD (H110 Chipset)");
        moboH110.setBrand("MSI");
        moboH110.setCategory("MOTHERBOARD");
        moboH110.setDescription("Budget micro-ATX motherboard for 6th/7th gen Intel processors");
        moboH110.setPrice(3500);
        moboH110.setRating(3.8);
        moboH110.getSpecifications().put("Socket", "LGA1151");
        moboH110.getSpecifications().put("RAM", "DDR4");
        moboH110.getSpecifications().put("Chipset", "H110");
        sampleComponents.add(moboH110);
        
        ComponentModel moboB250 = new ComponentModel();
        moboB250.setName("Gigabyte GA-B250M-DS3H (B250 Chipset)");
        moboB250.setBrand("Gigabyte");
        moboB250.setCategory("MOTHERBOARD");
        moboB250.setDescription("Micro-ATX motherboard for 6th/7th gen Intel processors");
        moboB250.setPrice(4500);
        moboB250.setRating(4.1);
        moboB250.getSpecifications().put("Socket", "LGA1151");
        moboB250.getSpecifications().put("RAM", "DDR4");
        moboB250.getSpecifications().put("Chipset", "B250");
        sampleComponents.add(moboB250);
        
        ComponentModel moboB360 = new ComponentModel();
        moboB360.setName("ASUS Prime B360M-A (B360 Chipset)");
        moboB360.setBrand("ASUS");
        moboB360.setCategory("MOTHERBOARD");
        moboB360.setDescription("Micro-ATX motherboard for 8th/9th gen Intel processors");
        moboB360.setPrice(6000);
        moboB360.setRating(4.2);
        moboB360.getSpecifications().put("Socket", "LGA1151");
        moboB360.getSpecifications().put("RAM", "DDR4");
        moboB360.getSpecifications().put("Chipset", "B360");
        sampleComponents.add(moboB360);
        
        ComponentModel moboB450 = new ComponentModel();
        moboB450.setName("MSI B450M Pro-M2 (B450 Chipset)");
        moboB450.setBrand("MSI");
        moboB450.setCategory("MOTHERBOARD");
        moboB450.setDescription("AMD B450 micro-ATX motherboard for Ryzen processors");
        moboB450.setPrice(6500);
        moboB450.setRating(4.0);
        moboB450.getSpecifications().put("Socket", "AM4");
        moboB450.getSpecifications().put("RAM", "DDR4");
        moboB450.getSpecifications().put("Chipset", "B450");
        sampleComponents.add(moboB450);
        
        ComponentModel mobo1 = new ComponentModel();
        mobo1.setName("MSI B550M Pro-VDH (B550 Chipset)");
        mobo1.setBrand("MSI");
        mobo1.setCategory("MOTHERBOARD");
        mobo1.setDescription("AMD B550 micro-ATX motherboard for Ryzen processors");
        mobo1.setPrice(8000);
        mobo1.setRating(4.1);
        mobo1.getSpecifications().put("Socket", "AM4");
        mobo1.getSpecifications().put("RAM", "DDR4");
        mobo1.getSpecifications().put("Chipset", "B550");
        sampleComponents.add(mobo1);
        
        ComponentModel mobo2 = new ComponentModel();
        mobo2.setName("ASUS ROG B660-F (B660 Chipset)");
        mobo2.setBrand("ASUS");
        mobo2.setCategory("MOTHERBOARD");
        mobo2.setDescription("Intel B660 ATX motherboard for 12th/13th gen processors");
        mobo2.setPrice(12000);
        mobo2.setRating(4.4);
        mobo2.getSpecifications().put("Socket", "LGA1700");
        mobo2.getSpecifications().put("RAM", "DDR4");
        mobo2.getSpecifications().put("Chipset", "B660");
        sampleComponents.add(mobo2);
        
        ComponentModel mobo3 = new ComponentModel();
        mobo3.setName("Gigabyte X570 Aorus Elite (X570 Chipset)");
        mobo3.setBrand("Gigabyte");
        mobo3.setCategory("MOTHERBOARD");
        mobo3.setDescription("AMD X570 ATX motherboard for high-end Ryzen processors");
        mobo3.setPrice(18000);
        mobo3.setRating(4.5);
        mobo3.getSpecifications().put("Socket", "AM4");
        mobo3.getSpecifications().put("RAM", "DDR4");
        mobo3.getSpecifications().put("Chipset", "X570");
        sampleComponents.add(mobo3);
        
        // RAM (including DDR3 for older systems)
        ComponentModel ramDDR3 = new ComponentModel();
        ramDDR3.setName("Kingston 4GB DDR3-1333");
        ramDDR3.setBrand("Kingston");
        ramDDR3.setCategory("RAM");
        ramDDR3.setDescription("4GB DDR3 memory for budget builds");
        ramDDR3.setPrice(1200);
        ramDDR3.setRating(3.8);
        ramDDR3.getSpecifications().put("Type", "DDR3");
        ramDDR3.getSpecifications().put("Capacity", "4GB");
        sampleComponents.add(ramDDR3);
        
        ComponentModel ramDDR3_8GB = new ComponentModel();
        ramDDR3_8GB.setName("Corsair 8GB DDR3-1600");
        ramDDR3_8GB.setBrand("Corsair");
        ramDDR3_8GB.setCategory("RAM");
        ramDDR3_8GB.setDescription("8GB DDR3 memory for older systems");
        ramDDR3_8GB.setPrice(1800);
        ramDDR3_8GB.setRating(3.9);
        ramDDR3_8GB.getSpecifications().put("Type", "DDR3");
        ramDDR3_8GB.getSpecifications().put("Capacity", "8GB");
        sampleComponents.add(ramDDR3_8GB);
        
        ComponentModel ramDDR4_4GB = new ComponentModel();
        ramDDR4_4GB.setName("Crucial 4GB DDR4-2400");
        ramDDR4_4GB.setBrand("Crucial");
        ramDDR4_4GB.setCategory("RAM");
        ramDDR4_4GB.setDescription("4GB DDR4 memory for budget builds");
        ramDDR4_4GB.setPrice(1500);
        ramDDR4_4GB.setRating(3.7);
        ramDDR4_4GB.getSpecifications().put("Type", "DDR4");
        ramDDR4_4GB.getSpecifications().put("Capacity", "4GB");
        sampleComponents.add(ramDDR4_4GB);
        
        ComponentModel ram1 = new ComponentModel();
        ram1.setName("Corsair Vengeance 8GB DDR4-3200");
        ram1.setBrand("Corsair");
        ram1.setCategory("RAM");
        ram1.setDescription("8GB DDR4 memory for gaming builds");
        ram1.setPrice(2500);
        ram1.setRating(4.2);
        ram1.getSpecifications().put("Type", "DDR4");
        ram1.getSpecifications().put("Capacity", "8GB");
        sampleComponents.add(ram1);
        
        ComponentModel ram2 = new ComponentModel();
        ram2.setName("G.Skill Ripjaws 16GB DDR4-3600");
        ram2.setBrand("G.Skill");
        ram2.setCategory("RAM");
        ram2.setDescription("16GB DDR4 memory for high-performance builds");
        ram2.setPrice(4500);
        ram2.setRating(4.4);
        ram2.getSpecifications().put("Type", "DDR4");
        ram2.getSpecifications().put("Capacity", "16GB");
        sampleComponents.add(ram2);
        
        ComponentModel ramDDR4_32GB = new ComponentModel();
        ramDDR4_32GB.setName("Corsair Dominator 32GB DDR4-3200");
        ramDDR4_32GB.setBrand("Corsair");
        ramDDR4_32GB.setCategory("RAM");
        ramDDR4_32GB.setDescription("32GB DDR4 memory for workstation builds");
        ramDDR4_32GB.setPrice(8000);
        ramDDR4_32GB.setRating(4.3);
        ramDDR4_32GB.getSpecifications().put("Type", "DDR4");
        ramDDR4_32GB.getSpecifications().put("Capacity", "32GB");
        sampleComponents.add(ramDDR4_32GB);
        
        ComponentModel ramDDR5_16GB = new ComponentModel();
        ramDDR5_16GB.setName("G.Skill Trident 16GB DDR5-5200");
        ramDDR5_16GB.setBrand("G.Skill");
        ramDDR5_16GB.setCategory("RAM");
        ramDDR5_16GB.setDescription("16GB DDR5 memory for latest generation builds");
        ramDDR5_16GB.setPrice(7000);
        ramDDR5_16GB.setRating(4.4);
        ramDDR5_16GB.getSpecifications().put("Type", "DDR5");
        ramDDR5_16GB.getSpecifications().put("Capacity", "16GB");
        sampleComponents.add(ramDDR5_16GB);
        
        ComponentModel ram3 = new ComponentModel();
        ram3.setName("Kingston Fury 32GB DDR5-5600");
        ram3.setBrand("Kingston");
        ram3.setCategory("RAM");
        ram3.setDescription("32GB DDR5 memory for enthusiast builds");
        ram3.setPrice(12000);
        ram3.setRating(4.5);
        ram3.getSpecifications().put("Type", "DDR5");
        ram3.getSpecifications().put("Capacity", "32GB");
        sampleComponents.add(ram3);

        // Add all components to the list
        allComponents.addAll(sampleComponents);
        
        // Debug logging
        Log.d("ComponentSearch", "Added " + sampleComponents.size() + " sample components to allComponents");
        Log.d("ComponentSearch", "Total components: " + allComponents.size());
        
        // Show success message
        if (getContext() != null) {
            Toast.makeText(getContext(), "Loaded " + sampleComponents.size() + " sample components", Toast.LENGTH_LONG).show();
        }
        
        applyFilters();
        
        // Debug logging
        Log.d("ComponentSearch", "applyFilters called after adding sample components");
    }

    private void applyFilters() {
        filteredComponents.clear();

        // Use a Set to track unique component IDs to prevent duplicates
        Set<String> addedComponentIds = new HashSet<>();

        for (ComponentModel component : allComponents) {
            // Skip if already added
            if (addedComponentIds.contains(component.getId())) {
                continue;
            }

            // Enhanced search matching with generation and budget relevance
            boolean matchesQuery = currentQuery.isEmpty() || matchesSearchQuery(component, currentQuery);

            boolean matchesPrice = component.getPrice() >= minPrice && component.getPrice() <= maxPrice;
            boolean matchesRating = component.getRating() >= minRating;

            if (matchesQuery && matchesPrice && matchesRating) {
                filteredComponents.add(component);
                addedComponentIds.add(component.getId());
            }
        }

        updateSearchResults();
    }

    private boolean matchesSearchQuery(ComponentModel component, String query) {
        String lowerQuery = query.toLowerCase();
        String lowerName = component.getName().toLowerCase();
        String lowerDescription = component.getDescription().toLowerCase();
        String lowerCategory = component.getCategory().toLowerCase();
        String lowerBrand = component.getBrand().toLowerCase();

        // Direct matches
        if (lowerName.contains(lowerQuery) || 
            lowerDescription.contains(lowerQuery) || 
            lowerCategory.contains(lowerQuery) || 
            lowerBrand.contains(lowerQuery)) {
            return true;
        }

        // Generation matching (e.g., "i5 10th gen" matches "Intel Core i5-10400 (10th Generation)")
        if (lowerQuery.contains("gen") || lowerQuery.contains("generation")) {
            if (lowerName.contains("generation") || lowerName.contains("gen")) {
                // Extract generation number from query
                String genNumber = extractGenerationNumber(lowerQuery);
                if (genNumber != null && lowerName.contains(genNumber)) {
                    return true;
                }
            }
        }

        // Budget component matching
        if (lowerQuery.contains("budget") || lowerQuery.contains("cheap") || lowerQuery.contains("low")) {
            if (component.getPrice() <= 5000) { // Budget threshold
                return true;
            }
        }

        // Chipset matching (e.g., "H61" matches "Gigabyte GA-H61M-S1")
        if (lowerQuery.contains("h61") || lowerQuery.contains("h81") || lowerQuery.contains("b550") || 
            lowerQuery.contains("b660") || lowerQuery.contains("x570")) {
            if (lowerName.contains(lowerQuery) || 
                (component.getSpecifications().get("Chipset") != null && 
                 component.getSpecifications().get("Chipset").toLowerCase().contains(lowerQuery))) {
                return true;
            }
        }

        // Processor series matching (e.g., "i5" matches all i5 processors)
        if (lowerQuery.contains("i3") || lowerQuery.contains("i5") || lowerQuery.contains("i7") || 
            lowerQuery.contains("i9") || lowerQuery.contains("ryzen")) {
            if (lowerName.contains(lowerQuery)) {
                return true;
            }
        }

        // Memory type matching (e.g., "DDR3" matches DDR3 RAM)
        if (lowerQuery.contains("ddr3") || lowerQuery.contains("ddr4") || lowerQuery.contains("ddr5")) {
            if (lowerName.contains(lowerQuery) || 
                (component.getSpecifications().get("Type") != null && 
                 component.getSpecifications().get("Type").toLowerCase().contains(lowerQuery))) {
                return true;
            }
        }

        return false;
    }

    private String extractGenerationNumber(String query) {
        // Extract generation number from query (e.g., "10th gen" -> "10th", "2nd gen" -> "2nd")
        if (query.contains("10th") || query.contains("10")) return "10th";
        if (query.contains("9th") || query.contains("9")) return "9th";
        if (query.contains("8th") || query.contains("8")) return "8th";
        if (query.contains("7th") || query.contains("7")) return "7th";
        if (query.contains("6th") || query.contains("6")) return "6th";
        if (query.contains("5th") || query.contains("5")) return "5th";
        if (query.contains("4th") || query.contains("4")) return "4th";
        if (query.contains("3rd") || query.contains("3")) return "3rd";
        if (query.contains("2nd") || query.contains("2")) return "2nd";
        if (query.contains("1st") || query.contains("1")) return "1st";
        return null;
    }

    private void clearFilters() {
        currentQuery = "";
        minPrice = 0;
        maxPrice = 100000;
        selectedBrand = "";
        minRating = 0;
        
        if (searchInput != null) {
            searchInput.setText("");
        }
        if (minPriceInput != null) {
            minPriceInput.setText("");
        }
        if (maxPriceInput != null) {
            maxPriceInput.setText("");
        }
        if (priceRangeSeekBar != null) {
            priceRangeSeekBar.setProgress(100);
        }
        if (priceRangeText != null) {
            priceRangeText.setText("Max Price: ₹100,000");
        }
        
        applyFilters();
    }

    private void updateSearchResults() {
        try {
            // Debug logging
            Log.d("ComponentSearch", "updateSearchResults called with " + filteredComponents.size() + " components");
            
            if (getContext() == null) {
                Log.e("ComponentSearch", "Context is null in updateSearchResults");
                return;
            }
            
            if (filteredComponents.isEmpty()) {
                if (!currentQuery.isEmpty()) {
                    Toast.makeText(getContext(), "No components found matching '" + currentQuery + "'", Toast.LENGTH_SHORT).show();
                }
                // Show empty state
                if (searchResultsRecyclerView != null) {
                    searchResultsRecyclerView.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(getContext(), "Found " + filteredComponents.size() + " components", Toast.LENGTH_SHORT).show();
                
                // Create new adapter with proper configuration
                try {
                    searchAdapter = new ComponentAdapter(getContext(), filteredComponents, component -> {
                        try {
                            // Add to cart
                            if (getContext() != null) {
                                CartManager.getInstance(requireContext()).addToCart(component);
                                Toast.makeText(getContext(), component.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("ComponentSearch", "Error adding component to cart", e);
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Error adding to cart", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    
                    // Set adapter and notify data change
                    if (searchResultsRecyclerView != null) {
                        searchResultsRecyclerView.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                        
                        // Ensure RecyclerView is visible
                        searchResultsRecyclerView.setVisibility(View.VISIBLE);
                        
                        // Debug logging
                        Log.d("ComponentSearch", "Adapter set with " + filteredComponents.size() + " items");
                    } else {
                        Log.e("ComponentSearch", "searchResultsRecyclerView is null in updateSearchResults");
                    }
                } catch (Exception e) {
                    Log.e("ComponentSearch", "Error creating adapter", e);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error displaying components", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ComponentSearch", "Error in updateSearchResults", e);
        }
    }
} 
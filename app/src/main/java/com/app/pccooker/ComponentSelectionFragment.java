package com.app.pccooker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.ComponentModel;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class ComponentSelectionFragment extends Fragment {

    private RecyclerView componentRecyclerView;
    private EditText searchInput;
    private TextView categoryTitle, priceRangeText, sortByText;
    private ImageView backButton, filterButton;
    private ComponentAdapter adapter;
    private List<ComponentModel> allComponents = new ArrayList<>();
    private List<ComponentModel> filteredComponents = new ArrayList<>();
    private String category;
    private double maxBudget;

    public static ComponentSelectionFragment newInstance(String category, double maxBudget) {
        ComponentSelectionFragment fragment = new ComponentSelectionFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        args.putDouble("maxBudget", maxBudget);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_component_selection, container, false);

        if (getArguments() != null) {
            category = getArguments().getString("category", "");
            maxBudget = getArguments().getDouble("maxBudget", 0.0);
        }

        initializeViews(view);
        setupRecyclerView();
        setupSearchFunctionality();
        loadComponents();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        componentRecyclerView = view.findViewById(R.id.componentRecyclerView);
        searchInput = view.findViewById(R.id.searchInput);
        categoryTitle = view.findViewById(R.id.categoryTitle);
        priceRangeText = view.findViewById(R.id.priceRangeText);
        sortByText = view.findViewById(R.id.sortByText);
        backButton = view.findViewById(R.id.backButton);
        filterButton = view.findViewById(R.id.filterButton);

        categoryTitle.setText(category);
        priceRangeText.setText("Up to ₹" + String.format("%.0f", maxBudget));
    }

    private void setupRecyclerView() {
        componentRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ComponentAdapter(requireContext(), filteredComponents, component -> {
            // Handle component selection
            ComponentManager.getInstance(requireContext()).selectComponent(category, component);
            
            // Also add to cart
            CartManager.getInstance(requireContext()).addToCart(component);
            
            Toast.makeText(getContext(), component.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
            
            // Update cart badge if in MainActivity
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateCartBadge();
            }
            
            // Navigate back to build PC fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        componentRecyclerView.setAdapter(adapter);
    }

    private void setupSearchFunctionality() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterComponents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        
        filterButton.setOnClickListener(v -> showFilterDialog());
        
        sortByText.setOnClickListener(v -> showSortDialog());
    }

    private void loadComponents() {
        // Show loading state
        showLoadingState();

        // Fetch components from Firebase
        Log.d("ComponentSelection", "Loading components from Firebase for category: " + category);
        
        FirebaseFirestore.getInstance()
                .collection("pc_components")
                .document(category)
                .collection("items")
                .get()
                .addOnSuccessListener(itemsQuery -> {
                    if (!isAdded()) return;
                    
                    Log.d("ComponentSelection", "Found " + itemsQuery.size() + " items for category: " + category);
                    allComponents.clear();
                    filteredComponents.clear();
                    
                    if (itemsQuery.isEmpty()) {
                        Log.w("ComponentSelection", "No components found for category: " + category);
                        showEmptyState();
                        return;
                    }
                    
                    for (QueryDocumentSnapshot doc : itemsQuery) {
                        try {
                            ComponentModel component = doc.toObject(ComponentModel.class);
                            if (component != null) {
                                // Set fallback values
                                if (component.getName() == null || component.getName().isEmpty()) {
                                    component.setName("Unnamed Component");
                                }
                                if (component.getDescription() == null || component.getDescription().isEmpty()) {
                                    component.setDescription("No description available");
                                }
                                if (component.getPrice() <= 0) {
                                    component.setPrice(1000);
                                }
                                if (component.getRating() <= 0) {
                                    component.setRating(3.0);
                                }
                                if (component.getRatingCount() <= 0) {
                                    component.setRatingCount(10);
                                }
                                if (component.getBrand() == null || component.getBrand().isEmpty()) {
                                    component.setBrand("Unknown Brand");
                                }
                                if (component.getCategory() == null || component.getCategory().isEmpty()) {
                                    component.setCategory(category);
                                }
                                
                                allComponents.add(component);
                                Log.d("ComponentSelection", "Added component: " + component.getName());
                            }
                        } catch (Exception e) {
                            Log.e("ComponentSelection", "Error parsing component from document: " + doc.getId(), e);
                        }
                    }
                    
                    filteredComponents.addAll(allComponents);
                    
                    if (allComponents.isEmpty()) {
                        showEmptyState();
                    } else {
                        hideLoadingState();
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    
                    Log.e("ComponentSelection", "Failed to fetch components for category: " + category, e);
                    hideLoadingState();
                    showErrorState("Failed to load components: " + e.getMessage());
                });
    }
    


    private void filterComponents(String query) {
        filteredComponents.clear();
        for (ComponentModel component : allComponents) {
            if (component.getName().toLowerCase().contains(query.toLowerCase()) ||
                component.getBrand().toLowerCase().contains(query.toLowerCase())) {
                filteredComponents.add(component);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showFilterDialog() {
        // TODO: Implement filter dialog with price range, brand, rating filters
        Toast.makeText(getContext(), "Filter options coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void showSortDialog() {
        // TODO: Implement sort dialog (Price: Low to High, High to Low, Rating, etc.)
        Toast.makeText(getContext(), "Sort options coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void showLoadingState() {
        // TODO: Show loading indicator
    }

    private void hideLoadingState() {
        // TODO: Hide loading indicator
    }

    private void showEmptyState() {
        // TODO: Show empty state with message
    }

    private void showErrorState(String message) {
        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }
} 
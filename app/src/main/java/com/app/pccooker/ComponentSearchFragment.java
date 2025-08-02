package com.app.pccooker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ComponentSearchFragment extends Fragment {

    private static final String ARG_QUERY = "search_query";

    private RecyclerView recyclerView;
    private ComponentAdapter adapter;
    private ProgressBar progressBar;
    private TextView noResultsText;
    private TextView searchQueryText;
    
    private List<ComponentModel> searchResults = new ArrayList<>();
    private FirebaseFirestore db;
    private String searchQuery;

    public static ComponentSearchFragment newInstance(String query) {
        ComponentSearchFragment fragment = new ComponentSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_component_search, container, false);

        recyclerView = view.findViewById(R.id.searchRecyclerView);
        progressBar = view.findViewById(R.id.searchProgressBar);
        noResultsText = view.findViewById(R.id.noResultsText);
        searchQueryText = view.findViewById(R.id.searchQueryText);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            searchQuery = getArguments().getString(ARG_QUERY, "");
            searchQueryText.setText("Search results for: " + searchQuery);
        }

        setupRecyclerView();
        performSearch();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ComponentAdapter(requireContext(), searchResults, component -> {
            // Add to cart
            CartManager.getInstance(requireContext()).addToCart(component);
            Toast.makeText(getContext(), component.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            
            // Update cart badge
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateCartBadge();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void performSearch() {
        progressBar.setVisibility(View.VISIBLE);
        noResultsText.setVisibility(View.GONE);

        // Search across all component categories
        searchResults.clear();
        adapter.notifyDataSetChanged();

        // Search in all categories
        String[] categories = {"PROCESSOR", "GRAPHIC CARD", "RAM", "MOTHERBOARD", "STORAGE", "POWER SUPPLY", "CABINET"};
        
        int[] searchCount = {0};
        int totalCategories = categories.length;

        for (String category : categories) {
            db.collection("pc_components")
                .document(category)
                .collection("items")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    searchCount[0]++;
                    
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        try {
                            ComponentModel component = doc.toObject(ComponentModel.class);
                            if (component != null) {
                                component.setId(doc.getId());
                                
                                // Check if component matches search query
                                if (matchesSearch(component, searchQuery)) {
                                    searchResults.add(component);
                                }
                            }
                        } catch (Exception e) {
                            // Skip this component if deserialization fails
                            System.err.println("Failed to deserialize component in search: " + e.getMessage());
                        }
                    }
                    
                    // If all categories searched, update UI
                    if (searchCount[0] == totalCategories) {
                        updateSearchResults();
                    }
                })
                .addOnFailureListener(e -> {
                    searchCount[0]++;
                    if (searchCount[0] == totalCategories) {
                        updateSearchResults();
                    }
                });
        }
    }

    private boolean matchesSearch(ComponentModel component, String query) {
        if (query == null || query.isEmpty()) return false;
        
        String searchLower = query.toLowerCase();
        return component.getName().toLowerCase().contains(searchLower) ||
               component.getBrand().toLowerCase().contains(searchLower) ||
               component.getCategory().toLowerCase().contains(searchLower) ||
               component.getDescription().toLowerCase().contains(searchLower);
    }

    private void updateSearchResults() {
        progressBar.setVisibility(View.GONE);
        
        if (searchResults.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);
            noResultsText.setText("No components found for: " + searchQuery);
        } else {
            noResultsText.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }
} 
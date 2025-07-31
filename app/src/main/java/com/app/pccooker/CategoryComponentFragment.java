package com.app.pccooker;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.app.pccooker.ComponentModel;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class CategoryComponentFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView titleText;

    private String category;
    private ComponentAdapter adapter;
    private final List<ComponentModel> componentList = new ArrayList<>();

    public static CategoryComponentFragment newInstance(String category) {
        CategoryComponentFragment fragment = new CategoryComponentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_component, container, false);

        recyclerView = view.findViewById(R.id.componentRecyclerView);
        progressBar = view.findViewById(R.id.loadingBar);
        titleText = view.findViewById(R.id.categoryTitle);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
            if (titleText != null) {
                titleText.setText(category);
            }
            Log.d("CategoryComponent", "Category: " + category);
        }

        if (getContext() == null) {
            Log.e("CategoryComponent", "Context is null");
            return;
        }

        adapter = new ComponentAdapter(requireContext(), componentList, new ComponentAdapter.OnComponentClickListener() {
            @Override
            public void onComponentClick(ComponentModel component) {
                if (getContext() == null) return;
                
                if (!CartManager.getInstance(requireContext()).isInCart(component)) {
                    CartManager.getInstance(requireContext()).addToCart(component);
                    Toast.makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).updateCartBadge();
                    }
                } else {
                    Toast.makeText(getContext(), "Already in cart", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
        
        if (category != null && !category.isEmpty()) {
            fetchComponents();
        } else {
            Log.e("CategoryComponent", "Category is null or empty");
            Toast.makeText(getContext(), "Invalid category", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchComponents() {
        if (!isAdded() || getContext() == null) {
            Log.e("CategoryComponent", "Fragment not attached or context is null");
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        
        Log.d("CategoryComponent", "Fetching components for category: " + category);

        // Fetch components from Firebase
                    FirebaseFirestore.getInstance()
                            .collection("pc_components")
                            .document(category)
                            .collection("items")
                            .get()
                            .addOnSuccessListener(itemsQuery -> {
                                if (!isAdded()) return;
                                
                                Log.d("CategoryComponent", "Found " + itemsQuery.size() + " items for category: " + category);
                                componentList.clear();
                                
                                if (itemsQuery.isEmpty()) {
                                    Log.w("CategoryComponent", "No components found for category: " + category);
                                    if (getContext() != null) {
                            Toast.makeText(getContext(), "No components found for " + category, Toast.LENGTH_SHORT).show();
                        }
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                                    }
                                    return;
                                }
                                
                                Log.d("CategoryComponent", "Processing " + itemsQuery.size() + " components for category: " + category);
                                
                                for (QueryDocumentSnapshot doc : itemsQuery) {
                                    try {
                                        Log.d("CategoryComponent", "Processing document: " + doc.getId());
                                        ComponentModel component = doc.toObject(ComponentModel.class);

                                        if (component == null) {
                                            Log.e("CategoryComponent", "Failed to parse component from document: " + doc.getId());
                                            continue;
                                        }

                                        // Set fallback/default values to avoid null issues
                                        if (component.getName() == null || component.getName().isEmpty()) {
                                            component.setName("Unnamed Component");
                                        }
                                        if (component.getDescription() == null || component.getDescription().isEmpty()) {
                                            component.setDescription("No description available");
                                        }
                                        if (component.getPrice() <= 0) {
                                            component.setPrice(1000); // Default price
                                        }
                                        if (component.getRating() <= 0) {
                                            component.setRating(3.0); // Default rating
                                        }
                                        if (component.getRatingCount() <= 0) {
                                            component.setRatingCount(10); // Default rating count
                                        }
                                        if (component.getImageUrl() == null) {
                                            component.setImageUrl("");
                                        }
                                        if (component.getBrand() == null || component.getBrand().isEmpty()) {
                                            component.setBrand("Unknown Brand");
                                        }
                                        if (component.getCategory() == null || component.getCategory().isEmpty()) {
                                            component.setCategory(category);
                                        }

                                        componentList.add(component);
                                        Log.d("CategoryComponent", "Successfully added component: " + component.getName() + " (Price: " + component.getPrice() + ", Rating: " + component.getRating() + ")");
                                    } catch (Exception e) {
                                        Log.e("CategoryComponent", "Error parsing component from document: " + doc.getId(), e);
                                        // Skip this component and continue with the next one
                                    }
                                }
                                
                                Log.d("CategoryComponent", "Total components loaded: " + componentList.size());
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                                if (progressBar != null) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (!isAdded()) return;
                                
                                Log.e("CategoryComponent", "Failed to fetch items for category: " + category, e);
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Failed to load components: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                if (progressBar != null) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
    }

}

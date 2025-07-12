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
            titleText.setText(category);
            Log.d("CategoryComponent", "Category: " + category);

        }

        adapter = new ComponentAdapter(requireContext(), componentList, new ComponentAdapter.OnComponentClickListener() {
            @Override
            public void onComponentClick(ComponentModel component) {
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

        recyclerView.setAdapter(adapter);
        fetchComponents();
    }

    private void fetchComponents() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance()
                .collection("pc_components")
                .document(category)
                .collection("items")
                .get()
                .addOnSuccessListener(query -> {
                    componentList.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        try {
                            ComponentModel component = doc.toObject(ComponentModel.class);

                            // Set fallback/default values to avoid null issues
                            if (component.getName() == null) component.setName("Unnamed");
                            if (component.getDescription() == null) component.setDescription("No description");
                            if (component.getPrice() == 0) component.setPrice(1);  // Avoid blank/0 price
                            if (component.getImageUrl() == null) component.setImageUrl(""); // Prevent crash

                            componentList.add(component);
                        } catch (Exception e) {
                            Log.e("CategoryComponent", "Error parsing component", e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Log.e("CategoryComponent", "Fetch failed", e);
                    progressBar.setVisibility(View.GONE);
                });

    }


}

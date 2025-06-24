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

import com.app.pccooker.models.PCComponent;
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
    private final List<PCComponent> componentList = new ArrayList<>();

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

        adapter = new ComponentAdapter(componentList, new ComponentAdapter.OnComponentActionListener() {
            @Override
            public void onSelectClicked(PCComponent component) {
                if (!CartManager.getInstance().isInCart(component)) {
                    CartManager.getInstance().addToCart(component);
                    Toast.makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).updateCartBadge();
                    }
                } else {
                    Toast.makeText(getContext(), "Already in cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSaveForLaterClicked(PCComponent component) {
                if (!CartManager.getInstance().isSavedForLater(component)) {
                    CartManager.getInstance().saveForLater(component);
                    Toast.makeText(getContext(), "Saved for later", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Already in saved list", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(adapter);
        fetchComponents();
    }

    private void fetchComponents() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance()
                .collection("pc_components")  // ✅ Corrected collection name
                .document(category)           // e.g., "PROCESSOR"
                .collection("items")
                .get()
                .addOnSuccessListener(query -> {
                    componentList.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        try {
                            PCComponent component = doc.toObject(PCComponent.class);
                            componentList.add(component);
                            Log.d("FirebaseData", "Fetched: " + component.getName());
                        } catch (Exception e) {
                            Log.e("ComponentParse", "Error parsing component", e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Log.e("ComponentFetch", "Firestore fetch failed", e);
                    progressBar.setVisibility(View.GONE);
                });
    }


}

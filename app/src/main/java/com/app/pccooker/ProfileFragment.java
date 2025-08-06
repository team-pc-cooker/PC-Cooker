package com.app.pccooker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.CartItem;
import com.app.pccooker.ComponentModel;
import com.app.pccooker.models.OrderModel;
import com.app.pccooker.adapters.SavedItemsAdapter;
import com.app.pccooker.adapters.OrderHistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView userNameText;
    private TextView userEmailText;
    private ImageView userAvatarImage;
    private Button logoutButton;
    private Button editProfileButton;
    
    // Profile sections
    private LinearLayout savedItemsSection;
    private LinearLayout orderHistorySection;
    private LinearLayout settingsSection;
    private LinearLayout helpSection;
    
    // Saved items
    private RecyclerView savedItemsRecyclerView;
    private SavedItemsAdapter savedItemsAdapter;
    private List<ComponentModel> savedItems = new ArrayList<>();
    
    // Order history
    private RecyclerView orderHistoryRecyclerView;
    private OrderHistoryAdapter orderHistoryAdapter;
    private List<OrderModel> orderHistory = new ArrayList<>();
    
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews(view);
        setupClickListeners();
        loadUserProfile();
        loadSavedItems();
        loadOrderHistory();

        return view;
    }

    private void initializeViews(View view) {
        // User info
        userNameText = view.findViewById(R.id.userNameText);
        userEmailText = view.findViewById(R.id.userEmailText);
        userAvatarImage = view.findViewById(R.id.userAvatarImage);
        logoutButton = view.findViewById(R.id.logoutButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        
        // Profile sections
        savedItemsSection = view.findViewById(R.id.savedItemsSection);
        orderHistorySection = view.findViewById(R.id.orderHistorySection);
        settingsSection = view.findViewById(R.id.settingsSection);
        helpSection = view.findViewById(R.id.helpSection);
        
        // Saved items
        savedItemsRecyclerView = view.findViewById(R.id.savedItemsRecyclerView);
        savedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Order history
        orderHistoryRecyclerView = view.findViewById(R.id.orderHistoryRecyclerView);
        orderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupClickListeners() {
        logoutButton.setOnClickListener(v -> logout());
        editProfileButton.setOnClickListener(v -> editProfile());
        
        // Section click listeners
        savedItemsSection.setOnClickListener(v -> toggleSavedItems());
        orderHistorySection.setOnClickListener(v -> toggleOrderHistory());
        settingsSection.setOnClickListener(v -> openSettings());
        helpSection.setOnClickListener(v -> openHelp());
    }

    private void loadUserProfile() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userNameText.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
            userEmailText.setText(user.getEmail());
            
            // Load user avatar if available
            if (user.getPhotoUrl() != null) {
                // Use Glide or similar library to load image
                // Glide.with(this).load(user.getPhotoUrl()).into(userAvatarImage);
            }
        } else {
            // User not logged in - redirect to login
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private void loadSavedItems() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid())
            .collection("saved_items")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                savedItems.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    ComponentModel component = document.toObject(ComponentModel.class);
                    if (component != null) {
                        component.setId(document.getId());
                        savedItems.add(component);
                    }
                }
                
                savedItemsAdapter = new SavedItemsAdapter(getContext(), savedItems, new SavedItemsAdapter.OnSavedItemActionListener() {
                    @Override
                    public void onAddToCartClicked(ComponentModel component) {
                        CartManager.getInstance(requireContext()).addToCart(component);
                        Toast.makeText(getContext(), component.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRemoveClicked(ComponentModel component) {
                        removeFromSavedItems(component.getId());
                    }
                });
                savedItemsRecyclerView.setAdapter(savedItemsAdapter);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to load saved items", Toast.LENGTH_SHORT).show();
            });
    }

    private void loadOrderHistory() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("orders")
            .whereEqualTo("userId", user.getUid())
            .orderBy("orderDate")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                orderHistory.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    OrderModel order = document.toObject(OrderModel.class);
                    if (order != null) {
                        order.setOrderId(document.getId());
                        orderHistory.add(order);
                    }
                }
                
                orderHistoryAdapter = new OrderHistoryAdapter(getContext(), orderHistory);
                orderHistoryRecyclerView.setAdapter(orderHistoryAdapter);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to load order history", Toast.LENGTH_SHORT).show();
            });
    }

    private void removeFromSavedItems(String itemId) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid())
            .collection("saved_items")
            .document(itemId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Item removed from saved items", Toast.LENGTH_SHORT).show();
                loadSavedItems(); // Reload the list
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to remove item", Toast.LENGTH_SHORT).show();
            });
    }

    private void logout() {
        auth.signOut();
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Redirect to login
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void editProfile() {
        // TODO: Implement edit profile functionality
        Toast.makeText(getContext(), "Edit profile coming soon", Toast.LENGTH_SHORT).show();
    }

    private void toggleSavedItems() {
        // TODO: Implement toggle functionality
        Toast.makeText(getContext(), "Saved items: " + savedItems.size() + " items", Toast.LENGTH_SHORT).show();
    }

    private void toggleOrderHistory() {
        // TODO: Implement toggle functionality
        Toast.makeText(getContext(), "Order history: " + orderHistory.size() + " orders", Toast.LENGTH_SHORT).show();
    }

    private void openSettings() {
        // TODO: Implement settings
        Toast.makeText(getContext(), "Settings coming soon", Toast.LENGTH_SHORT).show();
    }

    private void openHelp() {
        // TODO: Implement help/support
        Toast.makeText(getContext(), "Help & Support coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
        loadSavedItems();
        loadOrderHistory();
    }
}

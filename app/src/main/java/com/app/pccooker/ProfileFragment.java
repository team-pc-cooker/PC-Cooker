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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.pccooker.adapters.SavedItemsAdapter;
import com.app.pccooker.models.ComponentModel;
import com.app.pccooker.ui.UiNotifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.app.pccooker.models.UserModel;
import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment {

    // User info views
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
    private LinearLayout statisticsSection;
    private LinearLayout adminPanelSection; // New admin section
    
    // Count displays
    private TextView savedItemsCountText;
    private TextView orderHistoryCountText;
    
    // Saved items
    private RecyclerView savedItemsRecyclerView;
    private SavedItemsAdapter savedItemsAdapter;
    private List<ComponentModel> savedItems = new ArrayList<>();
    
    // Order history - now navigates to MyOrdersActivity
    private Button viewOrdersButton;
    
    // Firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    
    // Admin check
    private boolean isAdmin = false;

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
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload data when returning to profile
        loadUserProfile();
        loadSavedItems();
        loadOrderHistory();
    }

    private void initializeViews(View view) {
        try {
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
            statisticsSection = view.findViewById(R.id.statisticsSection);
            adminPanelSection = view.findViewById(R.id.adminPanelSection); // New admin section
            
            // Count displays
            savedItemsCountText = view.findViewById(R.id.savedItemsCount);
            orderHistoryCountText = view.findViewById(R.id.orderHistoryCount);
            
            // Saved items
            savedItemsRecyclerView = view.findViewById(R.id.savedItemsRecyclerView);
            if (savedItemsRecyclerView != null) {
                savedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
            
            // Order history - now navigates to MyOrdersActivity
            
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error initializing views: " + e.getMessage());
        }
    }

    private void setupClickListeners() {
        try {
            if (logoutButton != null) {
                logoutButton.setOnClickListener(v -> logout());
            }
            
            if (editProfileButton != null) {
                editProfileButton.setOnClickListener(v -> editProfile());
            }
            
            // Section click listeners with proper error handling
            if (savedItemsSection != null) {
                savedItemsSection.setOnClickListener(v -> toggleSavedItems());
            }
            
            if (orderHistorySection != null) {
                orderHistorySection.setOnClickListener(v -> toggleOrderHistory());
            }
            
            if (settingsSection != null) {
                settingsSection.setOnClickListener(v -> openSettings());
            }
            
            if (helpSection != null) {
                helpSection.setOnClickListener(v -> openHelp());
            }
            
            if (statisticsSection != null) {
                statisticsSection.setOnClickListener(v -> openStatistics());
            }
            
            // Admin panel click listener
            if (adminPanelSection != null) {
                adminPanelSection.setOnClickListener(v -> openAdminPanel());
            }
            
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error setting up click listeners: " + e.getMessage());
        }
    }

    private void loadUserProfile() {
        try {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                // First set basic info from Firebase Auth
                if (userNameText != null) {
                    userNameText.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
                }
                
                if (userEmailText != null) {
                    userEmailText.setText(user.getEmail());
                }
                
                // Load detailed profile from Firestore
                db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        try {
                            if (documentSnapshot.exists()) {
                                // Get raw document data to bypass UserModel deserialization issues
                                Map<String, Object> data = documentSnapshot.getData();
                                
                                // Set admin status directly from raw data
                                if (data != null && data.containsKey("isAdmin")) {
                                    Object isAdminValue = data.get("isAdmin");
                                    if (isAdminValue instanceof Boolean) {
                                        isAdmin = (Boolean) isAdminValue;
                                    } else if (isAdminValue instanceof String) {
                                        isAdmin = "true".equalsIgnoreCase((String) isAdminValue);
                                    } else {
                                        isAdmin = false;
                                    }
                                } else {
                                    isAdmin = false;
                                }
                                
                                // Log for debugging
                                Log.d("ProfileFragment", "Raw document data: " + data);
                                Log.d("ProfileFragment", "isAdmin value: " + isAdmin);
                                
                                // Update admin section visibility immediately
                                updateAdminSectionVisibility();
                                
                                // Try to create UserModel for other fields (but don't fail if it doesn't work)
                                try {
                                    UserModel userModel = documentSnapshot.toObject(UserModel.class);
                                    if (userModel != null && userNameText != null) {
                                        // Update display name if available
                                        if (userModel.getFirstName() != null && userModel.getLastName() != null) {
                                            String fullName = userModel.getFirstName() + " " + userModel.getLastName();
                                            userNameText.setText(fullName);
                                        }
                                        
                                        // Load profile image if available
                                        if (userModel.getProfileImageUrl() != null && !userModel.getProfileImageUrl().isEmpty() && userAvatarImage != null) {
                                            Glide.with(this)
                                                .load(userModel.getProfileImageUrl())
                                                .placeholder(R.drawable.ic_profile_placeholder)
                                                .error(R.drawable.ic_profile_placeholder)
                                                .into(userAvatarImage);
                                        } else if (user.getPhotoUrl() != null && userAvatarImage != null) {
                                            // Fallback to Firebase Auth photo URL
                                            Glide.with(this)
                                                .load(user.getPhotoUrl())
                                                .placeholder(R.drawable.ic_profile_placeholder)
                                                .error(R.drawable.ic_profile_placeholder)
                                                .into(userAvatarImage);
                                        }
                                    }
                                } catch (Exception userModelException) {
                                    Log.e("ProfileFragment", "UserModel creation failed: " + userModelException.getMessage());
                                    // Continue without UserModel - we already set isAdmin above
                                    // Try to set display name from raw data
                                    if (data != null && data.containsKey("firstName") && data.containsKey("lastName")) {
                                        String firstName = String.valueOf(data.get("firstName"));
                                        String lastName = String.valueOf(data.get("lastName"));
                                        if (userNameText != null) {
                                            String fullName = firstName + " " + lastName;
                                            userNameText.setText(fullName);
                                        }
                                    }
                                }
                                
                            } else {
                                Log.e("ProfileFragment", "Document does not exist");
                                isAdmin = false;
                                updateAdminSectionVisibility();
                            }
                        } catch (Exception e) {
                            Log.e("ProfileFragment", "Error processing document: " + e.getMessage());
                            isAdmin = false;
                            updateAdminSectionVisibility();
                            UiNotifier.showError(getContext(), "Error loading user profile: " + e.getMessage());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileFragment", "Failed to load user profile: " + e.getMessage());
                        isAdmin = false;
                        updateAdminSectionVisibility();
                        UiNotifier.showError(getContext(), "Failed to load user profile: " + e.getMessage());
                    });
            }
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error in loadUserProfile: " + e.getMessage());
        }
    }
    
    private void updateAdminSectionVisibility() {
        // Debug logging
        Log.d("ProfileFragment", "updateAdminSectionVisibility called");
        Log.d("ProfileFragment", "isAdmin: " + isAdmin);
        Log.d("ProfileFragment", "adminPanelSection: " + (adminPanelSection != null ? "not null" : "null"));
        
        if (adminPanelSection != null) {
            int visibility = isAdmin ? View.VISIBLE : View.GONE;
            Log.d("ProfileFragment", "Setting adminPanelSection visibility to: " + (visibility == View.VISIBLE ? "VISIBLE" : "GONE"));
            adminPanelSection.setVisibility(visibility);
            
            // Check actual visibility after setting
            Log.d("ProfileFragment", "adminPanelSection actual visibility: " + 
                (adminPanelSection.getVisibility() == View.VISIBLE ? "VISIBLE" : 
                 adminPanelSection.getVisibility() == View.GONE ? "GONE" : "INVISIBLE"));
        } else {
            Log.e("ProfileFragment", "adminPanelSection is null - cannot update visibility");
        }
    }
    
    private void openAdminPanel() {
        try {
            Intent intent = new Intent(getContext(), AdminPanelActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error opening admin panel: " + e.getMessage());
        }
    }

    private void loadSavedItems() {
        try {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) return;

            db.collection("users").document(user.getUid())
                .collection("saved_items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    try {
                        savedItems.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ComponentModel component = document.toObject(ComponentModel.class);
                            if (component != null) {
                                component.setId(document.getId());
                                savedItems.add(component);
                            }
                        }
                        
                        // Update count display
                        if (savedItemsCountText != null) {
                            savedItemsCountText.setText(String.valueOf(savedItems.size()));
                        }
                        
                        // Update adapter if recycler view exists
                        if (savedItemsRecyclerView != null) {
                            savedItemsAdapter = new SavedItemsAdapter(getContext(), savedItems, new SavedItemsAdapter.OnSavedItemActionListener() {
                                @Override
                                public void onAddToCartClicked(ComponentModel component) {
                                    try {
                                        CartManager.getInstance(requireContext()).addToCart(component);
                                        // Component added to cart (no toast shown)
                                    } catch (Exception e) {
                                        UiNotifier.showShort(getContext(), "Error adding to cart");
                                    }
                                }

                                @Override
                                public void onRemoveClicked(ComponentModel component) {
                                    removeFromSavedItems(component.getId());
                                }

                                @Override
                                public void onItemClicked(ComponentModel component) {
                                    UiNotifier.showInfo(getContext(), "Selected: " + component.getName());
                                }
                            });
                            savedItemsRecyclerView.setAdapter(savedItemsAdapter);
                        }
                    } catch (Exception e) {
                        UiNotifier.showError(getContext(), "Error processing saved items");
                    }
                })
                .addOnFailureListener(e -> {
                    UiNotifier.showError(getContext(), "Failed to load saved items");
                });
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error loading saved items: " + e.getMessage());
        }
    }

    private void loadOrderHistory() {
        try {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) return;

            // Get order count for display
            db.collection("orders")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    try {
                        int orderCount = querySnapshot.size();
                        
                        // Update count display
                        if (orderHistoryCountText != null) {
                            orderHistoryCountText.setText(String.valueOf(orderCount));
                        }
                    } catch (Exception e) {
                        UiNotifier.showError(getContext(), "Error processing order count");
                    }
                })
                .addOnFailureListener(e -> {
                    UiNotifier.showError(getContext(), "Failed to load order count");
                });
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error loading order count: " + e.getMessage());
        }
    }

    private void removeFromSavedItems(String itemId) {
        try {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) return;

            db.collection("users").document(user.getUid())
                .collection("saved_items")
                .document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    UiNotifier.showSuccess(getContext(), "Item removed from saved items");
                    loadSavedItems(); // Reload the list
                })
                .addOnFailureListener(e -> {
                    UiNotifier.showError(getContext(), "Failed to remove item");
                });
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error removing item: " + e.getMessage());
        }
    }

    private void logout() {
        try {
            auth.signOut();
            UiNotifier.showSuccess(getContext(), "Logged out successfully");
            
            // Redirect to login
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error during logout: " + e.getMessage());
        }
    }

    private void editProfile() {
        try {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error opening edit profile: " + e.getMessage());
        }
    }

    private void toggleSavedItems() {
        try {
            if (savedItemsRecyclerView != null) {
                if (savedItemsRecyclerView.getVisibility() == View.VISIBLE) {
                    savedItemsRecyclerView.setVisibility(View.GONE);
                } else {
                    savedItemsRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error toggling saved items: " + e.getMessage());
        }
    }

    private void toggleOrderHistory() {
        try {
            // Navigate to MyOrdersActivity instead of toggling visibility
            Intent intent = new Intent(getContext(), MyOrdersActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error opening orders: " + e.getMessage());
        }
    }

    private void openSettings() {
        try {
            Intent intent = new Intent(getContext(), ProfileSettingsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error opening settings: " + e.getMessage());
        }
    }

    private void openHelp() {
        try {
            Intent intent = new Intent(getContext(), HelpAndSupportActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error opening help: " + e.getMessage());
        }
    }

    private void openStatistics() {
        try {
            Intent intent = new Intent(getContext(), ProfileStatsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            UiNotifier.showError(getContext(), "Error opening statistics: " + e.getMessage());
        }
    }
}
